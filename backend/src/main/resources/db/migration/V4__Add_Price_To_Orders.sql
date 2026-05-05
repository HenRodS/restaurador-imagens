ALTER TABLE image_orders 
ADD COLUMN price DECIMAL(10, 2),
ADD COLUMN external_payment_id VARCHAR(255);
