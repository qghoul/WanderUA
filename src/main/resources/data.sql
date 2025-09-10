
INSERT INTO roles (name, description) VALUES
                                          ('ROLE_USER', 'Registered User'),
                                          ('ROLE_BUSINESS', 'Business Representative'),
                                          ('ROLE_ADMIN', 'Platform Administrator')
ON CONFLICT (name) DO NOTHING;

INSERT INTO users (username, email, password, full_name, business_represent_verify, enabled, created_at) VALUES
    ('business_user', 'business@wanderua.com', '$2a$10$N.zmdr9k7uOkXUJlkTRdMe8OBNeElqnLVqxb/v7YB2.VeROT7H9He', 'Business User', true, true, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'business_user' AND r.name = 'ROLE_BUSINESS'
ON CONFLICT (user_id, role_id) DO NOTHING;