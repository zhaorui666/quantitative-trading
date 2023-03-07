package com.example.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mapper.*;
import com.example.pojo.*;
import com.example.util.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class MarketService {

    @Autowired
    private StockBaseInfoMapper stockBaseInfoMapper;

    @Autowired
    private StcokMarketMapper stcokMarketMapper;

    @Autowired
    private StockFinaStaMapper stockFinaStaMapper;

    @Autowired
    private PlateInfoMapper plateInfoMapper;

    @Autowired
    private PlateMarketMapper plateMarketMapper;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    Date curDate = new Date();

    Calendar calendar = Calendar.getInstance();

    String curDatreStr = simpleDateFormat.format(curDate);

    Long time = curDate.getTime();

    CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    //营业收入增长
    BigDecimal totalRevenueInc = new BigDecimal("0.25");

    //扣非净利润增长
    BigDecimal dedNonNetProfitInc = new BigDecimal("0.25");

    //毛利率增长
    BigDecimal grossSellingRateInc = new BigDecimal("0.0");

    //资产负债率增长
    //BigDecimal assetLiabRatioInc = new BigDecimal("-0.1");

    //财务报表满足条件的往期数量
    long finaStaMeetConditionCount = 2;

    public void insertMarketData() throws IOException, InterruptedException {

        List<StockBaseInfo> stockBaseInfoList = stockBaseInfoMapper.selectAll();

        int count = 1;

        for (StockBaseInfo stockBaseInfo : stockBaseInfoList) {

            HttpGet httpGet = new HttpGet("https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=" + stockBaseInfo.getCode() + "&begin=" + time + "&period=day&type=before&count=-" + count + "&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance");

            httpGet.setHeader("Cookie", "xqat=6f2a74dcaf567c87c45208248c683353242d4781;");

            //Thread.currentThread().sleep(100);

            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

            int statusCode = httpResponse.getStatusLine().getStatusCode();

            System.out.println("响应状态码：" + statusCode);

            if (statusCode == 200) {

                HttpEntity httpEntity = httpResponse.getEntity();
                // 使用指定的字符编码解析响应消息实体
                String res = EntityUtils.toString(httpEntity, "UTF-8");

                System.out.println("请求个股：" + stockBaseInfo.getCode() + ";  响应结果：" + res);

                JSONObject jsonObject = JSONObject.parseObject(res);

                String data = jsonObject.getString("data");

                JSONObject dataJsonObject = JSONObject.parseObject(data);

                String stockCode = dataJsonObject.getString("symbol");

                //list 日K图数据
                JSONArray itemJsonObject = (JSONArray) dataJsonObject.get("item");

                ArrayList<StcokMarket> stockMarketList = new ArrayList();

                for (int i = 0; i < itemJsonObject.size(); i++) {

                    StcokMarket item = new StcokMarket();

                    //每个日K图里的参数
                    JSONArray dayArray = (JSONArray) itemJsonObject.get(i);

                    //日期-时间戳
                    String date = simpleDateFormat.format(new Date(Long.valueOf((Long) dayArray.get(0))));

                    //开盘价
                    BigDecimal beginPrice = (BigDecimal) dayArray.get(2);

                    //最高价
                    BigDecimal highestPrice = (BigDecimal) dayArray.get(3);

                    //最低价
                    BigDecimal lowestPrice = (BigDecimal) dayArray.get(4);

                    //收盘价
                    BigDecimal lastPrice = (BigDecimal) dayArray.get(5);

                    //涨跌幅
                    BigDecimal changePercent = (BigDecimal) dayArray.get(7);

                    //换手率
                    BigDecimal turnoverRate = (BigDecimal) dayArray.get(8);

                    //成交额
                    if (dayArray.get(9) instanceof Integer) {
                        Integer transAmt = (Integer) dayArray.get(9);
                        item.setTransamt(transAmt.toString());
                    }

                    if (dayArray.get(9) instanceof Long) {
                        Long transAmt = (Long) dayArray.get(9);
                        item.setTransamt(transAmt.toString());
                    }

                    if (dayArray.get(9) instanceof BigDecimal) {
                        BigDecimal transAmt = (BigDecimal) dayArray.get(9);
                        transAmt = transAmt.setScale(0, BigDecimal.ROUND_HALF_UP);
                        item.setTransamt(transAmt.toString());
                    }

                    item.setCode(stockCode);
                    item.setLastprice(lastPrice.toString());
                    item.setChangepercent(changePercent.toString());
                    item.setBeginprice(beginPrice.toString());
                    item.setHighestprice(highestPrice.toString());
                    item.setLowestprice(lowestPrice.toString());
                    item.setTurnoverrate(turnoverRate.toString());
                    item.setDate(date);

                    if (curDatreStr.equals(date)) {
                        stockMarketList.add(item);
                    }

                }

                if (stockMarketList.size() > 0) {
                    stcokMarketMapper.batchInsert(stockMarketList);
                }

            }
        }

    }

    public void countSingleResult() throws ParseException, ExecutionException, InterruptedException {

        ArrayList<String> result = countResultByCondition("20210802", "20210821", -10, 10, 50.0, 0.0, 0);

        //ArrayList<String> result1 = countResultByCondition("20210823", "20210828", -1.5, -0.5, 50.0, 0.0, 0);

        //result.retainAll(result1);

        ArrayList<String> resultLast = filterFinaCondition(result);

        resultLast.forEach(s -> System.out.println(s));

        System.out.println("========计算结束========");
    }

    //杯柄经典形态图
    public void countHandleOfCupResult() throws ParseException, ExecutionException, InterruptedException {

        //正在形成U型部分[回调幅度介于12%-15%(最高不会超过33%)]；下跌缩量、上涨放量
        ArrayList<String> result1 = countUShipResultByCondition("20210620", "20210820", -0.3, 0.3, 100.0, 0.0, 4);

//        ArrayList<String> result2 = countUShipResultByCondition("20210710", "20210820", 0.1, 0.3, 100.0, 0.0,4);
//
//        result1.retainAll(result2);

        ArrayList<String> resultLast = filterFinaCondition(result1);

        resultLast.forEach(s -> System.out.println(s));

        System.out.println("========计算结束========");

    }

    private ArrayList<String> countUShipResultByCondition(String begainDateParam, String endDateParam, double lowLimitParam, double upLimitParam, double marketValueParam, double pe, int capacityIncCount) throws ParseException, ExecutionException, InterruptedException {

        ArrayList<HashMap<String, Object>> resultList = selectMarketDataByThread(begainDateParam, endDateParam);

        ArrayList<String> returnList = new ArrayList<>();

        for (HashMap<String, Object> item : resultList) {

            ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) item.get("list");

            OptionalDouble averageChangePercentOpt = list.stream().mapToDouble(stcokMarket -> new Double(stcokMarket.get("changepercent").toString())).average();

            OptionalDouble maxLastPriceOpt = list.stream().mapToDouble(stcokMarket -> new Double(stcokMarket.get("lastprice").toString())).max();

            OptionalDouble minLastPriceOpt = list.stream().mapToDouble(stcokMarket -> new Double(stcokMarket.get("lastprice").toString())).min();

            OptionalDouble avgTransAmtOpt = list.stream().mapToDouble(stcokMarket -> new Double(stcokMarket.get("transamt").toString())).average();

            if (averageChangePercentOpt != null && averageChangePercentOpt.isPresent()
                    && maxLastPriceOpt != null && maxLastPriceOpt.isPresent()
                    && minLastPriceOpt != null && minLastPriceOpt.isPresent()
                    && avgTransAmtOpt != null && avgTransAmtOpt.isPresent()) {

                Double averageChangePercent = averageChangePercentOpt.getAsDouble();

                BigDecimal maxLastPrice = new BigDecimal(maxLastPriceOpt.getAsDouble());

                BigDecimal minLastPrice = new BigDecimal(minLastPriceOpt.getAsDouble());

                BigDecimal avgTransAmt = new BigDecimal(avgTransAmtOpt.getAsDouble());

                String marketValueStr = (String) item.get("marketValue");

                String unit = marketValueStr.substring(marketValueStr.length() - 1);

                if (!unit.equals("亿")) {
                    continue;
                }

                Double marketValue = new Double(marketValueStr.substring(0, marketValueStr.length() - 2));

                //在指定区间内，成交量大于平均成交量的1.5倍，且当日收盘为红的交易日次数
                int count = list.stream().filter(x -> new BigDecimal(x.get("transamt").toString()).compareTo(avgTransAmt.multiply(new BigDecimal(1.5))) > 0 && new BigDecimal(x.get("changepercent").toString()).compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList()).size();

                //1.指定区间内平均涨幅的区间
                //2.指定区间内最高价到最低价的回调区间
                //3.放量交易日的出现次数
                //4.市值需满足大于某一值
                if (averageChangePercent.compareTo(lowLimitParam) >= 0 && averageChangePercent.compareTo(upLimitParam) < 0
                        && minLastPrice.divide(maxLastPrice, 4, RoundingMode.HALF_UP).compareTo(new BigDecimal(0.88)) <= 0 && minLastPrice.divide(maxLastPrice, 4, RoundingMode.HALF_UP).compareTo(new BigDecimal(0.85)) >= 0
                        && count >= capacityIncCount
                        && marketValue > marketValueParam) {
                    returnList.add(item.get("code").toString() + "-" + item.get("name").toString());
                }

            }
        }

        return returnList;
    }

    private ArrayList<String> countResultByCondition(String begainDateParam, String endDateParam, double lowLimitParam, double upLimitParam, double marketValueParam, double pe, int capacityIncCount) throws ParseException, ExecutionException, InterruptedException {

        ArrayList<HashMap<String, Object>> resultList = selectMarketDataByThread(begainDateParam, endDateParam);

        ArrayList<String> returnList = new ArrayList<>();

        for (HashMap<String, Object> item : resultList) {

            ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) item.get("list");

            OptionalDouble averageChangePercentOpt = list.stream().mapToDouble(stcokMarket -> new Double(stcokMarket.get("changepercent").toString())).average();

            OptionalDouble maxLastPriceOpt = list.stream().mapToDouble(stcokMarket -> new Double(stcokMarket.get("lastprice").toString())).max();

            OptionalDouble minLastPriceOpt = list.stream().mapToDouble(stcokMarket -> new Double(stcokMarket.get("lastprice").toString())).min();

            OptionalDouble avgTransAmtOpt = list.stream().mapToDouble(stcokMarket -> new Double(stcokMarket.get("transamt").toString())).average();

            if (averageChangePercentOpt != null && averageChangePercentOpt.isPresent()
                    && maxLastPriceOpt != null && maxLastPriceOpt.isPresent()
                    && minLastPriceOpt != null && minLastPriceOpt.isPresent()
                    && avgTransAmtOpt != null && avgTransAmtOpt.isPresent()) {

                Double averageChangePercent = averageChangePercentOpt.getAsDouble();

                BigDecimal avgTransAmt = new BigDecimal(avgTransAmtOpt.getAsDouble());

                String marketValueStr = (String) item.get("marketValue");

                String unit = marketValueStr.substring(marketValueStr.length() - 1);

                if (!unit.equals("亿")) {
                    continue;
                }

                Double marketValue = new Double(marketValueStr.substring(0, marketValueStr.length() - 2));

                //在指定区间内，成交量大于平均成交量的1.5倍，且当日收盘为红的交易日次数
                //int count = list.stream().filter(x -> new BigDecimal(x.get("transamt").toString()).compareTo(avgTransAmt.multiply(new BigDecimal(1.5))) > 0 && new BigDecimal(x.get("changepercent").toString()).compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList()).size();

                //1.指定区间内平均涨幅的区间
                //3.放量交易日的出现次数
                //4.市值需满足大于某一值
                if (averageChangePercent.compareTo(lowLimitParam) >= 0 && averageChangePercent.compareTo(upLimitParam) < 0
                        && marketValue > marketValueParam
                       // && count >= capacityIncCount
                )
                {
                    returnList.add(item.get("code").toString() + "-" + item.get("name").toString());
                }

            }
        }

        return returnList;

    }

    public List<Map<String, Object>> countResultThreadService(String begainDate, String endDate) {

        List<HashMap<String, String>> stcokMarketList = stcokMarketMapper.selectAllByDateRange(begainDate, endDate);

        Map<String, List<HashMap<String, String>>> map = stcokMarketList.stream().collect(Collectors.groupingBy(f -> f.get("code")));

        List<Map<String, Object>> returnList = new ArrayList<>();

        map.forEach((k, v) -> {
            HashMap formatMap = new HashMap<String, Object>();
            formatMap.put("code", k);
            formatMap.put("list", v);
            returnList.add(formatMap);
        });

        return returnList;

    }


    public void insertFinaSta() throws IOException {

        List<StockBaseInfo> stockBaseInfosList = stockBaseInfoMapper.selectAll();

        int count = 10;

        for (StockBaseInfo stockBaseInfo : stockBaseInfosList) {

            HttpGet httpGet = new HttpGet("https://stock.xueqiu.com/v5/stock/finance/cn/indicator.json?symbol=" + stockBaseInfo.getCode() + "&type=all&is_detail=true&count=" + count + "&timestamp=" + time);

            httpGet.setHeader("Cookie", "xqat=6f2a74dcaf567c87c45208248c683353242d4781;");

            //Thread.currentThread().sleep(100);

            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

            int statusCode = httpResponse.getStatusLine().getStatusCode();

            System.out.println("响应状态码：" + statusCode);

            if (statusCode == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                // 使用指定的字符编码解析响应消息实体
                String res = EntityUtils.toString(httpEntity, "UTF-8");

                System.out.println("请求个股财务报表数据：" + stockBaseInfo.getCode() + ";  响应结果：" + res);

                JSONObject jsonObject = JSONObject.parseObject(res);

                JSONObject data = (JSONObject) jsonObject.get("data");

                JSONArray list = (JSONArray) data.get("list");

                ArrayList<StockFinaSta> stockFinaStasList = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {

                    JSONObject item = (JSONObject) list.get(i);

                    String date = simpleDateFormat.format(new Date(Long.valueOf((Long) item.get("report_date"))));

                    String reportName = (String) item.get("report_name");

                    JSONArray total_revenue_list = (JSONArray) item.get("total_revenue");

                    BigDecimal totalRevenue = (BigDecimal) total_revenue_list.get(0);

                    BigDecimal totalRevenueInc = (BigDecimal) total_revenue_list.get(1);

                    JSONArray net_profit_after_nrgal_atsolc_list = (JSONArray) item.get("net_profit_after_nrgal_atsolc");

                    BigDecimal dedNonNetProfit = (BigDecimal) net_profit_after_nrgal_atsolc_list.get(0);

                    BigDecimal dedNonNetProfitInc = (BigDecimal) net_profit_after_nrgal_atsolc_list.get(1);

                    JSONArray gross_selling_rate_list = (JSONArray) item.get("gross_selling_rate");

                    BigDecimal grossSellingRate = (BigDecimal) gross_selling_rate_list.get(0);

                    BigDecimal grossSellingRateInc = (BigDecimal) gross_selling_rate_list.get(1);

                    JSONArray asset_liab_ratio_list = (JSONArray) item.get("asset_liab_ratio");

                    BigDecimal assetLiabRatio = (BigDecimal) asset_liab_ratio_list.get(0);

                    BigDecimal assetLiabRatioInc = (BigDecimal) asset_liab_ratio_list.get(1);

                    JSONArray basicEpsList = (JSONArray) item.get("basic_eps");

                    BigDecimal basicEps = (BigDecimal) basicEpsList.get(0);

                    BigDecimal basicEpsInc = (BigDecimal) basicEpsList.get(1);

                    StockFinaSta stockFinaSta = new StockFinaSta();

                    stockFinaSta.setCode(stockBaseInfo.getCode());

                    stockFinaSta.setDate(new Integer(date));

                    stockFinaSta.setReportName(reportName);

                    if (totalRevenue != null) {
                        stockFinaSta.setTotalRevenue(totalRevenue.setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                    }

                    if (totalRevenueInc != null) {
                        stockFinaSta.setTotalRevenueInc(totalRevenueInc.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
                    }

                    if (dedNonNetProfit != null) {
                        stockFinaSta.setDedNonNetProfit(dedNonNetProfit.setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                    }

                    if (dedNonNetProfitInc != null) {
                        stockFinaSta.setDedNonNetProfitInc(dedNonNetProfitInc.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
                    }

                    if (grossSellingRate != null) {
                        stockFinaSta.setGrossSellingRate(grossSellingRate.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
                    }

                    if (grossSellingRateInc != null) {
                        stockFinaSta.setGrossSellingRateInc(grossSellingRateInc.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
                    }

                    if (assetLiabRatio != null) {
                        stockFinaSta.setAssetLiabRatio(assetLiabRatio.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
                    }

                    if (assetLiabRatioInc != null) {
                        stockFinaSta.setAssetLiabRatioInc(assetLiabRatioInc.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
                    }

                    if (basicEps != null) {
                        stockFinaSta.setBasicEps(basicEps.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
                    }

                    if (basicEpsInc != null) {
                        stockFinaSta.setBasicEpsInc(basicEpsInc.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
                    }

                    stockFinaStasList.add(stockFinaSta);

                }

                stockFinaStaMapper.batchInsert(stockFinaStasList);

            }
        }

    }

    public void collectPlateMarketData() throws IOException {

        List<PlateInfo> plateInfoList = plateInfoMapper.selectAll();

        String beg = "20210301";

        for (PlateInfo plateInfo : plateInfoList) {

            HttpGet httpGet = new HttpGet("https://push2his.eastmoney.com/api/qt/stock/kline/get?cb=jQuery112401976117106249089_1628907633892&secid=90." + plateInfo.getPalteCode() + "&ut=fa5fd1943c7b386f172d6893dbfba10b&fields1=f1%2Cf2%2Cf3%2Cf4%2Cf5&fields2=f51%2Cf52%2Cf53%2Cf54%2Cf55%2Cf56%2Cf57%2Cf58%2Cf59%2Cf60%2Cf61&klt=101&fqt=0&beg=" + beg + "&end=" + curDatreStr + "&_=1628907633896");

            httpGet.setHeader("Cookie", "xqat=0de231800ecb3f75e824dc0a23866218ead61a8e;");

            //Thread.currentThread().sleep(100);

            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

            int statusCode = httpResponse.getStatusLine().getStatusCode();

            System.out.println("响应状态码：" + statusCode);

            if (statusCode == 200) {

                HttpEntity httpEntity = httpResponse.getEntity();
                // 使用指定的字符编码解析响应消息实体
                String res = EntityUtils.toString(httpEntity, "UTF-8");

                System.out.println("请求板块：" + plateInfo.getPalteName() + ";  响应结果：" + res);

                String str = res.substring(res.indexOf("{\"rc\""));

                String jsonStr = str.substring(0, str.length() - 2);

                JSONObject jsonObject = JSONObject.parseObject(jsonStr);

                JSONObject data = (JSONObject) jsonObject.get("data");

                JSONArray list = (JSONArray) data.get("klines");

                ArrayList<PlateMarket> plateMarketList = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {

                    String item = (String) list.get(i);

                    String[] arr = item.split(",");

                    PlateMarket plateMarket = new PlateMarket();

                    if (arr.length != 11) {
                        continue;
                    }

                    String date = arr[0].replaceAll("-", "");

                    plateMarket.setChagnePercent(arr[8]);

                    plateMarket.setAmplitude(arr[7]);

                    plateMarket.setTransAmt(arr[6]);

                    plateMarket.setTurnoverRate(arr[10]);

                    plateMarket.setPlateCode(plateInfo.getPalteCode());

                    plateMarket.setPlateName(plateInfo.getPalteName());

                    plateMarket.setDate(date);

                    plateMarketList.add(plateMarket);

                }

                plateMarketMapper.batchInsert(plateMarketList);

            }
        }


    }

    public void countPlateResult() {

        ArrayList<String> result = countPlateResultByCondition("20210801", "20210827", 0.5, 1.5);

        ArrayList<String> result2 = countPlateResultByCondition("20210613", "20210801", -0.3, 0.3);

        result.retainAll(result2);

        for (String str : result) {
            System.out.println(str);
        } 

        System.out.println("========计算结束========");
    }

    private ArrayList<String> countPlateResultByCondition(String begainDate, String endDate, double lowLimit, double upLimit) {

        List<PlateInfo> plateInfoList = plateInfoMapper.selectAll();

        List<PlateMarket> plateMarketList = plateMarketMapper.selectAll(begainDate, endDate);

        List<HashMap<String, Object>> reslist = new ArrayList<>();

        //分组
        for (PlateInfo plateInfo : plateInfoList) {

            HashMap map = new HashMap<String, Object>();

            ArrayList<PlateMarket> list = new ArrayList<>();

            for (PlateMarket plateMarket : plateMarketList) {

                if (plateInfo.getPalteCode().equals(plateMarket.getPlateCode())) {
                    list.add(plateMarket);
                }

            }

            map.put("plateCode", plateInfo.getPalteCode());

            map.put("plateName", plateInfo.getPalteName());

            map.put("list", list);

            reslist.add(map);
        }

        ArrayList<String> returnList = new ArrayList<>();

        for (HashMap<String, Object> item : reslist) {

            ArrayList<PlateMarket> list = (ArrayList<PlateMarket>) item.get("list");

            OptionalDouble averageChangePercentOpt = list.stream().mapToDouble(plateMarket -> new Double(plateMarket.getChagnePercent())).average();

            if (averageChangePercentOpt != null && averageChangePercentOpt.isPresent()) {

                Double averageChangePercent = averageChangePercentOpt.getAsDouble();

                if (averageChangePercent.compareTo(lowLimit) >= 0 && averageChangePercent.compareTo(upLimit) < 0) {
                    returnList.add(item.get("plateCode").toString() + "-" + item.get("plateName").toString());
                }

            }
        }

        return returnList;
    }

    //多线程查询公用方法
    private ArrayList<HashMap<String, Object>> selectMarketDataByThread(String begainDateParam, String endDateParam) throws ParseException, InterruptedException, ExecutionException {

        Date begainDate = simpleDateFormat.parse(begainDateParam);

        List<Callable<List<Map<String, Object>>>> tasks = new ArrayList<>();

        int betweenMonth = DateUtils.getMonthSpace(begainDateParam, endDateParam);

        if (betweenMonth == 1) {

            Callable<List<Map<String, Object>>> work = new QueryThread(begainDateParam, endDateParam, this);

            tasks.add(work);

        } else {

            for (int i = 0; i < betweenMonth; i++) {

                calendar.setTime(begainDate);

                calendar.add(Calendar.MONTH, 1);

                //工作线程当前的截止查询日期
                String curEndStr = simpleDateFormat.format(calendar.getTime());

                Callable<List<Map<String, Object>>> work = new QueryThread(begainDateParam, curEndStr, this);

                tasks.add(work);

                //下一循环的起始时间
                begainDateParam = curEndStr;

                begainDate = simpleDateFormat.parse(begainDateParam);

            }

        }

        List<Future<List<Map<String, Object>>>> futures = executorService.invokeAll(tasks);

        List<List<Map<String, Object>>> totalGatherList = new ArrayList<>();

        if (futures != null && futures.size() > 0) {

            for (Future<List<Map<String, Object>>> future : futures) {
                totalGatherList.add(future.get());
            }

        }

        Map<String, List<Map<String, Object>>> resMap = totalGatherList.stream().flatMap(f -> f.stream()).collect(Collectors.groupingBy(s -> s.get("code").toString()));

        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();

        resMap.forEach((k, v) -> {
            HashMap<String, Object> map = new HashMap<>();
            ArrayList<HashMap<String, Object>> list = new ArrayList();
            for (Map<String, Object> m : v) {
                ArrayList valueList = (ArrayList) m.get("list");
                list.addAll(valueList);
            }
            map.put("code", k);
            map.put("name", list.get(0).get("name"));
            map.put("marketValue", list.get(0).get("marketValue"));
            map.put("pe", list.get(0).get("pe"));
            map.put("list", list);
            resultList.add(map);
        });

        return resultList;

    }

    //财务添加筛选公共方法
    private ArrayList<String> filterFinaCondition(ArrayList<String> result) {

        Iterator<String> iterator = result.iterator();

        while (iterator.hasNext()) {

            String str = iterator.next();

            String code = str.substring(0, str.indexOf("-"));

            //查询过去两个季度的财务报表
            List<StockFinaSta> stockFinaStaList = stockFinaStaMapper.selectByCode(code);

            if (stockFinaStaList != null && stockFinaStaList.size() >= 3) {

                OptionalDouble optionalDouble = null;

                //过去3个季度
                try {
                    optionalDouble = stockFinaStaList.stream().limit(3).mapToDouble(f -> new Double(f.getDedNonNetProfitInc())).average();

                    if (optionalDouble != null && optionalDouble.isPresent()) {

                        StockFinaSta lastFs = stockFinaStaList.remove(0);

                        if(!lastFs.getReportName().equals("2021中报")){
                            iterator.remove();
                            continue;
                        }

                        Long meetCount = stockFinaStaList.stream().limit(finaStaMeetConditionCount).filter(
                                s -> new BigDecimal(s.getTotalRevenueInc()).compareTo(totalRevenueInc) > 0
                                        && new BigDecimal(s.getDedNonNetProfitInc()).compareTo(dedNonNetProfitInc) > 0).count();

                        //如果查询财务报表存在某一季不满足条件则剔除
                        if (meetCount != finaStaMeetConditionCount) {
                            iterator.remove();
                            continue;
                        }

                        //当季扣非净利润没有超过平均增长指标的80%，则进行剔除
                        if (new Double(lastFs.getDedNonNetProfitInc()) < optionalDouble.getAsDouble() * 0.8) {
                            iterator.remove();
                            continue;
                        }

                        //当季每股收益增长必须大于40%
                        if (new Double(lastFs.getBasicEpsInc()) < 0.4) {
                            iterator.remove();
                            continue;
                        }

                    }
                } catch (Exception e) {
                    iterator.remove();
                    continue;
                }
            }
        }

        return result;

    }
}
