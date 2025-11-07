package cn.iocoder.yudao.module.amazon.controller.admin.reviewsnapshots;

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

import cn.iocoder.yudao.module.amazon.controller.admin.reviewsnapshots.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.reviewsnapshots.ReviewSnapshotsDO;
import cn.iocoder.yudao.module.amazon.service.reviewsnapshots.ReviewSnapshotsService;

@Tag(name = "管理后台 - 亚马逊评论快照")
@RestController
@RequestMapping("/amazon/review-snapshots")
@Validated
public class ReviewSnapshotsController {

    @Resource
    private ReviewSnapshotsService reviewSnapshotsService;

    @PostMapping("/create")
    @Operation(summary = "创建亚马逊评论快照")
    @PreAuthorize("@ss.hasPermission('amazon:review-snapshots:create')")
    public CommonResult<Integer> createReviewSnapshots(@Valid @RequestBody ReviewSnapshotsSaveReqVO createReqVO) {
        return success(reviewSnapshotsService.createReviewSnapshots(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新亚马逊评论快照")
    @PreAuthorize("@ss.hasPermission('amazon:review-snapshots:update')")
    public CommonResult<Boolean> updateReviewSnapshots(@Valid @RequestBody ReviewSnapshotsSaveReqVO updateReqVO) {
        reviewSnapshotsService.updateReviewSnapshots(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除亚马逊评论快照")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('amazon:review-snapshots:delete')")
    public CommonResult<Boolean> deleteReviewSnapshots(@RequestParam("id") Integer id) {
        reviewSnapshotsService.deleteReviewSnapshots(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除亚马逊评论快照")
                @PreAuthorize("@ss.hasPermission('amazon:review-snapshots:delete')")
    public CommonResult<Boolean> deleteReviewSnapshotsList(@RequestParam("ids") List<Integer> ids) {
        reviewSnapshotsService.deleteReviewSnapshotsListByIds(ids);
        return success(true);
    }

    @PostMapping("/batch-sync")
    @Operation(summary = "批量保存评论快照")
    @PreAuthorize("@ss.hasPermission('amazon:review-snapshots:create')")
    public CommonResult<Integer> batchSyncReviewSnapshots(@Valid @RequestBody ReviewSnapshotsBatchSyncReqVO batchSyncReqVO) {
        return success(reviewSnapshotsService.batchSyncReviewSnapshots(batchSyncReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得亚马逊评论快照")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('amazon:review-snapshots:query')")
    public CommonResult<ReviewSnapshotsRespVO> getReviewSnapshots(@RequestParam("id") Integer id) {
        ReviewSnapshotsDO reviewSnapshots = reviewSnapshotsService.getReviewSnapshots(id);
        return success(BeanUtils.toBean(reviewSnapshots, ReviewSnapshotsRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得亚马逊评论快照分页")
    @PreAuthorize("@ss.hasPermission('amazon:review-snapshots:query')")
    public CommonResult<PageResult<ReviewSnapshotsRespVO>> getReviewSnapshotsPage(@Valid ReviewSnapshotsPageReqVO pageReqVO) {
        PageResult<ReviewSnapshotsDO> pageResult = reviewSnapshotsService.getReviewSnapshotsPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ReviewSnapshotsRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出亚马逊评论快照 Excel")
    @PreAuthorize("@ss.hasPermission('amazon:review-snapshots:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportReviewSnapshotsExcel(@Valid ReviewSnapshotsPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ReviewSnapshotsDO> list = reviewSnapshotsService.getReviewSnapshotsPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "亚马逊评论快照.xls", "数据", ReviewSnapshotsRespVO.class,
                        BeanUtils.toBean(list, ReviewSnapshotsRespVO.class));
    }

}