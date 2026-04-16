package com.rabbiter.association.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rabbiter.association.entity.ForumPost;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("forumPostsDao")
public interface ForumPostsDao extends BaseMapper<ForumPost> {

    @Select({
            "<script>",
            "SELECT p.id, p.title, p.content, p.cover_image coverImage, p.topic_tag topicTag, p.create_time createTime, ",
            "u.id authorId, u.user_name authorUserName, u.name authorName, u.avatar_url authorAvatar, u.type authorType, ",
            "(SELECT COUNT(*) FROM forum_post_likes l WHERE l.post_id = p.id) likeCount, ",
            "(SELECT COUNT(*) FROM forum_post_comments c WHERE c.post_id = p.id) commentCount, ",
            "<choose>",
            "<when test='userId != null and userId != \"\"'>",
            "(SELECT COUNT(*) FROM forum_post_likes l2 WHERE l2.post_id = p.id AND l2.user_id = #{userId}) ",
            "</when>",
            "<otherwise>0 </otherwise>",
            "</choose>",
            "liked ",
            "FROM forum_posts p JOIN users u ON p.user_id = u.id ",
            "ORDER BY p.create_time DESC ",
            "<if test='limit != null and limit &gt; 0'>LIMIT #{limit}</if>",
            "</script>"
    })
    List<Map<String, Object>> listFeedRows(@Param("userId") String userId, @Param("limit") Integer limit);

    @Select({
            "<script>",
            "SELECT p.topic_tag topicTag, COUNT(*) postCount ",
            "FROM forum_posts p ",
            "WHERE p.topic_tag IS NOT NULL AND p.topic_tag != '' ",
            "GROUP BY p.topic_tag ",
            "ORDER BY COUNT(*) DESC, MAX(p.create_time) DESC ",
            "<if test='limit != null and limit &gt; 0'>LIMIT #{limit}</if>",
            "</script>"
    })
    List<Map<String, Object>> listHotTopics(@Param("limit") Integer limit);
}
