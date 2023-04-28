package com.zr.mapper;

import com.zr.pojo.StockBaseInfo;
import java.util.List;

public interface StockBaseInfoMapper {
    int insert(StockBaseInfo record);

    List<StockBaseInfo> selectAll();
}