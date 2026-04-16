package com.rabbiter.association.forum;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbiter.association.auth.AuthRole;
import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.dao.ForumPostCommentsDao;
import com.rabbiter.association.dao.ForumPostLikesDao;
import com.rabbiter.association.dao.ForumPostsDao;
import com.rabbiter.association.entity.ForumPost;
import com.rabbiter.association.entity.ForumPostComment;
import com.rabbiter.association.entity.ForumPostLike;
import com.rabbiter.association.forum.dto.CreateForumCommentRequest;
import com.rabbiter.association.forum.dto.CreateForumPostRequest;
import com.rabbiter.association.forum.dto.ForumCommentResponse;
import com.rabbiter.association.forum.dto.ForumFeedResponse;
import com.rabbiter.association.forum.dto.ForumPostResponse;
import com.rabbiter.association.forum.dto.ForumTopicResponse;
import com.rabbiter.association.forum.dto.LikeToggleResponse;
import com.rabbiter.association.utils.DateUtils;
import com.rabbiter.association.utils.IDUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
public class ForumFacade {

    private final ForumPostsDao forumPostsDao;
    private final ForumPostLikesDao forumPostLikesDao;
    private final ForumPostCommentsDao forumPostCommentsDao;

    public ForumFacade(ForumPostsDao forumPostsDao,
                       ForumPostLikesDao forumPostLikesDao,
                       ForumPostCommentsDao forumPostCommentsDao) {
        this.forumPostsDao = forumPostsDao;
        this.forumPostLikesDao = forumPostLikesDao;
        this.forumPostCommentsDao = forumPostCommentsDao;
    }

    @Transactional(readOnly = true)
    public List<ForumPostResponse> list(AuthUser authUser) {
        return buildPosts(authUser, 40, 2);
    }

    @Transactional(readOnly = true)
    public ForumFeedResponse feed(AuthUser authUser) {
        List<ForumPostResponse> posts = buildPosts(authUser, 40, 3);
        List<ForumTopicResponse> hotTopics = forumPostsDao.listHotTopics(8).stream()
                .map(row -> new ForumTopicResponse(
                        valueAsString(row.get("topicTag")),
                        valueAsInt(row.get("postCount"))
                ))
                .toList();
        return new ForumFeedResponse(posts, hotTopics);
    }

    @Transactional
    public void create(AuthUser authUser, CreateForumPostRequest request) {
        ForumPost post = new ForumPost();
        post.setId(IDUtils.makeIDByCurrent());
        post.setUserId(authUser.getUserId());
        post.setTitle(normalizeTitle(request.getTitle(), request.getContent()));
        post.setContent(request.getContent().trim());
        post.setCoverImage(StringUtils.hasText(request.getCoverImage()) ? request.getCoverImage() : defaultCover(request.getContent()));
        post.setTopicTag(normalizeTopic(request.getTopicTag()));
        post.setCreateTime(DateUtils.getNowDate());
        forumPostsDao.insert(post);
    }

    @Transactional
    public LikeToggleResponse toggleLike(AuthUser authUser, String postId) {
        requirePost(postId);
        QueryWrapper<ForumPostLike> query = new QueryWrapper<ForumPostLike>()
                .eq("post_id", postId)
                .eq("user_id", authUser.getUserId());
        ForumPostLike existing = forumPostLikesDao.selectOne(query);
        boolean liked;
        if (existing == null) {
            ForumPostLike like = new ForumPostLike();
            like.setId(IDUtils.makeIDByCurrent());
            like.setPostId(postId);
            like.setUserId(authUser.getUserId());
            like.setCreateTime(DateUtils.getNowDate());
            forumPostLikesDao.insert(like);
            liked = true;
        } else {
            forumPostLikesDao.deleteById(existing.getId());
            liked = false;
        }
        int likeCount = Math.toIntExact(forumPostLikesDao.selectCount(new QueryWrapper<ForumPostLike>().eq("post_id", postId)));
        return new LikeToggleResponse(liked, likeCount);
    }

    @Transactional
    public void createComment(AuthUser authUser, String postId, CreateForumCommentRequest request) {
        requirePost(postId);
        ForumPostComment comment = new ForumPostComment();
        comment.setId(IDUtils.makeIDByCurrent());
        comment.setPostId(postId);
        comment.setUserId(authUser.getUserId());
        comment.setContent(request.getContent().trim());
        comment.setCreateTime(DateUtils.getNowDate());
        forumPostCommentsDao.insert(comment);
    }

    @Transactional(readOnly = true)
    public List<ForumCommentResponse> comments(AuthUser authUser, String postId) {
        requirePost(postId);
        return forumPostCommentsDao.listByPostId(postId, null).stream()
                .map(row -> mapComment(row, authUser))
                .toList();
    }

    private List<ForumPostResponse> buildPosts(AuthUser authUser, int limit, int previewCommentLimit) {
        String userId = authUser == null ? "" : authUser.getUserId();
        return forumPostsDao.listFeedRows(userId, limit).stream()
                .map(row -> mapPost(row, authUser, previewCommentLimit))
                .toList();
    }

    private ForumPostResponse mapPost(Map<String, Object> row, AuthUser authUser, int previewCommentLimit) {
        String authorId = valueAsString(row.get("authorId"));
        String postId = valueAsString(row.get("id"));
        return new ForumPostResponse(
                postId,
                valueAsString(row.get("title")),
                valueAsString(row.get("content")),
                valueAsString(row.get("coverImage")),
                valueAsString(row.get("createTime")),
                authorId,
                authorName(row),
                valueAsString(row.get("authorAvatar")),
                roleLabel(valueAsInt(row.get("authorType"))),
                valueAsString(row.get("topicTag")),
                authUser != null && StringUtils.hasText(authUser.getUserId()) && authorId.equals(authUser.getUserId()),
                valueAsInt(row.get("liked")) > 0,
                valueAsInt(row.get("likeCount")),
                valueAsInt(row.get("commentCount")),
                forumPostCommentsDao.listByPostId(postId, previewCommentLimit).stream()
                        .map(commentRow -> mapComment(commentRow, authUser))
                        .toList()
        );
    }

    private ForumCommentResponse mapComment(Map<String, Object> row, AuthUser authUser) {
        String authorId = valueAsString(row.get("authorId"));
        return new ForumCommentResponse(
                valueAsString(row.get("id")),
                valueAsString(row.get("content")),
                valueAsString(row.get("createTime")),
                authorId,
                authorName(row),
                valueAsString(row.get("authorAvatar")),
                roleLabel(valueAsInt(row.get("authorType"))),
                authUser != null && StringUtils.hasText(authUser.getUserId()) && authorId.equals(authUser.getUserId())
        );
    }

    private void requirePost(String postId) {
        if (forumPostsDao.selectById(postId) == null) {
            throw new IllegalArgumentException("帖子不存在");
        }
    }

    private String normalizeTitle(String title, String content) {
        if (StringUtils.hasText(title)) {
            return title.trim();
        }
        String normalized = content == null ? "" : content.trim().replace('\n', ' ');
        if (!StringUtils.hasText(normalized)) {
            return "校园动态";
        }
        return normalized.length() > 28 ? normalized.substring(0, 28) : normalized;
    }

    private String normalizeTopic(String topicTag) {
        if (!StringUtils.hasText(topicTag)) {
            return "";
        }
        String normalized = topicTag.trim().replace("#", "");
        if (!StringUtils.hasText(normalized)) {
            return "";
        }
        return normalized.length() > 20 ? normalized.substring(0, 20) : normalized;
    }

    private String valueAsString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private int valueAsInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private String authorName(Map<String, Object> row) {
        String authorName = valueAsString(row.get("authorName"));
        return StringUtils.hasText(authorName) ? authorName : valueAsString(row.get("authorUserName"));
    }

    private String roleLabel(Integer type) {
        AuthRole role = AuthRole.fromType(type);
        if (role == AuthRole.SUPER_ADMIN) {
            return "系统管理员";
        }
        if (role == AuthRole.CLUB_ADMIN) {
            return "社团管理员";
        }
        if (role == AuthRole.TEACHER) {
            return "指导教师";
        }
        return "学生";
    }

    private String defaultCover(String seed) {
        String[] covers = {
                "/covers/campus-01.svg",
                "/covers/campus-02.svg",
                "/covers/campus-03.svg",
                "/covers/campus-04.svg",
                "/covers/campus-05.svg",
                "/covers/campus-06.svg",
                "/covers/campus-07.svg",
                "/covers/campus-08.svg"
        };
        int index = Math.abs(seed.hashCode()) % covers.length;
        return covers[index];
    }
}
