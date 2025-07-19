package cn.iocoder.yudao.module.amazon.service.keywordranking;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import cn.iocoder.yudao.module.amazon.controller.admin.keywordranking.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordranking.KeywordRankingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.amazon.dal.mysql.keywordranking.KeywordRankingMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.module.amazon.enums.ErrorCodeConstants.*;

/**
 * 关键词排名任务结果 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class KeywordRankingServiceImpl implements KeywordRankingService {

    @Resource
    private KeywordRankingMapper keywordRankingMapper;

    @Override
    public Long createKeywordRanking(KeywordRankingSaveReqVO createReqVO) {
        // 插入
        KeywordRankingDO keywordRanking = BeanUtils.toBean(createReqVO, KeywordRankingDO.class);
        keywordRankingMapper.insert(keywordRanking);

        // 返回
        return keywordRanking.getId();
    }

    @Override
    public void updateKeywordRanking(KeywordRankingSaveReqVO updateReqVO) {
        // 校验存在
        validateKeywordRankingExists(updateReqVO.getId());
        // 更新
        KeywordRankingDO updateObj = BeanUtils.toBean(updateReqVO, KeywordRankingDO.class);
        keywordRankingMapper.updateById(updateObj);
    }

    @Override
    public void deleteKeywordRanking(Long id) {
        // 校验存在
        validateKeywordRankingExists(id);
        // 删除
        keywordRankingMapper.deleteById(id);
    }

    @Override
        public void deleteKeywordRankingListByIds(List<Long> ids) {
        // 删除
        keywordRankingMapper.deleteByIds(ids);
        }


    private void validateKeywordRankingExists(Long id) {
        if (keywordRankingMapper.selectById(id) == null) {
            throw exception(KEYWORD_RANKING_NOT_EXISTS);
        }
    }

    @Override
    public KeywordRankingDO getKeywordRanking(Long id) {
        return keywordRankingMapper.selectById(id);
    }

    @Override
    public PageResult<KeywordRankingDO> getKeywordRankingPage(KeywordRankingPageReqVO pageReqVO) {
        return keywordRankingMapper.selectPage(pageReqVO);
    }

    @Override
    public KeywordRankingAnalysisRespVO getKeywordRankingAnalysis(KeywordRankingAnalysisReqVO reqVO) {
        // 设置默认时间范围
        LocalDate startDate = reqVO.getStartDate();
        LocalDate endDate = reqVO.getEndDate();
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(6); // 默认7天前
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // 查询数据 - 基于taskId范围查询
        List<KeywordRankingDO> rawData = queryAnalysisData(reqVO, startDate, endDate);

        // 构建响应
        KeywordRankingAnalysisRespVO response = new KeywordRankingAnalysisRespVO();
        
        // 构建汇总信息
        response.setSummary(buildSummaryInfo(reqVO, startDate, endDate, rawData));
        
        // 构建矩阵数据
        response.setMatrix(buildMatrixData(rawData, startDate, endDate, reqVO));
        
        // 构建统计信息
        response.setStatistics(buildStatisticsData(rawData, reqVO));
        
        // 构建对比分析（如果是对比模式）
        if ("comparison".equals(reqVO.getDisplayMode())) {
            response.setComparison(buildComparisonAnalysis(rawData, reqVO));
        }

        return response;
    }

    private List<KeywordRankingDO> queryAnalysisData(KeywordRankingAnalysisReqVO reqVO, 
                                                    LocalDate startDate, LocalDate endDate) {
        String aggregationType = reqVO.getAggregationType();
        
        // 先获取所有符合条件的数据 - 基于taskId查询
        List<KeywordRankingDO> allData = keywordRankingMapper.selectAnalysisDataAll(
                reqVO.getCity(), reqVO.getRankType(),
                startDate, endDate, reqVO.getTaskId(), reqVO.getKeywords(), reqVO.getMaxPosition());
        
        // 根据聚合类型处理数据
        return switch (aggregationType) {
            case "first" -> filterFirstData(allData);
            case "all" -> allData;
            default -> filterLatestData(allData);
        };
    }

    /**
     * 过滤获取最新数据（每个位置每天最新的一条）
     */
    private List<KeywordRankingDO> filterLatestData(List<KeywordRankingDO> allData) {
        Map<String, KeywordRankingDO> latestDataMap = new HashMap<>();
        
        for (KeywordRankingDO data : allData) {
            String key = data.getKeyword() + "-" + data.getPageNumber() + "-" + 
                        data.getPositionInPage() + "-" + data.getCrawlDate();
            
            KeywordRankingDO existing = latestDataMap.get(key);
            if (existing == null || data.getCrawlTime().isAfter(existing.getCrawlTime())) {
                latestDataMap.put(key, data);
            }
        }
        
        return new ArrayList<>(latestDataMap.values());
    }

    /**
     * 过滤获取最早数据（每个位置每天最早的一条）
     */
    private List<KeywordRankingDO> filterFirstData(List<KeywordRankingDO> allData) {
        Map<String, KeywordRankingDO> firstDataMap = new HashMap<>();
        
        for (KeywordRankingDO data : allData) {
            String key = data.getKeyword() + "-" + data.getPageNumber() + "-" + 
                        data.getPositionInPage() + "-" + data.getCrawlDate();
            
            KeywordRankingDO existing = firstDataMap.get(key);
            if (existing == null || data.getCrawlTime().isBefore(existing.getCrawlTime())) {
                firstDataMap.put(key, data);
            }
        }
        
        return new ArrayList<>(firstDataMap.values());
    }

    private KeywordRankingAnalysisRespVO.SummaryInfo buildSummaryInfo(KeywordRankingAnalysisReqVO reqVO, 
                                                                     LocalDate startDate, LocalDate endDate,
                                                                     List<KeywordRankingDO> rawData) {
        KeywordRankingAnalysisRespVO.SummaryInfo summary = new KeywordRankingAnalysisRespVO.SummaryInfo();
        
        // 生成日期范围
        List<LocalDate> dateRange = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dateRange.add(current);
            current = current.plusDays(1);
        }
        
        summary.setDateRange(dateRange);
        summary.setTotalDays(dateRange.size());
        summary.setTotalPositions(reqVO.getMaxPosition());
        summary.setCity(reqVO.getCity());
        summary.setRankType(reqVO.getRankType());
        
        // 获取实际的关键词列表
        Set<String> actualKeywords = rawData.stream()
                .map(KeywordRankingDO::getKeyword)
                .collect(Collectors.toSet());
        summary.setKeywords(new ArrayList<>(actualKeywords));
        
        return summary;
    }

    private KeywordRankingAnalysisRespVO.MatrixData buildMatrixData(List<KeywordRankingDO> rawData, 
                                                                   LocalDate startDate, LocalDate endDate,
                                                                   KeywordRankingAnalysisReqVO reqVO) {
        KeywordRankingAnalysisRespVO.MatrixData matrixData = new KeywordRankingAnalysisRespVO.MatrixData();
        
        // 生成表头
        List<String> headers = new ArrayList<>();
        headers.add("位置");
        LocalDate current = startDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M月d日");
        while (!current.isAfter(endDate)) {
            headers.add(current.format(formatter));
            current = current.plusDays(1);
        }
        matrixData.setHeaders(headers);
        
        // 按关键词分组构建矩阵
        Map<String, List<KeywordRankingDO>> keywordGroups = rawData.stream()
                .collect(Collectors.groupingBy(KeywordRankingDO::getKeyword));
        
        Map<String, KeywordRankingAnalysisRespVO.KeywordMatrix> keywordMatrices = new HashMap<>();
        
        for (Map.Entry<String, List<KeywordRankingDO>> entry : keywordGroups.entrySet()) {
            String keyword = entry.getKey();
            List<KeywordRankingDO> keywordData = entry.getValue();
            
            KeywordRankingAnalysisRespVO.KeywordMatrix keywordMatrix = buildKeywordMatrix(keyword, keywordData, startDate, endDate, reqVO.getMaxPosition());
            keywordMatrices.put(keyword, keywordMatrix);
        }
        
        matrixData.setKeywordMatrices(keywordMatrices);
        return matrixData;
    }

    private KeywordRankingAnalysisRespVO.KeywordMatrix buildKeywordMatrix(String keyword, 
                                                                         List<KeywordRankingDO> keywordData,
                                                                         LocalDate startDate, LocalDate endDate,
                                                                         Integer maxPosition) {
        KeywordRankingAnalysisRespVO.KeywordMatrix matrix = new KeywordRankingAnalysisRespVO.KeywordMatrix();
        matrix.setKeyword(keyword);
        
        // 构建位置到数据的映射
        Map<String, Map<LocalDate, String>> positionDateAsinMap = new HashMap<>();
        
        for (KeywordRankingDO data : keywordData) {
            String positionKey = data.getPageNumber() + "-" + data.getPositionInPage();
            positionDateAsinMap.computeIfAbsent(positionKey, k -> new HashMap<>())
                    .put(data.getCrawlDate(), data.getAsin());
        }
        
        // 生成矩阵行
        List<KeywordRankingAnalysisRespVO.MatrixRow> rows = new ArrayList<>();
        
        // 按位置排序
        List<String> sortedPositions = positionDateAsinMap.keySet().stream()
                .sorted((p1, p2) -> {
                    String[] parts1 = p1.split("-");
                    String[] parts2 = p2.split("-");
                    int page1 = Integer.parseInt(parts1[0]);
                    int pos1 = Integer.parseInt(parts1[1]);
                    int page2 = Integer.parseInt(parts2[0]);
                    int pos2 = Integer.parseInt(parts2[1]);
                    
                    if (page1 != page2) {
                        return Integer.compare(page1, page2);
                    }
                    return Integer.compare(pos1, pos2);
                })
                .collect(Collectors.toList());
        
        for (String positionKey : sortedPositions) {
            String[] parts = positionKey.split("-");
            int pageNumber = Integer.parseInt(parts[0]);
            int positionInPage = Integer.parseInt(parts[1]);
            
            // 检查是否超过最大位置限制
            int overallPosition = (pageNumber - 1) * 16 + positionInPage;
            if (maxPosition != null && overallPosition > maxPosition) {
                continue;
            }
            
            KeywordRankingAnalysisRespVO.MatrixRow row = new KeywordRankingAnalysisRespVO.MatrixRow();
            row.setPosition(String.format("P%d-%02d", pageNumber, positionInPage));
            row.setPositionCode(positionKey);
            row.setPageNumber(pageNumber);
            row.setPositionInPage(positionInPage);
            
            // 构建日期数据
            List<String> dateData = new ArrayList<>();
            LocalDate current = startDate;
            Map<LocalDate, String> dateAsinMap = positionDateAsinMap.get(positionKey);
            
            while (!current.isAfter(endDate)) {
                String asin = dateAsinMap.get(current);
                dateData.add(asin != null ? asin : "");
                current = current.plusDays(1);
            }
            
            row.setData(dateData);
            rows.add(row);
        }
        
        matrix.setRows(rows);
        return matrix;
    }

    private KeywordRankingAnalysisRespVO.StatisticsData buildStatisticsData(List<KeywordRankingDO> rawData,
                                                                           KeywordRankingAnalysisReqVO reqVO) {
        KeywordRankingAnalysisRespVO.StatisticsData statistics = new KeywordRankingAnalysisRespVO.StatisticsData();
        
        // ASIN频次统计
        Map<String, Integer> asinFrequency = rawData.stream()
                .filter(data -> StrUtil.isNotBlank(data.getAsin()))
                .collect(Collectors.groupingBy(
                        KeywordRankingDO::getAsin,
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
        statistics.setAsinFrequency(asinFrequency);
        
        // 位置稳定性计算
        Map<String, Double> positionStability = calculatePositionStability(rawData);
        statistics.setPositionStability(positionStability);
        
        // 关键词覆盖度
        Map<String, KeywordRankingAnalysisRespVO.KeywordCoverage> keywordCoverage = calculateKeywordCoverage(rawData, reqVO.getMaxPosition());
        statistics.setKeywordCoverage(keywordCoverage);
        
        return statistics;
    }

    private Map<String, Double> calculatePositionStability(List<KeywordRankingDO> rawData) {
        Map<String, Double> stabilityMap = new HashMap<>();
        
        // 按位置分组
        Map<String, List<KeywordRankingDO>> positionGroups = rawData.stream()
                .collect(Collectors.groupingBy(data -> 
                        String.format("P%d-%02d", data.getPageNumber(), data.getPositionInPage())));
        
        for (Map.Entry<String, List<KeywordRankingDO>> entry : positionGroups.entrySet()) {
            String position = entry.getKey();
            List<KeywordRankingDO> positionData = entry.getValue();
            
            if (positionData.isEmpty()) {
                stabilityMap.put(position, 0.0);
                continue;
            }
            
            // 计算最频繁的ASIN占比
            Map<String, Long> asinCounts = positionData.stream()
                    .filter(data -> StrUtil.isNotBlank(data.getAsin()))
                    .collect(Collectors.groupingBy(KeywordRankingDO::getAsin, Collectors.counting()));
            
            if (asinCounts.isEmpty()) {
                stabilityMap.put(position, 0.0);
                continue;
            }
            
            long maxCount = asinCounts.values().stream().mapToLong(Long::longValue).max().orElse(0);
            double stabilityScore = (double) maxCount / positionData.size();
            stabilityMap.put(position, Math.round(stabilityScore * 100.0) / 100.0);
        }
        
        // 按照页面位置顺序排序返回LinkedHashMap
        return stabilityMap.entrySet().stream()
                .sorted((entry1, entry2) -> {
                    String pos1 = entry1.getKey();
                    String pos2 = entry2.getKey();
                    
                    // 从位置字符串中解析页码和页内位置
                    String[] parts1 = pos1.replace("P", "").split("-");
                    String[] parts2 = pos2.replace("P", "").split("-");
                    
                    int page1 = Integer.parseInt(parts1[0]);
                    int pagePos1 = Integer.parseInt(parts1[1]);
                    int page2 = Integer.parseInt(parts2[0]);
                    int pagePos2 = Integer.parseInt(parts2[1]);
                    
                    // 先按页码排序，再按页内位置排序
                    if (page1 != page2) {
                        return Integer.compare(page1, page2);
                    }
                    return Integer.compare(pagePos1, pagePos2);
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    private Map<String, KeywordRankingAnalysisRespVO.KeywordCoverage> calculateKeywordCoverage(List<KeywordRankingDO> rawData, Integer maxPosition) {
        Map<String, KeywordRankingAnalysisRespVO.KeywordCoverage> coverage = new HashMap<>();
        
        // 按关键词分组
        Map<String, List<KeywordRankingDO>> keywordGroups = rawData.stream()
                .collect(Collectors.groupingBy(KeywordRankingDO::getKeyword));
        
        for (Map.Entry<String, List<KeywordRankingDO>> entry : keywordGroups.entrySet()) {
            String keyword = entry.getKey();
            List<KeywordRankingDO> keywordData = entry.getValue();
            
            // 计算覆盖的位置数
            Set<String> coveredPositions = keywordData.stream()
                    .map(data -> data.getPageNumber() + "-" + data.getPositionInPage())
                    .collect(Collectors.toSet());
            
            KeywordRankingAnalysisRespVO.KeywordCoverage keywordCoverage = new KeywordRankingAnalysisRespVO.KeywordCoverage();
            keywordCoverage.setKeyword(keyword);
            keywordCoverage.setCoveredPositions(coveredPositions.size());
            keywordCoverage.setTotalPositions(maxPosition != null ? maxPosition : 50);
            keywordCoverage.setCoverageRate((double) coveredPositions.size() / (maxPosition != null ? maxPosition : 50));
            
            coverage.put(keyword, keywordCoverage);
        }
        
        return coverage;
    }

    private KeywordRankingAnalysisRespVO.ComparisonAnalysis buildComparisonAnalysis(List<KeywordRankingDO> rawData,
                                                                                   KeywordRankingAnalysisReqVO reqVO) {
        KeywordRankingAnalysisRespVO.ComparisonAnalysis comparison = new KeywordRankingAnalysisRespVO.ComparisonAnalysis();
        
        // 位置竞争分析
        List<KeywordRankingAnalysisRespVO.PositionCompetition> positionCompetitions = buildPositionCompetitions(rawData);
        comparison.setPositionCompetitions(positionCompetitions);
        
        // ASIN覆盖度分析
        Map<String, KeywordRankingAnalysisRespVO.AsinCoverage> asinCoverages = buildAsinCoverages(rawData);
        comparison.setAsinCoverages(asinCoverages);
        
        return comparison;
    }

    private List<KeywordRankingAnalysisRespVO.PositionCompetition> buildPositionCompetitions(List<KeywordRankingDO> rawData) {
        // 按位置和日期分组
        Map<String, Map<LocalDate, List<KeywordRankingDO>>> positionDateGroups = rawData.stream()
                .collect(Collectors.groupingBy(
                        data -> String.format("P%d-%02d", data.getPageNumber(), data.getPositionInPage()),
                        Collectors.groupingBy(KeywordRankingDO::getCrawlDate)
                ));
        
        List<KeywordRankingAnalysisRespVO.PositionCompetition> competitions = new ArrayList<>();
        
        for (Map.Entry<String, Map<LocalDate, List<KeywordRankingDO>>> positionEntry : positionDateGroups.entrySet()) {
            String position = positionEntry.getKey();
            Map<LocalDate, List<KeywordRankingDO>> dateGroups = positionEntry.getValue();
            
            KeywordRankingAnalysisRespVO.PositionCompetition competition = new KeywordRankingAnalysisRespVO.PositionCompetition();
            competition.setPosition(position);
            competition.setDates(new ArrayList<>(dateGroups.keySet()));
            
            List<KeywordRankingAnalysisRespVO.CompetitionDetail> competitionDetails = new ArrayList<>();
            
            for (Map.Entry<LocalDate, List<KeywordRankingDO>> dateEntry : dateGroups.entrySet()) {
                LocalDate date = dateEntry.getKey();
                List<KeywordRankingDO> dayData = dateEntry.getValue();
                
                // 分析同一天不同关键词的ASIN
                Map<String, String> keywordAsinMap = dayData.stream()
                        .collect(Collectors.toMap(
                                KeywordRankingDO::getKeyword,
                                KeywordRankingDO::getAsin,
                                (existing, replacement) -> existing
                        ));
                
                // 找出相同和不同的ASIN
                Set<String> allAsins = new HashSet<>(keywordAsinMap.values());
                List<String> sameAsins = new ArrayList<>();
                Map<String, String> differentAsins = new HashMap<>();
                
                if (allAsins.size() == 1) {
                    sameAsins.addAll(allAsins);
                } else {
                    differentAsins.putAll(keywordAsinMap);
                }
                
                KeywordRankingAnalysisRespVO.CompetitionDetail detail = new KeywordRankingAnalysisRespVO.CompetitionDetail();
                detail.setDate(date);
                detail.setSameAsins(sameAsins);
                detail.setDifferentAsins(differentAsins);
                
                competitionDetails.add(detail);
            }
            
            competition.setCompetitions(competitionDetails);
            competitions.add(competition);
        }
        
        return competitions;
    }

    private Map<String, KeywordRankingAnalysisRespVO.AsinCoverage> buildAsinCoverages(List<KeywordRankingDO> rawData) {
        Map<String, KeywordRankingAnalysisRespVO.AsinCoverage> coverages = new HashMap<>();
        
        // 按ASIN分组
        Map<String, List<KeywordRankingDO>> asinGroups = rawData.stream()
                .filter(data -> StrUtil.isNotBlank(data.getAsin()))
                .collect(Collectors.groupingBy(KeywordRankingDO::getAsin));
        
        for (Map.Entry<String, List<KeywordRankingDO>> entry : asinGroups.entrySet()) {
            String asin = entry.getKey();
            List<KeywordRankingDO> asinData = entry.getValue();
            
            // 计算覆盖的关键词和位置
            Set<String> keywords = asinData.stream()
                    .map(KeywordRankingDO::getKeyword)
                    .collect(Collectors.toSet());
            
            Set<String> positions = asinData.stream()
                    .map(data -> String.format("P%d-%02d", data.getPageNumber(), data.getPositionInPage()))
                    .collect(Collectors.toSet());
            
            // 计算平均排名
            double averagePosition = asinData.stream()
                    .mapToInt(data -> (data.getPageNumber() - 1) * 16 + data.getPositionInPage())
                    .average()
                    .orElse(0.0);
            
            // 计算稳定性评分（基于位置变化的标准差）
            double stabilityScore = calculateStabilityScore(asinData);
            
            KeywordRankingAnalysisRespVO.AsinCoverage coverage = new KeywordRankingAnalysisRespVO.AsinCoverage();
            coverage.setAsin(asin);
            coverage.setKeywords(new ArrayList<>(keywords));
            coverage.setPositions(new ArrayList<>(positions));
            coverage.setCoverageRate((double) keywords.size() / Math.max(1, rawData.stream()
                    .map(KeywordRankingDO::getKeyword)
                    .collect(Collectors.toSet()).size()));
            coverage.setAveragePosition(Math.round(averagePosition * 100.0) / 100.0);
            coverage.setStabilityScore(Math.round(stabilityScore * 100.0) / 100.0);
            
            coverages.put(asin, coverage);
        }
        
        return coverages;
    }

    private double calculateStabilityScore(List<KeywordRankingDO> asinData) {
        if (asinData.size() <= 1) {
            return 1.0;
        }
        
        List<Integer> positions = asinData.stream()
                .mapToInt(data -> (data.getPageNumber() - 1) * 16 + data.getPositionInPage())
                .boxed()
                .collect(Collectors.toList());
        
        double mean = positions.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        double variance = positions.stream()
                .mapToDouble(pos -> Math.pow(pos - mean, 2))
                .average()
                .orElse(0.0);
        
        double stdDev = Math.sqrt(variance);
        
        // 将标准差转换为0-1的稳定性评分，标准差越小稳定性越高
        return Math.max(0.0, 1.0 - (stdDev / 50.0)); // 假设50个位置的变化为完全不稳定
    }

}