package cn.iocoder.yudao.module.amazon.service.asinreview;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import cn.iocoder.yudao.module.amazon.controller.admin.asinreview.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.asinreview.AsinReviewDO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordtask.KeywordTaskDO;
import cn.iocoder.yudao.module.amazon.dal.mysql.keywordtask.KeywordTaskMapper;
import cn.iocoder.yudao.module.amazon.service.lingXingAPI.LingXingApiService;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.amazon.dal.mysql.asinreview.AsinReviewMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.amazon.enums.ErrorCodeConstants.*;

/**
 * Amazon ASIN商品评论数据 Service 实现类
 *
 * @author Demons
 */
@Service
@Validated
@Slf4j
public class AsinReviewServiceImpl implements AsinReviewService {

    @Resource
    private AsinReviewMapper asinReviewMapper;
    
    @Resource
    private KeywordTaskMapper keywordTaskMapper;
    
    @Resource
    private LingXingApiService lingXingApiService;

    @Override
    public Long createAsinReview(AsinReviewSaveReqVO createReqVO) {
        // 插入
        AsinReviewDO asinReview = BeanUtils.toBean(createReqVO, AsinReviewDO.class);
        asinReviewMapper.insert(asinReview);

        // 返回
        return asinReview.getId();
    }

    @Override
    public void updateAsinReview(AsinReviewSaveReqVO updateReqVO) {
        // 校验存在
        validateAsinReviewExists(updateReqVO.getId());
        // 更新
        AsinReviewDO updateObj = BeanUtils.toBean(updateReqVO, AsinReviewDO.class);
        asinReviewMapper.updateById(updateObj);
    }

    @Override
    public void deleteAsinReview(Integer id) {
        // 校验存在
        validateAsinReviewExists(id);
        // 删除
        asinReviewMapper.deleteById(id);
    }

    @Override
        public void deleteAsinReviewListByIds(List<Integer> ids) {
        // 删除
        asinReviewMapper.deleteByIds(ids);
        }


    private void validateAsinReviewExists(Integer id) {
        if (asinReviewMapper.selectById(id) == null) {
            throw exception(ASIN_REVIEW_NOT_EXISTS);
        }
    }

    @Override
    public AsinReviewDO getAsinReview(Integer id) {
        return asinReviewMapper.selectById(id);
    }

    @Override
    public PageResult<AsinReviewDO> getAsinReviewPage(AsinReviewPageReqVO pageReqVO) {
        return asinReviewMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional
    public Map<String, Object> processAsinReviewMonitoring(Long taskId) {
        log.info("开始处理任务 [{}] 的ASIN评论监控", taskId);
        
        Map<String, Object> result = new HashMap<>();
        int initializedCount = 0;
        int incrementUpdatedCount = 0;
        int errorCount = 0;
        
        try {
            // 1. 获取任务信息
            KeywordTaskDO task = keywordTaskMapper.selectById(taskId);
            if (task == null) {
                throw new IllegalArgumentException("任务不存在: " + taskId);
            }
            
            if (task.getScraperType() != 4) {
                log.warn("任务 [{}] 的scraperType不是4，跳过处理", taskId);
                result.put("status", "skipped");
                result.put("message", "任务类型不是评论监控");
                return result;
            }
            
            List<String> asins = task.getAsins();
            if (CollUtil.isEmpty(asins)) {
                log.warn("任务 [{}] 没有配置ASIN列表", taskId);
                result.put("status", "skipped");
                result.put("message", "ASIN列表为空");
                return result;
            }
            
            log.info("任务 [{}] 包含 {} 个ASIN需要处理", taskId, asins.size());
            
            // 2. 处理每个ASIN
            for (String asin : asins) {
                try {
                    // 检查是否已存在记录
                    AsinReviewDO existing = getByAsinAndTaskId(asin, taskId);
                    
                    if (existing == null || existing.getInitialized() == 0) {
                        // 初始化历史数据
                        if (initializeAsinReviewHistory(asin, taskId, task.getUserId())) {
                            initializedCount++;
                            log.info("ASIN [{}] 历史数据初始化成功", asin);
                        } else {
                            errorCount++;
                            log.error("ASIN [{}] 历史数据初始化失败", asin);
                        }
                    } else {
                        // 更新增量数据
                        int increment = updateAsinReviewIncrement(asin, taskId);
                        if (increment >= 0) {
                            incrementUpdatedCount++;
                            log.info("ASIN [{}] 昨日新增评论数量: {}", asin, increment);
                        } else {
                            errorCount++;
                            log.error("ASIN [{}] 增量更新失败", asin);
                        }
                    }
                    
                    // 防止API调用过于频繁
                    Thread.sleep(1000);
                    
                } catch (Exception e) {
                    errorCount++;
                    log.error("处理ASIN [{}] 时发生异常", asin, e);
                }
            }
            
            result.put("status", "completed");
            result.put("totalAsins", asins.size());
            result.put("initializedCount", initializedCount);
            result.put("incrementUpdatedCount", incrementUpdatedCount);
            result.put("errorCount", errorCount);
            
            log.info("任务 [{}] 处理完成，总数:{}, 初始化:{}, 增量更新:{}, 错误:{}", 
                taskId, asins.size(), initializedCount, incrementUpdatedCount, errorCount);
            
        } catch (Exception e) {
            log.error("处理任务 [{}] 时发生异常", taskId, e);
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean initializeAsinReviewHistory(String asin, Long taskId, Long userId) {
        if (StrUtil.isBlank(asin) || taskId == null) {
            return false;
        }
        
        log.info("开始初始化ASIN [{}] 的历史评论数据", asin);
        
        try {
            // 计算时间范围：2023-10-01 至昨天
            LocalDate yesterday = LocalDate.now().minusDays(1);
            LocalDate startDate = LocalDate.of(2023, 10, 1);
            LocalDate endDate = yesterday;
            
            String startDateStr = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endDateStr = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // 调用领星API获取历史总数（使用Review接口而不是ReportReview接口）
            Integer totalCount = lingXingApiService.getAsinReviewCount(asin, startDateStr, endDateStr);
            
            if (totalCount == null) {
                log.error("获取ASIN [{}] 历史评论总数失败", asin);
                return false;
            }
            
            // 检查今天是否已经创建过初始化记录
            LocalDate today = LocalDate.now();
            AsinReviewDO existing = asinReviewMapper.selectByAsinTaskIdAndDate(asin, taskId, today);
            
            if (existing != null && existing.getIsInitial()) {
                log.info("ASIN [{}] 今日已有初始化记录，跳过", asin);
                return true;
            }
            
            // 创建初始化记录
            AsinReviewDO newRecord = AsinReviewDO.builder()
                .userId(userId)
                .asin(asin)
                .taskId(taskId.intValue())
                .reviewNum(totalCount)
                .reviewIncrement(0)  // 初始化记录增量为0
                .score(BigDecimal.ZERO)
                .ratings(0)
                .recordDate(today)
                .isInitial(true)
                .initialized(1)
                .lastUpdateDate(LocalDateTime.now())
                .remark("历史数据初始化")
                .build();
            
            asinReviewMapper.insert(newRecord);
            log.info("创建ASIN [{}] 历史评论数据成功，总数: {}", asin, totalCount);
            
            return true;
            
        } catch (Exception e) {
            log.error("初始化ASIN [{}] 历史评论数据失败", asin, e);
            return false;
        }
    }

    @Override
    @Transactional
    public int updateAsinReviewIncrement(String asin, Long taskId) {
        if (StrUtil.isBlank(asin) || taskId == null) {
            return -1;
        }
        
        log.info("开始更新ASIN [{}] 的增量评论数据", asin);
        
        try {
            // 检查今天是否已经记录过
            LocalDate today = LocalDate.now();
            AsinReviewDO todayRecord = asinReviewMapper.selectByAsinTaskIdAndDate(asin, taskId, today);
            
            if (todayRecord != null) {
                log.info("ASIN [{}] 今日数据已记录，跳过", asin);
                return todayRecord.getReviewIncrement();
            }
            
            // 获取昨日新增评论数量
            Integer yesterdayCount = lingXingApiService.getYesterdayReviewCount(asin, 1);
            
            if (yesterdayCount == null) {
                log.error("获取ASIN [{}] 昨日新增评论数量失败", asin);
                return -1;
            }
            
            // 获取昨天的评论数据（增量更新只查询昨天）
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String yesterdayStr = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // 增量更新：只查询昨天一天的数据（这就是昨天的增量）
            Integer yesterdayReviews = lingXingApiService.getAsinReviewCount(asin, yesterdayStr, yesterdayStr);
            if (yesterdayReviews == null) {
                log.error("获取ASIN [{}] 昨天评论数失败", asin);
                return -1;
            }
            
            // 获取最新的累计总数（查询前一天的记录来计算累计值）
            AsinReviewDO latestRecord = asinReviewMapper.selectOne(new LambdaQueryWrapperX<AsinReviewDO>()
                .eq(AsinReviewDO::getAsin, asin)
                .eq(AsinReviewDO::getTaskId, taskId)
                .orderByDesc(AsinReviewDO::getRecordDate)
                .last("LIMIT 1"));
            
            Integer cumulativeTotal = yesterdayReviews; // 默认值
            if (latestRecord != null) {
                cumulativeTotal = latestRecord.getReviewNum() + yesterdayReviews;
            }
            
            // 创建今日增量记录
            AsinReviewDO newRecord = new AsinReviewDO();
            newRecord.setAsin(asin);
            newRecord.setTaskId(taskId.intValue());
            newRecord.setReviewNum(cumulativeTotal);        // 累计总数
            newRecord.setReviewIncrement(yesterdayReviews);  // 昨天增量
            newRecord.setRecordDate(today);
            newRecord.setIsInitial(false);
            newRecord.setInitialized(1);
            newRecord.setLastUpdateDate(LocalDateTime.now());
            
            // 设置用户ID（从任务中获取）
            KeywordTaskDO task = keywordTaskMapper.selectById(taskId);
            if (task != null) {
                newRecord.setUserId(task.getUserId());
            }
            
            asinReviewMapper.insert(newRecord);
            
            log.info("创建ASIN [{}] 增量记录成功，昨日新增: {}, 当前总数: {}", 
                asin, yesterdayReviews, cumulativeTotal);
            
            return yesterdayReviews;
            
        } catch (Exception e) {
            log.error("更新ASIN [{}] 增量评论数据失败", asin, e);
            return -1;
        }
    }

    @Override
    public AsinReviewDO getByAsinAndTaskId(String asin, Long taskId) {
        if (StrUtil.isBlank(asin) || taskId == null) {
            return null;
        }
        // 查询该ASIN的最新记录（按日期倒序）
        return asinReviewMapper.selectOne(new LambdaQueryWrapperX<AsinReviewDO>()
                .eq(AsinReviewDO::getAsin, asin)
                .eq(AsinReviewDO::getTaskId, taskId)
                .orderByDesc(AsinReviewDO::getRecordDate)
                .last("LIMIT 1"));
    }

    @Override
    public List<AsinReviewDO> getIncrementsByAsinAndDateRange(String asin, Long taskId, LocalDate startDate, LocalDate endDate) {
        if (StrUtil.isBlank(asin) || taskId == null || startDate == null || endDate == null) {
            return Collections.emptyList();
        }
        return asinReviewMapper.selectIncrementsByAsinAndDateRange(asin, taskId, startDate, endDate);
    }

    @Override
    public AsinReviewAnalysisRespVO getReviewAnalysisData(AsinReviewAnalysisReqVO reqVO) {
        if (reqVO == null || reqVO.getTaskId() == null || reqVO.getStartDate() == null || reqVO.getEndDate() == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }

        log.info("开始获取任务 [{}] 在日期范围 [{} - {}] 的评论统计分析数据", 
            reqVO.getTaskId(), reqVO.getStartDate(), reqVO.getEndDate());

        // 1. 查询数据库获取原始统计数据
        List<AsinReviewStatItemVO> rawData = asinReviewMapper.selectReviewStatsByTaskAndDateRange(
            reqVO.getTaskId(), reqVO.getStartDate(), reqVO.getEndDate());

        log.info("查询到 {} 条原始统计数据", rawData.size());

        // 2. 转换为嵌套Map结构
        Map<String, Map<String, Integer>> analysisData = new HashMap<>();
        Set<String> asinSet = new HashSet<>();
        Set<String> dateSet = new HashSet<>();
        int totalCount = 0;

        for (AsinReviewStatItemVO item : rawData) {
            String asin = item.getAsin();
            String date = item.getReviewDate();
            Integer count = item.getReviewCount() != null ? item.getReviewCount() : 0;

            // 构建嵌套Map结构
            analysisData.computeIfAbsent(asin, k -> new HashMap<>()).put(date, count);
            asinSet.add(asin);
            dateSet.add(date);
            totalCount += count;
        }

        // 3. 构建响应数据
        AsinReviewAnalysisRespVO response = new AsinReviewAnalysisRespVO();
        response.setAnalysisData(analysisData);
        response.setAsinList(new ArrayList<>(asinSet));
        response.setDateList(new ArrayList<>(dateSet));
        response.setTotalCount(totalCount);
        response.setTaskId(reqVO.getTaskId());
        response.setDateRange(reqVO.getStartDate() + " 至 " + reqVO.getEndDate());

        log.info("统计分析完成，共 {} 个ASIN，{} 个日期，总评论数: {}", 
            asinSet.size(), dateSet.size(), totalCount);

        return response;
    }
}