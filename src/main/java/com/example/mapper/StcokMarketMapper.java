package com.example.mapper;

import com.example.pojo.StcokMarket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StcokMarketMapper {

    int deleteByPrimaryKey(@Param("code") String code, @Param("date") String date);

    int insert(StcokMarket record);

    StcokMarket selectByPrimaryKey(@Param("code") String code, @Param("date") String date);

    List<HashMap<String,String>> selectAllByDateRange(@Param("begainDate") String date, @Param("endDate") String endDate);

    int updateByPrimaryKey(StcokMarket record);

    void batchInsert(ArrayList<StcokMarket> stockMarketList);
}