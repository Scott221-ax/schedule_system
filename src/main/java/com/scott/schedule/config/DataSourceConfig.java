package com.scott.schedule.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 数据源配置类
 * 用于配置数据源类型和相关参数
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "schedule.datasource")
public class DataSourceConfig {

    /**
     * 数据源类型
     * 支持: json, database
     */
    private String type = "json";

    /**
     * 是否在启动时初始化测试数据
     */
    private boolean initializeTestData = true;

    /**
     * 是否在启动时验证数据完整性
     */
    private boolean validateOnStartup = true;

    /**
     * JSON文件存储目录
     */
    private String jsonDataDirectory = "data";

    /**
     * 数据库连接配置
     */
    private DatabaseConfig database = new DatabaseConfig();

    /**
     * 数据库配置内部类
     */
    @Data
    public static class DatabaseConfig {
        /**
         * 数据库URL
         */
        private String url = "jdbc:h2:mem:schedule";

        /**
         * 数据库用户名
         */
        private String username = "sa";

        /**
         * 数据库密码
         */
        private String password = "";

        /**
         * 数据库驱动类名
         */
        private String driverClassName = "org.h2.Driver";

        /**
         * 是否显示SQL语句
         */
        private boolean showSql = false;

        /**
         * DDL自动生成策略
         */
        private String ddlAuto = "create-drop";
    }

    /**
     * 验证配置参数
     */
    public void validate() {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("数据源类型不能为空");
        }

        if (!type.equals("json") && !type.equals("database")) {
            throw new IllegalArgumentException("不支持的数据源类型: " + type + "，支持的类型: json, database");
        }

        if (jsonDataDirectory == null || jsonDataDirectory.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON数据目录不能为空");
        }

        if (database != null) {
            validateDatabaseConfig(database);
        }
    }

    /**
     * 验证数据库配置
     */
    private void validateDatabaseConfig(DatabaseConfig dbConfig) {
        if (dbConfig.url == null || dbConfig.url.trim().isEmpty()) {
            throw new IllegalArgumentException("数据库URL不能为空");
        }

        if (dbConfig.username == null) {
            throw new IllegalArgumentException("数据库用户名不能为null");
        }

        if (dbConfig.password == null) {
            throw new IllegalArgumentException("数据库密码不能为null");
        }

        if (dbConfig.driverClassName == null || dbConfig.driverClassName.trim().isEmpty()) {
            throw new IllegalArgumentException("数据库驱动类名不能为空");
        }
    }

    /**
     * 是否使用JSON数据源
     */
    public boolean isJsonDataSource() {
        return "json".equalsIgnoreCase(type);
    }

    /**
     * 是否使用数据库数据源
     */
    public boolean isDatabaseDataSource() {
        return "database".equalsIgnoreCase(type);
    }

    /**
     * 打印配置信息
     */
    public void printConfig() {
        System.out.printf(
                """
                        🔧 数据源配置信息:
                        ├── 数据源类型: %s
                        ├── 初始化测试数据: %s
                        ├── 启动时验证数据: %s
                        ├── JSON数据目录: %s
                        └── 数据库配置:
                            ├── URL: %s
                            ├── 用户名: %s
                            ├── 显示SQL: %s
                            └── DDL策略: %s
                        %n""",
                type.toUpperCase(),
                initializeTestData ? "是" : "否",
                validateOnStartup ? "是" : "否",
                jsonDataDirectory,
                database.url,
                database.username,
                database.showSql ? "是" : "否",
                database.ddlAuto
        );
    }
}

