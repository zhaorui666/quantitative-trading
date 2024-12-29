package com.zr.mapper;

import com.zr.pojo.StockFinaSta;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StockFinaStaMapper {
    int deleteByPrimaryKey(@Param("code") String code, @Param("reportName") String reportName);

    int insert(StockFinaSta record);

    StockFinaSta selectByPrimaryKey(@Param("code") String code, @Param("reportName") String reportName);

    List<StockFinaSta> selectAll();

    int updateByPrimaryKey(StockFinaSta record);

    void batchInsert(ArrayList<StockFinaSta> stockFinaStasList);

    List<StockFinaSta> selectByCode(String code);
}