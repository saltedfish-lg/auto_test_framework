package com.baidu.utils;

import java.io.File;

/**
 * 报告工具类
 */
public class ReportUtils {

    private static final String REPORT_DIR = "target/reports/";

    public static String getLatestReportPath() {
        File reportDirectory = new File(REPORT_DIR);
        if (!reportDirectory.exists() || !reportDirectory.isDirectory()) {
            throw new RuntimeException("报告目录不存在：" + REPORT_DIR);
        }

        File[] files = reportDirectory.listFiles();
        if (files == null || files.length == 0) {
            throw new RuntimeException("报告目录为空：" + REPORT_DIR);
        }

        // 返回最后修改时间最近的文件（假设最新生成的报告）
        File latest = files[0];
        for (File file : files) {
            if (file.lastModified() > latest.lastModified()) {
                latest = file;
            }
        }

        return latest.getAbsolutePath();
    }
}
