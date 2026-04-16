package com.rabbiter.association.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.association.entity.Activities;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("activitiesDao")
public interface ActivitiesDao extends BaseMapper<Activities> {

    @Select("<script>" +
            "SELECT " +
            "a.id, a.name, a.comm, a.detail, a.ask, a.total, " +
            "a.active_time activeTime, a.location, a.capacity, a.cover_image coverImage, " +
            "a.completion_status completionStatus, a.completion_summary completionSummary, " +
            "a.completion_images completionImages, a.completion_submitted_at completionSubmittedAt, " +
            "a.completion_review_comment completionReviewComment, a.completion_reviewed_at completionReviewedAt, " +
            "a.team_id teamId, t.name teamName " +
            "FROM activities a, teams t " +
            "<where>" +
            "a.team_id = t.id " +
            "<if test='teamName != null and teamName.trim() != &quot;&quot; '>" +
            "AND t.name LIKE CONCAT('%', #{teamName}, '%') " +
            "</if>" +
            "<if test='activeName != null and activeName.trim() != &quot;&quot; '>" +
            "AND a.name LIKE CONCAT('%', #{activeName}, '%') " +
            "</if>" +
            "</where>" +
            "ORDER BY a.active_time DESC" +
            "</script>")
    Page<Map<String, Object>> qryPageAll(Page<Map<String, Object>> page,
                                         @Param("activeName") String activeName,
                                         @Param("teamName") String teamName);

    @Select("<script>" +
            "SELECT " +
            "a.id, a.name, a.comm, a.detail, a.ask, a.total, " +
            "a.active_time activeTime, a.location, a.capacity, a.cover_image coverImage, " +
            "a.completion_status completionStatus, a.completion_summary completionSummary, " +
            "a.completion_images completionImages, a.completion_submitted_at completionSubmittedAt, " +
            "a.completion_review_comment completionReviewComment, a.completion_reviewed_at completionReviewedAt, " +
            "a.team_id teamId, t.name teamName " +
            "FROM activities a, teams t " +
            "<where>" +
            "a.team_id = t.id AND a.team_id IN (SELECT team_id FROM members WHERE user_id = #{memId}) " +
            "<if test='teamName != null and teamName.trim() != &quot;&quot; '>" +
            "AND t.name LIKE CONCAT('%', #{teamName}, '%') " +
            "</if>" +
            "<if test='activeName != null and activeName.trim() != &quot;&quot; '>" +
            "AND a.name LIKE CONCAT('%', #{activeName}, '%') " +
            "</if>" +
            "</where>" +
            "ORDER BY a.active_time DESC" +
            "</script>")
    Page<Map<String, Object>> qryPageByMemId(Page<Map<String, Object>> page,
                                             @Param("memId") String memId,
                                             @Param("activeName") String activeName,
                                             @Param("teamName") String teamName);
}
