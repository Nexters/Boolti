#!/usr/bin/env bash
# 실수로 시작한 릴리즈·핫픽스를 start 이전 상태로 되돌린다
#   버전 커밋 하나만 있을 때만 브랜치를 지우고(PR은 자동으로 닫힌다) 빈 마일스톤을 지운다
# 사용법: cancel.sh <release|hotfix> [버전] [--apply]
source "$(dirname "$0")/lib.sh"
parse_args "$@"

[ -n "$VERSION" ] || VERSION=$(pick_one "$(open_branches "$KIND")" 취소할)
BRANCH="$KIND/$VERSION"
git fetch -q origin "$BASE" "$BRANCH" 2> /dev/null || die "$BRANCH 브랜치가 없어요"

remote_tag_exists "v$VERSION" && die "v$VERSION 태그가 있어요. 이미 출시된 버전은 취소할 수 없어요"

# 브랜치에만 있는 커밋이 버전 커밋 하나이고, 그 커밋이 버전 파일만 바꿨는지 확인한다
if [ "$(git rev-list --count "origin/$BASE..origin/$BRANCH")" -ne 1 ] ||
  [ "$(git diff --name-only "origin/$BRANCH~1" "origin/$BRANCH")" != "$TOML" ]; then
  echo "$BRANCH 에 버전 커밋 말고 다른 변경이 있어요:"
  git log --oneline "origin/$BASE..origin/$BRANCH"
  die "취소하지 않았어요. 추가 커밋을 먼저 정리하세요"
fi

# 지우기 전에 읽어 둔다. 마일스톤에 이 PR 말고 다른 이슈·PR이 없으면 지운다
read -r PR PR_MILESTONE <<< "$(gh pr list --head "$BRANCH" --base main --state open \
  --json number,milestone -q '.[] | "\(.number) \(.milestone.title // "")"')"
MILESTONE=$(milestone_number "$VERSION")
if [ -n "$MILESTONE" ]; then
  OTHERS=$(gh api "repos/{owner}/{repo}/milestones/$MILESTONE" -q '.open_issues + .closed_issues')
  [ "${PR_MILESTONE:-}" != "$VERSION" ] || OTHERS=$((OTHERS - 1))
fi

# 권한이 없으면 여기서 GitHub이 거절하므로 아무것도 바뀌지 않는다
info "브랜치 $BRANCH 삭제"
run git push -q origin --delete "$BRANCH"
[ -z "${PR:-}" ] || run gh pr comment "$PR" --body "잘못 시작해서 취소했어요. 요청: @$REQUESTER"

if [ -n "$MILESTONE" ]; then
  if [ "$OTHERS" -eq 0 ]; then
    info "빈 마일스톤 $VERSION 삭제"
    run gh api -X DELETE "repos/{owner}/{repo}/milestones/$MILESTONE" --silent
  else
    info "마일스톤 $VERSION 에 다른 이슈·PR이 있어서 남겨둬요"
  fi
fi

notify "↩️ $BRANCH 취소 (요청: $REQUESTER)"
preview_hint
