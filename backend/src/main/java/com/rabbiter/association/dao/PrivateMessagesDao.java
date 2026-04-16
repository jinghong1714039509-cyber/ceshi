package com.rabbiter.association.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rabbiter.association.entity.PrivateMessage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PrivateMessagesDao extends BaseMapper<PrivateMessage> {

    @Select("SELECT m.id, m.sender_id senderId, m.receiver_id receiverId, m.team_id teamId, m.content, m.create_time createTime, " +
            "m.read_status readStatus, t.name teamName, " +
            "su.name senderName, su.user_name senderUserName, ru.name receiverName, ru.user_name receiverUserName " +
            "FROM private_messages m " +
            "LEFT JOIN teams t ON m.team_id = t.id " +
            "JOIN users su ON m.sender_id = su.id " +
            "JOIN users ru ON m.receiver_id = ru.id " +
            "WHERE (m.sender_id = #{userId} AND m.receiver_id = #{contactId}) " +
            "OR (m.sender_id = #{contactId} AND m.receiver_id = #{userId}) " +
            "ORDER BY m.create_time ASC")
    List<Map<String, Object>> listConversation(@Param("userId") String userId, @Param("contactId") String contactId);

    @Select("SELECT m.id, m.sender_id senderId, m.receiver_id receiverId, m.team_id teamId, m.content, m.create_time createTime, " +
            "m.read_status readStatus, t.name teamName, " +
            "su.name senderName, su.user_name senderUserName, ru.name receiverName, ru.user_name receiverUserName " +
            "FROM private_messages m " +
            "LEFT JOIN teams t ON m.team_id = t.id " +
            "JOIN users su ON m.sender_id = su.id " +
            "JOIN users ru ON m.receiver_id = ru.id " +
            "WHERE m.sender_id = #{userId} OR m.receiver_id = #{userId} " +
            "ORDER BY m.create_time DESC")
    List<Map<String, Object>> listForUser(@Param("userId") String userId);

    @Update("UPDATE private_messages SET read_status = 1 " +
            "WHERE sender_id = #{senderId} AND receiver_id = #{receiverId} AND read_status = 0")
    void markConversationRead(@Param("senderId") String senderId, @Param("receiverId") String receiverId);
}
