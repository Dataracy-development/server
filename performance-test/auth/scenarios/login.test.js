import http from 'k6/http';
import {check, sleep} from 'k6';
import {SharedArray} from 'k6/data';

/**
 * 로그인 시나리오
 */

// ====================
// 공통 설정
// ====================
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const AUTH_MODE = (__ENV.AUTH_MODE || 'dev').toLowerCase(); // 'dev' | 'prod'
const DEFAULT_EMAIL = __ENV.EMAIL || 'test@email.com';
const DEFAULT_PASSWORD = __ENV.PASSWORD || 'test_password';

// 실행할 시나리오 선택 (기본: smoke)
const RUN_SCENARIO = __ENV.SCENARIO || 'smoke';

// 유저 목록 로딩 (csv or 단일 계정)
const USERS = new SharedArray('users', () => {
    if (__ENV.USERS_FILE) {
        const raw = open(__ENV.USERS_FILE).trim();
        return raw.split('\n')
            .map(line => {
                const [email, password] = line.split(',').map(s => s.trim());
                return { email, password };
            })
            .filter(u => u.email && u.password);
    }
    return [{ email: DEFAULT_EMAIL, password: DEFAULT_PASSWORD }];
});
function credForVu() { return USERS[(__VU - 1) % USERS.length]; }

// ====================
// k6 options
// ====================
export let options = {
    scenarios: {
        smoke: {
            executor: 'constant-vus',
            vus: 5,
            duration: '20s',
            exec: 'smoke'
        },
        load: {
            executor: 'ramping-vus',
            startVUs: 10,
            exec: 'load',
            stages: [
                { duration: '2m', target: 200 },   // 서서히 200까지
                { duration: '5m', target: 200 },   // 200 유지
                { duration: '1m', target: 0 },     // 정리
            ]
        },
        stress: {
            executor: 'ramping-vus',
            startVUs: 100,
            exec: 'stress',
            stages: [
                { duration: '3m', target: 500 },
                { duration: '3m', target: 1000 },
                { duration: '3m', target: 2000 },
                { duration: '2m', target: 0 },
            ]
        },
        soak: {
            executor: 'constant-vus',
            vus: 300,
            duration: '1h',   // 최소 1시간 이상
            exec: 'soak'
        },
        spike: {
            executor: 'ramping-vus',
            startVUs: 50,
            exec: 'spike',
            stages: [
                { duration: '10s', target: 2000 },  // 급격히 증가
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
                { target: 0, duration: '1m' },
            ]
        },
    },
    thresholds: {
        // Smoke (기본 동작 확인) → 응답은 빨라야 하고, 실패율 거의 0에 가까워야 함
        'http_req_failed{scenario:smoke}': ['rate<0.01'],   // 1% 미만 실패
        'http_req_duration{scenario:smoke}': ['p(95)<600'], // 95%가 600ms 이내

        // Load (일상적 트래픽) → 95%는 800ms 이내
        'http_req_failed{scenario:load}': ['rate<0.02'],
        'http_req_duration{scenario:load}': ['p(95)<800'],

        // Stress (한계 부하 탐색) → 실패율 5% 허용, 99%는 2.5초 이내
        'http_req_failed{scenario:stress}': ['rate<0.05'],
        'http_req_duration{scenario:stress}': ['p(99)<2500'],

        // Soak (장시간 안정성) → 평균 응답 1초 이내 유지, 실패율 2% 이내
        'http_req_failed{scenario:soak}': ['rate<0.02'],
        'http_req_duration{scenario:soak}': ['avg<1000'],

        // Spike (급격한 부하) → 99%는 3초 이내, 실패율 5% 이내
        'http_req_failed{scenario:spike}': ['rate<0.05'],
        'http_req_duration{scenario:spike}': ['p(99)<3000'],

        // Capacity (최대 수용량) → 조금 더 느려도 됨, 95%가 3초 이내
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
function safeJson(txt) {
    try { return JSON.parse(txt); } catch { return null; }
}

function doLogin() {
    const { email, password } = credForVu();

    // 매번 요청이 달라지도록 nocache 쿼리 추가
    const url = AUTH_MODE === 'dev'
        ? `${BASE_URL}/api/v1/auth/dev/login?nocache=${Math.random()}`
        : `${BASE_URL}/api/v1/auth/login?nocache=${Math.random()}`;

    const res = http.post(url, JSON.stringify({ email, password }), {
        headers: {
            'Content-Type': 'application/json',
            'Cache-Control': 'no-store, no-cache, must-revalidate',
            'Pragma': 'no-cache',
            'Expires': '0',
        },
    });

    if (AUTH_MODE === 'dev') {
        const json = safeJson(res.body);
        check(res, {
            'login(DEV) 200': (r) => r.status === 200,
            'login(DEV) refreshToken in body': () => !!json?.data?.refreshToken,
        });
    } else {
        const hasCookie = !!(res.cookies?.refreshToken?.[0]?.value);
        check(res, {
            'login(PROD) 200': (r) => r.status === 200,
            'login(PROD) refreshToken cookie': () => hasCookie,
        });
    }
}

// ====================
// 시나리오 엔트리포인트
// ====================
function scenarioExec(sleepTime = 0) {
    doLogin();
    if (sleepTime > 0) sleep(sleepTime);
}

export function smoke() { scenarioExec(1); }
export function load() { scenarioExec(0.5); }
export function stress() { scenarioExec(); }
export function soak() { scenarioExec(1); }
export function spike() { scenarioExec(); }
export function capacity() { scenarioExec(); }

// ============ 실행 예시 ============
// k6 run -e SCENARIO=smoke -e BASE_URL=http://localhost:8080 -e AUTH_MODE=dev -e EMAIL=test@gmail.com -e PASSWORD=test123@ performance-test/auth/scenarios/login.test.js
