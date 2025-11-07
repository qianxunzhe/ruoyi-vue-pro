package cn.iocoder.yudao.module.amazon.framework.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordranking.KeywordRankingDO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordtask.KeywordTaskDO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.shops.ShopsDO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.taskexeclog.TaskExecLogDO;
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
            rule.addUserColumn(TaskExecLogDO.class);

            // 🔥 注意：amazon_shops表已改用user_ids（JSONB数组）存储多个用户
            // 框架的数据权限规则不支持JSONB类型，无法使用addUserColumn()
            // 因此在ShopsMapper.selectPage()中手动实现了权限过滤
            // 如果传入userId参数，会自动过滤当前用户的店铺（user_ids @> '[userId]'::jsonb）
//            rule.addUserColumn("amazon_shops", "user_id");  // ❌ 已废弃，改为JSONB数组
//            rule.addUserColumn(ShopsDO.class);
        };
    }

}
