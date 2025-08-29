package com.scott.schedule.data;

import com.scott.schedule.config.DataSourceConfig;
import com.scott.schedule.data.impl.DatabaseDataSource;
import com.scott.schedule.data.impl.JsonDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 数据源工厂类
 * 负责根据配置创建和管理数据源实例
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Component
public class DataSourceFactory {

    private final DataSourceConfig dataSourceConfig;
    private final ApplicationContext applicationContext;

    @Autowired
    public DataSourceFactory(DataSourceConfig dataSourceConfig, ApplicationContext applicationContext) {
        this.dataSourceConfig = dataSourceConfig;
        this.applicationContext = applicationContext;
    }

    /**
     * 创建数据源实例
     *
     * @param dataSourceType 数据源类型
     * @return 数据源实例
     */
    public DataSource createDataSource(String dataSourceType) {
        try {
            return switch (dataSourceType.toLowerCase()) {
                case "json" -> createJsonDataSource();
                case "database" -> createDatabaseDataSource();
                default -> throw new IllegalArgumentException("不支持的数据源类型: " + dataSourceType);
            };
        } catch (Exception e) {
            System.err.printf("❌ 创建数据源失败: %s，降级到JSON数据源%n", e.getMessage());
            return createJsonDataSource();
        }
    }

    /**
     * 根据当前配置创建数据源
     *
     * @return 数据源实例
     */
    public DataSource createDataSource() {
        return createDataSource(dataSourceConfig.getType());
    }

    /**
     * 创建JSON数据源
     *
     * @return JSON数据源实例
     */
    private DataSource createJsonDataSource() {
        try {
            return applicationContext.getBean("jsonDataSource", JsonDataSource.class);
        } catch (Exception e) {
            // 如果Bean获取失败，直接创建实例
            return new JsonDataSource((JsonDataManager) applicationContext.getBean("jsonDataManager"));
        }
    }

    /**
     * 创建数据库数据源
     *
     * @return 数据库数据源实例
     */
    private DataSource createDatabaseDataSource() {
        try {
            return applicationContext.getBean("databaseDataSource", DatabaseDataSource.class);
        } catch (Exception e) {
            // 如果Bean获取失败，直接创建实例
            return new DatabaseDataSource();
        }
    }

    /**
     * 验证数据源类型是否支持
     *
     * @param dataSourceType 数据源类型
     * @return 是否支持
     */
    public boolean isSupported(String dataSourceType) {
        return "json".equalsIgnoreCase(dataSourceType) ||
               "database".equalsIgnoreCase(dataSourceType);
    }

    /**
     * 获取支持的数据源类型列表
     *
     * @return 支持的数据源类型
     */
    public String[] getSupportedTypes() {
        return new String[]{"json", "database"};
    }
}

