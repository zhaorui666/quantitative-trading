package com.example.mapper;

import com.example.pojo.StockBaseInfo;
import java.util.List;

public interface StockBaseInfoMapper {
    int insert(StockBaseInfo record);

    List<StockBaseInfo> selectAll();
}