package cn.iocoder.yudao.module.amazon.controller.admin.keywordranking;

import cn.hutool.Hutool;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

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

import cn.iocoder.yudao.module.amazon.controller.admin.keywordranking.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordranking.KeywordRankingDO;
import cn.iocoder.yudao.module.amazon.service.keywordranking.KeywordRankingService;

@Tag(name = "管理后台 - 关键词排名任务结果")
@RestController
@RequestMapping("/amazon/keyword-ranking")
@Validated
public class KeywordRankingController {

    @Resource
    private KeywordRankingService keywordRankingService;

    @PostMapping("/create")
    @Operation(summary = "创建关键词排名任务结果")
    @PreAuthorize("@ss.hasPermission('amazon:keyword-ranking:create')")
    public CommonResult<Long> createKeywordRanking(@Valid @RequestBody KeywordRankingSaveReqVO createReqVO) {
        return success(keywordRankingService.createKeywordRanking(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新关键词排名任务结果")
    @PreAuthorize("@ss.hasPermission('amazon:keyword-ranking:update')")
    public CommonResult<Boolean> updateKeywordRanking(@Valid @RequestBody KeywordRankingSaveReqVO updateReqVO) {
        keywordRankingService.updateKeywordRanking(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除关键词排名任务结果")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('amazon:keyword-ranking:delete')")
    public CommonResult<Boolean> deleteKeywordRanking(@RequestParam("id") Long id) {
        keywordRankingService.deleteKeywordRanking(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除关键词排名任务结果")
                @PreAuthorize("@ss.hasPermission('amazon:keyword-ranking:delete')")
    public CommonResult<Boolean> deleteKeywordRankingList(@RequestParam("ids") List<Long> ids) {
        keywordRankingService.deleteKeywordRankingListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得关键词排名任务结果")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('amazon:keyword-ranking:query')")
    @DataPermission(enable = true)
    public CommonResult<KeywordRankingRespVO> getKeywordRanking(@RequestParam("id") Long id) {
        KeywordRankingDO keywordRanking = keywordRankingService.getKeywordRanking(id);
        return success(BeanUtils.toBean(keywordRanking, KeywordRankingRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得关键词排名任务结果分页")
    @PreAuthorize("@ss.hasPermission('amazon:keyword-ranking:query')")
    @DataPermission(enable = true)
    public CommonResult<PageResult<KeywordRankingRespVO>> getKeywordRankingPage(@Valid KeywordRankingPageReqVO pageReqVO) {
        PageResult<KeywordRankingDO> pageResult = keywordRankingService.getKeywordRankingPage(pageReqVO);

        PageResult<KeywordRankingRespVO> rankingDOPageResult = BeanUtils.toBean(pageResult, KeywordRankingRespVO.class);
        rankingDOPageResult.getList().forEach(item -> {
            String position = StrUtil.strBuilder()
                    .append("第").append(item.getPageNumber()).append("页 | ")
                    .append("第").append(item.getPositionInPage()).append("个")
                    .toString();
            item.setPosition(position);
        });
        return success(rankingDOPageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出关键词排名任务结果 Excel")
    @PreAuthorize("@ss.hasPermission('amazon:keyword-ranking:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportKeywordRankingExcel(@Valid KeywordRankingPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<KeywordRankingDO> list = keywordRankingService.getKeywordRankingPage(pageReqVO).getList();
        List<KeywordRankingRespVO> resultList = BeanUtils.toBean(list, KeywordRankingRespVO.class);
        resultList.forEach(item -> {
            String position = String.format("Page %d - No. %d",
                    item.getPageNumber(), item.getPositionInPage());
            item.setPosition(position);
        });
        String fileName = "关键词ASIN匹配结果-" + DateUtil.today() + ".xlsx";
        // 导出 Excel
        ExcelUtils.write(response, fileName, "数据", KeywordRankingRespVO.class,resultList);
    }

}