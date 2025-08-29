package com.scott.schedule.strategy;

import com.scott.schedule.common.enums.GreedyStrategyEnum;
import com.scott.schedule.config.GreedyConfig;
import com.scott.schedule.service.DataService;
import com.scott.schedule.strategy.impl.LargestDegreeFirstStrategy;
import com.scott.schedule.strategy.impl.MinimumRemainingValuesStrategy;
import com.scott.schedule.strategy.impl.MostConstrainedFirstStrategy;
import com.scott.schedule.strategy.impl.RandomGreedyStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * 贪心策略工厂类
 * 负责创建和管理不同的贪心策略实例
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public class GreedyStrategyFactory {

    private final Map<GreedyStrategyEnum, GreedyStrategy> strategyCache = new HashMap<>();
    private final GreedyConfig config;
    private final DataService dataService;

    public GreedyStrategyFactory(GreedyConfig config, DataService dataService) {
        this.config = config;
        this.dataService = dataService;
    }

    /**
     * 获取贪心策略实例
     *
     * @param strategyType 策略类型
     * @return 策略实例
     */
    public GreedyStrategy getStrategy(GreedyStrategyEnum strategyType) {
        return strategyCache.computeIfAbsent(strategyType, this::createStrategy);
    }

    /**
     * 创建策略实例
     *
     * @param strategyType 策略类型
     * @return 策略实例
     */
    private GreedyStrategy createStrategy(GreedyStrategyEnum strategyType) {
        return switch (strategyType) {
            case LARGEST_DEGREE_FIRST -> new LargestDegreeFirstStrategy(config, dataService);
            case MINIMUM_REMAINING_VALUES -> new MinimumRemainingValuesStrategy(config, dataService);
            case MOST_CONSTRAINED_FIRST -> new MostConstrainedFirstStrategy(config, dataService);
            case RANDOM_GREEDY -> new RandomGreedyStrategy(config, dataService);
            default -> throw new IllegalArgumentException("不支持的贪心策略类型: " + strategyType);
        };
    }

    /**
     * 获取所有可用策略
     *
     * @return 策略映射
     */
    public Map<GreedyStrategyEnum, GreedyStrategy> getAllStrategies() {
        // 确保所有策略都已创建
        for (GreedyStrategyEnum strategyType : GreedyStrategyEnum.values()) {
            getStrategy(strategyType);
        }
        return new HashMap<>(strategyCache);
    }

    /**
     * 清除策略缓存
     */
    public void clearCache() {
        strategyCache.clear();
    }

    /**
     * 获取策略信息
     *
     * @param strategyType 策略类型
     * @return 策略信息
     */
    public String getStrategyInfo(GreedyStrategyEnum strategyType) {
        GreedyStrategy strategy = getStrategy(strategyType);
        return String.format("策略: %s, 描述: %s", 
            strategy.getStrategyName(), 
            strategy.getStrategyDescription());
    }
}
