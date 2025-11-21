CREATE TABLE IF NOT EXISTS fitness_data (
        id BIGSERIAL NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    average_heart_rate DOUBLE PRECISION,
    total_steps INTEGER,
    total_distance DOUBLE PRECISION,
    total_calories_burned DOUBLE PRECISION,
    workout_list JSONB,
    timestamp TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (id, timestamp)
);

SELECT create_hypertable('fitness_data', 'timestamp', if_not_exists => TRUE);