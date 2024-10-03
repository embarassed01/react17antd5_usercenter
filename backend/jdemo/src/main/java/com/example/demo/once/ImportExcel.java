package com.example.demo.once;

import java.util.List;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;

/**
 * 导入excel数据
 */
public class ImportExcel {
    
    /**
     * 监听器读取
     * @param fileName
     */
    public static void readByListener(String fileName) {
        EasyExcel.read(
            fileName, 
            XingQiuTableUserInfo.class,
             new TableListener())
            .sheet().doRead();
    }

    /**
     * 同步读，这一种明显简单！jian
     * @param fileName
     */
    public static void synchronousRead(String fileName) {
        // 这里，需要指定读 用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<XingQiuTableUserInfo> totalDataList = EasyExcel.read(fileName)
            .head(XingQiuTableUserInfo.class).sheet().doReadSync();
            for (XingQiuTableUserInfo xingQiuTableUserInfo : totalDataList) {
                System.out.println(xingQiuTableUserInfo);
            }
    }

    /**
     * main方法，不依赖springboot的
     * @param args
     */
    public static void main(String[] args) {
        String fileName = "/Users/liuguihan/Development/act/antdereact/usercenter/backend/jdemo/src/main/resources/testexcel.xlsx";
        // 读取模式1
        // readByListener(fileName);

        // 读取模式2
        synchronousRead(fileName);
    }
}
