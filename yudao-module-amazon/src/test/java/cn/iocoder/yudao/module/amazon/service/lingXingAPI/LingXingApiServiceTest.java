package cn.iocoder.yudao.module.amazon.service.lingXingAPI;

import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonStoreDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.LingXingApiResponseDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 领星API服务测试
 *
 * @author 芋道源码
 */
@SpringBootTest
@Slf4j
public class LingXingApiServiceTest {

    @Resource
    private LingXingApiService lingXingApiService;

    /**
     * 测试获取亚马逊店铺列表
     */
    @Test
    public void testGetAmazonStoreList() {
        try {
            log.info("开始测试获取亚马逊店铺列表...");
            
            LingXingApiResponseDTO<List<AmazonStoreDTO>> result = lingXingApiService.getAmazonStoreList();
            
            log.info("API响应: code={}, message={}", result.getCode(), result.getMsg());
            
            if (result.isSuccess() && result.getData() != null) {
                log.info("成功获取店铺列表，共{}个店铺", result.getData().size());
                
                for (AmazonStoreDTO store : result.getData()) {
                    log.info("店铺信息: sid={}, name={}, region={}, country={}, status={}", 
                        store.getSid(), store.getName(), store.getRegion(), store.getCountry(), store.getStatus());
                }
            } else {
                log.error("获取店铺列表失败");
            }
            
        } catch (Exception e) {
            log.error("测试获取亚马逊店铺列表失败", e);
        }
    }

    /**
     * 测试获取美国正常店铺sid列表
     */
    @Test
    public void testGetUSNormalStoreSids() {
        try {
            log.info("开始测试获取美国正常店铺sid列表...");
            
            List<Long> usSids = lingXingApiService.getUSNormalStoreSids();
            
            log.info("美国正常店铺sid列表: {}", usSids);
            log.info("美国正常店铺数量: {}", usSids.size());
            
        } catch (Exception e) {
            log.error("测试获取美国正常店铺sid列表失败", e);
        }
    }
} 