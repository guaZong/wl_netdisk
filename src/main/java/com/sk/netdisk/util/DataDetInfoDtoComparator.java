package com.sk.netdisk.util;

import com.sk.netdisk.enums.DataEnum;
import com.sk.netdisk.pojo.dto.DataDetInfoDto;

import java.util.Comparator;

/**
 * @author lsj
 */
public class DataDetInfoDtoComparator implements Comparator<DataDetInfoDto> {

    final private int sortType;
    final private int sortOrder;

    public DataDetInfoDtoComparator(int sortType, int sortOrder) {
        this.sortType = sortType;
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(DataDetInfoDto data1, DataDetInfoDto data2) {
        int result = data1.getType().compareTo(data2.getType());

        if (result == 0) {
            if (sortType == DataEnum.SORT_TYPE_NAME.getIndex()) {
                result = data1.getName().compareTo(data2.getName());
            } else {
                result = data1.getCreateTime().compareTo(data2.getCreateTime());
            }
        }

        return (sortOrder == DataEnum.SORT_ORDER_DESC.getIndex()) ? -result : result;
    }
    
}
