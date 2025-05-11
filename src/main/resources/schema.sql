CREATE TABLE IF NOT EXISTS app_user (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_user VARCHAR(10) NOT NULL,
    package_type VARCHAR(10) NOT NULL,
    auth_provider VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS consumption (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INT NOT NULL,
    date DATE NOT NULL,
    max_upload_size_mb INT NOT NULL,
    daily_upload_count INT NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);