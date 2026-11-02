#!/usr/bin/env bash
# 릴리즈·핫픽스 스크립트 공통 함수. start.sh, finish.sh, cancel.sh가 source 한다
# 작업 트리는 건드리지 않고 origin의 ref만 읽고 쓴다. 그래서 로컬·CI 어디서 실행해도 결과가 같다
set -euo pipefail

TOML=gradle/libs.versions.toml
APPLY=false

die() { echo "❌ $*" >&2; exit 1; }
info() { echo "▶ $*"; }

# 미리보기에서는 명령만 출력하고, --apply일 때만 실행한다
run() {
  if $APPLY; then "$@"; else echo "  [미리보기] $*"; fi
}

is_version() { [[ $1 =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; }

# 사용법: <script> <release|hotfix> [버전] [--apply]
# KIND, VERSION(선택), BASE, REQUESTER, APPLY를 정한다
parse_args() {
  local args=()
  for arg in "$@"; do
    if [ "$arg" = --apply ]; then APPLY=true; else args+=("$arg"); fi
  done
  KIND=${args[0]:-}
  case "$KIND" in
    release) BASE=develop ;;
    hotfix) BASE=main ;;
    *) die "종류를 지정하세요: release 또는 hotfix (입력: '$KIND')" ;;
  esac
  VERSION=${args[1]:-}
  [ -z "$VERSION" ] || is_version "$VERSION" || die "버전 형식이 아니에요: $VERSION"
  # 실행 권한은 GitHub 브랜치 보호·Ruleset이 막는다. 여기서는 PR·알림에 적을 이름만 구한다
  REQUESTER=${GITHUB_TRIGGERING_ACTOR:-$(gh api user -q .login)}
}

preview_hint() { $APPLY || echo $'\n미리보기만 했어요. 실행하려면 --apply를 붙이세요.'; }

version_of() { git show "$1:$TOML" | awk -F '"' '/^versionName = /{ print $2; exit }'; }

higher_version() { printf '%s\n%s\n' "$1" "$2" | sort -V | tail -1; }

# release는 minor + 1, hotfix는 patch + 1. 범위는 app/app-version.gradle과 같다
next_version() {
  local kind=$1 major minor patch
  IFS=. read -r major minor patch <<< "$2"
  if [ "$kind" = release ]; then minor=$((minor + 1)); patch=0; else patch=$((patch + 1)); fi
  [ "$minor" -le 999 ] && [ "$patch" -le 99 ] || die "버전 범위 초과: $major.$minor.$patch"
  echo "$major.$minor.$patch"
}

remote_tag_exists() { [ -n "$(git ls-remote --tags origin "refs/tags/$1")" ]; }

# 지금 진행 중인 <kind>/<버전> 브랜치들. 예전 hotfix/webview-crash 같은 이름은 뺀다
open_branches() {
  git ls-remote --heads origin "refs/heads/$1/*" | sed 's#.*refs/heads/##' |
    while read -r b; do is_version "${b#*/}" && echo "$b"; done || true
}

# 후보 브랜치가 정확히 하나일 때 그 버전을 돌려준다
pick_one() {
  [ -n "$1" ] || die "$2 $KIND 브랜치가 없어요"
  [ "$(wc -l <<< "$1")" -eq 1 ] || die "대상이 여러 개예요. 버전을 지정하세요: $(xargs <<< "$1")"
  echo "${1#*/}"
}

# 버전 이름과 같은 마일스톤 번호. 없으면 빈 값
milestone_number() {
  gh api "repos/{owner}/{repo}/milestones?state=all&per_page=100" \
    -q ".[] | select(.title == \"$1\") | .number" | head -1
}

ensure_milestone() {
  [ -z "$(milestone_number "$1")" ] || return 0
  info "마일스톤 $1 만들기"
  run gh api -X POST "repos/{owner}/{repo}/milestones" -f title="$1" --silent
}

# 웹훅 주소는 CI에서는 환경변수, 로컬에서는 local.properties에서 읽는다. 없으면 알림만 건너뛴다
notify() {
  [ -n "${DISCORD_RELEASE_WEBHOOK_URL:-}" ] || [ ! -f local.properties ] ||
    DISCORD_RELEASE_WEBHOOK_URL=$(sed -n 's/^DISCORD_RELEASE_WEBHOOK_URL=//p' local.properties | tr -d '"')
  [ -n "${DISCORD_RELEASE_WEBHOOK_URL:-}" ] || return 0
  if ! $APPLY; then echo "  [미리보기] 디스코드 알림: $1"; return 0; fi
  jq -n --arg c "$1" '{content: $c}' |
    curl -sS --fail-with-body -H 'Content-Type: application/json' -d @- "$DISCORD_RELEASE_WEBHOOK_URL" > /dev/null ||
    echo "⚠️ 디스코드 알림 실패 (릴리즈 작업은 끝났어요)"
}

# 스크립트 위치와 상관없이 저장소 루트에서 실행한다
cd "$(git rev-parse --show-toplevel)"
