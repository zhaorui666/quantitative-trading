package com.zr.threadService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zr.constant.Constants;
import com.zr.mapper.StcokMarketMapper;
import com.zr.pojo.StcokMarket;
import com.zr.pojo.StockBaseInfo;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 从雪球网进行行情数据查询，从而进行落库
 */

public class MarketDateInsertThread implements  Runnable{

    private List<StockBaseInfo> stockBaseInfoList;

    private StcokMarketMapper stcokMarketMapper;

    public MarketDateInsertThread(){}

    Logger logger = LogManager.getLogger(this.getClass());

    CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    public MarketDateInsertThread(List<StockBaseInfo> stockBaseInfoList, StcokMarketMapper stcokMarketMapper){
        this.stcokMarketMapper=stcokMarketMapper;
        this.stockBaseInfoList = stockBaseInfoList;
    }

    //指定获取过去多少个交易日
    int count = 365 * 3;

    @Override
    public void run() {

        for (StockBaseInfo stockBaseInfo: stockBaseInfoList) {

            HttpGet httpGet = new HttpGet("https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=" + stockBaseInfo.getCode() + "&begin=" + LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + "&period=day&type=before&count=-" + count + "&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance&md5__1632=eqAxyDBmitKYu405DIB0DIx7qAIx488222bD");

            httpGet.setHeader("Cookie", Constants.Cookie);

            //Thread.currentThread().sleep(100);

            CloseableHttpResponse httpResponse = null;

            try {
                httpResponse = httpClient.execute(httpGet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int statusCode = httpResponse.getStatusLine().getStatusCode();

            logger.info("响应状态码：{}", statusCode);

            if (statusCode == 200) {

                HttpEntity httpEntity = httpResponse.getEntity();

                // 使用指定的字符编码解析响应消息实体
                String res = null;

                try {
                    res = EntityUtils.toString(httpEntity, "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                logger.info("请求个股：{}； 响应结果：{}", stockBaseInfo.getCode(), res);

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

                    //时间戳->日期
                    String date = DateTimeFormatter.ofPattern(Constants.STANDARD_DATE_FORMAT).format(LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) dayArray.get(0)), ZoneId.systemDefault()));

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
                    Object transAmt = dayArray.get(9);

                    item.setTransamt(transAmt.toString());
                    item.setCode(stockCode);
                    item.setLastprice(lastPrice.toString());
                    item.setChangepercent(changePercent.toString());
                    item.setBeginprice(beginPrice.toString());
                    item.setHighestprice(highestPrice.toString());
                    item.setLowestprice(lowestPrice.toString());
                    item.setTurnoverrate(turnoverRate.toString());
                    item.setDate(date);

                    stockMarketList.add(item);
                }

                if (stockMarketList.size() > 0) {
                    stcokMarketMapper.batchInsert(stockMarketList);
                }
            }
        }
    }
}
