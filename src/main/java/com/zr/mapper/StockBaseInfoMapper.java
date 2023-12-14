package com.zr.mapper;

import com.zr.pojo.StockBaseInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StockBaseInfoMapper {

    int insert(StockBaseInfo record);

    List<StockBaseInfo> selectAll();
}