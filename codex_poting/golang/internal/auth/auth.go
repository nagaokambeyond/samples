package auth

import (
	"sync"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

type TokenService struct {
	secret    []byte
	expiresIn time.Duration
}

func NewTokenService(secret string, expiresSeconds int64) *TokenService {
	return &TokenService{secret: []byte(secret), expiresIn: time.Duration(expiresSeconds) * time.Second}
}

func (s *TokenService) Issue(username string) (string, error) {
	now := time.Now()
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.RegisteredClaims{
		Subject:   username,
		IssuedAt:  jwt.NewNumericDate(now),
		ExpiresAt: jwt.NewNumericDate(now.Add(s.expiresIn)),
	})
	return token.SignedString(s.secret)
}

func (s *TokenService) Validate(raw string) error {
	token, err := jwt.ParseWithClaims(raw, &jwt.RegisteredClaims{}, func(token *jwt.Token) (interface{}, error) {
		return s.secret, nil
	})
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
	mu      sync.Mutex
	counts  map[string]loginCount
}

type loginCount struct {
	day   string
	count int
}

func NewRateLimiter(enabled bool, limit int, zone string) *RateLimiter {
	loc, err := time.LoadLocation(zone)
	if err != nil {
		loc = time.Local
	}
	return &RateLimiter{enabled: enabled, limit: limit, loc: loc, counts: map[string]loginCount{}}
}

func (r *RateLimiter) Consume(username string) bool {
	if !r.enabled {
		return true
	}
	r.mu.Lock()
	defer r.mu.Unlock()
	day := time.Now().In(r.loc).Format("2006-01-02")
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
