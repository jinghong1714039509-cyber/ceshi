package com.rabbiter.association.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rabbiter.association.entity.ClubPost;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ClubPostsDao extends BaseMapper<ClubPost> {

    @Select("SELECT p.id, p.team_id teamId, p.user_id userId, p.title, p.content, p.pinned, p.create_time createTime, " +
            "u.name authorName, u.user_name authorUserName " +
            "FROM club_posts p JOIN users u ON p.user_id = u.id " +
            "WHERE p.team_id = #{teamId} ORDER BY p.pinned DESC, p.create_time DESC")
    List<Map<String, Object>> listByTeamId(@Param("teamId") String teamId);
}
