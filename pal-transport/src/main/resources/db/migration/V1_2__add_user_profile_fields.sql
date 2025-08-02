-- Add new user profile fields
ALTER TABLE users 
ADD COLUMN first_name VARCHAR(50) NOT NULL DEFAULT '',
ADD COLUMN last_name VARCHAR(50) NOT NULL DEFAULT '',
ADD COLUMN phone_number VARCHAR(15),
ADD COLUMN profile_photo VARCHAR(255);

-- Update existing users to have proper names
-- Split the existing 'name' field into first_name and last_name
UPDATE users 
SET 
    first_name = CASE 
        WHEN name IS NOT NULL AND name != '' THEN 
            CASE 
                WHEN POSITION(' ' IN name) > 0 THEN LEFT(name, POSITION(' ' IN name) - 1)
                ELSE name
            END
        ELSE 'Unknown'
    END,
    last_name = CASE 
        WHEN name IS NOT NULL AND name != '' THEN 
            CASE 
                WHEN POSITION(' ' IN name) > 0 THEN SUBSTRING(name FROM POSITION(' ' IN name) + 1)
                ELSE ''
            END
        ELSE 'User'
    END;

-- Drop the old name column
ALTER TABLE users DROP COLUMN IF EXISTS name; 