package com.zr.threadService;

import com.zr.service.MarketService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 *
 * 行情数据加载，从而进行处理筛选
 */
public class MarketDateQueryThread implements  Callable<List<Map<String, Object>>>{

    private MarketService marketService;
    private String begainDate;
    private String endDate;

    public MarketDateQueryThread(){}

    public MarketDateQueryThread(String begainDate, String endDate, MarketService marketService){
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
