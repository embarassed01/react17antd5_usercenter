package com.example.demo.once;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TableListener implements ReadListener<XingQiuTableUserInfo> {  
    /**
     * 所有数据解析完成了，都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("已经解析完成");
    }

    /**
     * 这个每一条数据解析都会来调用
     */
    @Override
    public void invoke(XingQiuTableUserInfo data, AnalysisContext context) {
        System.out.println(data);
    }
    
}
