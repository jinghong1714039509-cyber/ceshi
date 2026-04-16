SET @sql = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'activities' AND COLUMN_NAME = 'location') = 0,
  'ALTER TABLE `activities` ADD COLUMN `location` VARCHAR(120) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '''' AFTER `active_time`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'activities' AND COLUMN_NAME = 'capacity') = 0,
  'ALTER TABLE `activities` ADD COLUMN `capacity` INT NOT NULL DEFAULT 0 AFTER `location`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'activities' AND COLUMN_NAME = 'cover_image') = 0,
  'ALTER TABLE `activities` ADD COLUMN `cover_image` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL AFTER `capacity`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS `forum_posts` (
  `id` CHAR(13) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `user_id` CHAR(13) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `title` VARCHAR(80) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `content` VARCHAR(2000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `cover_image` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `create_time` CHAR(19) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_forum_posts_create_time` (`create_time`),
  CONSTRAINT `forum_posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

UPDATE `activities` SET
  `location` = '工程训练中心 A105',
  `capacity` = 24,
  `cover_image` = '/covers/campus-01.svg'
WHERE `id` = '1760000003001';

UPDATE `activities` SET
  `location` = '老校区钟楼与礼堂片区',
  `capacity` = 18,
  `cover_image` = '/covers/campus-02.svg'
WHERE `id` = '1760000003002';

UPDATE `activities` SET
  `location` = '公共教学楼 301',
  `capacity` = 36,
  `cover_image` = '/covers/campus-03.svg'
WHERE `id` = '1760000003003';

UPDATE `activities` SET
  `location` = '城市社区服务站二层活动室',
  `capacity` = 28,
  `cover_image` = '/covers/campus-04.svg'
WHERE `id` = '1760000003004';

UPDATE `activities` SET
  `location` = '艺术中心地下排练室',
  `capacity` = 16,
  `cover_image` = '/covers/campus-05.svg'
WHERE `id` = '1760000003005';

UPDATE `activities` SET
  `location` = '综合楼中庭',
  `capacity` = 80,
  `cover_image` = '/covers/campus-06.svg'
WHERE `id` = '1760000003006';

UPDATE `activities` SET
  `location` = '图书馆创作工坊',
  `capacity` = 20,
  `cover_image` = '/covers/campus-07.svg'
WHERE `id` = '1760000003007';

UPDATE `activities` SET
  `location` = '社区阅读空间',
  `capacity` = 30,
  `cover_image` = '/covers/campus-08.svg'
WHERE `id` = '1760000003008';

DELETE FROM `forum_posts`;

INSERT INTO `forum_posts` (`id`, `user_id`, `title`, `content`, `cover_image`, `create_time`) VALUES
('1760000009001', '1760000000006', '开放周最值得看的社团活动有哪些？', '我准备带室友一起去开放周逛一圈，想优先看互动性强、现场氛围好的活动，大家有推荐吗？', '/covers/campus-02.svg', '2026-04-12 20:10:00'),
('1760000009002', '1760000000008', '拍活动照片时怎么快速找到故事线', '最近跟着影像社拍了两次活动，发现素材很多但叙事不够清楚。有人能分享一下现场找故事线的方法吗？', '/covers/campus-07.svg', '2026-04-12 21:00:00'),
('1760000009003', '1760000000009', '第一次上台做活动主持要准备什么', '下个月要给公益活动做主持，最担心临场冷场和串场不自然。有没有适合新手的准备清单？', '/covers/campus-03.svg', '2026-04-13 09:15:00');
