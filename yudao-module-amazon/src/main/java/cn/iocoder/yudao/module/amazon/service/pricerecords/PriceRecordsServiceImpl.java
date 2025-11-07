package cn.iocoder.yudao.module.amazon.service.pricerecords;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import cn.iocoder.yudao.module.amazon.controller.admin.pricerecords.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.pricerecords.PriceRecordsDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.iocoder.yudao.module.amazon.dal.mysql.pricerecords.PriceRecordsMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.amazon.enums.ErrorCodeConstants.*;

/**
 * 亚马逊产品价格记录 Service 实现类
 *
 * @author Demons
 */
@Service
@Validated
public class PriceRecordsServiceImpl extends ServiceImpl<PriceRecordsMapper, PriceRecordsDO> implements PriceRecordsService {

    @Resource
    private PriceRecordsMapper priceRecordsMapper;

    @Override
    public Long createPriceRecords(PriceRecordsSaveReqVO createReqVO) {
        // 插入
        PriceRecordsDO priceRecords = BeanUtils.toBean(createReqVO, PriceRecordsDO.class);
        priceRecordsMapper.insert(priceRecords);

        // 返回
        return priceRecords.getId();
    }

    @Override
    public void updatePriceRecords(PriceRecordsSaveReqVO updateReqVO) {
        // 校验存在
        validatePriceRecordsExists(updateReqVO.getId());
        // 更新
        PriceRecordsDO updateObj = BeanUtils.toBean(updateReqVO, PriceRecordsDO.class);
        priceRecordsMapper.updateById(updateObj);
    }

    @Override
    public void deletePriceRecords(Long id) {
        // 校验存在
        validatePriceRecordsExists(id);
        // 删除
        priceRecordsMapper.deleteById(id);
    }

    @Override
        public void deletePriceRecordsListByIds(List<Long> ids) {
        // 删除
        priceRecordsMapper.deleteByIds(ids);
        }


    private void validatePriceRecordsExists(Long id) {
        if (priceRecordsMapper.selectById(id) == null) {
            throw exception(PRICE_RECORDS_NOT_EXISTS);
        }
    }

    @Override
    public PriceRecordsDO getPriceRecords(Long id) {
        return priceRecordsMapper.selectById(id);
    }

    @Override
    public PageResult<PriceRecordsDO> getPriceRecordsPage(PriceRecordsPageReqVO pageReqVO) {
        return priceRecordsMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchSavePriceRecords(PriceRecordsBatchSaveReqVO batchSaveReqVO) {
        if (CollUtil.isEmpty(batchSaveReqVO.getRecords())) {
            return 0;
        }

        List<PriceRecordsBatchSaveReqVO.PriceRecordItem> records = batchSaveReqVO.getRecords();
        
        // 构建查询参数
        List<Map<String, Object>> queryPairs = records.stream()
                .map(item -> {
                    Map<String, Object> pair = new HashMap<>();
                    pair.put("asin", item.getAsin());
                    pair.put("scrapeDate", item.getScrapeDate());
                    return pair;
                })
                .collect(Collectors.toList());

        // 批量查询已存在的记录
        // 假设所有记录的userId相同，取第一个记录的userId
        Long userId = records.get(0).getUserId();
        List<PriceRecordsDO> existingRecords = priceRecordsMapper.selectByAsinAndScrapeDateBatch(queryPairs, userId);
        
        // 创建已存在记录的索引 (ASIN + ScrapeDate 作为key)
        Map<String, PriceRecordsDO> existingMap = existingRecords.stream()
                .collect(Collectors.toMap(
                        record -> buildKey(record.getAsin(), record.getScrapeDate()),
                        record -> record
                ));

        // 分离需要更新和需要插入的记录
        List<PriceRecordsDO> toUpdate = new ArrayList<>();
        List<PriceRecordsDO> toInsert = new ArrayList<>();

        for (PriceRecordsBatchSaveReqVO.PriceRecordItem item : records) {
            String key = buildKey(item.getAsin(), item.getScrapeDate());
            PriceRecordsDO existingRecord = existingMap.get(key);
            
            if (existingRecord != null) {
                // 更新已存在的记录，保留原有的title和imageUrl
                PriceRecordsDO updateRecord = buildPriceRecordDO(item);
                updateRecord.setId(existingRecord.getId());
                updateRecord.setTitle(existingRecord.getTitle());      // 保留原有标题
                updateRecord.setImageUrl(existingRecord.getImageUrl()); // 保留原有图片
                toUpdate.add(updateRecord);
            } else {
                // 新增记录
                PriceRecordsDO newRecord = buildPriceRecordDO(item);
                toInsert.add(newRecord);
            }
        }

        // 批量更新
        if (!toUpdate.isEmpty()) {
            this.updateBatchById(toUpdate);
        }

        // 批量插入
        if (!toInsert.isEmpty()) {
            this.saveBatch(toInsert);
        }

        return toUpdate.size() + toInsert.size();
    }

    /**
     * 构建唯一键
     */
    private String buildKey(String asin, LocalDate scrapeDate) {
        return asin + "_" + scrapeDate.toString();
    }

    /**
     * 构建PriceRecordsDO对象
     */
    private PriceRecordsDO buildPriceRecordDO(PriceRecordsBatchSaveReqVO.PriceRecordItem item) {
        PriceRecordsDO record = new PriceRecordsDO();
        record.setAsin(item.getAsin());
        record.setSpu(item.getSpu());
        record.setPrice(item.getPrice());
        record.setPrimePrice(item.getPrimePrice());
        record.setCouponDiscount(item.getCouponDiscount());
        record.setCouponPrice(item.getCouponPrice());
        record.setCurrency(item.getCurrency());
        record.setAvailability(item.getAvailability());
        record.setFrequentlyReturned(item.getFrequentlyReturned());
        record.setScrapeDate(item.getScrapeDate());
        record.setUserId(item.getUserId());
        return record;
    }

}