import {
  commonOptions,
  envDuration,
  envInt,
  runMixedWorkload,
  setupAuthentication,
} from './common.js';

const targetRps = envInt('TARGET_RPS', 50);

export const options = {
  ...commonOptions,
  scenarios: {
    mixed_load: {
      executor: 'ramping-arrival-rate',
      startRate: 1,
      timeUnit: '1s',
      gracefulStop: '0s',
      preAllocatedVUs: envInt('PRE_ALLOCATED_VUS', 100),
      maxVUs: envInt('MAX_VUS', 500),
      stages: [
        { duration: envDuration('WARMUP_DURATION', '10s'), target: Math.min(10, targetRps) },
        { duration: envDuration('RAMP_DURATION', '10s'), target: targetRps },
        { duration: envDuration('HOLD_DURATION', '80s'), target: targetRps },
        { duration: envDuration('RAMP_DOWN_DURATION', '10s'), target: 0 },
      ],
    },
  },
};

export function setup() {
  return setupAuthentication();
}

export default function (data) {
  runMixedWorkload(data);
}
