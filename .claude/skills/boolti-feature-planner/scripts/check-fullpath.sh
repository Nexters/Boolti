#!/usr/bin/env bash
# 프로젝트 내부 클래스를 fully-qualified name으로 사용한 경우 검출
# 패턴: com.nexters.boolti.<package>.<UpperCase> 가 import/package 선언 외 위치에 등장
# 외부 라이브러리(android.*, androidx.*, kotlinx.*)는 의도적 사용이 잦아 제외
# Note: 경고용 스크립트. exit 1은 quality-gate.sh가 warn 모드로 처리한다.
# Usage: check-fullpath.sh [target_path]

set -euo pipefail

TARGET="${1:-$(git rev-parse --show-toplevel 2>/dev/null || pwd)}"

raw=$(git -C "$TARGET" ls-files -z '*.kt' \
  | xargs -0 grep -En '\bcom\.nexters\.boolti(\.[a-z][a-zA-Z0-9_]*)+\.[A-Z][A-Za-z0-9_]*' 2>/dev/null \
  || true)

if [ -z "$raw" ]; then
  echo "✅ No project FQN usage detected"
  exit 0
fi

filtered=$(echo "$raw" | awk -F: '
{
  content=""
  for (i=3; i<=NF; i++) content = content (i==3 ? "" : ":") $i
  sub(/^[[:space:]]+/, "", content)

  if (content ~ /^package /) next
  if (content ~ /^import /) next
  if (content ~ /^\/\//) next
  if (content ~ /^\/\*/) next
  if (content ~ /^\*/) next
  if (content ~ /^@(file|Suppress|OptIn|JvmName)/) next

  print
}')

if [ -z "$filtered" ]; then
  echo "✅ No project FQN usage detected"
  exit 0
fi

echo "⚠️  Project class used by fully-qualified name (manual verify):"
echo ""
echo "$filtered"
echo ""
echo "→ import로 옮기고 simple name 사용을 권장합니다."
echo "→ 의도적인 경우 (e.g. 이름 충돌 회피) 무시하세요."
exit 1
