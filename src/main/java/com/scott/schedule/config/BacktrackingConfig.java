package com.scott.schedule.config;

import com.scott.schedule.common.enums.ConstraintPropagationType;
import com.scott.schedule.common.enums.SearchStrategy;
import com.scott.schedule.common.enums.ValueSelectionStrategy;
import com.scott.schedule.common.enums.VariableSelectionStrategy;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 回溯算法配置类
 * 包含回溯算法的所有参数配置
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "algorithm.backtracking")
public class BacktrackingConfig {

    /**
     * 变量选择策略
     * 决定在搜索过程中如何选择下一个要赋值的变量
     */
    private VariableSelectionStrategy variableSelectionStrategy = VariableSelectionStrategy.MINIMUM_REMAINING_VALUES;

    /**
     * 值选择策略
     * 决定为选定变量尝试值的顺序
     */
    private ValueSelectionStrategy valueSelectionStrategy = ValueSelectionStrategy.LEAST_CONSTRAINING;

    /**
     * 最大搜索时间（秒）
     * 超过此时间将终止搜索
     */
    private int maxSearchTimeSeconds = 300;

    /**
     * 最大搜索深度
     * 限制搜索树的最大深度，防止无限递归
     */
    private int maxSearchDepth = 1000;

    /**
     * 是否启用约束传播
     * 启用后会在每次赋值后进行约束传播以减少搜索空间
     */
    private boolean enableConstraintPropagation = true;

    /**
     * 约束传播类型
     * 决定使用哪种约束传播算法
     */
    private ConstraintPropagationType constraintPropagationType = ConstraintPropagationType.AC3;

    /**
     * 是否启用前向检查
     * 在赋值前检查是否会导致其他变量域为空
     */
    private boolean enableForwardChecking = true;

    /**
     * 是否启用回跳
     * 智能回溯到冲突的真正原因处，而不是简单的时间顺序回溯
     */
    private boolean enableBackjumping = false;

    /**
     * 是否启用学习
     * 记录冲突原因，避免重复相同的错误
     */
    private boolean enableLearning = false;

    /**
     * 最大学习子句数量
     * 限制学习子句的数量，防止内存过度使用
     */
    private int maxLearnedClauses = 1000;

    /**
     * 是否启用重启
     * 定期重启搜索以避免陷入困难的搜索空间
     */
    private boolean enableRestart = false;

    /**
     * 重启间隔
     * 每隔多少次失败后重启搜索
     */
    private int restartInterval = 100;

    /**
     * 是否启用随机化
     * 在变量和值选择中引入随机性
     */
    private boolean enableRandomization = false;

    /**
     * 随机化概率
     * 进行随机选择的概率
     */
    private double randomizationProbability = 0.1;

    /**
     * 是否启用对称破缺
     * 消除问题中的对称性以减少搜索空间
     */
    private boolean enableSymmetryBreaking = false;

    /**
     * 是否启用动态变量排序
     * 根据搜索过程中的信息动态调整变量选择策略
     */
    private boolean enableDynamicVariableOrdering = true;

    /**
     * 是否启用动态值排序
     * 根据搜索过程中的信息动态调整值选择策略
     */
    private boolean enableDynamicValueOrdering = true;

    /**
     * 节点限制
     * 最大访问节点数，超过后终止搜索
     */
    private long maxNodes = 1000000L;

    /**
     * 失败限制
     * 最大失败次数，超过后终止搜索
     */
    private int maxFailures = 10000;

    /**
     * 解的数量限制
     * 找到指定数量的解后停止搜索（0表示找到第一个解就停止）
     */
    private int maxSolutions = 1;

    /**
     * 是否启用解的验证
     * 找到解后验证其正确性
     */
    private boolean enableSolutionValidation = true;

    /**
     * 搜索策略
     * 整体的搜索策略类型
     */
    private SearchStrategy searchStrategy = SearchStrategy.DEPTH_FIRST;

    /**
     * 启发式权重配置
     * 用于调整不同启发式在选择过程中的重要性
     */
    private HeuristicWeights heuristicWeights = new HeuristicWeights();

    /**
     * 获取推荐配置
     * 根据问题规模和性能要求返回推荐配置
     *
     * @param problemSize     问题规模
     * @param performanceMode 性能模式
     * @return 推荐配置
     */
    public static BacktrackingConfig getRecommendedConfig(int problemSize, PerformanceMode performanceMode) {
        BacktrackingConfig config = new BacktrackingConfig();

        switch (performanceMode) {
            case FAST -> {
                config.setMaxSearchTimeSeconds(60);
                config.setEnableConstraintPropagation(false);
                config.setEnableForwardChecking(true);
                config.setEnableLearning(false);
                config.setVariableSelectionStrategy(VariableSelectionStrategy.FIRST_UNASSIGNED);
                config.setValueSelectionStrategy(ValueSelectionStrategy.NATURAL_ORDER);
            }
            case BALANCED -> {
                config.setMaxSearchTimeSeconds(300);
                config.setEnableConstraintPropagation(true);
                config.setEnableForwardChecking(true);
                config.setEnableLearning(false);
                config.setVariableSelectionStrategy(VariableSelectionStrategy.MINIMUM_REMAINING_VALUES);
                config.setValueSelectionStrategy(ValueSelectionStrategy.LEAST_CONSTRAINING);
            }
            case THOROUGH -> {
                config.setMaxSearchTimeSeconds(1800);
                config.setEnableConstraintPropagation(true);
                config.setEnableForwardChecking(true);
                config.setEnableLearning(true);
                config.setEnableBackjumping(true);
                config.setVariableSelectionStrategy(VariableSelectionStrategy.MOST_CONSTRAINING);
                config.setValueSelectionStrategy(ValueSelectionStrategy.LEAST_CONSTRAINING);
            }
        }

        // 根据问题规模调整参数
        if (problemSize < 50) {
            config.setMaxSearchDepth(500);
            config.setMaxNodes(100000L);
        } else if (problemSize < 200) {
            config.setMaxSearchDepth(1000);
            config.setMaxNodes(500000L);
        } else {
            config.setMaxSearchDepth(2000);
            config.setMaxNodes(1000000L);
            config.setEnableRestart(true);
            config.setRestartInterval(200);
        }

        return config;
    }

    /**
     * 验证配置参数的有效性
     */
    public void validate() {
        if (maxSearchTimeSeconds <= 0) {
            throw new IllegalArgumentException("最大搜索时间必须大于0");
        }

        if (maxSearchDepth <= 0) {
            throw new IllegalArgumentException("最大搜索深度必须大于0");
        }

        if (maxLearnedClauses < 0) {
            throw new IllegalArgumentException("最大学习子句数量必须非负");
        }

        if (restartInterval <= 0) {
            throw new IllegalArgumentException("重启间隔必须大于0");
        }

        if (randomizationProbability < 0.0 || randomizationProbability > 1.0) {
            throw new IllegalArgumentException("随机化概率必须在[0.0, 1.0]范围内");
        }

        if (maxNodes <= 0) {
            throw new IllegalArgumentException("节点限制必须大于0");
        }

        if (maxFailures <= 0) {
            throw new IllegalArgumentException("失败限制必须大于0");
        }

        if (maxSolutions < 0) {
            throw new IllegalArgumentException("解的数量限制必须非负");
        }

        // 验证启发式权重
        if (heuristicWeights != null) {
            validateHeuristicWeights(heuristicWeights);
        }
    }

    /**
     * 验证启发式权重配置
     */
    private void validateHeuristicWeights(HeuristicWeights weights) {
        if (weights.mrvWeight < 0 || weights.degreeWeight < 0 ||
                weights.mostConstrainingWeight < 0 || weights.leastConstrainingWeight < 0 ||
                weights.failFirstWeight < 0) {
            throw new IllegalArgumentException("启发式权重必须非负");
        }
    }

    /**
     * 打印配置信息
     */
    public void printConfig() {
        System.out.printf("""
                        🔍 回溯算法配置信息:
                        ├── 变量选择策略: %s
                        ├── 值选择策略: %s
                        ├── 最大搜索时间: %d 秒
                        ├── 最大搜索深度: %d
                        ├── 约束传播: %s (%s)
                        ├── 前向检查: %s
                        ├── 回跳: %s
                        ├── 学习: %s (最大子句: %d)
                        ├── 重启: %s (间隔: %d)
                        ├── 随机化: %s (概率: %.2f)
                        ├── 对称破缺: %s
                        ├── 动态排序: 变量(%s) 值(%s)
                        ├── 搜索策略: %s
                        ├── 节点限制: %d
                        ├── 失败限制: %d
                        ├── 解数量限制: %d
                        └── 解验证: %s
                        %n""",
                variableSelectionStrategy.getDescription(),
                valueSelectionStrategy.getDescription(),
                maxSearchTimeSeconds,
                maxSearchDepth,
                enableConstraintPropagation ? "启用" : "禁用", constraintPropagationType.name(),
                enableForwardChecking ? "启用" : "禁用",
                enableBackjumping ? "启用" : "禁用",
                enableLearning ? "启用" : "禁用", maxLearnedClauses,
                enableRestart ? "启用" : "禁用", restartInterval,
                enableRandomization ? "启用" : "禁用", randomizationProbability,
                enableSymmetryBreaking ? "启用" : "禁用",
                enableDynamicVariableOrdering ? "启用" : "禁用",
                enableDynamicValueOrdering ? "启用" : "禁用",
                searchStrategy.getDescription(),
                maxNodes,
                maxFailures,
                maxSolutions,
                enableSolutionValidation ? "启用" : "禁用"
        );
    }

    /**
     * 性能模式枚举
     */
    public enum PerformanceMode {
        // 快速模式
        FAST,
        // 平衡模式
        BALANCED,
        // 彻底模式
        THOROUGH
    }

    /**
     * 启发式权重配置类
     */
    @Data
    public static class HeuristicWeights {
        /**
         * 最小剩余值权重
         */
        private double mrvWeight = 1.0;

        /**
         * 度启发式权重
         */
        private double degreeWeight = 0.5;

        /**
         * 最约束变量权重
         */
        private double mostConstrainingWeight = 0.8;

        /**
         * 最少约束值权重
         */
        private double leastConstrainingWeight = 1.0;

        /**
         * 失败优先权重
         */
        private double failFirstWeight = 0.3;
    }
}

