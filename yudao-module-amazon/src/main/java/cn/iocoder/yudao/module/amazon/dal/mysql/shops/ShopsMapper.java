package cn.iocoder.yudao.module.amazon.dal.mysql.shops;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.amazon.dal.dataobject.shops.ShopsDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.amazon.controller.admin.shops.vo.*;

/**
 * 亚马逊店铺信息 Mapper
 *
 * @author Demons
 */
@Mapper
public interface ShopsMapper extends BaseMapperX<ShopsDO> {

    default PageResult<ShopsDO> selectPage(ShopsPageReqVO reqVO) {
        LambdaQueryWrapperX<ShopsDO> queryWrapper = new LambdaQueryWrapperX<ShopsDO>()
                .eqIfPresent(ShopsDO::getMid, reqVO.getMid())
                .likeIfPresent(ShopsDO::getName, reqVO.getName())
                .eqIfPresent(ShopsDO::getSellerId, reqVO.getSellerId())
//                .likeIfPresent(ShopsDO::getAccountName, reqVO.getAccountName())
                .eqIfPresent(ShopsDO::getSellerAccountId, reqVO.getSellerAccountId())
                .eqIfPresent(ShopsDO::getRegion, reqVO.getRegion())
                .eqIfPresent(ShopsDO::getCountry, reqVO.getCountry())
                .eqIfPresent(ShopsDO::getHasAdsSetting, reqVO.getHasAdsSetting())
                .eqIfPresent(ShopsDO::getMarketplaceId, reqVO.getMarketplaceId())
                .eqIfPresent(ShopsDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ShopsDO::getCreatedAt, reqVO.getCreatedAt())
                .eqIfPresent(ShopsDO::getUpdatedAt, reqVO.getUpdatedAt());

        // 🔥 修复：支持JSONB数组查询，查询包含指定用户ID的店铺
        if (reqVO.getUserId() != null) {
            // PostgreSQL JSONB包含查询：user_ids @> '[userId]'::jsonb
            queryWrapper.apply("user_ids @> CAST('[' || {0} || ']' AS jsonb)", reqVO.getUserId());
        }

        queryWrapper.orderByDesc(ShopsDO::getSid);

        return selectPage(reqVO, queryWrapper);
    }

    /**
     * 查询指定用户的所有店铺（支持JSONB数组查询）
     *
     * @param userId 用户ID
     * @return 店铺列表
     */
    default List<ShopsDO> selectListByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        // PostgreSQL JSONB包含查询：user_ids @> '[userId]'::jsonb
        return selectList(new LambdaQueryWrapperX<ShopsDO>()
                .apply("user_ids @> CAST('[' || {0} || ']' AS jsonb)", userId)
                .orderByDesc(ShopsDO::getSid));
    }

    /**
     * 查询指定用户的所有启用状态的店铺
     *
     * @param userId 用户ID
     * @return 店铺列表
     */
    default List<ShopsDO> selectActiveListByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        // PostgreSQL JSONB包含查询：user_ids @> '[userId]'::jsonb
        return selectList(new LambdaQueryWrapperX<ShopsDO>()
                .apply("user_ids @> CAST('[' || {0} || ']' AS jsonb)", userId)
                .eq(ShopsDO::getStatus, 1)
                .orderByDesc(ShopsDO::getSid));
    }

}