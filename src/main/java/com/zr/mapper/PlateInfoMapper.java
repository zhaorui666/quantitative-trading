package com.zr.mapper;

import com.zr.pojo.PlateInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlateInfoMapper {
    int insert(PlateInfo record);

    List<PlateInfo> selectAll();
}