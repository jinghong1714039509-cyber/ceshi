package com.rabbiter.association.handle;

import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.auth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 缓存处理
 */
@Component("cacheHandle")
public class CacheHandle {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final String USER_KEY = "users";

    /**
     * 获取用户缓存对象
     * @return
     */
    public Cache getUserCache(){

        Cache cache = cacheManager.getCache(USER_KEY);

        return  cache;
    }

    /**
     * 存储登录用户信息
     * @param key 缓存用户 token
     * @param val 登录用户 信息
     */
    public void addUserCache(String key, Object val) {

        Cache cache = getUserCache();

        cache.put(key, val);
    }

    /**
     * 移除缓存登录用户信息
     * @param key 缓存用户 token
     */
    public void removeUserCache(String key){

        Cache cache = getUserCache();

        cache.evict(key);
    }

    /**
     * 获取缓存的登录用户信息
     * @param key 缓存用户 token
     * @return
     */
    public String getUserInfoCache(String key){

        if (!StringUtils.hasText(key)) {
            return null;
        }

        Cache cache = getUserCache();

        String userId = cache.get(key, String.class);

        if (!StringUtils.hasText(userId) && jwtTokenProvider.isValid(key)) {
            AuthUser authUser = jwtTokenProvider.parseAuthUser(key);
            userId = authUser.getUserId();
        }

        return userId;
    }
}
