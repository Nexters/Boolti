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
if git merge-base --is-ancestor "$MERGE_SHA" origin/develop 2> /dev/null; then
  echo "  이미 develop에 들어가 있어요"
elif [ -n "$(gh pr list --head "$BACKMERGE" --base develop --state open --json number -q '.[0].number')" ]; then
  echo "  역머지 PR이 이미 열려 있어요"
else
  run git push -q origin "$MERGE_SHA:refs/heads/$BACKMERGE"
  run gh pr create --base develop --head "$BACKMERGE" --title "$TAG 역머지" --milestone "$VERSION" \
    --body "- $TAG 을 develop에 반영해요. 요청: @$REQUESTER
- 승인하면 자동으로 머지돼요"
  run gh pr merge "$BACKMERGE" --auto --merge
fi

MILESTONE=$(milestone_number "$VERSION")
if [ -n "$MILESTONE" ]; then
  info "마일스톤 $VERSION 닫기"
  run gh api -X PATCH "repos/{owner}/{repo}/milestones/$MILESTONE" -f state=closed --silent
fi
ensure_milestone "$(next_version release "$VERSION")"

notify "🎉 $TAG 출시 마무리 (요청: $REQUESTER). 역머지 PR 승인이 필요해요"
preview_hint
