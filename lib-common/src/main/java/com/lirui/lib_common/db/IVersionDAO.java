package com.lirui.lib_common.db;

import com.lirui.lib_common.version.bean.VersionBean;

/**
 * 数据库访问接口
 */
interface IVersionDAO {
    /**
     * 插入版本信息
     */
    void insert(VersionBean info);

    /**
     * 查询版本信息
     */
    VersionBean query(int version);

    /**
     * 版本是否存在
     */
    boolean isExists(int version);
}