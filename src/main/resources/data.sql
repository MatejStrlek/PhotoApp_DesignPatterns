INSERT INTO app_user (email, password, role_user, package_type, auth_provider)
VALUES
    ('admin@example.com', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'ADMIN', 'GOLD', 'LOCAL'),
    ('user1@example.com', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'REGISTERED', 'PRO', 'LOCAL'),
    ('user2@example.com', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'REGISTERED', 'FREE', 'LOCAL'),
    ('user3@example.com', '$2a$10$xszlj1pbFnz6kKreb3X9pugjNUg0MAcC1ff/NR43oKNthJYYcCRj6', 'REGISTERED', NULL, 'LOCAL');


INSERT INTO consumption (user_id, date, max_upload_size_mb, daily_upload_count)
VALUES (1, CURRENT_DATE, 50, 45),
       (2, CURRENT_DATE, 9, 7),
       (3, CURRENT_DATE, 3, 2);