package cn.iocoder.yudao.module.amazon.framework.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordranking.KeywordRankingDO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordtask.KeywordTaskDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DataPermissionRuleCustomizer {


    @Bean
    public DeptDataPermissionRuleCustomizer amazonTaskDataPermissionRuleCustomizer() {
        return rule -> {
            // dept 基于部门的数据权限
//            rule.addDeptColumn(KeywordTaskDO.class); // WHERE dept_id = ?
//            rule.addDeptColumn(DeptDO.class, "id"); // WHERE id = ?

            // user 基于用户的数据权限
//            rule.addUserColumn(KeywordTaskDO.class, "id"); // WHERE id = ?
            rule.addUserColumn(KeywordTaskDO.class); // WHERE user_id = ?
            rule.addUserColumn(KeywordRankingDO.class);
        };
    }

}
