package cn.iocoder.yudao.module.amazon.job.keywordTask;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.job.TenantJob;
import cn.iocoder.yudao.module.amazon.service.keywordtask.KeywordTaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KeywordTaskJob implements JobHandler {

    @Resource
    private KeywordTaskService keywordTaskService;

    @TenantJob
    @Override
    public String execute(String param) throws Exception {
        log.info(JsonUtils.toJsonString(param));
        log.info(JsonUtils.toJsonString(param));
        return "";
    }
}
