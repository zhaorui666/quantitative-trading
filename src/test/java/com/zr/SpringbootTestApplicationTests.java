package com.zr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootTestApplicationTests {

    @Test
    public void contextLoads() {
        Date curDate = new Date();
        Long time = curDate.getTime();
        System.out.println(time);

        Executors.newCachedThreadPool();
    }

    @Test
    public void test1() {
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
