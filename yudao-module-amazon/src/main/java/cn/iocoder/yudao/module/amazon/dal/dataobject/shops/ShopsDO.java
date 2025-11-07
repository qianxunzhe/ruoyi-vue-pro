package cn.iocoder.yudao.module.amazon.dal.dataobject.shops;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import org.apache.ibatis.type.JdbcType;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.type.LongListJsonbTypeHandler;

/**
 * 亚马逊店铺信息 DO
 *
 * @author Demons
 */
@TableName("amazon_shops")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopsDO {

    /**
     * 店铺ID，主键
     */
    @TableId
    private Integer sid;
    /**
     * 商户ID
     */
    private Integer mid;
    /**
     * 店铺名称
     */
    private String name;
    /**
     * 卖家ID
     */
    private String sellerId;
    /**
     * 账户名称
     */
    private String accountName;
    /**
     * 卖家账户ID
     */
    private Integer sellerAccountId;
    /**
     * 区域
     */
    private String region;
    /**
     * 国家
     */
    private String country;
    /**
     * 是否有广告设置 (0:无, 1:有)
     */
    private Integer hasAdsSetting;
    /**
     * 市场ID
     */
    private String marketplaceId;
    /**
     * 状态 (0:禁用, 1:启用)
     */
    private Integer status;
    /**
     * 用户ID（已废弃，使用userIds）
     */
    private Long userId;
    /**
     * 负责人用户ID列表（JSONB数组）
     */
    @TableField(typeHandler = LongListJsonbTypeHandler.class)
    private List<Long> userIds;
    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    private String creator;
    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.VARCHAR)
    private String updater;
    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
    /**
     * 是否同步产品表现 (0:不同步, 1:同步)
     */
    private Integer syncFlag;

}