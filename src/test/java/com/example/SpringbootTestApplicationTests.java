package com.example;

import com.alibaba.fastjson.JSONArray;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootTestApplicationTests {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    public void contextLoads() throws IOException, ParseException {
        Date curDate = new Date();
        Long time = curDate.getTime();
        System.out.println(time);

        Executors.newCachedThreadPool();
    }

    @Test
    public void test1() throws IOException, ParseException {
        List<List<HashMap>> listR = new ArrayList<>();
        List<HashMap> list = new ArrayList<>();
        List<HashMap> list1 = new ArrayList<>();
        HashMap m =  new HashMap();
        m.put("a","a");
        list.add(m);

        HashMap m1 =  new HashMap();
        m1.put("a","a");
        list1.add(m1);

        listR.add(list);
        listR.add(list1);

        listR.stream().flatMap(s->s.stream()).forEach(System.out::println);
    }

}
