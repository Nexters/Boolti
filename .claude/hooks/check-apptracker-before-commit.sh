#!/usr/bin/env bash
# PreToolUse(Bash) 훅.
# `git commit` 직전에 스테이지된 변경에 AppTracker/Mixpanel 관련 수정이 있으면
# boolti-mixpanel-validator 스킬로 검증하도록 Claude에게 안내·차단한다.
#
# 건너뛰려면 환경변수 SKIP_APPTRACKER_CHECK=1 로 재실행.

set -euo pipefail

input=$(cat)

# jq가 있으면 사용, 없으면 grep fallback
if command -v jq >/dev/null 2>&1; then
    tool_name=$(printf '%s' "$input" | jq -r '.tool_name // empty')
    command=$(printf '%s' "$input" | jq -r '.tool_input.command // empty')
else
    tool_name=$(printf '%s' "$input" | grep -oE '"tool_name"[[:space:]]*:[[:space:]]*"[^"]+"' | head -1 | sed -E 's/.*"([^"]+)"$/\1/' || true)
    command=$(printf '%s' "$input" | grep -oE '"command"[[:space:]]*:[[:space:]]*"[^"]*"' | head -1 | sed -E 's/.*"command"[[:space:]]*:[[:space:]]*"(.*)"$/\1/' || true)
fi

[[ "$tool_name" == "Bash" ]] || exit 0

# git commit 호출만 대상 (amend, commit-tree 포함 / fixup 등도 포함)
if ! grep -qE '(^|[[:space:]]|;|&&|\|\|)git[[:space:]]+commit([[:space:]]|$)' <<<"$command"; then
    exit 0
fi

# 사용자가 수동으로 스킵 플래그를 설정한 경우 통과
[[ "${SKIP_APPTRACKER_CHECK:-}" == "1" ]] && exit 0

repo_root=$(git rev-parse --show-toplevel 2>/dev/null || true)
[[ -z "$repo_root" ]] && exit 0
cd "$repo_root"

staged=$(git diff --cached --name-only 2>/dev/null || true)
[[ -z "$staged" ]] && exit 0

tracker_module_changed=$(printf '%s\n' "$staged" | grep -E '^common/tracker/' || true)

apptracker_usage_files=""
kt_files=$(printf '%s\n' "$staged" | grep -E '\.(kt|kts)$' || true)
if [[ -n "$kt_files" ]]; then
    while IFS= read -r file; do
        [[ -z "$file" ]] && continue
        if git diff --cached -U0 -- "$file" 2>/dev/null \
            | grep -E '^\+[^+]' \
            | grep -qE 'AppTracker\.|\btrackEvent\('; then
            apptracker_usage_files+="$file"$'\n'
        fi
    done <<<"$kt_files"
fi

if [[ -z "$tracker_module_changed" && -z "$apptracker_usage_files" ]]; then
    exit 0
fi

{
    echo "🔔 AppTracker(Mixpanel) 관련 변경이 감지되었습니다."
    if [[ -n "$tracker_module_changed" ]]; then
        echo
        echo "▸ common/tracker 모듈 변경:"
        printf '%s\n' "$tracker_module_changed" | sed 's/^/  - /'
    fi
    if [[ -n "$apptracker_usage_files" ]]; then
        echo
        echo "▸ AppTracker 호출 추가/수정 파일:"
        printf '%s' "$apptracker_usage_files" | sed 's/^/  - /'
    fi
    echo
    echo "커밋 전에 boolti-mixpanel-validator 스킬을 호출해 이벤트 컨벤션을 검증하세요."
    echo "  1) Skill 도구로 skill=boolti-mixpanel-validator 실행"
    echo "  2) 지적 사항 반영 후 다시 커밋 시도"
    echo
    echo "검증이 이미 끝났거나 불필요한 변경이면 SKIP_APPTRACKER_CHECK=1 을 앞에 붙여 재실행:"
    echo "  SKIP_APPTRACKER_CHECK=1 git commit ..."
} >&2

exit 2
