// 릴리즈 지표를 모아 github-action-benchmark 형식(metrics.json)으로 저장하고,
// PR에서는 gh-pages에 쌓인 직전 main 값과 비교한 댓글을 단다.
const fs = require('fs');
const path = require('path');

const MARKER = '<!-- release-metrics-diff -->';
const BOT_LOGIN = 'github-actions[bot]';

const APK_PATH = 'app/build/outputs/apk/release/app-release.apk';
const COMPOSE_METRICS_PATH = 'presentation/build/compose_metrics/release/presentation-module.json';
const KOVER_REPORT_PATH = 'build/reports/kover/reportUnit.xml';
const VERSIONS_PATH = 'gradle/libs.versions.toml';
const MODULES = ['app', 'data', 'domain', 'presentation', 'tosspayments', 'common/logger', 'common/tracker'];

function findFiles(dir, predicate) {
  if (!fs.existsSync(dir)) return [];
  return fs.readdirSync(dir, { withFileTypes: true }).flatMap((entry) => {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) return findFiles(full, predicate);
    return predicate(full) ? [full] : [];
  });
}

function sumMatches(files, regex) {
  return files
    .map((file) => [...fs.readFileSync(file, 'utf8').matchAll(regex)].reduce((acc, m) => acc + Number(m[1] ?? 1), 0))
    .reduce((a, b) => a + b, 0);
}

function collect() {
  const compose = JSON.parse(fs.readFileSync(COMPOSE_METRICS_PATH, 'utf8'));

  // 리포트 맨 끝의 LINE counter가 전체 합계
  const kover = fs.readFileSync(KOVER_REPORT_PATH, 'utf8');
  const [, missed, covered] = [...kover.matchAll(/<counter type="LINE" missed="(\d+)" covered="(\d+)"\/>/g)].at(-1);
  const coverage = (Number(covered) / (Number(missed) + Number(covered))) * 100;

  const testFiles = MODULES.flatMap((m) => findFiles(path.join(m, 'build/test-results'), (f) => f.endsWith('.xml')));
  const lintFiles = MODULES.map((m) => path.join(m, 'build/reports/lint-results-release.xml')).filter(fs.existsSync);
  const version = fs.readFileSync(VERSIONS_PATH, 'utf8').match(/^versionName = "(.+)"/m)[1];

  // extra는 github-action-benchmark가 그대로 저장하는 문자열 필드라 앱 버전을 담는다
  const metrics = [
    { name: 'Compose unstable 파라미터', unit: '개', value: compose.knownUnstableArguments },
    { name: 'Compose 불안정 클래스', unit: '개', value: compose.inferredUnstableClasses },
    { name: 'release APK 크기', unit: 'MB', value: Number((fs.statSync(APK_PATH).size / 1024 / 1024).toFixed(2)) },
    { name: 'lint 경고', unit: '개', value: sumMatches(lintFiles, /<issue\b[^>]*severity="Warning"/g) },
    { name: '테스트', unit: '개', value: sumMatches(testFiles, /<testsuite\b[^>]*\btests="(\d+)"/g) },
    { name: '줄 커버리지', unit: '%', value: Number(coverage.toFixed(2)) },
  ];
  return metrics.map((m) => ({ ...m, extra: version }));
}

// gh-pages의 data.js에서 마지막 기록을 읽는다
function readBaseline(dataJsPath) {
  if (!fs.existsSync(dataJsPath)) return null;
  const json = fs.readFileSync(dataJsPath, 'utf8').replace(/^window\.BENCHMARK_DATA\s*=\s*/, '');
  const entries = Object.values(JSON.parse(json).entries)[0];
  return entries?.at(-1) ?? null;
}

function buildBody(current, baseline) {
  if (!baseline) {
    const rows = current.map((m) => `| ${m.name} | ${m.value} ${m.unit} |`).join('\n');
    return `${MARKER}\n### 📊 릴리즈 지표\n기준값이 없어서 이번 값만 보여줘요.\n\n| 지표 | 이 PR |\n|---|---|\n${rows}`;
  }

  const before = Object.fromEntries(baseline.benches.map((b) => [b.name, b.value]));
  const rows = current.map((m) => {
    const prev = before[m.name];
    if (prev === undefined) return `| ${m.name} | - | ${m.value} ${m.unit} | - |`;
    const diff = Number((m.value - prev).toFixed(2));
    const sign = diff > 0 ? '+' : '';
    return `| ${m.name} | ${prev} ${m.unit} | ${m.value} ${m.unit} | ${diff === 0 ? '0' : sign + diff} |`;
  }).join('\n');
  const version = baseline.benches[0]?.extra ?? baseline.commit.id.slice(0, 7);
  return `${MARKER}\n### 📊 릴리즈 지표 (직전 main \`${version}\` 대비)\n\n| 지표 | main | 이 PR | 차이 |\n|---|---|---|---|\n${rows}`;
}

async function comment({ github, context, dataJsPath }) {
  const body = buildBody(collect(), readBaseline(dataJsPath));
  const { owner, repo } = context.repo;
  const issue_number = context.payload.pull_request.number;

  const { data: comments } = await github.rest.issues.listComments({ owner, repo, issue_number });
  const existing = comments.find((c) => c.body?.includes(MARKER) && c.user?.login === BOT_LOGIN);
  if (existing) {
    await github.rest.issues.updateComment({ owner, repo, comment_id: existing.id, body });
  } else {
    await github.rest.issues.createComment({ owner, repo, issue_number, body });
  }
}

module.exports = { collect, comment };

if (require.main === module) {
  fs.writeFileSync(process.argv[2] ?? 'metrics.json', JSON.stringify(collect(), null, 2));
}
