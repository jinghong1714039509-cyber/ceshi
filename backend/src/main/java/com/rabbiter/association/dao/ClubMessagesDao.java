package com.rabbiter.association.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rabbiter.association.entity.ClubMessage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ClubMessagesDao extends BaseMapper<ClubMessage> {

    @Select("SELECT m.id, m.team_id teamId, m.user_id userId, m.content, m.create_time createTime, " +
            "u.name authorName, u.user_name authorUserName " +
            "FROM club_messages m JOIN users u ON m.user_id = u.id " +
            "WHERE m.team_id = #{teamId} ORDER BY m.create_time DESC LIMIT 30")
    List<Map<String, Object>> listRecentByTeamId(@Param("teamId") String teamId);
}
