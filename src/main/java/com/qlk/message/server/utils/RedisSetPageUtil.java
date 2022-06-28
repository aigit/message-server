/**
 *
 */
package com.qlk.message.server.utils;

import com.qlk.baymax.common.redis.RedisUtil;

/**
 * redis数据分页工具类
 *
 * @author Ldl
 * @since 1.0.0
 */
public class RedisSetPageUtil {

    private String cacheKey;// 缓存key

    private int pageSize;// 每页记录数

    private int pageCount;// 页数

    /**
     * 默认每页记录数
     */
    public static final int EX_PAGESIZE = 1000;

    /**
     * 构造函数
     */
    public RedisSetPageUtil() {
    }

    /**
     * 构造函数
     */
    public RedisSetPageUtil(String cacheKey, int pageSize) {
        this.cacheKey = cacheKey;
        this.pageSize = pageSize;
        if (this.pageSize == 0) {
            this.pageSize = EX_PAGESIZE;
        }

    }

    public int getPageCount() {
        Long totalCount = RedisUtil.zsetOps().zcard(this.cacheKey);
        if (totalCount <= pageSize) {
            return 1;
        }
        int pageCount = totalCount.intValue() / pageSize;
        int pageResidue = totalCount.intValue() % pageSize;
        return pageResidue == 0 ? pageCount : (pageCount + 1);
    }

    public String getCacheKey() {
        return this.cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


}
