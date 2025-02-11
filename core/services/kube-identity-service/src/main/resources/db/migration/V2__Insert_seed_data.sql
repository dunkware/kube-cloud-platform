-- Insert admin permissions
INSERT INTO permissions (name, description, resource, action) VALUES
                                                                  ('user.create', 'Create users', 'user', 'create'),
                                                                  ('user.read', 'Read user information', 'user', 'read'),
                                                                  ('user.update', 'Update user information', 'user', 'update'),
                                                                  ('user.delete', 'Delete users', 'user', 'delete'),
                                                                  ('role.create', 'Create roles', 'role', 'create'),
                                                                  ('role.read', 'Read role information', 'role', 'read'),
                                                                  ('role.update', 'Update role information', 'role', 'update'),
                                                                  ('role.delete', 'Delete roles', 'role', 'delete'),
                                                                  ('permission.create', 'Create permissions', 'permission', 'create'),
                                                                  ('permission.read', 'Read permission information', 'permission', 'read'),
                                                                  ('permission.update', 'Update permission information', 'permission', 'update'),
                                                                  ('permission.delete', 'Delete permissions', 'permission', 'delete');

-- Insert admin role
INSERT INTO roles (name, description) VALUES
    ('ROLE_ADMIN', 'Administrator role with full system access');

-- Associate all permissions with admin role
INSERT INTO role_permissions (role_id, permission_id)
SELECT
    (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'),
    p.id
FROM permissions p;

-- Insert admin user with BCrypt encoded password ('dunk') using strength 12
INSERT INTO users (
    username,
    email,
    password,
    first_name,
    last_name,
    enabled,
    account_non_expired,
    credentials_non_expired,
    account_non_locked
) VALUES (
             'dunkware',
             'duncan@dunkware.com',
             'fuckme',
             'Duncan',
             'Krebs',
             true,
             true,
             true,
             true
         );




-- Associate admin role with user
INSERT INTO user_roles (user_id, role_id)
SELECT
    (SELECT id FROM users WHERE username = 'dunkware'),
    (SELECT id FROM roles WHERE name = 'ROLE_ADMIN');

-- Grant all permissions directly to user (optional, since they're already granted via role)
INSERT INTO user_permissions (user_id, permission_id, granted)
SELECT
    (SELECT id FROM users WHERE username = 'dunkware'),
    p.id,
    true
FROM permissions p;