CREATE TABLE app_user(
id BIGSERIAL PRIMARY KEY,
email VARCHAR(50) NOT NULL UNIQUE,
CONSTRAINT app_user_email_not_blank CHECK (length(trim(email)) > 0)
);


CREATE TABLE booking(
id BIGSERIAL PRIMARY KEY,
user_id BIGINT NOT NULL,
event_id BIGINT NOT NULL,
status VARCHAR(20) NOT NULL,
CONSTRAINT booking_status_valid CHECK (status in ('ACTIVE', 'CANCELLED')),
created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
CONSTRAINT booking_user_id_fk FOREIGN KEY (user_id) REFERENCES app_user(id),
CONSTRAINT booking_event_id_fk FOREIGN KEY (event_id) REFERENCES event(id),
CONSTRAINT booking_id_event_unique UNIQUE (id, event_id)
);


CREATE TABLE booking_seat(
id BIGSERIAL PRIMARY KEY,
booking_id BIGINT NOT NULL,
seat_id BIGINT NOT NULL,
event_id BIGINT NOT NULL,
CONSTRAINT booking_seat_seat_id FOREIGN KEY (seat_id) REFERENCES seat(id),
CONSTRAINT booking_seat_unique UNIQUE (event_id, seat_id),
CONSTRAINT booking_seat_booking_fk
    FOREIGN KEY (booking_id, event_id)
    REFERENCES booking(id, event_id)
    ON DELETE CASCADE
);

CREATE INDEX idx_booking_user_id ON booking(user_id);
CREATE INDEX idx_booking_seat_seat_id ON booking_seat(seat_id);