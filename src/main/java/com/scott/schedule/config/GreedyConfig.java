package com.scott.schedule.config;

import com.scott.schedule.common.enums.GreedyStrategyEnum;
import com.scott.schedule.common.enums.TieBreakingStrategy;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 贪心算法配置类
 * 包含贪心算法的所有参数配置
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "algorithm.greedy")
public class GreedyConfig {

    /**
     * 贪心策略
     * 决定使用哪种贪心算法策略
     */
    private GreedyStrategyEnum strategy = GreedyStrategyEnum.LARGEST_DEGREE_FIRST;

    /**
     * 是否启用随机化
     * 在贪心选择中引入随机性，增加解的多样性
     */
    private boolean randomized = false;

    /**
     * 随机化因子
     * 控制随机性的强度，值越大随机性越强
     * 取值范围：[0.0, 1.0]
     */
    private double randomizationFactor = 0.1;

    /**
     * 是否启用快速模式
     * 快速模式下会跳过一些耗时的优化步骤
     */
    private boolean fastMode = false;

    /**
     * 是否启用局部改进
     * 在贪心算法完成后进行局部搜索改进
     */
    private boolean enableLocalImprovement = true;

    /**
     * 局部改进最大迭代次数
     * 控制局部改进的计算时间
     */
    private int maxLocalImprovementIterations = 100;

    /**
     * 约束权重配置
     * 用于调整不同约束在贪心选择中的重要性
     */
    private ConstraintWeights constraintWeights = new ConstraintWeights();

    /**
     * 时间偏好权重
     * 控制时间偏好在评估函数中的重要性
     */
    private double timePreferenceWeight = 2.0;

    /**
     * 教室适配度权重
     * 控制教室适配度在评估函数中的重要性
     */
    private double classroomFitnessWeight = 1.0;

    /**
     * 冲突惩罚权重
     * 控制冲突惩罚在评估函数中的重要性
     */
    private double conflictPenaltyWeight = 2.0;

    /**
     * 资源利用率权重
     * 控制资源利用率在评估函数中的重要性
     */
    private double resourceUtilizationWeight = 0.8;

    /**
     * 是否启用平局打破机制
     * 当多个选择具有相同评分时，使用额外规则打破平局
     */
    private boolean enableTieBreaking = true;

    /**
     * 平局打破策略
     * 决定如何打破平局
     */
    private TieBreakingStrategy tieBreakingStrategy = TieBreakingStrategy.RANDOM;

    /**
     * 最大回溯深度
     * 当贪心选择导致无解时，允许回溯的最大深度
     */
    private int maxBacktrackDepth = 3;

    /**
     * 是否启用前瞻
     * 在做贪心选择时考虑对未来选择的影响
     */
    private boolean enableLookahead = false;

    /**
     * 前瞻深度
     * 前瞻时考虑的未来步数
     */
    private int lookaheadDepth = 2;

    /**
     * 约束权重配置类
     */
    @Data
    public static class ConstraintWeights {
        /**
         * 教师约束权重
         */
        private double teacherWeight = 2.0;

        /**
         * 教室约束权重
         */
        private double classroomWeight = 1.5;

        /**
         * 时间约束权重
         */
        private double timeWeight = 1.0;

        /**
         * 学生约束权重
         */
        private double studentWeight = 1.2;

        /**
         * 设备约束权重
         */
        private double equipmentWeight = 0.8;

        /**
         * 课程连续性约束权重
         */
        private double continuityWeight = 1.3;
    }

    /**
     * 验证配置参数的有效性
     */
    public void validate() {
        if (randomizationFactor < 0.0 || randomizationFactor > 1.0) {
            throw new IllegalArgumentException("随机化因子必须在[0.0, 1.0]范围内");
        }

        if (maxLocalImprovementIterations < 0) {
            throw new IllegalArgumentException("局部改进最大迭代次数必须非负");
        }

        if (timePreferenceWeight < 0 || classroomFitnessWeight < 0 ||
            conflictPenaltyWeight < 0 || resourceUtilizationWeight < 0) {
            throw new IllegalArgumentException("权重参数必须非负");
        }

        if (maxBacktrackDepth < 0) {
            throw new IllegalArgumentException("最大回溯深度必须非负");
        }

        if (lookaheadDepth < 1) {
            throw new IllegalArgumentException("前瞻深度必须大于0");
        }

        // 验证约束权重
        if (constraintWeights != null) {
            validateConstraintWeights(constraintWeights);
        }
    }

    /**
     * 验证约束权重配置
     */
    private void validateConstraintWeights(ConstraintWeights weights) {
        if (weights.teacherWeight < 0 || weights.classroomWeight < 0 ||
            weights.timeWeight < 0 || weights.studentWeight < 0 ||
            weights.equipmentWeight < 0 || weights.continuityWeight < 0) {
            throw new IllegalArgumentException("约束权重必须非负");
        }
    }

    /**
     * 获取推荐配置
     * 根据问题规模和性能要求返回推荐配置
     *
     * @param problemSize 问题规模
     * @param performanceMode 性能模式：FAST-快速，BALANCED-平衡，QUALITY-高质量
     * @return 推荐配置
     */
    public static GreedyConfig getRecommendedConfig(int problemSize, PerformanceMode performanceMode) {
        GreedyConfig config = new GreedyConfig();

        switch (performanceMode) {
            case FAST -> {
                config.setFastMode(true);
                config.setEnableLocalImprovement(false);
                config.setEnableLookahead(false);
                config.setStrategy(GreedyStrategyEnum.LARGEST_DEGREE_FIRST);
            }
            case BALANCED -> {
                config.setFastMode(false);
                config.setEnableLocalImprovement(true);
                config.setMaxLocalImprovementIterations(50);
                config.setStrategy(GreedyStrategyEnum.MOST_CONSTRAINED_FIRST);
            }
            case QUALITY -> {
                config.setFastMode(false);
                config.setEnableLocalImprovement(true);
                config.setMaxLocalImprovementIterations(200);
                config.setEnableLookahead(true);
                config.setStrategy(GreedyStrategyEnum.HYBRID_GREEDY);
            }
        }

        // 根据问题规模调整参数
        if (problemSize > 500) {
            config.setMaxBacktrackDepth(1); // 大规模问题减少回溯
            config.setLookaheadDepth(1);    // 减少前瞻深度
        }

        return config;
    }

    /**
     * 打印配置信息
     */
    public void printConfig() {
        System.out.printf("""
                🎯 贪心算法配置信息:
                ├── 贪心策略: %s
                ├── 随机化: %s (因子: %.2f)
                ├── 快速模式: %s
                ├── 局部改进: %s (最大迭代: %d)
                ├── 权重配置:
                │   ├── 时间偏好权重: %.2f
                │   ├── 教室适配度权重: %.2f
                │   ├── 冲突惩罚权重: %.2f
                │   └── 资源利用率权重: %.2f
                ├── 平局打破: %s (%s)
                ├── 最大回溯深度: %d
                └── 前瞻: %s (深度: %d)
                %n""",
                strategy.getDescription(),
                randomized ? "启用" : "禁用", randomizationFactor,
                fastMode ? "启用" : "禁用",
                enableLocalImprovement ? "启用" : "禁用", maxLocalImprovementIterations,
                timePreferenceWeight, classroomFitnessWeight,
                conflictPenaltyWeight, resourceUtilizationWeight,
                enableTieBreaking ? "启用" : "禁用", tieBreakingStrategy.name(),
                maxBacktrackDepth,
                enableLookahead ? "启用" : "禁用", lookaheadDepth
        );
    }

    /**
     * 性能模式枚举
     */
    public enum PerformanceMode {
        FAST,       // 快速模式
        BALANCED,   // 平衡模式
        QUALITY     // 高质量模式
    }
}

