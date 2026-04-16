ALTER TABLE users
    MODIFY COLUMN pass_word VARCHAR(100) NOT NULL COMMENT '用户密码';

CREATE UNIQUE INDEX uk_users_user_name ON users (user_name);
CREATE INDEX idx_apply_logs_team_status ON apply_logs (team_id, status);
CREATE INDEX idx_members_user_team ON members (user_id, team_id);
CREATE INDEX idx_activities_team_time ON activities (team_id, active_time);
CREATE INDEX idx_active_logs_active_user ON active_logs (active_id, user_id);
