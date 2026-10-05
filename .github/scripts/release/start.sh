#!/usr/bin/env bash
# 릴리즈·핫픽스 브랜치를 만들고 버전을 올린 뒤 main 대상 PR을 연다
# 사용법: start.sh <release|hotfix> [--apply]
#   release: develop에서 release/<minor + 1> 브랜치
#   hotfix:  main에서 hotfix/<patch + 1> 브랜치
source "$(dirname "$0")/lib.sh"
parse_args "$@"

info "origin 최신 상태 가져오기"
git fetch -q origin develop main

OPEN=$(open_branches "$KIND")
[ -z "$OPEN" ] || die "이미 진행 중인 $KIND 브랜치가 있어요: $(xargs <<< "$OPEN")"
# 직전 릴리즈·핫픽스의 역머지가 끝나야 develop에 출시된 수정과 버전이 모두 들어 있다
[ "$KIND" = hotfix ] || git merge-base --is-ancestor origin/main origin/develop ||
  die "main이 아직 develop에 다 들어가지 않았어요. 열려 있는 역머지 PR을 먼저 머지하세요"

CURRENT=$(version_of "origin/$BASE")
VERSION=$(next_version "$KIND" "$CURRENT")
BRANCH="$KIND/$VERSION"
remote_tag_exists "v$VERSION" && die "v$VERSION 태그가 이미 있어요"

echo
echo "  종류: $KIND (기준: $BASE)"
echo "  버전: $CURRENT → $VERSION"
echo "  브랜치: $BRANCH"
echo "  요청: $REQUESTER"
echo

# 작업 트리를 바꾸지 않도록 임시 index로 버전 커밋을 만든다 (push 전까지는 원격에 영향이 없다)
make_version_commit() {
  local index blob tree
  index=$(mktemp)
  GIT_INDEX_FILE=$index git read-tree "origin/$BASE"
  blob=$(git show "origin/$BASE:$TOML" |
    awk -v v="$VERSION" '/^versionName = /{ $0 = "versionName = \"" v "\"" } { print }' |
    git hash-object -w --stdin)
  GIT_INDEX_FILE=$index git update-index --cacheinfo "100644,$blob,$TOML"
  tree=$(GIT_INDEX_FILE=$index git write-tree)
  rm -f "$index"
  git commit-tree "$tree" -p "origin/$BASE" -m "버전 상향 ($CURRENT → $VERSION)"
}

info "버전 커밋을 만들어 $BRANCH 로 push"
run git push -q origin "$(make_version_commit):refs/heads/$BRANCH"

ensure_milestone "$VERSION"

info "main 대상 PR 생성"
TITLE=$([ "$KIND" = release ] && echo "릴리즈 $VERSION" || echo "핫픽스 $VERSION")
BODY="- $TITLE 브랜치예요. 요청: @$REQUESTER
- QA 수정은 이 브랜치로 PR을 올려요
- 출시 후 \`finish.sh $KIND --apply\`로 마무리하고, 잘못 시작했으면 \`cancel.sh $KIND --apply\`로 취소해요"
run gh pr create --base main --head "$BRANCH" --title "$TITLE" --milestone "$VERSION" --body "$BODY"

notify "🚀 $TITLE 시작 ($CURRENT → $VERSION, 요청: $REQUESTER)"
preview_hint
