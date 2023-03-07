package com.example.mapper;

import com.example.pojo.PlateInfo;
import java.util.List;

public interface PlateInfoMapper {
    int insert(PlateInfo record);

    List<PlateInfo> selectAll();
}