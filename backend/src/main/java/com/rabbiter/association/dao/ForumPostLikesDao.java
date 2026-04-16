package com.rabbiter.association.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rabbiter.association.entity.ForumPostLike;
import org.springframework.stereotype.Repository;

@Repository("forumPostLikesDao")
public interface ForumPostLikesDao extends BaseMapper<ForumPostLike> {
}
