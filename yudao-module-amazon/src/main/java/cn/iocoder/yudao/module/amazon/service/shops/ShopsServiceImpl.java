package cn.iocoder.yudao.module.amazon.service.shops;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.amazon.controller.admin.shops.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.shops.ShopsDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.amazon.dal.mysql.shops.ShopsMapper;
import cn.iocoder.yudao.module.amazon.service.lingXingAPI.LingXingApiService;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonStoreDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.LingXingApiResponseDTO;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.security.core.service.SecurityFrameworkService;
import lombok.extern.slf4j.Slf4j;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.amazon.enums.ErrorCodeConstants.*;

/**
 * 亚马逊店铺信息 Service 实现类
 *
 * @author Demons
 */
@Slf4j
@Service
@Validated
public class ShopsServiceImpl implements ShopsService {

    @Resource
    private ShopsMapper shopsMapper;

    @Resource
    private LingXingApiService lingXingApiService;

    @Resource
    private SecurityFrameworkService securityFrameworkService;

    @Override
    public Integer createShops(ShopsSaveReqVO createReqVO) {
        // 插入
        ShopsDO shops = BeanUtils.toBean(createReqVO, ShopsDO.class);
        shopsMapper.insert(shops);

        // 返回
        return shops.getSid();
    }

    @Override
    public void updateShops(ShopsSaveReqVO updateReqVO) {
        // 校验存在
        validateShopsExists(updateReqVO.getSid());
        // 更新
        ShopsDO updateObj = BeanUtils.toBean(updateReqVO, ShopsDO.class);
        shopsMapper.updateById(updateObj);
    }

    @Override
    public void deleteShops(Integer id) {
        // 校验存在
        validateShopsExists(id);
        // 删除
        shopsMapper.deleteById(id);
    }

    @Override
        public void deleteShopsListByIds(List<Integer> ids) {
        // 删除
        shopsMapper.deleteByIds(ids);
        }


    private void validateShopsExists(Integer id) {
        if (shopsMapper.selectById(id) == null) {
            throw exception(SHOPS_NOT_EXISTS);
        }
    }

    @Override
    public ShopsDO getShops(Integer id) {
        return shopsMapper.selectById(id);
    }

    @Override
    public PageResult<ShopsDO> getShopsPage(ShopsPageReqVO pageReqVO) {
        // 🔥 自动权限过滤：非管理员只能查看自己负责的店铺
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();

        // 判断是否是管理员（admin角色）
        boolean isAdmin = securityFrameworkService.hasAnyRoles("admin", "super_admin");

        if (!isAdmin) {
            // 非管理员：强制过滤只查看自己负责的店铺
            pageReqVO.setUserId(loginUserId);
            log.debug("非管理员用户（userId={}）查询店铺，自动过滤权限", loginUserId);
        } else {
            log.debug("管理员用户（userId={}）查询店铺，不过滤权限", loginUserId);
        }

        return shopsMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ShopsDO> getAllShopsActive() {
        // 查询所有活跃店铺：user_ids不为空数组，status=1
        LambdaQueryWrapperX<ShopsDO> queryWrapper = new LambdaQueryWrapperX<>();

        // 🔥 修复：判断userIds不为空（PostgreSQL JSONB查询）
        // user_ids IS NOT NULL AND user_ids != '[]'::jsonb AND jsonb_array_length(user_ids) > 0
        queryWrapper.isNotNull(ShopsDO::getUserIds);
        queryWrapper.apply("user_ids != '[]'::jsonb");
        queryWrapper.apply("jsonb_array_length(user_ids) > 0");

        // status=1(启用状态)
        queryWrapper.eq(ShopsDO::getStatus, 1);

        return shopsMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncShopsFromLingXing() {
        log.info("开始同步领星店铺数据...");
        
        try {
            // 1. 调用领星API获取所有店铺数据
            LingXingApiResponseDTO<List<AmazonStoreDTO>> response = lingXingApiService.getAmazonStoreList();
            
            if (!response.isSuccess() || response.getData() == null) {
                log.error("获取领星店铺数据失败: code={}, message={}", response.getCode(), response.getMsg());
                throw new RuntimeException("获取领星店铺数据失败: " + response.getMsg());
            }
            
            List<AmazonStoreDTO> storeDTOList = response.getData();
            log.info("从领星API获取到{}个店铺", storeDTOList.size());
            
            // 2. 批量处理店铺数据
            int newCount = 0;
            for (AmazonStoreDTO storeDTO : storeDTOList) {
                // 检查数据库中是否已存在该店铺（根据sid判断）
                ShopsDO existingShop = shopsMapper.selectById(storeDTO.getSid().intValue());
                
                if (existingShop == null) {
                    // 不存在则新增
                    ShopsDO newShop = convertToShopsDO(storeDTO);
                    shopsMapper.insert(newShop);
                    newCount++;
                    log.debug("新增店铺: sid={}, name={}", storeDTO.getSid(), storeDTO.getName());
                } else {
                    // 已存在则跳过
                    log.debug("店铺已存在，跳过: sid={}, name={}", storeDTO.getSid(), storeDTO.getName());
                }
            }
            
            log.info("领星店铺同步完成，共处理{}个店铺，新增{}个店铺", storeDTOList.size(), newCount);
            return newCount;
            
        } catch (Exception e) {
            log.error("同步领星店铺数据失败", e);
            throw new RuntimeException("同步领星店铺数据失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将领星API返回的店铺DTO转换为数据库实体
     */
    private ShopsDO convertToShopsDO(AmazonStoreDTO storeDTO) {
        ShopsDO shopsDO = new ShopsDO();
        shopsDO.setSid(storeDTO.getSid().intValue());
        shopsDO.setMid(storeDTO.getMid().intValue());
        shopsDO.setName(storeDTO.getName());
        shopsDO.setSellerId(storeDTO.getSellerId());
        shopsDO.setAccountName(storeDTO.getAccountName());
        shopsDO.setSellerAccountId(storeDTO.getSellerAccountId().intValue());
        shopsDO.setRegion(storeDTO.getRegion());
        shopsDO.setCountry(storeDTO.getCountry());
        shopsDO.setHasAdsSetting(storeDTO.getHasAdsSetting());
        shopsDO.setMarketplaceId(storeDTO.getMarketplaceId());
        shopsDO.setStatus(storeDTO.getStatus());
        // 这里可以根据需要设置其他字段，如userId、createdAt、updatedAt等
        return shopsDO;
    }

    @Override
    @Deprecated
    public void assignShopOwner(Integer sid, Long userId) {
        log.info("分配店铺负责人（旧方法，已废弃）: sid={}, userId={}", sid, userId);

        // 校验店铺是否存在
        validateShopsExists(sid);

        // 更新店铺的负责人（旧字段，兼容性保留）
        ShopsDO updateDO = new ShopsDO();
        updateDO.setSid(sid);
        updateDO.setUserId(userId);
        // 同时更新新字段userIds（包含单个用户）
        updateDO.setUserIds(Collections.singletonList(userId));
        shopsMapper.updateById(updateDO);

        log.info("店铺负责人分配成功: sid={}, userId={}", sid, userId);
    }

    @Override
    public void assignShopOwners(Integer sid, List<Long> userIds) {
        log.info("分配店铺负责人（批量）: sid={}, userIds={}", sid, userIds);

        // 校验店铺是否存在
        validateShopsExists(sid);

        // 校验用户ID列表不为空
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("用户ID列表不能为空");
        }

        // 更新店铺的负责人列表
        ShopsDO updateDO = new ShopsDO();
        updateDO.setSid(sid);
        updateDO.setUserIds(userIds);
        // 为了兼容性，如果只有一个用户，也更新旧的userId字段
        if (userIds.size() == 1) {
            updateDO.setUserId(userIds.get(0));
        }
        shopsMapper.updateById(updateDO);

        log.info("店铺负责人分配成功: sid={}, userIds={}", sid, userIds);
    }

    @Override
    public void updateShopSyncFlag(Integer sid, Integer syncFlag) {
        log.info("更新店铺同步标记: sid={}, syncFlag={}", sid, syncFlag);
        
        // 校验店铺是否存在
        validateShopsExists(sid);
        
        // 更新店铺的同步标记
        ShopsDO updateDO = new ShopsDO();
        updateDO.setSid(sid);
        updateDO.setSyncFlag(syncFlag);
        shopsMapper.updateById(updateDO);
        
        log.info("店铺同步标记更新成功: sid={}, syncFlag={}", sid, syncFlag);
    }

    @Override
    public List<Integer> getSyncEnabledShopSids() {
        log.info("获取所有需要同步的店铺");
        
        // 查询syncFlag = 1的所有店铺
        LambdaQueryWrapperX<ShopsDO> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(ShopsDO::getSyncFlag, 1);
        queryWrapper.select(ShopsDO::getSid);
        
        List<ShopsDO> shops = shopsMapper.selectList(queryWrapper);
        List<Integer> sids = convertList(shops, ShopsDO::getSid);
        
        log.info("找到{}个需要同步的店铺", sids.size());
        return sids;
    }

    @Override
    public void updateShopStatus(Integer sid, Integer status) {
        log.info("更新店铺状态: sid={}, status={}", sid, status);
        
        // 校验店铺是否存在
        validateShopsExists(sid);
        
        // 更新店铺的状态
        ShopsDO updateDO = new ShopsDO();
        updateDO.setSid(sid);
        updateDO.setStatus(status);
        shopsMapper.updateById(updateDO);
        
        log.info("店铺状态更新成功: sid={}, status={}", sid, status);
    }

}