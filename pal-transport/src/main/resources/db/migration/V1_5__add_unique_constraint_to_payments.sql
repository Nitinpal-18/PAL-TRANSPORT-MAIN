-- Add unique constraint to prevent duplicate payment records for the same order
-- This migration adds a unique constraint on orderId in the payments table

-- First, handle any existing duplicates by keeping only the most recent payment record for each order
-- Delete duplicate payment records, keeping only the one with the highest ID for each orderId

DELETE p1 FROM payments p1
INNER JOIN payments p2 
WHERE p1.id < p2.id 
AND p1.orderId = p2.orderId;

-- Add unique constraint on orderId
ALTER TABLE payments ADD CONSTRAINT uk_payments_order_id UNIQUE (orderId);

-- Add index for better performance
CREATE INDEX idx_payments_order_id ON payments(orderId); 