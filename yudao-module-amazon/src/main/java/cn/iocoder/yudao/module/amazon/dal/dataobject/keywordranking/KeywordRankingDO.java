package cn.iocoder.yudao.module.amazon.dal.dataobject.keywordranking;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 关键词排名任务结果 DO
 *
 * @author 芋道源码
 */
@TableName("amazon_keyword_ranking")
@KeySequence("amazon_keyword_ranking_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordRankingDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 任务ID
     */
    private Long taskId;
    /**
     * 关键词
     */
    private String keyword;
    /**
     * 关键词的哈希值
     */
    private String keywordHash;
    /**
     * ASIN
     */
    private String asin;
    /**
     * 搜索城市/地区
     */
    private String city;
    /**
     * 排名类型
     */
    private Short rankType;
    /**
     * 整体排名位置
     */
    private Integer rankPosition;
    /**
     * 搜索结果页码
     */
    private Integer pageNumber;
    /**
     * 页内位置
     */
    private Integer positionInPage;
    /**
     * 广告类型
     */
    private Short adsType;
    /**
     * 是否为付费广告位
     */
    private Boolean isSponsored;
    /**
     * 采集时间
     */
    private LocalDateTime crawlTime;
    /**
     * 爬取日期
     */
    private LocalDate crawlDate;
    /**
     * 备注信息
     */
    private String remark;


}