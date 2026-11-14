#!/usr/bin/env bash
# 출시가 끝난 릴리즈·핫픽스를 마무리한다
#   main 머지 → 태그 → GitHub Release → develop 역머지 PR(자동 머지 예약) → 마일스톤 정리
# 사용법: finish.sh <release|hotfix> [버전] [--apply]
# 단계마다 이미 끝났는지 먼저 확인하므로 중간에 실패해도 다시 실행하면 이어서 진행한다
source "$(dirname "$0")/lib.sh"
parse_args "$@"
git fetch -q origin develop main

if [ -z "$VERSION" ]; then
  CANDIDATES=$(open_branches "$KIND")
  # 머지된 브랜치는 저장소 설정으로 자동 삭제된다. 그때는 태그가 없는 main 버전이 마무리 대상이다
  if [ -z "$CANDIDATES" ]; then
    MAIN_VERSION=$(version_of origin/main)
    remote_tag_exists "v$MAIN_VERSION" || CANDIDATES="$KIND/$MAIN_VERSION"
  fi
  VERSION=$(pick_one "$CANDIDATES" 마무리할)
fi
BRANCH="$KIND/$VERSION"
TAG="v$VERSION"

read -r PR STATE <<< "$(gh pr list --head "$BRANCH" --base main --state all --limit 1 \
  --json number,state -q '.[] | "\(.number) \(.state)"')"
[ -n "${PR:-}" ] || die "$BRANCH → main PR이 없어요"
info "$BRANCH (PR #$PR, $STATE)"
[ "$STATE" != CLOSED ] || die "PR #$PR이 닫혀 있어요"

if [ "$STATE" = OPEN ]; then
  git fetch -q origin "$BRANCH"
  # 핫픽스가 먼저 main에 들어갔으면 사람이 main을 머지하고 CI·리뷰를 다시 받아야 한다
  git merge-base --is-ancestor origin/main "origin/$BRANCH" ||
    die "main에 $BRANCH 에 없는 커밋이 있어요. main을 머지해 PR에 올리고(versionName 충돌은 $VERSION 유지), CI·리뷰 뒤에 다시 실행하세요"

  if ! gh pr checks "$PR" > /dev/null 2>&1; then
    msg="PR #$PR 체크가 아직 진행 중이거나 실패했어요: gh pr checks $PR"
    if $APPLY; then die "$msg"; else echo "  ⚠️ $msg"; fi
  fi
  info "PR #$PR main에 머지"
  run gh pr merge "$PR" --merge
  if $APPLY; then git fetch -q origin main; else MERGE_SHA="<머지 커밋>"; fi
fi
MERGE_SHA=${MERGE_SHA:-$(gh pr view "$PR" --json mergeCommit -q .mergeCommit.oid)}

info "태그 $TAG"
if remote_tag_exists "$TAG"; then
  echo "  이미 있어요"
else
  run git tag -f -a "$TAG" "$MERGE_SHA" -m "$TAG"
  run git push -q origin "$TAG"
fi

info "GitHub Release $TAG"
if gh release view "$TAG" > /dev/null 2>&1; then
  echo "  이미 있어요"
else
  # 직전 버전 태그부터의 PR을 .github/release.yml 분류대로 정리한다. 로컬 태그는 원격과 다를 수 있어서 원격 목록을 쓴다
  PREV_TAG=$({ git ls-remote --tags --refs origin 'v*' | sed 's#.*refs/tags/##'; echo "$TAG"; } |
    sort -uV | grep -B1 -x "$TAG" | grep -vx "$TAG" || true)
  run gh release create "$TAG" --verify-tag --title "$TAG" --generate-notes ${PREV_TAG:+--notes-start-tag "$PREV_TAG"}
fi

info "develop 역머지"
BACKMERGE="backmerge/$VERSION"
BACKMERGE_WAITING=false
if git merge-base --is-ancestor "$MERGE_SHA" origin/develop 2> /dev/null; then
  echo "  이미 develop에 들어가 있어요"
else
  if [ -z "$(gh pr list --head "$BACKMERGE" --base develop --state open --json number -q '.[0].number')" ]; then
    run git push -q origin "$MERGE_SHA:refs/heads/$BACKMERGE"
    run gh pr create --base develop --head "$BACKMERGE" --title "$TAG 역머지" --milestone "$VERSION" \
      --body "- $TAG 을 develop에 반영해요. 요청: @$REQUESTER
- release 브랜치에서 리뷰를 받은 커밋이라 리뷰 없이 바로 머지해요
- 충돌이나 권한 문제로 바로 머지하지 못하면 승인 뒤 자동으로 머지돼요"
  fi
  # 리뷰 대기 동안 develop에서 딴 브랜치가 QA 수정을 놓쳐 충돌하지 않게 바로 머지한다
  # 열린 PR에 누가 커밋을 더 올렸으면 리뷰 없이 들어가지 않게 main 머지 커밋일 때만 머지한다
  # 관리자나 develop 리뷰 예외(bypass)에 등록된 앱만 성공한다. 실패하면 승인 뒤 자동 머지로 넘긴다
  if ! $APPLY; then
    echo "  [미리보기] gh pr merge $BACKMERGE --merge --admin --match-head-commit $MERGE_SHA (실패하면 --auto --merge로 예약)"
  elif ! gh pr merge "$BACKMERGE" --merge --admin --match-head-commit "$MERGE_SHA"; then
    echo "  ⚠️ 바로 머지하지 못했어요. 승인하면 자동으로 머지되게 예약해요"
    gh pr merge "$BACKMERGE" --auto --merge
    BACKMERGE_WAITING=true
  fi
fi

MILESTONE=$(milestone_number "$VERSION")
if [ -n "$MILESTONE" ]; then
  info "마일스톤 $VERSION 닫기"
  run gh api -X PATCH "repos/{owner}/{repo}/milestones/$MILESTONE" -f state=closed --silent
fi
ensure_milestone "$(next_version release "$VERSION")"

if $BACKMERGE_WAITING; then
  notify "🎉 $TAG 출시 마무리 (요청: $REQUESTER). 역머지 PR 승인이 필요해요"
else
  notify "🎉 $TAG 출시 마무리 (요청: $REQUESTER). develop 역머지까지 끝났어요"
fi
preview_hint
