SET @sql = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'forum_posts' AND COLUMN_NAME = 'topic_tag') = 0,
  'ALTER TABLE `forum_posts` ADD COLUMN `topic_tag` VARCHAR(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '''' AFTER `cover_image`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS `forum_post_likes` (
  `id` CHAR(13) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `post_id` CHAR(13) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `user_id` CHAR(13) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` CHAR(19) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_forum_post_likes_post_user` (`post_id`, `user_id`),
  KEY `idx_forum_post_likes_user` (`user_id`),
  CONSTRAINT `forum_post_likes_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `forum_posts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `forum_post_likes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

CREATE TABLE IF NOT EXISTS `forum_post_comments` (
  `id` CHAR(13) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `post_id` CHAR(13) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `user_id` CHAR(13) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `content` VARCHAR(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` CHAR(19) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_forum_post_comments_post_time` (`post_id`, `create_time`),
  CONSTRAINT `forum_post_comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `forum_posts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `forum_post_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

UPDATE `forum_posts` SET `topic_tag` = '校园活动' WHERE `id` = '1760000009001';
UPDATE `forum_posts` SET `topic_tag` = '影像记录' WHERE `id` = '1760000009002';
UPDATE `forum_posts` SET `topic_tag` = '主持经验' WHERE `id` = '1760000009003';

DELETE FROM `forum_post_likes`;
DELETE FROM `forum_post_comments`;

INSERT INTO `forum_post_likes` (`id`, `post_id`, `user_id`, `create_time`) VALUES
('1760000010001', '1760000009001', '1760000000007', '2026-04-13 08:30:00'),
('1760000010002', '1760000009001', '1760000000008', '2026-04-13 08:41:00'),
('1760000010003', '1760000009001', '1760000000009', '2026-04-13 09:05:00'),
('1760000010004', '1760000009002', '1760000000006', '2026-04-13 10:10:00'),
('1760000010005', '1760000009002', '1760000000009', '2026-04-13 10:22:00'),
('1760000010006', '1760000009003', '1760000000006', '2026-04-13 11:02:00');

INSERT INTO `forum_post_comments` (`id`, `post_id`, `user_id`, `content`, `create_time`) VALUES
('1760000011001', '1760000009001', '1760000000007', '如果想看现场氛围，先去辩论社和音乐实验室，这两个活动互动感最强。', '2026-04-13 08:45:00'),
('1760000011002', '1760000009001', '1760000000009', '公共表达社这周也有开放活动，适合带室友一起去。', '2026-04-13 09:12:00'),
('1760000011003', '1760000009002', '1760000000006', '我一般会先找“人物动作 + 环境细节 + 一个情绪瞬间”这三个点。', '2026-04-13 10:36:00'),
('1760000011004', '1760000009002', '1760000000007', '可以提前设一个小主题，比如“准备、上场、结束后”，拍摄时会更有线索。', '2026-04-13 10:50:00'),
('1760000011005', '1760000009003', '1760000000008', '建议提前准备开场句、过渡句和冷场时的救场问题，现场会稳很多。', '2026-04-13 11:20:00');
