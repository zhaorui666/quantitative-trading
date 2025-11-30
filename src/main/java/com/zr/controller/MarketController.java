package com.zr.controller;

import com.zr.annotation.CurrentLimiter;
import com.zr.constant.LimitType;
import com.zr.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

@RestController
public class MarketController {

    @Autowired
    private MarketService service;

    @CurrentLimiter(limit = 2, expire = 60, limitType = LimitType.IP)
    @RequestMapping("/insertMarketData")
    public void insertMarketData() throws IOException, InterruptedException {
        service.insertMarketData();
    }

    @RequestMapping("/countSingleResult")
    public void countSingleResult() throws ParseException, ExecutionException, InterruptedException {
        service.countSingleResult();
    }

    @RequestMapping("/countHandleOfCupResult")
    public void countHandleOfCupResult() throws IOException, ParseException, ExecutionException, InterruptedException {
        service.countHandleOfCupResult();
    }

    @RequestMapping("/insertFinaSta")
    public void insertFinaSta() throws IOException {
        service.insertFinaSta();
    }

    @RequestMapping("/collectPlateMarketData")
    public void collectPlateMarketData() throws IOException {
        service.collectPlateMarketData();
    }

    @RequestMapping("/countPlateResult")
    public void countPlateResult() throws IOException {
        service.countPlateResult();
    }

    @RequestMapping("/selectTest")
    public void selectTest() throws IOException {
        service.selectTest();
    }
}
