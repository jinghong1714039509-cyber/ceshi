package com.rabbiter.association.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rabbiter.association.entity.ForumPostComment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("forumPostCommentsDao")
public interface ForumPostCommentsDao extends BaseMapper<ForumPostComment> {

    @Select({
            "<script>",
            "SELECT c.id, c.post_id postId, c.content, c.create_time createTime, ",
            "u.id authorId, u.user_name authorUserName, u.name authorName, u.avatar_url authorAvatar, u.type authorType ",
            "FROM forum_post_comments c ",
            "JOIN users u ON c.user_id = u.id ",
            "WHERE c.post_id = #{postId} ",
            "ORDER BY c.create_time DESC ",
            "<if test='limit != null and limit &gt; 0'>LIMIT #{limit}</if>",
            "</script>"
    })
    List<Map<String, Object>> listByPostId(@Param("postId") String postId, @Param("limit") Integer limit);
}
