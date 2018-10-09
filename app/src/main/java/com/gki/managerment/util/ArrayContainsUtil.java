package com.gki.managerment.util;
/**
 * 判断数组是否包含某个值的工具类
 */

import java.util.Arrays;

public class ArrayContainsUtil {
    public static <T> boolean ListContains(T[] arr, T targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }


}
