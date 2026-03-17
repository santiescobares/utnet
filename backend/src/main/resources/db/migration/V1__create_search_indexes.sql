ALTER TABLE study_records
    ADD COLUMN search_vector tsvector GENERATED ALWAYS AS (
        to_tsvector('spanish',
                    coalesce(title, '') || ' ' ||
                    coalesce(description, '') || ' ' ||
                    coalesce(tags::text, '')
        )
        ) STORED;

CREATE INDEX idx_study_records_search ON study_records USING GIN (search_vector);

ALTER TABLE forum_threads
    ADD COLUMN search_vector tsvector GENERATED ALWAYS AS (
        to_tsvector('spanish', coalesce(content, ''))
        ) STORED;

CREATE INDEX idx_forum_threads_search ON forum_threads USING GIN (search_vector);