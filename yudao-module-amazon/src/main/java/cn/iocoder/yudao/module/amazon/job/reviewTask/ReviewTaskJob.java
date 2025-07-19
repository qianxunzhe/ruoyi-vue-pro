package cn.iocoder.yudao.module.amazon.job.reviewTask;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.job.TenantJob;
import cn.iocoder.yudao.module.amazon.service.keywordtask.KeywordTaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ReviewTaskJob implements JobHandler {

    @Resource
    private KeywordTaskService keywordTaskService;


    @TenantJob
    @Override
    public String execute(String param) throws Exception {
        // 处理评论监控任务
        String reviewResult = keywordTaskService.handleReviewTask();
        log.info("评论监控结果: {}", reviewResult);
        
        String combinedResult = String.format(" 评论监控: %s",  reviewResult);
        return combinedResult;
    }
}
