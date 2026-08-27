CREATE TABLE event (
id BIGSERIAL PRIMARY KEY,
venue_id BIGINT NOT NULL,
title VARCHAR(50) NOT NULL,
starts_at TIMESTAMPTZ NOT NULL,
price NUMERIC(10,2) NOT NULL,
CONSTRAINT event_venue_fk FOREIGN KEY (venue_id) REFERENCES venue(id),
CONSTRAINT event_price_positive CHECK (price > 0),
CONSTRAINT event_title_not_blank CHECK (length(trim(title)) > 0)
);

CREATE INDEX idx_event_venue_id ON event(venue_id);
CREATE INDEX idx_event_starts_at ON event(starts_at);