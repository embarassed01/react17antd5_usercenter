package com.example.demo.once;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.StringUtils;

/**
 * 导入星球用户到数据库
 */
public class ImportXingQiuUser {
    
    public static void main(String[] args) {
        String fileName = "/Users/liuguihan/Development/act/antdereact/usercenter/backend/jdemo/src/main/resources/prodExcel.xlsx";
        // 这里，需要指定读 用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<XingQiuTableUserInfo> userInfoList = EasyExcel.read(fileName)
            .head(XingQiuTableUserInfo.class).sheet().doReadSync();
        // for (XingQiuTableUserInfo xingQiuTableUserInfo : totalDataList) {
        //     System.out.println(xingQiuTableUserInfo);
        // }
        System.out.println("总数=" + userInfoList.size());
        Map<String, List<XingQiuTableUserInfo>> listMap = userInfoList
            .stream()
            .filter(userInfo -> StringUtils.isEmpty(userInfo.getUsername()))
            .collect(Collectors.groupingBy(XingQiuTableUserInfo::getUsername));
        System.out.println("不重复的昵称数=" + listMap.keySet().size());
    }
}
