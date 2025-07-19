package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 评论统计数据DTO - 对应领星评价统计API返回数据
 * 
 * @author 芋道源码
 */
@Data
public class ReviewReportDTO {

    /**
     * 日期
     */
    @JsonProperty("report_date")
    private String reportDate;

    /**
     * review新增数
     */
    @JsonProperty("review_num")
    private Integer reviewNum;

    /**
     * 5星review新增数
     */
    @JsonProperty("five_star")
    private Integer fiveStar;

    /**
     * 4星review新增数
     */
    @JsonProperty("four_star")
    private Integer fourStar;

    /**
     * 3星review新增数
     */
    @JsonProperty("three_star")
    private Integer threeStar;

    /**
     * 2星review新增数
     */
    @JsonProperty("two_star")
    private Integer twoStar;

    /**
     * 1星review新增数
     */
    @JsonProperty("one_star")
    private Integer oneStar;

    /**
     * rating总数
     */
    @JsonProperty("ratings")
    private Integer ratings;

    /**
     * 评分
     */
    @JsonProperty("score")
    private Double score;

    /**
     * rating新增数
     * 备注：可以为负值，代表减少数
     */
    @JsonProperty("ratings_inc")
    private Integer ratingsInc;
}