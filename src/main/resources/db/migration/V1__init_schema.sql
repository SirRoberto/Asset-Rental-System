CREATE TABLE IF NOT EXISTS category (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS asset (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id UUID NOT NULL REFERENCES category(id),
    daily_rate DECIMAL(10,2) NOT NULL,
    hourly_rate DECIMAL(10,2) NOT NULL,
    deposit_amount DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory_item (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL REFERENCES asset(id),
    serial_number VARCHAR(255) NOT NULL,
    qr_code_token VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    photo_url TEXT,
    last_maintenance_date DATE,
    next_maintenance_date DATE
);
