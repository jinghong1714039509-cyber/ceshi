UPDATE activities
SET
  completion_status = 2,
  completion_summary = '完成机械臂快闪展示、互动讲解与作品答疑，现场共接待160余名同学，收集到47份意向报名。',
  completion_images = '/covers/campus-01.svg,/covers/campus-03.svg,/covers/campus-06.svg',
  completion_submitted_by = '1760000000002',
  completion_submitted_at = '2026-05-08 19:40:00',
  completion_review_comment = '现场组织顺畅，展示动线清晰，建议后续补充不同年级的互动记录。',
  completion_reviewed_by = '1760000000003',
  completion_reviewed_at = '2026-05-09 10:20:00'
WHERE id = '1760000003006';

UPDATE activities
SET
  completion_status = 1,
  completion_summary = '完成短片放映与复盘交流，收集到22条观众反馈，正在整理问卷与活动财务明细。',
  completion_images = '/covers/campus-02.svg,/covers/campus-05.svg',
  completion_submitted_by = '1760000000004',
  completion_submitted_at = '2026-05-10 21:15:00',
  completion_review_comment = '',
  completion_reviewed_by = '',
  completion_reviewed_at = ''
WHERE id = '1760000003007';

UPDATE activities
SET
  completion_status = 3,
  completion_summary = '完成开放麦舞台和现场签到，已整理主持串词与节目单，但现场照片数量不足。',
  completion_images = '/covers/campus-04.svg',
  completion_submitted_by = '1760000000005',
  completion_submitted_at = '2026-05-12 22:05:00',
  completion_review_comment = '请补充观众区域与志愿者工作照，并完善活动总结中的安全保障部分。',
  completion_reviewed_by = '1760000000003',
  completion_reviewed_at = '2026-05-13 09:30:00'
WHERE id = '1760000003008';

UPDATE activities
SET
  completion_status = 2,
  completion_summary = '完成旧衣改造成品展示和义卖统计，活动共回收旧衣96件，完成31份手工改造成品。',
  completion_images = '/covers/campus-07.svg,/covers/campus-08.svg',
  completion_submitted_by = '1760000000006',
  completion_submitted_at = '2026-05-03 18:10:00',
  completion_review_comment = '流程完整，结项材料清楚，后续可以补一份参与者成品合集。',
  completion_reviewed_by = '1760000000003',
  completion_reviewed_at = '2026-05-04 11:00:00'
WHERE id = '1760000003004';
