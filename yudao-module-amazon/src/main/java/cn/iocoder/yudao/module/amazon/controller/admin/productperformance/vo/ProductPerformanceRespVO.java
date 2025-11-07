package cn.iocoder.yudao.module.amazon.controller.admin.productperformance.vo;

import cn.iocoder.yudao.module.amazon.dal.dataobject.productperformance.ProductPerformanceDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 亚马逊产品表现 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProductPerformanceRespVO extends ProductPerformanceDO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "27099")
    private Long id;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;
}