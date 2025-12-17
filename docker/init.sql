
-- Vector extension

CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- FITNESS DATA

CREATE TABLE IF NOT EXISTS fitness_data (
        id BIGSERIAL NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    average_heart_rate DOUBLE PRECISION,
    total_steps DOUBLE PRECISION,
    total_distance DOUBLE PRECISION,
    total_calories_burned DOUBLE PRECISION,
    workout_list JSONB,
    timestamp TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (id, timestamp)
);

SELECT create_hypertable('fitness_data', 'timestamp', if_not_exists => TRUE);


-- USER DATA

CREATE TABLE IF NOT EXISTS user_data (
     user_id VARCHAR(255) PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     surname VARCHAR(255) NOT NULL,
     age INTEGER,
     height INTEGER,
     weight INTEGER,
     gender VARCHAR(50),
     lifestyle VARCHAR(50),
     data_source_id VARCHAR(255),
     external_source_user_id VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS user_diseases (
     user_id VARCHAR(255) NOT NULL,
     disease VARCHAR(255),
     FOREIGN KEY (user_id) REFERENCES user_data(user_id)
);