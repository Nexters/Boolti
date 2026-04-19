#!/usr/bin/env bash
# 와일드카드 import 검출 (Kotlin)
# Usage: check-imports.sh [target_path]

set -euo pipefail

TARGET="${1:-$(git rev-parse --show-toplevel 2>/dev/null || pwd)}"

violations=$(git -C "$TARGET" ls-files -z '*.kt' '*.kts' \
  | xargs -0 grep -En '^import [^[:space:]]+\.\*[[:space:]]*$' 2>/dev/null \
  || true)

if [ -n "$violations" ]; then
  echo "❌ Wildcard imports found:"
  echo ""
  echo "$violations"
  echo ""
  echo "→ 사용하는 클래스/함수를 명시적으로 import 하세요."
  exit 1
fi

echo "✅ No wildcard imports"
