DROP TABLE IF EXISTS coupons;

DROP INDEX IF EXISTS idx_code;
DROP INDEX IF EXISTS idx_unique_code;

CREATE TABLE coupons (
     id INT AUTO_INCREMENT PRIMARY KEY,
     code  VARCHAR(250) NOT NULL,
     discount NUMBER(10,2) NOT NULL,
     min_basket_value NUMBER(10,2) DEFAULT NULL

);

CREATE INDEX idx_code ON coupons (code);
ALTER TABLE coupons ADD CONSTRAINT idx_unique_code UNIQUE (code);