package com.zr.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class QueryThread implements  Callable<List<Map<String, Object>>>{

    private MarketService marketService;
    private String begainDate;
    private String endDate;

    public QueryThread(){}

    public QueryThread(String begainDate,String endDate,MarketService marketService){
        this.begainDate=begainDate;
        this.endDate=endDate;
        this.marketService = marketService;
    }

    @Override
    public List<Map<String, Object>>  call() {
        List<Map<String, Object>> res = marketService.countResultThreadService(begainDate,endDate);
        return res;
    }
}
