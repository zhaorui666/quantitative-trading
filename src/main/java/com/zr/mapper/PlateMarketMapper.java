package com.zr.mapper;

import com.zr.pojo.PlateMarket;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PlateMarketMapper {

    int deleteByPrimaryKey(@Param("plateCode") String plateCode, @Param("date") String date);

    int insert(PlateMarket record);

    PlateMarket selectByPrimaryKey(@Param("plateCode") String plateCode, @Param("date") String date);

    List<PlateMarket> selectAll(@Param("begainDate") String begainDate, @Param("endDate") String endDate);

    int updateByPrimaryKey(PlateMarket record);

    int batchInsert(ArrayList<PlateMarket> plateMarketList);
}