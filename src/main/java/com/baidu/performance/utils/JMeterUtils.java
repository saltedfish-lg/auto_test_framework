package com.baidu.performance.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * JMeter CLI 执行工具类
 */
public class JMeterUtils {

    public static void runTest(String jmxFilePath, String resultsDir) {
        try {
            File outputDir = new File(resultsDir);
            if (!outputDir.exists()) outputDir.mkdirs();

            ProcessBuilder builder = new ProcessBuilder(
                    "jmeter", "-n",
                    "-t", jmxFilePath,
                    "-l", resultsDir + "/results.jtl",
                    "-e", "-o", resultsDir + "/html-report"
            );

            builder.inheritIO(); // 输出到控制台
            Process process = builder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("JMeter 测试失败，退出码：" + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("JMeter 执行异常", e);
        }
    }
}
