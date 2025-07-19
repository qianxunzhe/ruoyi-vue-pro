package cn.iocoder.yudao.module.amazon.service.keywordranking;

import cn.iocoder.yudao.module.amazon.dal.mysql.keywordranking.KeywordRankingMapper;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordranking.KeywordRankingDO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 关键词排名Mapper测试
 * 
 * @author ultrathink
 */
@SpringBootTest
@ActiveProfiles("test")
public class KeywordRankingMapperTest {

    @Resource
    private KeywordRankingMapper keywordRankingMapper;

    @Test
    public void testSelectAnalysisDataLatest() {
        try {
            List<KeywordRankingDO> result = keywordRankingMapper.selectAnalysisDataLatest(
                    1L, // userId
                    "New York", // city
                    (short) 1, // rankType
                    LocalDate.now().minusDays(7), // startDate
                    LocalDate.now(), // endDate
                    null, // taskId
                    Arrays.asList("test keyword"), // keywords
                    50 // maxPosition
            );
            
            System.out.println("查询成功，返回数据条数: " + result.size());
            
        } catch (Exception e) {
            System.out.println("查询异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testSelectAnalysisDataFirst() {
        try {
            List<KeywordRankingDO> result = keywordRankingMapper.selectAnalysisDataFirst(
                    1L, // userId
                    "New York", // city
                    (short) 1, // rankType
                    LocalDate.now().minusDays(7), // startDate
                    LocalDate.now(), // endDate
                    null, // taskId
                    Arrays.asList("test keyword"), // keywords
                    50 // maxPosition
            );
            
            System.out.println("查询成功，返回数据条数: " + result.size());
            
        } catch (Exception e) {
            System.out.println("查询异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testSelectAnalysisDataAll() {
        try {
            List<KeywordRankingDO> result = keywordRankingMapper.selectAnalysisDataAll(
                    1L, // userId
                    "New York", // city
                    (short) 1, // rankType
                    LocalDate.now().minusDays(7), // startDate
                    LocalDate.now(), // endDate
                    null, // taskId
                    Arrays.asList("test keyword"), // keywords
                    50 // maxPosition
            );
            
            System.out.println("查询成功，返回数据条数: " + result.size());
            
        } catch (Exception e) {
            System.out.println("查询异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}