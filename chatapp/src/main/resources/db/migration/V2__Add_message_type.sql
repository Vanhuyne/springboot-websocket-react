
ALTER TABLE message DROP COLUMN message_type;
ALTER TABLE message ADD COLUMN message_type VARCHAR(10);
