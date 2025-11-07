package cn.iocoder.yudao.module.amazon.controller.admin.keywordasinranking;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.amazon.controller.admin.keywordranking.vo.KeywordRankingRespVO;
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

import cn.iocoder.yudao.module.amazon.controller.admin.keywordasinranking.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordasinranking.KeywordAsinRankingDO;
import cn.iocoder.yudao.module.amazon.service.keywordasinranking.KeywordAsinRankingService;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Tag(name = "管理后台 - 关键词排名任务结果")
@RestController
@RequestMapping("/amazon/keyword-asin-ranking")
@Validated
public class KeywordAsinRankingController {

    @Resource
    private KeywordAsinRankingService keywordAsinRankingService;

    @PostMapping("/create")
    @Operation(summary = "创建关键词排名任务结果")
    @PreAuthorize("@ss.hasPermission('amazon:keyword-asin-ranking:create')")
    public CommonResult<Long> createKeywordAsinRanking(@Valid @RequestBody KeywordAsinRankingSaveReqVO createReqVO) {
        return success(keywordAsinRankingService.createKeywordAsinRanking(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新关键词排名任务结果")
    @PreAuthorize("@ss.hasPermission('amazon:keyword-asin-ranking:update')")
    public CommonResult<Boolean> updateKeywordAsinRanking(@Valid @RequestBody KeywordAsinRankingSaveReqVO updateReqVO) {
        keywordAsinRankingService.updateKeywordAsinRanking(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除关键词排名任务结果")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('amazon:keyword-asin-ranking:delete')")
    public CommonResult<Boolean> deleteKeywordAsinRanking(@RequestParam("id") Long id) {
        keywordAsinRankingService.deleteKeywordAsinRanking(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除关键词排名任务结果")
                @PreAuthorize("@ss.hasPermission('amazon:keyword-asin-ranking:delete')")
    public CommonResult<Boolean> deleteKeywordAsinRankingList(@RequestParam("ids") List<Long> ids) {
        keywordAsinRankingService.deleteKeywordAsinRankingListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得关键词排名任务结果")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('amazon:keyword-asin-ranking:query')")
    public CommonResult<KeywordAsinRankingRespVO> getKeywordAsinRanking(@RequestParam("id") Long id) {
        KeywordAsinRankingDO keywordAsinRanking = keywordAsinRankingService.getKeywordAsinRanking(id);
        return success(BeanUtils.toBean(keywordAsinRanking, KeywordAsinRankingRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得关键词排名任务结果分页")
    @PreAuthorize("@ss.hasPermission('amazon:keyword-asin-ranking:query')")
    public CommonResult<PageResult<KeywordAsinRankingRespVO>> getKeywordAsinRankingPage(@Valid KeywordAsinRankingPageReqVO pageReqVO) {
        // 处理时间范围
        if (pageReqVO.getCrawlTime() == null) {
            pageReqVO.setCrawlTime(new LocalDateTime[2]);
        }
        if (pageReqVO.getCrawlTime()[0] == null || pageReqVO.getCrawlTime()[1] == null) {
            // 如果时间范围为空，则不进行时间过滤
            pageReqVO.setCrawlTime(null);
        }
        
        // 处理日期范围
        if (pageReqVO.getCrawlDate() == null) {
            pageReqVO.setCrawlDate(new LocalDate[2]);
        }
        if (pageReqVO.getCrawlDate()[0] == null || pageReqVO.getCrawlDate()[1] == null) {
            // 如果日期范围为空，则不进行日期过滤
            pageReqVO.setCrawlDate(null);
        }
        
        PageResult<KeywordAsinRankingDO> pageResult = keywordAsinRankingService.getKeywordAsinRankingPage(pageReqVO);

        PageResult<KeywordAsinRankingRespVO> rankingDOPageResult = BeanUtils.toBean(pageResult, KeywordAsinRankingRespVO.class);
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
    @PreAuthorize("@ss.hasPermission('amazon:keyword-asin-ranking:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportKeywordAsinRankingExcel(@Valid KeywordAsinRankingPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<KeywordAsinRankingDO> list = keywordAsinRankingService.getKeywordAsinRankingPage(pageReqVO).getList();

        List<KeywordAsinRankingRespVO> resultList = BeanUtils.toBean(list, KeywordAsinRankingRespVO.class);
        resultList.forEach(item -> {
            String position = String.format("Page %d - No. %d",
                    item.getPageNumber(), item.getPositionInPage());
            item.setPosition(position);
        });
        // 导出 Excel
        String fileName = "关键词ASIN匹配结果-" + DateUtil.today() + ".xlsx";
        ExcelUtils.write(response, fileName, "数据", KeywordAsinRankingRespVO.class,
                resultList);
    }

}