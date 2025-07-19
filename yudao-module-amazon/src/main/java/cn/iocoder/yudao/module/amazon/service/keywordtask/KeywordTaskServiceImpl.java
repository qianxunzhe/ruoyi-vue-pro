package cn.iocoder.yudao.module.amazon.service.keywordtask;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;

import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonListingDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.listingprice.ListingPriceDO;
import cn.iocoder.yudao.module.amazon.service.lingXingAPI.LingXingApiService;
import cn.iocoder.yudao.module.amazon.service.listingprice.ListingPriceService;
import cn.iocoder.yudao.module.amazon.service.asinreview.AsinReviewService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import cn.iocoder.yudao.module.amazon.controller.admin.keywordtask.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordtask.KeywordTaskDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.amazon.dal.mysql.keywordtask.KeywordTaskMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.module.amazon.enums.ErrorCodeConstants.*;

/**
 * 关键词排名任务 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class KeywordTaskServiceImpl implements KeywordTaskService {

    @Resource
    private KeywordTaskMapper keywordTaskMapper;

    @Value("${amazon.keyword-task-url}")
    private String AMAZON_KEYWODTASK_URL;

    @Value("${amazon.listing-price-task-url}")
    private String AMAZON_LISTING_PRICE_TASK_URL;
    @Autowired
    private LingXingApiService lingXingApiService;
    @Autowired
    private ListingPriceService listingPriceService;
    @Autowired
    private AsinReviewService asinReviewService;

    private List<Long> SID_LIST = List.of(
            9366L, 4970L, 10045L, 10049L, 10046L, 4975L,
            4980L, 4982L, 10493L, 4986L, 4983L, 4965L,
            4977L, 4972L, 9100L, 4985L, 10565L, 4966L,
            9087L, 4976L, 10549L
    );

    @Override
    public Long createKeywordTask(KeywordTaskSaveReqVO createReqVO) {
        // 插入
        KeywordTaskDO keywordTask = BeanUtils.toBean(createReqVO, KeywordTaskDO.class);
        keywordTaskMapper.insert(keywordTask);


        if (keywordTask.getTaskType() == 1) {

            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("task_id", keywordTask.getId().toString());

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json;charset=UTF-8");
            headers.put("Accept", "application/json;charset=UTF-8");
            StringBuilder result = new StringBuilder();
            if (keywordTask.getScraperType() == 1) {
               String responseStr =  HttpUtils.post(AMAZON_KEYWODTASK_URL, headers, JsonUtils.toJsonString(paramMap));
               result.append(responseStr);
            }
            if (keywordTask.getScraperType() == 2) {
               Boolean res = this.handelPriceTaskByTaskId(keywordTask.getId());
               result.append(res);
            }
            if (keywordTask.getScraperType() == 4) {
                String resStr = this.handleSingleReviewTask(keywordTask.getId());
                result.append(resStr);
            }
            log.info("<OK>[{}]<OK>", result);
        }

        // 返回
        return keywordTask.getId();
    }

    @Override
    public void updateKeywordTask(KeywordTaskSaveReqVO updateReqVO) {
        // 校验存在
        validateKeywordTaskExists(updateReqVO.getId());
        // 更新
        KeywordTaskDO updateObj = BeanUtils.toBean(updateReqVO, KeywordTaskDO.class);
        keywordTaskMapper.updateById(updateObj);
    }

    @Override
    public void updateKeywordTaskStatus(@Valid KeywordTaskUpdateStatusReqVO updateStatusReqVO) {
        // 校验存在
        validateKeywordTaskExists(updateStatusReqVO.getId());
//        KeywordTaskDO updateObj = new KeywordTaskDO();
//        updateObj.setEnabled(updateStatusReqVO.getEnabled());
//        UpdateWrapper<KeywordTaskDO> whereWrapper = new UpdateWrapper<>();
//        whereWrapper.eq("id", updateStatusReqVO.getId());
//        keywordTaskMapper.update(updateObj, whereWrapper);

        KeywordTaskDO keywordTaskDO = keywordTaskMapper.selectById(updateStatusReqVO.getId());
        keywordTaskDO.setEnabled(updateStatusReqVO.getEnabled());
        keywordTaskMapper.updateById(keywordTaskDO);
    }



    @Override
    public void deleteKeywordTask(Long id) {
        // 校验存在
        validateKeywordTaskExists(id);
        // 删除
        keywordTaskMapper.deleteById(id);
    }

    @Override
        public void deleteKeywordTaskListByIds(List<Long> ids) {
        // 删除
        keywordTaskMapper.deleteByIds(ids);
        }


    private void validateKeywordTaskExists(Long id) {
        if (keywordTaskMapper.selectById(id) == null) {
            throw exception(KEYWORD_TASK_NOT_EXISTS);
        }
    }

    @Override
    public KeywordTaskDO getKeywordTask(Long id) {
        return keywordTaskMapper.selectById(id);
    }

    @Override
    public PageResult<KeywordTaskDO> getKeywordTaskPage(KeywordTaskPageReqVO pageReqVO) {
        return keywordTaskMapper.selectPage(pageReqVO);
    }


    @Override
    public Boolean handelPriceTaskByTaskId(Long id) {
        KeywordTaskDO keywordTaskDO = keywordTaskMapper.selectById(id);
        if (Objects.isNull(keywordTaskDO)) {
            return false;
        }
            List<String> asins = keywordTaskDO.getAsins();
            Long taskId = keywordTaskDO.getId();
            if(CollectionUtil.isEmpty(asins)) {
                return true;
            }

            List<ListingPriceDO> priceDOList = new LinkedList<>();
            List<AmazonListingDTO> allAmazonListingList = lingXingApiService.getAllAmazonListingList(asins, SID_LIST);
            allAmazonListingList.forEach(amazonListingDTO -> {
                ListingPriceDO priceDO = new ListingPriceDO();
                priceDO.setTaskId(taskId);
                priceDO.setUserId(keywordTaskDO.getUserId());
                priceDO.setPrice(NumberUtil.toBigDecimal(amazonListingDTO.getPrice()));
                priceDO.setAsin(amazonListingDTO.getAsin());
                priceDO.setListPrice(NumberUtil.toBigDecimal(amazonListingDTO.getListingPrice()));
                priceDO.setLandedPrice(NumberUtil.toBigDecimal(amazonListingDTO.getLandedPrice()));
                priceDOList.add(priceDO);
            });
            //保存数据到数据库
            if (CollectionUtil.isNotEmpty(priceDOList)) {
                listingPriceService.createListingPriceBatch(priceDOList);
            }
        return true;
    }

    @Override
    public String handelPriceTask(){

        KeywordTaskPageReqVO pageReqVO = new KeywordTaskPageReqVO();
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        pageReqVO.setScraperType((short) 2);
        pageReqVO.setTaskType((short) 2);
        pageReqVO.setEnabled((short) 0);
        List<KeywordTaskDO> list = getKeywordTaskPage(pageReqVO).getList();
        if (CollectionUtil.isEmpty(list)) {
            return String.format("未找到对应的价格监控任务");
        }
        AtomicReference<Integer> count = new AtomicReference<>(0);
        list.forEach(keywordTaskDO -> {
            List<String> asins = keywordTaskDO.getAsins();
            Long taskId = keywordTaskDO.getId();
            if(CollectionUtil.isEmpty(asins)) {
                return;
            }

            List<ListingPriceDO> priceDOList = new LinkedList<>();
            List<AmazonListingDTO> allAmazonListingList = lingXingApiService.getAllAmazonListingList(asins, SID_LIST);
            allAmazonListingList.forEach(amazonListingDTO -> {
                ListingPriceDO priceDO = new ListingPriceDO();
                priceDO.setTaskId(taskId);
                priceDO.setUserId(keywordTaskDO.getUserId());
                priceDO.setPrice(NumberUtil.toBigDecimal(amazonListingDTO.getPrice()));
                priceDO.setAsin(amazonListingDTO.getAsin());
                priceDO.setListPrice(NumberUtil.toBigDecimal(amazonListingDTO.getListingPrice()));
                priceDO.setLandedPrice(NumberUtil.toBigDecimal(amazonListingDTO.getLandedPrice()));
                priceDOList.add(priceDO);
            });
            //保存数据到数据库
            if (!priceDOList.isEmpty()) {
                listingPriceService.createListingPriceBatch(priceDOList);
                count.updateAndGet(v -> v + priceDOList.size());
            }
        });

        return String.format("价格定时任务执行， 共 %d 个任务，新增 %d 条记录。",list.size(),  count.get());
    }

    @Override
    public String handleReviewTask() {
        log.info("开始处理ASIN评论监控任务");
        
        // 查询所有启用的评论监控任务 (scraperType=4)
        KeywordTaskPageReqVO pageReqVO = new KeywordTaskPageReqVO();
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        pageReqVO.setScraperType((short) 4); // 评论监控任务
        pageReqVO.setTaskType((short) 2); // 定时任务
        pageReqVO.setEnabled((short) 0); // 启用状态
        
        List<KeywordTaskDO> taskList = getKeywordTaskPage(pageReqVO).getList();
        
        if (CollectionUtil.isEmpty(taskList)) {
            String result = "未找到对应的评论监控任务";
            log.info(result);
            return result;
        }
        
        log.info("找到 {} 个评论监控任务", taskList.size());
        
        int totalTasks = taskList.size();
        int successTasks = 0;
        int errorTasks = 0;
        int totalProcessedAsins = 0;
        
        // 处理每个任务
        for (KeywordTaskDO task : taskList) {
            try {
                log.info("处理任务 [{}] - {}", task.getId(), task.getTaskName());
                
                Map<String, Object> result = asinReviewService.processAsinReviewMonitoring(task.getId());
                
                String status = (String) result.get("status");
                if ("completed".equals(status) || "skipped".equals(status)) {
                    successTasks++;
                    Integer processedCount = (Integer) result.getOrDefault("totalAsins", 0);
                    totalProcessedAsins += processedCount;
                    
                    log.info("任务 [{}] 处理成功: {}", task.getId(), result);
                } else {
                    errorTasks++;
                    log.error("任务 [{}] 处理失败: {}", task.getId(), result);
                }
                
                // 防止API调用过于频繁，任务间隔2秒
                Thread.sleep(2000);
                
            } catch (Exception e) {
                errorTasks++;
                log.error("处理任务 [{}] 时发生异常", task.getId(), e);
            }
        }
        
        String finalResult = String.format("评论监控任务执行完成，共 %d 个任务，成功 %d 个，失败 %d 个，处理 %d 个ASIN", 
            totalTasks, successTasks, errorTasks, totalProcessedAsins);
        
        log.info(finalResult);
        return finalResult;
    }

    /**
     * 处理单个任务的评论监控
     * 
     * @param taskId 任务ID
     * @return 处理结果
     */
    public String handleSingleReviewTask(Long taskId) {
        if (taskId == null) {
            log.error("任务ID不能为空");
            return "任务ID不能为空";
        }
        
        log.info("开始处理单个评论监控任务，任务ID: {}", taskId);
        
        try {
            // 查询指定任务
            KeywordTaskDO task = keywordTaskMapper.selectById(taskId);
            if (task == null) {
                String errorMsg = String.format("任务 [%d] 不存在", taskId);
                log.error(errorMsg);
                return errorMsg;
            }
            
            log.info("找到任务: [{}] - {}", task.getId(), task.getTaskName());
            
            // 处理任务
            Map<String, Object> result = asinReviewService.processAsinReviewMonitoring(task.getId());
            
            String status = (String) result.get("status");
            String finalResult;
            
            if ("completed".equals(status) || "skipped".equals(status)) {
                Integer totalAsins = (Integer) result.getOrDefault("totalAsins", 0);
                Integer initializedCount = (Integer) result.getOrDefault("initializedCount", 0);
                Integer incrementUpdatedCount = (Integer) result.getOrDefault("incrementUpdatedCount", 0);
                Integer errorCount = (Integer) result.getOrDefault("errorCount", 0);
                
                finalResult = String.format("任务 [%d] 处理成功: 总ASIN数=%d, 初始化=%d, 增量更新=%d, 错误=%d", 
                    taskId, totalAsins, initializedCount, incrementUpdatedCount, errorCount);
                
                log.info(finalResult);
            } else {
                finalResult = String.format("任务 [%d] 处理失败: %s", taskId, result);
                log.error(finalResult);
            }
            return finalResult;
        } catch (Exception e) {
            String errorMsg = String.format("处理任务 [%d] 时发生异常: %s", taskId, e.getMessage());
            log.error(errorMsg, e);
            return errorMsg;
        }
    }
}