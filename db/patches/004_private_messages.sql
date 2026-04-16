SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS private_messages (
  id VARCHAR(32) NOT NULL PRIMARY KEY,
  sender_id VARCHAR(32) NOT NULL,
  receiver_id VARCHAR(32) NOT NULL,
  team_id VARCHAR(32) DEFAULT NULL,
  content VARCHAR(500) NOT NULL,
  create_time VARCHAR(32) NOT NULL,
  read_status TINYINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_private_messages_sender_receiver ON private_messages(sender_id, receiver_id);
CREATE INDEX IF NOT EXISTS idx_private_messages_receiver_read ON private_messages(receiver_id, read_status);

INSERT INTO private_messages (id, sender_id, receiver_id, team_id, content, create_time, read_status)
SELECT '1760000009001', '1760000000002', '1760000000006', '1760000001001', '周三晚上的机器人调试别迟到，到了先去实验楼 305。', '2026-04-10 19:20:00', 1
WHERE NOT EXISTS (SELECT 1 FROM private_messages WHERE id = '1760000009001');

INSERT INTO private_messages (id, sender_id, receiver_id, team_id, content, create_time, read_status)
SELECT '1760000009002', '1760000000006', '1760000000002', '1760000001001', '收到，我会提前十分钟到。', '2026-04-10 19:24:00', 1
WHERE NOT EXISTS (SELECT 1 FROM private_messages WHERE id = '1760000009002');

INSERT INTO private_messages (id, sender_id, receiver_id, team_id, content, create_time, read_status)
SELECT '1760000009003', '1760000000004', '1760000000008', '1760000001002', '这周影像社外拍要带长焦，天气会偏阴。', '2026-04-11 13:10:00', 0
WHERE NOT EXISTS (SELECT 1 FROM private_messages WHERE id = '1760000009003');

INSERT INTO private_messages (id, sender_id, receiver_id, team_id, content, create_time, read_status)
SELECT '1760000009004', '1760000000003', '1760000000010', '1760000001004', '公益行动社周六集合地点改到图书馆东门，注意看群公告。', '2026-04-12 17:45:00', 0
WHERE NOT EXISTS (SELECT 1 FROM private_messages WHERE id = '1760000009004');
