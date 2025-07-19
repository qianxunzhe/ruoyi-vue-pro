package cn.iocoder.yudao.module.amazon.controller.admin.asinreview;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.amazon.controller.admin.asinreview.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.asinreview.AsinReviewDO;
import cn.iocoder.yudao.module.amazon.service.asinreview.AsinReviewService;

@Tag(name = "管理后台 - Amazon ASIN商品评论数据")
@RestController
@RequestMapping("/amazon/asin-review")
@Validated
public class AsinReviewController {

    @Resource
    private AsinReviewService asinReviewService;

    @PostMapping("/create")
    @Operation(summary = "创建Amazon ASIN商品评论数据")
    @PreAuthorize("@ss.hasPermission('amazon:asin-review:create')")
    public CommonResult<Long> createAsinReview(@Valid @RequestBody AsinReviewSaveReqVO createReqVO) {
        return success(asinReviewService.createAsinReview(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新Amazon ASIN商品评论数据")
    @PreAuthorize("@ss.hasPermission('amazon:asin-review:update')")
    public CommonResult<Boolean> updateAsinReview(@Valid @RequestBody AsinReviewSaveReqVO updateReqVO) {
        asinReviewService.updateAsinReview(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除Amazon ASIN商品评论数据")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('amazon:asin-review:delete')")
    public CommonResult<Boolean> deleteAsinReview(@RequestParam("id") Integer id) {
        asinReviewService.deleteAsinReview(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除Amazon ASIN商品评论数据")
                @PreAuthorize("@ss.hasPermission('amazon:asin-review:delete')")
    public CommonResult<Boolean> deleteAsinReviewList(@RequestParam("ids") List<Integer> ids) {
        asinReviewService.deleteAsinReviewListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得Amazon ASIN商品评论数据")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('amazon:asin-review:query')")
    public CommonResult<AsinReviewRespVO> getAsinReview(@RequestParam("id") Integer id) {
        AsinReviewDO asinReview = asinReviewService.getAsinReview(id);
        return success(BeanUtils.toBean(asinReview, AsinReviewRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得Amazon ASIN商品评论数据分页")
    @PreAuthorize("@ss.hasPermission('amazon:asin-review:query')")
    public CommonResult<PageResult<AsinReviewRespVO>> getAsinReviewPage(@Valid AsinReviewPageReqVO pageReqVO) {
        PageResult<AsinReviewDO> pageResult = asinReviewService.getAsinReviewPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AsinReviewRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出Amazon ASIN商品评论数据 Excel")
    @PreAuthorize("@ss.hasPermission('amazon:asin-review:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportAsinReviewExcel(@Valid AsinReviewPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AsinReviewDO> list = asinReviewService.getAsinReviewPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "Amazon ASIN商品评论数据.xls", "数据", AsinReviewRespVO.class,
                        BeanUtils.toBean(list, AsinReviewRespVO.class));
    }

    @PostMapping("/analysis")
    @Operation(summary = "获取Amazon ASIN评论统计分析数据")
    @PreAuthorize("@ss.hasPermission('amazon:asin-review:query')")
    public CommonResult<AsinReviewAnalysisRespVO> getReviewAnalysisData(@Valid @RequestBody AsinReviewAnalysisReqVO reqVO) {
        AsinReviewAnalysisRespVO result = asinReviewService.getReviewAnalysisData(reqVO);
        return success(result);
    }

}