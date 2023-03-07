package com.example.mapper;

import com.example.pojo.PlateMarket;

import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PlateMarketMapper {

    int deleteByPrimaryKey(@Param("plateCode") String plateCode, @Param("date") String date);

    int insert(PlateMarket record);

    PlateMarket selectByPrimaryKey(@Param("plateCode") String plateCode, @Param("date") String date);

    List<PlateMarket> selectAll(@Param("begainDate") String begainDate, @Param("endDate") String endDate);

    int updateByPrimaryKey(PlateMarket record);

    int batchInsert(ArrayList<PlateMarket> plateMarketList);
}