-- Fix demo user role to ensure it has ADMIN access
UPDATE users 
SET 
    role = 'ADMIN',
    first_name = COALESCE(first_name, 'Demo'),
    last_name = COALESCE(last_name, 'Admin'),
    phone_number = COALESCE(phone_number, '+1234567890'),
    is_enabled = true,
    is_account_non_expired = true,
    is_account_non_locked = true,
    is_credentials_non_expired = true
WHERE email = 'admin@example.com';

-- If the demo user doesn't exist, create it
INSERT INTO users (
    email, 
    first_name, 
    last_name, 
    phone_number, 
    password, 
    role, 
    provider, 
    is_enabled, 
    is_account_non_expired, 
    is_account_non_locked, 
    is_credentials_non_expired,
    created_at,
    updated_at
)
SELECT 
    'admin@example.com',
    'Demo',
    'Admin',
    '+1234567890',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password123 encoded
    'ADMIN',
    'EMAIL',
    true,
    true,
    true,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@example.com'
); 