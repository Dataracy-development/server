import http from 'k6/http';
import {check, sleep} from 'k6';

/**
 * 댓글 조회 시나리오
 */

// ====================
// 공통 설정
// ====================
const BASE_URL      = __ENV.BASE_URL      || 'http://localhost:8080';
const RUN_SCENARIO  = __ENV.SCENARIO      || 'smoke';

const PROJECT_ID    = __ENV.PROJECT_ID    || 1;
const PAGE          = __ENV.PAGE          || 0;
const SIZE          = __ENV.SIZE          || 5;
const ACCESS_TOKEN  = __ENV.ACCESS_TOKEN  || 'paste-access-token';

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
                { duration: '2m', target: 300 },
                { duration: '6m', target: 300 },
                { duration: '2m', target: 0   },
            ]
        },
        stress: {
            executor: 'ramping-vus',
            startVUs: 100,
            exec: 'stress',
            stages: [
                { duration: '3m', target: 800  },
                { duration: '4m', target: 1600 },
                { duration: '4m', target: 2400 },
                { duration: '3m', target: 0    },
            ]
        },
        soak: {
            executor: 'constant-vus',
            vus: 400,
            duration: '1h',
            exec: 'soak'
        },
        spike: {
            executor: 'ramping-vus',
            startVUs: 60,
            exec: 'spike',
            stages: [
                { duration: '15s', target: 2500 },
                { duration: '2m',  target: 2500 },
                { duration: '1m',  target: 0     },
            ]
        },
        capacity: {
            executor: 'ramping-arrival-rate',
            startRate: 150, timeUnit: '1s',
            preAllocatedVUs: 600, maxVUs: 6000, exec: 'capacity',
            stages: [
                { target: 800,  duration: '2m' },
                { target: 1600, duration: '2m' },
                { target: 0,    duration: '2m' },
            ]
        },
    },
    thresholds: {
        // 읽기 전용, 인덱스/캐시 적중 가정 시 매우 빠름
        'http_req_failed{scenario:smoke}':   ['rate<0.01'],
        'http_req_duration{scenario:smoke}': ['p(95)<400'],

        'http_req_failed{scenario:load}':    ['rate<0.02'],
        'http_req_duration{scenario:load}':  ['p(95)<600'],

        'http_req_failed{scenario:stress}':  ['rate<0.05'],
        'http_req_duration{scenario:stress}':['p(99)<1000'],

        'http_req_failed{scenario:soak}':    ['rate<0.02'],
        'http_req_duration{scenario:soak}':  ['avg<700'],

        'http_req_failed{scenario:spike}':   ['rate<0.05'],
        'http_req_duration{scenario:spike}': ['p(99)<1500'],

        'http_req_failed{scenario:capacity}':['rate<0.05'],
        'http_req_duration{scenario:capacity}':['p(95)<1800'],
    },
};

// 실행할 시나리오만 남기기
for (const s of Object.keys(options.scenarios)) {
    if (s !== RUN_SCENARIO) delete options.scenarios[s];
}

// ====================
// 유틸
// ====================
function headers() {
    return {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${ACCESS_TOKEN}`,
            'Cache-Control': 'no-store, no-cache, must-revalidate',
            'Pragma': 'no-cache',
            'Expires': '0',
        }
    };
}

// ====================
// 시나리오 엔트리포인트
// ====================
function scenarioExec(sleepTime = 0) {
    const url = `${BASE_URL}/api/v1/projects/${PROJECT_ID}/comments?page=${PAGE}&size=${SIZE}&nocache=${Math.random()}`;

    const res = http.get(url, headers());

    check(res, { 'findComments 200': (r) => r.status === 200 });

    if (sleepTime > 0) sleep(sleepTime);
}

export function smoke()    { scenarioExec(1);   }
export function load()     { scenarioExec(0.5); }
export function stress()   { scenarioExec();    }
export function soak()     { scenarioExec(1);   }
export function spike()    { scenarioExec();    }
export function capacity() { scenarioExec();    }

// ============ 실행 예시 ============
// k6 run performance-test/comment/scenarios/find-comments.test.js \
//   -e SCENARIO=smoke -e BASE_URL=http://localhost:8080 -e PROJECT_ID=1 -e PAGE=0 -e SIZE=5 \
//   -e ACCESS_TOKEN="발급토큰"
