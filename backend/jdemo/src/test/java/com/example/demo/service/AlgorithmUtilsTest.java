package com.example.demo.service;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.demo.utils.AlgorithmUtils;

/**
 * 算法工具类测试
 */
public class AlgorithmUtilsTest {

    @Test
    void test() {
        String str1 = "鱼皮是狗";
        String str2 = "鱼皮不是狗";
        String str3 = "鱼皮是鱼不是狗";
        int score1 = AlgorithmUtils.minDistanceStr(str1, str2);  // (String, String)
        int score2 = AlgorithmUtils.minDistanceStr(str1, str3);
        System.out.println(score1);  // 1
        System.out.println(score2);  // 3
    }

    @Test 
    void testCompareTags() {
        List<String> tagList1 = Arrays.asList("java", "大一", "男");
        List<String> tagList2 = Arrays.asList("java", "大一", "女");
        List<String> tagList3 = Arrays.asList("Python", "大二", "女");
        System.out.println(AlgorithmUtils.minDistance(tagList1, tagList2));  // 1
        System.out.println(AlgorithmUtils.minDistance(tagList1, tagList3));  // 3
    }
}
