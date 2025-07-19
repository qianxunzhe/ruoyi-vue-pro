package cn.iocoder.yudao.module.amazon.utils;

import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonStoreDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 亚马逊店铺工具类
 *
 * @author 芋道源码
 */
@Slf4j
public class AmazonStoreUtils {

    /**
     * 店铺状态：停止同步
     */
    public static final int STATUS_STOP_SYNC = 0;

    /**
     * 店铺状态：正常
     */
    public static final int STATUS_NORMAL = 1;

    /**
     * 店铺状态：授权异常
     */
    public static final int STATUS_AUTH_ERROR = 2;

    /**
     * 店铺状态：欠费停服
     */
    public static final int STATUS_DEBT_STOP = 3;

    /**
     * 区域：北美
     */
    public static final String REGION_NA = "NA";

    /**
     * 区域：欧洲
     */
    public static final String REGION_EU = "EU";

    /**
     * 区域：亚洲
     */
    public static final String REGION_ASIA = "ASIA";

    /**
     * 筛选指定区域的店铺
     *
     * @param stores 店铺列表
     * @param region 区域代码
     * @return 指定区域的店铺列表
     */
    public static List<AmazonStoreDTO> filterByRegion(List<AmazonStoreDTO> stores, String region) {
        if (stores == null || stores.isEmpty()) {
            return stores;
        }
        
        return stores.stream()
            .filter(store -> region.equals(store.getRegion()))
            .collect(Collectors.toList());
    }

    /**
     * 筛选指定状态的店铺
     *
     * @param stores 店铺列表
     * @param status 状态代码
     * @return 指定状态的店铺列表
     */
    public static List<AmazonStoreDTO> filterByStatus(List<AmazonStoreDTO> stores, int status) {
        if (stores == null || stores.isEmpty()) {
            return stores;
        }
        
        return stores.stream()
            .filter(store -> store.getStatus() != null && store.getStatus() == status)
            .collect(Collectors.toList());
    }

    /**
     * 筛选正常状态的店铺
     *
     * @param stores 店铺列表
     * @return 正常状态的店铺列表
     */
    public static List<AmazonStoreDTO> filterNormalStores(List<AmazonStoreDTO> stores) {
        return filterByStatus(stores, STATUS_NORMAL);
    }

    /**
     * 筛选美国正常店铺
     *
     * @param stores 店铺列表
     * @return 美国正常店铺列表
     */
    public static List<AmazonStoreDTO> filterUSNormalStores(List<AmazonStoreDTO> stores) {
        if (stores == null || stores.isEmpty()) {
            return stores;
        }
        
        return stores.stream()
            .filter(store -> REGION_NA.equals(store.getRegion()) && store.getStatus() != null && store.getStatus() == STATUS_NORMAL)
            .collect(Collectors.toList());
    }

    /**
     * 提取店铺sid列表
     *
     * @param stores 店铺列表
     * @return sid列表
     */
    public static List<Long> extractSids(List<AmazonStoreDTO> stores) {
        if (stores == null || stores.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        return stores.stream()
            .map(AmazonStoreDTO::getSid)
            .collect(Collectors.toList());
    }

    /**
     * 根据区域和状态筛选店铺sid
     *
     * @param stores 店铺列表
     * @param region 区域代码
     * @param status 状态代码
     * @return 筛选后的sid列表
     */
    public static List<Long> filterSidsByRegionAndStatus(List<AmazonStoreDTO> stores, String region, int status) {
        if (stores == null || stores.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        return stores.stream()
            .filter(store -> region.equals(store.getRegion()) && store.getStatus() != null && store.getStatus() == status)
            .map(AmazonStoreDTO::getSid)
            .collect(Collectors.toList());
    }

    /**
     * 获取店铺状态描述
     *
     * @param status 状态代码
     * @return 状态描述
     */
    public static String getStatusDescription(int status) {
        switch (status) {
            case STATUS_STOP_SYNC:
                return "停止同步";
            case STATUS_NORMAL:
                return "正常";
            case STATUS_AUTH_ERROR:
                return "授权异常";
            case STATUS_DEBT_STOP:
                return "欠费停服";
            default:
                return "未知状态";
        }
    }

    /**
     * 获取区域描述
     *
     * @param region 区域代码
     * @return 区域描述
     */
    public static String getRegionDescription(String region) {
        switch (region) {
            case REGION_NA:
                return "北美";
            case REGION_EU:
                return "欧洲";
            case REGION_ASIA:
                return "亚洲";
            default:
                return "未知区域";
        }
    }
} 