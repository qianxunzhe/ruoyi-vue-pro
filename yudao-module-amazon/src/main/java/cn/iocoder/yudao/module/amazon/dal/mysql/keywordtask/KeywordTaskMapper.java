package cn.iocoder.yudao.module.amazon.dal.mysql.keywordtask;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordtask.KeywordTaskDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.amazon.controller.admin.keywordtask.vo.*;

/**
 * 关键词排名任务 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface KeywordTaskMapper extends BaseMapperX<KeywordTaskDO> {

    default PageResult<KeywordTaskDO> selectPage(KeywordTaskPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<KeywordTaskDO>()
                .eqIfPresent(KeywordTaskDO::getUserId, reqVO.getUserId())
                .likeIfPresent(KeywordTaskDO::getTaskName, reqVO.getTaskName())
                .eqIfPresent(KeywordTaskDO::getKeywords, reqVO.getKeywords())
                .eqIfPresent(KeywordTaskDO::getCities, reqVO.getCities())
                .eqIfPresent(KeywordTaskDO::getAsins, reqVO.getAsins())
                .eqIfPresent(KeywordTaskDO::getCronTab, reqVO.getCronTab())
                .eqIfPresent(KeywordTaskDO::getEnabled, reqVO.getEnabled())
                .eqIfPresent(KeywordTaskDO::getTaskType, reqVO.getTaskType())
                .eqIfPresent(KeywordTaskDO::getShared, reqVO.getShared())
                .eqIfPresent(KeywordTaskDO::getDescription, reqVO.getDescription())
                .eqIfPresent(KeywordTaskDO::getScraperType, reqVO.getScraperType())
                .betweenIfPresent(KeywordTaskDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(KeywordTaskDO::getId));
    }

    default void updateStatusById(KeywordTaskDO keywordTaskDO) {

    }

    default void selectList(KeywordTaskPageReqVO reqVO) {

    }

}