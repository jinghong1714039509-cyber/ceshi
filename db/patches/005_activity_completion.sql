SET NAMES utf8mb4;

ALTER TABLE activities
  ADD COLUMN completion_status INT NOT NULL DEFAULT 0,
  ADD COLUMN completion_summary TEXT NULL,
  ADD COLUMN completion_images TEXT NULL,
  ADD COLUMN completion_submitted_by VARCHAR(32) NULL,
  ADD COLUMN completion_submitted_at VARCHAR(32) NULL,
  ADD COLUMN completion_review_comment VARCHAR(500) NULL,
  ADD COLUMN completion_reviewed_by VARCHAR(32) NULL,
  ADD COLUMN completion_reviewed_at VARCHAR(32) NULL;
