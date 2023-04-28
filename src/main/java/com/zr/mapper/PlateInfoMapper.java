package com.zr.mapper;

import com.zr.pojo.PlateInfo;
import java.util.List;

public interface PlateInfoMapper {
    int insert(PlateInfo record);

    List<PlateInfo> selectAll();
}