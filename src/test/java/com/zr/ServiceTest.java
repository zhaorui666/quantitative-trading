//package com.zr;
//
//import com.zr.mapper.StockBaseInfoMapper;
//import com.zr.pojo.StockBaseInfo;
//import com.zr.unittest.SpringBootTestDB;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.jdbc.Sql;
//import static com.zr.util.RandomUtils.randomPojo;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = SpringBootTestDB.Application.class)
//@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) // 每个单元测试结束后，清理 DB
//public class ServiceTest extends SpringBootTestDB {
//
//    @Autowired
//    private StockBaseInfoMapper mapper;
//
//    @Test
//    public void testCreateStockBaseInfo_success() {
//        // 准备参数
//        StockBaseInfo baseInfo = randomPojo(StockBaseInfo.class);
//
//        // 调用
//        Integer i = mapper.insert(baseInfo);
//        // 断言
//        assertNotNull(i);
//        // 校验记录的属性是否正确
//        StockBaseInfo result = mapper.selectAll().stream().filter(f -> f.getCode().equals(baseInfo.getCode())).findFirst().orElse(null);
//        assertEquals(baseInfo.getCode(), result.getCode());
//    }
//}
