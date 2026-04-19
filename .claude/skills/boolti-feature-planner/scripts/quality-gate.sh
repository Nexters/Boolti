#!/usr/bin/env bash
# 불티 프로젝트 Quality Gate 통합 실행
# Usage: quality-gate.sh [--no-test] [--no-build]
#   --no-test:   테스트 단계 건너뛰기 (테스트 변경 없을 때)
#   --no-build:  빌드 단계 건너뛰기 (정말 필요할 때만)

set -uo pipefail

ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

SKIP_TEST=false
SKIP_BUILD=false
for arg in "$@"; do
  case "$arg" in
    --no-test) SKIP_TEST=true ;;
    --no-build) SKIP_BUILD=true ;;
    *) echo "Unknown option: $arg"; exit 2 ;;
  esac
done

failed=()
warnings=()

run_step() {
  local name="$1"
  local cmd="$2"
  local mode="${3:-block}"  # block | warn

  echo ""
  echo "▶ $name"
  if eval "$cmd"; then
    echo "✅ $name"
  else
    if [ "$mode" = "warn" ]; then
      echo "⚠️  $name (warning)"
      warnings+=("$name")
    else
      echo "❌ $name"
      failed+=("$name")
    fi
  fi
}

echo "═══════════════════════════════════════"
echo "  Boolti Quality Gate"
echo "  Root: $ROOT"
echo "═══════════════════════════════════════"

# 1. Build + Tests (one gradle invocation = one daemon hit)
gradle_tasks=()
[ "$SKIP_BUILD" = false ] && gradle_tasks+=("assembleDebug")
[ "$SKIP_TEST" = false ] && gradle_tasks+=("btTest")

if [ ${#gradle_tasks[@]} -gt 0 ]; then
  label="Gradle (${gradle_tasks[*]})"
  run_step "$label" "./gradlew ${gradle_tasks[*]} --quiet"
else
  echo ""
  echo "⏭  Build/Test skipped"
fi

# 2. Ktlint (if configured)
echo ""
echo "▶ Ktlint"
ktlint_out=$(./gradlew ktlintCheck --quiet 2>&1) && ktlint_status=0 || ktlint_status=$?
if echo "$ktlint_out" | grep -q "Task 'ktlintCheck' not found"; then
  echo "⚠️  Ktlint not configured (skipped)"
  echo "    → 사용자에게 도입 여부를 확인하세요. (baseline 적용 권장)"
elif [ $ktlint_status -eq 0 ]; then
  echo "✅ Ktlint"
else
  echo "$ktlint_out"
  echo "❌ Ktlint"
  failed+=("Ktlint")
fi

# 4. Wildcard imports
run_step "Wildcard import check" "bash '$SCRIPT_DIR/check-imports.sh' '$ROOT'"

# 5. Full-path usage (warn only)
run_step "Full-path class name check" "bash '$SCRIPT_DIR/check-fullpath.sh' '$ROOT'" warn

# Summary
echo ""
echo "═══════════════════════════════════════"
if [ ${#failed[@]} -eq 0 ]; then
  echo "  ✅ All blocking checks passed"
  if [ ${#warnings[@]} -gt 0 ]; then
    echo "  ⚠️  Warnings: ${warnings[*]}"
  fi
  echo "═══════════════════════════════════════"
  exit 0
else
  echo "  ❌ Failed: ${failed[*]}"
  if [ ${#warnings[@]} -gt 0 ]; then
    echo "  ⚠️  Warnings: ${warnings[*]}"
  fi
  echo "═══════════════════════════════════════"
  exit 1
fi
