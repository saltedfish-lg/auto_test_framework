package com.baidu.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.Map;

public class SchemaRegistry {

    private static final Logger logger = LoggerFactory.getLogger(SchemaRegistry.class);
    private static final String MAPPING_FILE = "src/test/resources/schema/schema-mapping.json";

    private static final Map<String, Map<String, String>> mappings;

    static {
        Map<String, Map<String, String>> loaded = Collections.emptyMap();
        try {
            ObjectMapper mapper = new ObjectMapper();
            loaded = mapper.readValue(new File(MAPPING_FILE), Map.class);
            logger.info("✅ 加载路径+状态码 Schema 映射成功: {} 条", loaded.size());
        } catch (Exception e) {
            logger.error("❌ 加载 Schema 映射失败: {}", e.getMessage());
        }
        mappings = loaded;
    }

    public static String getSchemaFile(String apiPath, int statusCode) {
        Map<String, String> codeMap = mappings.get(apiPath);
        if (codeMap == null) return null;
        return codeMap.getOrDefault(String.valueOf(statusCode), null);
    }
}
