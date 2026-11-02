#!/usr/bin/env bash
# 릴리즈 스크립트 셀프 체크. 임시 git 저장소와 가짜 gh로 실제 GitHub을 건드리지 않고 시나리오를 돌린다
# 사용법: .github/scripts/release/test.sh
set -euo pipefail
SRC=$(cd "$(dirname "$0")" && pwd)
WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT
fail() { echo "❌ $*"; exit 1; }
pass() { echo "✅ $*"; }

# 1. 버전 계산
(
  source "$SRC/lib.sh" > /dev/null
  [ "$(next_version release 1.14.3)" = 1.15.0 ] || fail "release 1.14.3"
  [ "$(next_version hotfix 1.14.3)" = 1.14.4 ] || fail "hotfix 1.14.3"
  [ "$(higher_version 1.9.0 1.10.0)" = 1.10.0 ] || fail "higher_version"
  ! (next_version hotfix 1.14.99 2> /dev/null) || fail "patch 범위 초과를 막지 않음"
)
pass "버전 계산"

# 2. 임시 원격 저장소 (main·develop 모두 1.14.3)
git init -q --bare "$WORK/origin.git"
git init -q -b main "$WORK/repo"
cd "$WORK/repo"
git config user.email t@t && git config user.name t
mkdir -p gradle .github/scripts && cp -r "$SRC" .github/scripts/release
printf '[versions]\nminSdk = "28"\nversionName = "1.14.3"\n' > gradle/libs.versions.toml
git add -A && git commit -qm init
git remote add origin "$WORK/origin.git"
git tag v1.14.3 && git push -q origin main main:develop v1.14.3

# 가짜 gh: 호출을 기록하고 필요한 값만 돌려준다
mkdir "$WORK/bin"
cat > "$WORK/bin/gh" << 'GH'
#!/usr/bin/env bash
echo "gh $*" >> "$GH_LOG"
case "$*" in
  "api user"*) echo mangbaam ;;
  "pr list"*"--state all"*) [ -n "${FAKE_PR:-}" ] && echo "$FAKE_PR" ;;
  "pr list"*"--state open"*"--base main"*) echo 7 ;;
  "pr view"*) git rev-parse origin/main ;;
  "release view"*) exit 1 ;;
esac
exit 0
GH
chmod +x "$WORK/bin/gh"
export PATH="$WORK/bin:$PATH" GH_LOG="$WORK/gh.log" DISCORD_RELEASE_WEBHOOK_URL=
S=.github/scripts/release
toml_on() { (source "$S/lib.sh" > /dev/null && version_of "origin/$1"); }

# 3. 종류 없이 실행하면 멈춘다
! $S/start.sh --apply > /dev/null 2>&1 || fail "종류 없이 실행됨"
pass "종류 필수"

# 4. 릴리즈 시작: release/1.15.0, 작업 트리는 그대로
$S/start.sh release --apply > /dev/null
git fetch -q origin
[ "$(toml_on release/1.15.0)" = 1.15.0 ] || fail "release 버전"
[ "$(git rev-list --count origin/develop..origin/release/1.15.0)" = 1 ] || fail "버전 커밋 1개"
[ "$(git branch --show-current)" = main ] && [ -z "$(git status --porcelain)" ] || fail "작업 트리가 바뀜"
grep -q "pr create --base main --head release/1.15.0" "$GH_LOG" || fail "PR 생성 안 함"
! $S/start.sh release --apply > /dev/null 2>&1 || fail "릴리즈 중복 시작됨"
pass "릴리즈 시작"

# 5. 커밋이 더 있으면 취소를 거부하고, 정리하면 취소된다
git checkout -q -b tmp origin/release/1.15.0 && echo x > fix.txt && git add fix.txt && git commit -qm fix
git push -q origin tmp:release/1.15.0 && git checkout -q main
! $S/cancel.sh release --apply > /dev/null 2>&1 || fail "추가 커밋이 있는데 취소됨"
git push -q -f origin origin/release/1.15.0~1:refs/heads/release/1.15.0
$S/cancel.sh release --apply > /dev/null
! git ls-remote --exit-code origin refs/heads/release/1.15.0 > /dev/null || fail "브랜치가 남음"
pass "릴리즈 취소"

# 6. 릴리즈 중 핫픽스가 먼저 main에 들어가면 finish가 main 머지를 안내하고 아무것도 하지 않고 멈춘다
$S/start.sh release --apply > /dev/null
$S/start.sh hotfix --apply > /dev/null
git fetch -q origin
[ "$(toml_on hotfix/1.14.4)" = 1.14.4 ] || fail "hotfix 버전"
git push -q origin origin/hotfix/1.14.4:main
BEFORE=$(git ls-remote origin refs/heads/release/1.15.0)
OUT=$(FAKE_PR="7 OPEN" $S/finish.sh release --apply 2>&1) && fail "main이 빠졌는데 멈추지 않음"
grep -q "main을 머지해" <<< "$OUT" || fail "main 머지 안내 없음"
[ "$(git ls-remote origin refs/heads/release/1.15.0)" = "$BEFORE" ] || fail "브랜치가 바뀜"
pass "핫픽스 선반영 시 멈춤"

# 7. 머지된 뒤 finish 미리보기: 태그·Release·역머지 순서로 진행하고 아무것도 바꾸지 않는다
git push -q -f origin origin/release/1.15.0:main
OUT=$(FAKE_PR="7 MERGED" $S/finish.sh release 1.15.0 2>&1) || { echo "$OUT"; fail "finish 미리보기 실패"; }
for step in "git tag -f -a v1.15.0" "release create v1.15.0" "--notes-start-tag v1.14.3" "backmerge/1.15.0" "--auto --merge" "title=1.16.0"; do
  grep -q -- "$step" <<< "$OUT" || fail "finish 미리보기에 '$step' 없음"
done
! git ls-remote --exit-code --tags origin v1.15.0 > /dev/null || fail "미리보기가 태그를 만듦"
pass "마무리 미리보기"
# 8. 역머지 전에는 다음 릴리즈를 시작하지 않는다 (main에 develop에 없는 커밋이 있음)
git push -q origin --delete release/1.15.0
OUT=$($S/start.sh release 2>&1) && fail "역머지 전에 릴리즈가 시작됨"
grep -q "역머지 PR을 먼저" <<< "$OUT" || fail "역머지 안내 없음"
pass "역머지 전 릴리즈 시작 차단"
echo "모든 체크 통과"
