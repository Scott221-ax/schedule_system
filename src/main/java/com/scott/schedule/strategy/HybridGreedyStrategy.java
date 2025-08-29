package com.scott.schedule.strategy;

import com.scott.schedule.algorithm.ScheduleChromosome;
import com.scott.schedule.common.enums.GreedyStrategyEnum;
import com.scott.schedule.config.GreedyConfig;
import com.scott.schedule.model.Course;
import com.scott.schedule.model.TimeSlot;
import com.scott.schedule.service.DataService;

import java.util.*;

/**
 * 混合贪心策略
 * 结合多种贪心策略的优势，选择最优结果
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public class HybridGreedyStrategy implements GreedyStrategy {

    private final GreedyStrategyFactory strategyFactory;
    private final List<GreedyStrategy> strategies;

    public HybridGreedyStrategy(GreedyConfig config, DataService dataService) {
        this.strategyFactory = new GreedyStrategyFactory(config, dataService);
        this.strategies = new ArrayList<>();

        // 添加所有策略
        strategies.add(strategyFactory.getStrategy(GreedyStrategyEnum.LARGEST_DEGREE_FIRST));
        strategies.add(strategyFactory.getStrategy(GreedyStrategyEnum.MINIMUM_REMAINING_VALUES));
        strategies.add(strategyFactory.getStrategy(GreedyStrategyEnum.MOST_CONSTRAINED_FIRST));
        strategies.add(strategyFactory.getStrategy(GreedyStrategyEnum.RANDOM_GREEDY));
    }

    @Override
    public ScheduleChromosome execute() {
        System.out.println("🎯 执行混合贪心算法...");

        // 执行多种策略并选择最优结果
        List<ScheduleChromosome> solutions = new ArrayList<>();

        for (GreedyStrategy strategy : strategies) {
            System.out.printf("🔄 执行策略: %s%n", strategy.getStrategyName());
            try {
                ScheduleChromosome solution = strategy.execute();
                solutions.add(solution);
                System.out.printf("✅ 策略 %s 完成，适应度: %.2f%n",
                        strategy.getStrategyName(), solution.getFitness());
            } catch (Exception e) {
                System.err.printf("❌ 策略 %s 执行失败: %s%n", strategy.getStrategyName(), e.getMessage());
            }
        }

        if (solutions.isEmpty()) {
            System.out.println("⚠️ 所有策略都执行失败，返回空解");
            return new ScheduleChromosome();
        }

        // 选择最优解
        ScheduleChromosome bestSolution = solutions.stream()
                .max(Comparator.comparingDouble(ScheduleChromosome::getFitness))
                .orElse(new ScheduleChromosome());

        System.out.printf("🏆 混合策略完成，最优解适应度: %.2f%n", bestSolution.getFitness());
        return bestSolution;
    }

    @Override
    public String getStrategyName() {
        return "混合贪心";
    }

    @Override
    public String getStrategyDescription() {
        return "结合多种贪心策略的优势，选择最优结果";
    }

    @Override
    public double calculatePriority(Course course) {
        // 计算所有策略的平均优先级
        double totalPriority = 0.0;
        int validStrategies = 0;

        for (GreedyStrategy strategy : strategies) {
            try {
                totalPriority += strategy.calculatePriority(course);
                validStrategies++;
            } catch (Exception e) {
                // 忽略失败的策略
            }
        }

        return validStrategies > 0 ? totalPriority / validStrategies : 0.0;
    }

    @Override
    public TimeSlot selectBestTimeSlot(Course course, ScheduleChromosome solution) {
        // 使用投票机制选择最佳时间段
        Map<TimeSlot, Integer> votes = new HashMap<>();

        for (GreedyStrategy strategy : strategies) {
            try {
                TimeSlot selectedSlot = strategy.selectBestTimeSlot(course, solution);
                if (selectedSlot != null) {
                    votes.merge(selectedSlot, 1, Integer::sum);
                }
            } catch (Exception e) {
                // 忽略失败的策略
            }
        }

        if (votes.isEmpty()) {
            return null;
        }

        // 返回得票最多的时间段
        return votes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * 获取策略统计信息
     */
    public String getStrategyStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("📊 混合策略统计信息:\n");

        for (GreedyStrategy strategy : strategies) {
            stats.append(String.format("  - %s: %s\n",
                    strategy.getStrategyName(),
                    strategy.getStrategyDescription()));
        }

        return stats.toString();
    }

    /**
     * 添加自定义策略
     */
    public void addStrategy(GreedyStrategy strategy) {
        if (strategy != null) {
            strategies.add(strategy);
        }
    }

    /**
     * 移除策略
     */
    public boolean removeStrategy(GreedyStrategyEnum strategy) {
        return strategies.remove(strategy);
    }

    /**
     * 获取策略数量
     */
    public int getStrategyCount() {
        return strategies.size();
    }
}
