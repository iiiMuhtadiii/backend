-- V3: seed admin user (idempotent)
INSERT INTO users (name, email, password, role, created_at, updated_at)
SELECT 'admin', 'admin@example.com', '$2a$10$7q7bQ1o9Qv1KZ0oZpY8q0eQk4Iu3vC1YxZ5QmYV7j1K9sF5aZpH2a', 'ADMIN', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='admin@example.com');
