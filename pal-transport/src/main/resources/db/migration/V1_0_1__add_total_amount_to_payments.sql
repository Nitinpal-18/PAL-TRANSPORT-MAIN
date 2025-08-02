-- Migration: Add totalAmount column to payments table
-- Version: 1.0.1
-- Description: Add totalAmount column to store the total order amount for better record keeping

-- Add totalAmount column to payments table
ALTER TABLE payments ADD COLUMN total_amount DOUBLE PRECISION DEFAULT 0.0;

-- Update existing records to set totalAmount equal to amount for backward compatibility
UPDATE payments SET total_amount = amount WHERE total_amount = 0.0 OR total_amount IS NULL;

-- Make totalAmount NOT NULL after setting default values
ALTER TABLE payments ALTER COLUMN total_amount SET NOT NULL;

-- Add comment to the column
COMMENT ON COLUMN payments.total_amount IS 'Total order amount for record keeping'; 