package cn.iocoder.yudao.module.amazon.service.shops;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.amazon.controller.admin.shops.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.shops.ShopsDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 亚马逊店铺信息 Service 接口
 *
 * @author Demons
 */
public interface ShopsService {

    /**
     * 创建亚马逊店铺信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createShops(@Valid ShopsSaveReqVO createReqVO);

    /**
     * 更新亚马逊店铺信息
     *
     * @param updateReqVO 更新信息
     */
    void updateShops(@Valid ShopsSaveReqVO updateReqVO);

    /**
     * 删除亚马逊店铺信息
     *
     * @param id 编号
     */
    void deleteShops(Integer id);

    /**
    * 批量删除亚马逊店铺信息
    *
    * @param ids 编号
    */
    void deleteShopsListByIds(List<Integer> ids);

    /**
     * 获得亚马逊店铺信息
     *
     * @param id 编号
     * @return 亚马逊店铺信息
     */
    ShopsDO getShops(Integer id);

    /**
     * 获得亚马逊店铺信息分页
     *
     * @param pageReqVO 分页查询
     * @return 亚马逊店铺信息分页
     */
    PageResult<ShopsDO> getShopsPage(ShopsPageReqVO pageReqVO);



    List<ShopsDO> getAllShopsActive();

    /**
     * 同步领星店铺数据
     * 从领星API获取所有店铺数据，检查数据库中是否存在，不存在则新增
     *
     * @return 同步结果（新增数量）
     */
    int syncShopsFromLingXing();

    /**
     * 分配店铺负责人
     *
     * @param sid 店铺ID
     * @param userId 负责人用户ID（已废弃，使用userIds）
     */
    @Deprecated
    void assignShopOwner(Integer sid, Long userId);

    /**
     * 分配店铺负责人（批量）
     *
     * @param sid 店铺ID
     * @param userIds 负责人用户ID列表
     */
    void assignShopOwners(Integer sid, List<Long> userIds);

    /**
     * 更新店铺同步标记
     *
     * @param sid 店铺ID
     * @param syncFlag 同步标记 (0:不同步, 1:同步)
     */
    void updateShopSyncFlag(Integer sid, Integer syncFlag);

    /**
     * 获取所有需要同步的店铺ID列表
     * 查询条件：syncFlag = 1
     *
     * @return 店铺ID列表
     */
    List<Integer> getSyncEnabledShopSids();

    /**
     * 更新店铺状态
     *
     * @param sid 店铺ID
     * @param status 状态 (0:禁用, 1:启用)
     */
    void updateShopStatus(Integer sid, Integer status);

}