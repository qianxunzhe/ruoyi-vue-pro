package cn.iocoder.yudao.module.amazon.job.keywordTask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.job.TenantJob;
import cn.iocoder.yudao.module.amazon.controller.admin.keywordtask.vo.KeywordTaskPageReqVO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordtask.KeywordTaskDO;
import cn.iocoder.yudao.module.amazon.service.keywordtask.KeywordTaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
public class KeywordTaskJob implements JobHandler {

    @Resource
    private KeywordTaskService keywordTaskService;


    @TenantJob
    @Override
    public String execute(String param) throws Exception {
        // 处理价格监控任务
        String priceResult = keywordTaskService.handelPriceTask();
        log.info("价格监控结果: {}", priceResult);
        

        
        String combinedResult = String.format("价格监控: %s", priceResult);
        return combinedResult;
    }
}
