const fs = require('fs');

const CURRENT_APK_PATH = 'app/build/outputs/apk/debug/app-debug.apk';
const BASELINE_SIZE_PATH = 'baseline/baseline-apk-size.txt';
const MARKER = '<!-- apk-size-diff -->';
const BOT_LOGIN = 'github-actions[bot]';

const KB = 1024;
const MB = 1024 * 1024;

const toMB = (bytes) => (bytes / 1024 / 1024).toFixed(2) + ' MB';

function buildBody(currentSize, baselineSize) {
  if (baselineSize === null) {
    return `${MARKER}\n📦 APK size: **${toMB(currentSize)}** (baseline 없음 — develop push 후 재실행하면 비교 가능)`;
  }

  const diff = currentSize - baselineSize;
  const absDiff = Math.abs(diff);
  const pct = baselineSize > 0
    ? ((diff / baselineSize) * 100).toFixed(2)
    : '0.00';
  const sign = diff > 0 ? '+' : '';
  const header = `APK size: **${toMB(currentSize)}** (${sign}${toMB(diff)}, ${sign}${pct}%) vs \`develop\``;

  let icon, note;
  if (absDiff < KB) {
    icon = '🟢';
    note = '거의 변화 없음';
  } else if (diff < 0) {
    if (absDiff >= MB) {
      icon = '🎉';
      note = `무려 ${toMB(absDiff)} 감량! 사이즈 다이어트 성공 🚀`;
    } else if (absDiff >= 100 * KB) {
      icon = '🔻';
      note = '감량 고생하셨어요 👏';
    } else {
      icon = '🔻';
      note = '살짝 슬림해졌어요 🙂';
    }
  } else if (absDiff >= 5 * MB) {
    icon = '🚨';
    note = '상당히 커졌어요! 리소스·의존성 점검 필요';
  } else if (absDiff >= MB) {
    icon = '⚠️';
    note = '꽤 무거워졌어요. 꼭 필요한 변경인지 확인 부탁';
  } else if (absDiff >= 100 * KB) {
    icon = '🔺';
    note = '사이즈가 살짝 늘었네요';
  } else {
    icon = '🔺';
    note = '작은 증가';
  }

  return `${MARKER}\n${icon} ${header} — ${note}`;
}

module.exports = async ({ github, context, core }) => {
  if (!fs.existsSync(CURRENT_APK_PATH)) {
    core.setFailed('Current APK not found: ' + CURRENT_APK_PATH);
    return;
  }

  const currentSize = fs.statSync(CURRENT_APK_PATH).size;
  const baselineSize = fs.existsSync(BASELINE_SIZE_PATH)
    ? parseInt(fs.readFileSync(BASELINE_SIZE_PATH, 'utf8').trim(), 10)
    : null;

  const body = buildBody(currentSize, baselineSize);

  const pr = context.payload.pull_request;
  const { data: comments } = await github.rest.issues.listComments({
    owner: context.repo.owner,
    repo: context.repo.repo,
    issue_number: pr.number,
  });
  const existing = comments.find(c =>
    c.body &&
    c.body.includes(MARKER) &&
    c.user &&
    c.user.login === BOT_LOGIN
  );

  if (existing) {
    await github.rest.issues.updateComment({
      owner: context.repo.owner,
      repo: context.repo.repo,
      comment_id: existing.id,
      body,
    });
  } else {
    await github.rest.issues.createComment({
      owner: context.repo.owner,
      repo: context.repo.repo,
      issue_number: pr.number,
      body,
    });
  }
};
