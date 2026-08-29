import http from 'k6/http';
import { check, fail } from 'k6';

const baseUrl = (__ENV.BASE_URL || 'http://host.docker.internal:18080').replace(/\/$/, '');
const username = __ENV.APP_USERNAME || 'admin';
const password = __ENV.APP_PASSWORD || 'password';

const initialStockPairs = [
  { isbn: '0000000000001', storeId: 1 },
  { isbn: '0000000000001', storeId: 2 },
  { isbn: '0000000000001', storeId: 3 },
  { isbn: '0000000000002', storeId: 1 },
  { isbn: '0000000000002', storeId: 2 },
  { isbn: '0000000000002', storeId: 3 },
  { isbn: '0000000000003', storeId: 4 },
  { isbn: '0000000000004', storeId: 5 },
  { isbn: '0000000000005', storeId: 5 },
];

const searchKeywords = ['', 'Spring', 'はじめて', 'Hanako'];

export const commonOptions = {
  discardResponseBodies: true,
  summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
  thresholds: {
    checks: ['rate>0.99'],
    dropped_iterations: ['count==0'],
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    http_req_failed: ['rate<0.01'],
    'http_req_failed{endpoint:book-get}': ['rate<0.01'],
    'http_req_failed{endpoint:book-search}': ['rate<0.01'],
    'http_req_failed{endpoint:purchase-create}': ['rate<0.01'],
  },
};

export function envInt(name, defaultValue) {
  const rawValue = __ENV[name];
  if (rawValue === undefined || rawValue === '') {
    return defaultValue;
  }

  const value = Number.parseInt(rawValue, 10);
  if (!Number.isInteger(value) || value <= 0) {
    throw new Error(`${name} must be a positive integer: ${rawValue}`);
  }
  return value;
}

export function envDuration(name, defaultValue) {
  return __ENV[name] || defaultValue;
}

export function envRpsStages() {
  const rawValue = __ENV.TARGET_RPS_STAGES || '25,50,100';
  return rawValue.split(',').map((part) => {
    const value = Number.parseInt(part.trim(), 10);
    if (!Number.isInteger(value) || value <= 0) {
      throw new Error(`TARGET_RPS_STAGES must contain positive integers: ${rawValue}`);
    }
    return value;
  });
}

export function setupAuthentication() {
  const response = http.post(
    `${baseUrl}/api/auth/login`,
    JSON.stringify({ username, password }),
    {
      headers: { 'Content-Type': 'application/json' },
      responseType: 'text',
      tags: { endpoint: 'auth-login' },
    },
  );

  const authenticated = check(response, {
    'authentication returns 200': (result) => result.status === 200,
  });
  if (!authenticated) {
    fail(`Authentication failed: status=${response.status}, body=${response.body}`);
  }

  const loginResponse = response.json();
  if (!loginResponse.tokenType || !loginResponse.accessToken) {
    fail('Authentication response does not contain tokenType and accessToken');
  }

  return {
    authorization: `${loginResponse.tokenType} ${loginResponse.accessToken}`,
  };
}

export function runMixedWorkload(data) {
  const selection = Math.random();
  if (selection < 0.70) {
    searchBooks();
  } else if (selection < 0.95) {
    getBook();
  } else {
    createPurchase(data.authorization);
  }
}

export function runSmokeWorkload(data) {
  searchBooks();
  getBook();
  createPurchase(data.authorization);
}

function searchBooks() {
  const keyword = randomElement(searchKeywords);
  const page = randomInt(0, 1);
  const keywordParameter = keyword === '' ? '' : `&keyword=${encodeURIComponent(keyword)}`;
  const response = http.get(`${baseUrl}/api/books/search?page=${page}${keywordParameter}`, {
    tags: { endpoint: 'book-search' },
  });

  check(response, {
    'book search returns 200': (result) => result.status === 200,
  });
}

function getBook() {
  const bookId = randomInt(1, 21);
  const response = http.get(`${baseUrl}/api/books/${bookId}`, {
    tags: { endpoint: 'book-get' },
  });

  check(response, {
    'book get returns 200': (result) => result.status === 200,
  });
}

function createPurchase(authorization) {
  const stock = randomElement(initialStockPairs);
  const request = {
    purchaseInvoiceDate: new Date().toISOString().slice(0, 10),
    supplierId: randomInt(1, 5),
    receivingStoreId: stock.storeId,
    details: [
      {
        purchaseInvoiceDetailIsbn: stock.isbn,
        purchaseInvoiceDetailUnitPrice: randomInt(500, 2000),
        purchaseInvoiceDetailQuantity: randomInt(1, 10),
      },
    ],
  };

  const response = http.post(`${baseUrl}/api/purchases/create`, JSON.stringify(request), {
    headers: {
      Authorization: authorization,
      'Content-Type': 'application/json',
    },
    tags: { endpoint: 'purchase-create' },
  });

  check(response, {
    'purchase create returns 200': (result) => result.status === 200,
  });
}

function randomElement(values) {
  return values[Math.floor(Math.random() * values.length)];
}

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}
