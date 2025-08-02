-- Add remark column to truck_history table
ALTER TABLE truck_history ADD COLUMN remark VARCHAR(500);

-- Update existing records with default remarks based on their status
UPDATE truck_history SET remark = 'Truck added' WHERE status = 'AVAILABLE' AND order_id IS NULL;
UPDATE truck_history SET remark = 'Truck assigned to order' WHERE status = 'IN_TRANSIT' AND order_id IS NOT NULL;
UPDATE truck_history SET remark = 'Maintenance scheduled' WHERE status IN ('MAINTENANCE', 'SCHEDULED_MAINTENANCE');
UPDATE truck_history SET remark = 'Truck Decommissioned' WHERE status = 'DECOMMISSIONED';
UPDATE truck_history SET remark = 'Trip completed' WHERE status = 'AVAILABLE' AND order_id IS NULL AND occupancy_end_date IS NOT NULL; 