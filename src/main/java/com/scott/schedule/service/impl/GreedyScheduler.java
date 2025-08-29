package com.scott.schedule.service.impl;

import com.scott.schedule.algorithm.ScheduleChromosome;
import com.scott.schedule.common.enums.GreedyStrategyEnum;
import com.scott.schedule.config.GreedyConfig;
import com.scott.schedule.service.ClassScheduler;
import com.scott.schedule.service.DataService;
import com.scott.schedule.strategy.GreedyStrategy;
import com.scott.schedule.strategy.GreedyStrategyFactory;
import com.scott.schedule.strategy.HybridGreedyStrategy;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于贪心算法的排课服务实现（重构版）
 * 采用策略模式，支持多种贪心策略的灵活切换
 * 支持的贪心策略：
 * 1. 最大度优先 (Largest Degree First)
 * 2. 最小剩余值 (Minimum Remaining Values)
 * 3. 最大约束优先 (Most Constrained First)
 * 4. 随机贪心 (Random Greedy)
 * 5. 混合贪心 (Hybrid Greedy)
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Service
public class GreedyScheduler implements ClassScheduler {

    /**
     * 贪心算法配置参数
     */
    private final GreedyConfig config;

    /**
     * 数据服务
     */
    private final DataService dataService;

    /**
     * 策略工厂
     */
    private final GreedyStrategyFactory strategyFactory;

    /**
     * 当前最优解
     * -- GETTER --
     *  获取当前最优解

     */
    @Getter
    private ScheduleChromosome bestSolution;

    /**
     * 构造函数
     *
     * @param config      贪心算法配置参数
     * @param dataService 数据服务
     */
    public GreedyScheduler(GreedyConfig config, DataService dataService) {
        this.config = config;
        this.dataService = dataService;
        this.strategyFactory = new GreedyStrategyFactory(config, dataService);
    }

    /**
     * 执行贪心算法排课
     * 根据配置的策略执行相应的贪心算法
     */
    @Override
    public void schedule() {
        System.out.printf(
                """
                        🎯 开始执行贪心算法排课
                        📋 贪心策略: %s
                        🔄 是否启用随机化: %s
                        🎲 随机化因子: %.2f
                        ⚡ 快速模式: %s
                        %n""",
                config.getStrategy().getDescription(),
                config.isRandomized() ? "是" : "否",
                config.getRandomizationFactor(),
                config.isFastMode() ? "启用" : "禁用"
        );

        // 根据策略类型选择相应的策略实现
        ScheduleChromosome solution = executeStrategy(config.getStrategy());

        if (solution != null && solution.getFitness() > 0) {
            bestSolution = solution;
            System.out.printf("✅ 贪心算法排课完成，适应度: %.2f%n", solution.getFitness());
        } else {
            System.out.println("❌ 贪心算法排课失败");
        }
    }

    /**
     * 执行指定的贪心策略
     *
     * @param strategyType 策略类型
     * @return 排课解
     */
    private ScheduleChromosome executeStrategy(GreedyStrategyEnum strategyType) {
        try {
            switch (strategyType) {
                case HYBRID_GREEDY -> {
                    // 混合策略
                    HybridGreedyStrategy hybridStrategy = new HybridGreedyStrategy(config, dataService);
                    return hybridStrategy.execute();
                }
                default -> {
                    // 单一策略
                    GreedyStrategy strategy = strategyFactory.getStrategy(strategyType);
                    return strategy.execute();
                }
            }
        } catch (Exception e) {
            System.err.printf("❌ 策略 %s 执行失败: %s%n", strategyType, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取策略信息
     */
    public String getStrategyInfo(GreedyStrategyEnum strategyType) {
        return strategyFactory.getStrategyInfo(strategyType);
    }

    /**
     * 获取所有可用策略
     */
    public Map<GreedyStrategyEnum, GreedyStrategy> getAllStrategies() {
        return strategyFactory.getAllStrategies();
    }

    /**
     * 清除策略缓存
     */
    public void clearStrategyCache() {
        strategyFactory.clearCache();
    }

    /**
     * 执行多种策略并比较结果
     */
    public Map<String, Double> compareStrategies() {
        Map<String, Double> results = new HashMap<>();

        for (GreedyStrategyEnum strategyType : GreedyStrategyEnum.values()) {
            try {
                ScheduleChromosome solution = executeStrategy(strategyType);
                if (solution != null) {
                    results.put(strategyType.getDescription(), solution.getFitness());
                }
            } catch (Exception e) {
                System.err.printf("策略 %s 比较失败: %s%n", strategyType, e.getMessage());
            }
        }

        return results;
    }

    /**
     * 获取策略执行统计
     */
    public String getStrategyExecutionStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("📊 贪心策略执行统计:\n");

        Map<GreedyStrategyEnum, GreedyStrategy> allStrategies = getAllStrategies();
        for (Map.Entry<GreedyStrategyEnum, GreedyStrategy> entry : allStrategies.entrySet()) {
            GreedyStrategy strategy = entry.getValue();
            stats.append(String.format("  - %s: %s\n",
                    strategy.getStrategyName(),
                    strategy.getStrategyDescription()));
        }

        return stats.toString();
    }
}

