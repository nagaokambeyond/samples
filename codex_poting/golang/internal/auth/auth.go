package auth

import (
	"fmt"
	"sync"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

type TokenService struct {
	secret    []byte
	expiresIn time.Duration
	now       func() time.Time
}

func NewTokenService(secret string, expiresSeconds int64) *TokenService {
	return NewTokenServiceWithClock(secret, expiresSeconds, time.Now)
}

func NewTokenServiceWithClock(secret string, expiresSeconds int64, clock func() time.Time) *TokenService {
	if clock == nil {
		clock = time.Now
	}
	return &TokenService{secret: []byte(secret), expiresIn: time.Duration(expiresSeconds) * time.Second, now: clock}
}

func (s *TokenService) Issue(username string) (string, error) {
	now := s.now()
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.RegisteredClaims{
		Subject:   username,
		IssuedAt:  jwt.NewNumericDate(now),
		ExpiresAt: jwt.NewNumericDate(now.Add(s.expiresIn)),
	})
	return token.SignedString(s.secret)
}

func (s *TokenService) Validate(raw string) error {
	token, err := jwt.ParseWithClaims(raw, &jwt.RegisteredClaims{}, func(token *jwt.Token) (interface{}, error) {
		if token.Method != jwt.SigningMethodHS256 {
			return nil, fmt.Errorf("unexpected signing method: %s", token.Method.Alg())
		}
		return s.secret, nil
	}, jwt.WithTimeFunc(s.now), jwt.WithValidMethods([]string{jwt.SigningMethodHS256.Alg()}))
	if err != nil {
		return err
	}
	if !token.Valid {
		return jwt.ErrTokenInvalidClaims
	}
	return nil
}

type RateLimiter struct {
	enabled bool
	limit   int
	loc     *time.Location
	now     func() time.Time
	mu      sync.Mutex
	counts  map[string]loginCount
}

type loginCount struct {
	day   string
	count int
}

func NewRateLimiter(enabled bool, limit int, zone string) *RateLimiter {
	return NewRateLimiterWithClock(enabled, limit, zone, time.Now)
}

func NewRateLimiterWithClock(enabled bool, limit int, zone string, clock func() time.Time) *RateLimiter {
	loc, err := time.LoadLocation(zone)
	if err != nil {
		loc = time.Local
	}
	if clock == nil {
		clock = time.Now
	}
	return &RateLimiter{enabled: enabled, limit: limit, loc: loc, now: clock, counts: map[string]loginCount{}}
}

func (r *RateLimiter) Consume(username string) bool {
	if !r.enabled {
		return true
	}
	r.mu.Lock()
	defer r.mu.Unlock()
	day := r.now().In(r.loc).Format("2006-01-02")
	c := r.counts[username]
	if c.day != day {
		c = loginCount{day: day}
	}
	if c.count >= r.limit {
		r.counts[username] = c
		return false
	}
	c.count++
	r.counts[username] = c
	return true
}

func (r *RateLimiter) Reset() {
	r.mu.Lock()
	defer r.mu.Unlock()
	r.counts = map[string]loginCount{}
}
