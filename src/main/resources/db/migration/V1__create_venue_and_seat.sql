CREATE TABLE venue (
id BIGSERIAL PRIMARY KEY,
name VARCHAR(50) NOT NULL,
CHECK (length(trim(name)) > 0)
);

CREATE TABLE seat (
id BIGSERIAL PRIMARY KEY,
venue_id BIGINT NOT NULL,
row_label VARCHAR(3) NOT NULL,
seat_number INT NOT NULL,
CONSTRAINT seat_number_positive CHECK (seat_number > 0),
CONSTRAINT seat_position_unique UNIQUE (venue_id, row_label, seat_number),
CONSTRAINT seat_venue_fk FOREIGN KEY (venue_id) REFERENCES venue(id) ON DELETE CASCADE
);