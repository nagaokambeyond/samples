import {
  commonOptions,
  runSmokeWorkload,
  setupAuthentication,
} from './common.js';

export const options = {
  ...commonOptions,
  scenarios: {
    mixed_load: {
      executor: 'shared-iterations',
      iterations: 1,
      maxDuration: '30s',
      vus: 1,
    },
  },
};

export function setup() {
  return setupAuthentication();
}

export default function (data) {
  runSmokeWorkload(data);
}
