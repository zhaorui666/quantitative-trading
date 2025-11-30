package com.zr.mapper;

import com.zr.pojo.StockBaseInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StockBaseInfoMapper {

    Integer insert(StockBaseInfo record);

    List<StockBaseInfo> selectAll();
}