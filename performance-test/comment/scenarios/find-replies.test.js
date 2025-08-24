import http from 'k6/http';
import {check, sleep} from 'k6';

/**
 * 답글 조회 시나리오
 */

// ====================
// 공통 설정
// ====================
const BASE_URL      = __ENV.BASE_URL      || 'http://localhost:8080';
const RUN_SCENARIO  = __ENV.SCENARIO      || 'smoke';

const PROJECT_ID    = __ENV.PROJECT_ID    || 1;
const COMMENT_ID    = __ENV.COMMENT_ID    || 1;
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
                { duration: '2m', target: 250 },
                { duration: '6m', target: 250 },
                { duration: '2m', target: 0   },
            ]
        },
        stress: {
            executor: 'ramping-vus',
            startVUs: 100,
            exec: 'stress',
            stages: [
                { duration: '3m', target: 700  },
                { duration: '4m', target: 1400 },
                { duration: '4m', target: 2100 },
                { duration: '3m', target: 0    },
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
                { duration: '15s', target: 2200 },
                { duration: '2m',  target: 2200 },
                { duration: '1m',  target: 0     },
            ]
        },
        capacity: {
            executor: 'ramping-arrival-rate',
            startRate: 120, timeUnit: '1s',
            preAllocatedVUs: 500, maxVUs: 5000, exec: 'capacity',
            stages: [
                { target: 700,  duration: '2m' },
                { target: 1400, duration: '2m' },
                { target: 0,    duration: '2m' },
            ]
        },
    },
    thresholds: {
        // 부모-자식 조회/페이징 → 댓글 목록보다 살짝 느리게 허용
        'http_req_failed{scenario:smoke}':   ['rate<0.01'],
        'http_req_duration{scenario:smoke}': ['p(95)<600'],

        'http_req_failed{scenario:load}':    ['rate<0.02'],
        'http_req_duration{scenario:load}':  ['p(95)<900'],

        'http_req_failed{scenario:stress}':  ['rate<0.05'],
        'http_req_duration{scenario:stress}':['p(99)<1500'],

        'http_req_failed{scenario:soak}':    ['rate<0.02'],
        'http_req_duration{scenario:soak}':  ['avg<1000'],

        'http_req_failed{scenario:spike}':   ['rate<0.05'],
        'http_req_duration{scenario:spike}': ['p(99)<2000'],

        'http_req_failed{scenario:capacity}':['rate<0.05'],
        'http_req_duration{scenario:capacity}':['p(95)<2200'],
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
    const url =
        `${BASE_URL}/api/v1/projects/${PROJECT_ID}/comments/${COMMENT_ID}?page=${PAGE}&size=${SIZE}&nocache=${Math.random()}`;

    const res = http.get(url, headers());

    check(res, { 'findReplyComments 200': (r) => r.status === 200 });

    if (sleepTime > 0) sleep(sleepTime);
}

export function smoke()    { scenarioExec(1);   }
export function load()     { scenarioExec(0.5); }
export function stress()   { scenarioExec();    }
export function soak()     { scenarioExec(1);   }
export function spike()    { scenarioExec();    }
export function capacity() { scenarioExec();    }

// ============ 실행 예시 ============
// k6 run performance-test/comment/scenarios/find-replies.test.js \
//   -e SCENARIO=smoke -e BASE_URL=http://localhost:8080 -e PROJECT_ID=1 -e COMMENT_ID=10 \
//   -e PAGE=0 -e SIZE=5 -e ACCESS_TOKEN="발급토큰"
