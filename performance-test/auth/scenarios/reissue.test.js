import http from 'k6/http';
import {check, sleep} from 'k6';

// ====================
// 공통 설정
// ====================
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const AUTH_MODE = (__ENV.AUTH_MODE || 'dev').toLowerCase(); // 'dev' | 'prod'

// 실행할 시나리오 선택 (기본: smoke)
const RUN_SCENARIO = __ENV.SCENARIO || 'smoke';

// 환경변수에서 리프레시 토큰 주입
const REFRESH_TOKEN = __ENV.REFRESH_TOKEN || 'dummy-refresh-token';

// ====================
// k6 options
// ====================
export let options = {
    scenarios: {
        smoke: {
            executor: 'constant-vus',
            vus: 5,
            duration: '30s',
            exec: 'smoke'
        },
        load: {
            executor: 'ramping-vus',
            startVUs: 10,
            exec: 'load',
            stages: [
                { duration: '2m', target: 200 },   // 천천히 올림
                { duration: '6m', target: 200 },   // 유지
                { duration: '2m', target: 0 },     // 감소
            ]
        },
        stress: {
            executor: 'ramping-vus',
            startVUs: 100,
            exec: 'stress',
            stages: [
                { duration: '3m', target: 500 },
                { duration: '4m', target: 1000 },
                { duration: '4m', target: 2000 },
                { duration: '3m', target: 0 },
            ]
        },
        soak: {
            executor: 'constant-vus',
            vus: 300,
            duration: '1h',
            exec: 'soak'
        },
        spike: {
            executor: 'ramping-vus',
            startVUs: 50,
            exec: 'spike',
            stages: [
                { duration: '15s', target: 2000 },  // 단시간 급상승
                { duration: '2m', target: 2000 },   // 유지
                { duration: '1m', target: 0 },      // 감소
            ]
        },
        capacity: {
            executor: 'ramping-arrival-rate',
            startRate: 100, timeUnit: '1s',
            preAllocatedVUs: 500, maxVUs: 5000, exec: 'capacity',
            stages: [
                { target: 500, duration: '2m' },
                { target: 1000, duration: '2m' },
                { target: 2000, duration: '2m' },
                { target: 3000, duration: '2m' },
                { target: 0, duration: '2m' },
            ]
        },
    },
    thresholds: {
        // Smoke (정상 동작 확인) → 빠르게 확인
        'http_req_failed{scenario:smoke}': ['rate<0.01'],
        'http_req_duration{scenario:smoke}': ['p(95)<500'],

        // Load (일상적 트래픽) → 95%가 800ms 이내
        'http_req_failed{scenario:load}': ['rate<0.02'],
        'http_req_duration{scenario:load}': ['p(95)<800'],

        // Stress (한계 테스트) → 99%가 2.5초 이내
        'http_req_failed{scenario:stress}': ['rate<0.05'],
        'http_req_duration{scenario:stress}': ['p(99)<2500'],

        // Soak (장시간 안정성) → 평균 1.2초 이하
        'http_req_failed{scenario:soak}': ['rate<0.02'],
        'http_req_duration{scenario:soak}': ['avg<1200'],

        // Spike (순간 트래픽 폭증) → 3초 이내
        'http_req_failed{scenario:spike}': ['rate<0.05'],
        'http_req_duration{scenario:spike}': ['p(99)<3000'],

        // Capacity (최대 수용량) → 95%가 3초 이내
        'http_req_failed{scenario:capacity}': ['rate<0.05'],
        'http_req_duration{scenario:capacity}': ['p(95)<3000'],
    },
};

// 실행할 시나리오만 남기기
for (const s of Object.keys(options.scenarios)) {
    if (s !== RUN_SCENARIO) {
        delete options.scenarios[s];
    }
}

// ====================
// 유틸
// ====================
function safeJson(txt) { try { return JSON.parse(txt); } catch { return null; } }

// BASE_URL → host 추출 함수
function getHostFromBaseUrl(baseUrl) {
    return baseUrl.replace(/^https?:\/\//, '').split('/')[0];
}

function doReissue(refreshToken) {
    if (AUTH_MODE === 'dev') {
        const url = `${BASE_URL}/api/v1/auth/dev/token/re-issue`;
        const res = http.post(url, JSON.stringify({ refreshToken }), {
            headers: { 'Content-Type': 'application/json' }
        });
        const json = safeJson(res.body);
        check(res, {
            'reissue(DEV) 200': (r) => r.status === 200,
            'reissue(DEV) accessToken': () => !!json?.data?.accessToken,
            'reissue(DEV) refreshToken': () => !!json?.data?.refreshToken,
        });
        return json?.data?.refreshToken || refreshToken;
    } else {
        const url = `${BASE_URL}/api/v1/auth/token/re-issue`;
        const host = getHostFromBaseUrl(BASE_URL);
        const jar = http.cookieJar();
        jar.set(host, 'refreshToken', refreshToken);

        const res = http.post(url, null, { headers: { 'Content-Type': 'application/json' } });
        const hasRefresh = !!(res.cookies?.refreshToken?.[0]?.value);
        check(res, {
            'reissue(PROD) 200': (r) => r.status === 200,
            'reissue(PROD) refresh cookie': () => hasRefresh,
        });
        return hasRefresh ? res.cookies.refreshToken[0].value : refreshToken;
    }
}

// ====================
// 시나리오 엔트리포인트
// ====================
function scenarioExec(sleepTime = 0) {
    doReissue(REFRESH_TOKEN);
    if (sleepTime > 0) sleep(sleepTime);
}

export function smoke() { scenarioExec(1); }
export function load() { scenarioExec(0.5); }
export function stress() { scenarioExec(); }
export function soak() { scenarioExec(1); }
export function spike() { scenarioExec(); }
export function capacity() { scenarioExec(); }

// ============ 실행 예시 ============
// k6 run -e SCENARIO=smoke -e BASE_URL=http://localhost:8080 -e AUTH_MODE=dev -e REFRESH_TOKEN="발급받은리프레시토큰" performance-test/auth/scenarios/reissue.test.js
