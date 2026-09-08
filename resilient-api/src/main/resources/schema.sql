CREATE TABLE IF NOT EXISTS jobs (
    id BINARY(16) NOT NULL,
    payload VARCHAR(500) NOT NULL,
    should_fail BOOLEAN NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    finished_at TIMESTAMP(6) NULL,
    PRIMARY KEY (id)
);
