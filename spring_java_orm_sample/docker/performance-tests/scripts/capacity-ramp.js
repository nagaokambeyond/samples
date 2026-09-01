import {
  commonOptions,
  envDuration,
  envInt,
  envRpsStages,
  runMixedWorkload,
  setupAuthentication,
} from './common.js';

const startRps = envInt('START_RPS', 10);
const warmupDuration = envDuration('WARMUP_DURATION', '10s');
const rampDuration = envDuration('RAMP_DURATION', '10s');
const stepDuration = envDuration('STEP_DURATION', '20s');
const rampDownDuration = envDuration('RAMP_DOWN_DURATION', '10s');
const stages = [{ duration: warmupDuration, target: startRps }];

for (const target of envRpsStages()) {
  stages.push({ duration: rampDuration, target });
  stages.push({ duration: stepDuration, target });
}
stages.push({ duration: rampDownDuration, target: 0 });

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
      stages,
    },
  },
};

export function setup() {
  return setupAuthentication();
}

export default function (data) {
  runMixedWorkload(data);
}
