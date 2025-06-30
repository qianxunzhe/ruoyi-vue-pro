package cn.iocoder.yudao.module.amazon.controller.admin.keywordtask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Schema(description = "管理后台 - 关键词排名任务新增/修改 Request VO")
@Data
public class KeywordTaskUpdateStatusReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30685")
    @NotNull
    private Long id;


    @Schema(description = "启用状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    private Integer enabled;


}