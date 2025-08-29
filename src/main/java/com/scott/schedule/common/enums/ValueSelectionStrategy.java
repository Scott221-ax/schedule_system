package com.scott.schedule.common.enums;

/**
 * 值选择策略枚举
 * 定义回溯算法中为变量选择值的不同策略
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public enum ValueSelectionStrategy {

    /**
     * 自然顺序
     * 按照值的自然顺序进行尝试
     *
     * 特点：
     * - 实现简单，执行速度快
     * - 确定性选择，结果可重现
     * - 不考虑启发式信息
     * - 适合简单问题或调试
     */
    NATURAL_ORDER("自然顺序", "按值的自然顺序进行尝试"),

    /**
     * 最少约束值 (Least Constraining Value, LCV)
     * 选择对其他变量约束影响最小的值
     *
     * 特点：
     * - 保留最多的选择空间
     * - 减少未来冲突的可能性
     * - 提高找到解的概率
     * - 是最常用的值选择启发式
     */
    LEAST_CONSTRAINING("最少约束值", "选择对其他变量约束影响最小的值"),

    /**
     * 最多约束值 (Most Constraining Value)
     * 选择对其他变量约束影响最大的值
     *
     * 特点：
     * - 尽早暴露冲突
     * - 快速剪枝无效分支
     * - 适合寻找所有解的场景
     * - 与LCV相反的策略
     */
    MOST_CONSTRAINING("最多约束值", "选择对其他变量约束影响最大的值"),

    /**
     * 随机顺序
     * 随机选择值的尝试顺序
     *
     * 特点：
     * - 增加搜索的多样性
     * - 避免陷入固定模式
     * - 适合多次运行取最优
     * - 可以发现不同的解
     */
    RANDOM_ORDER("随机顺序", "随机选择值的尝试顺序");

    /**
     * 策略名称
     */
    private final String name;

    /**
     * 策略描述
     */
    private final String description;

    /**
     * 构造函数
     *
     * @param name 策略名称
     * @param description 策略描述
     */
    ValueSelectionStrategy(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * 获取策略名称
     *
     * @return 策略名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取策略描述
     *
     * @return 策略描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取策略的计算复杂度
     *
     * @return 复杂度等级 (1-3, 3最复杂)
     */
    public int getComplexity() {
        return switch (this) {
            case NATURAL_ORDER -> 1;
            case RANDOM_ORDER -> 1;
            case LEAST_CONSTRAINING -> 3;
            case MOST_CONSTRAINING -> 3;
        };
    }

    /**
     * 获取策略的预期效果
     *
     * @return 效果等级 (1-4, 4效果最好)
     */
    public int getEffectiveness() {
        return switch (this) {
            case NATURAL_ORDER -> 1;
            case RANDOM_ORDER -> 2;
            case LEAST_CONSTRAINING -> 4;
            case MOST_CONSTRAINING -> 3;
        };
    }

    /**
     * 判断策略是否为确定性策略
     *
     * @return 是否为确定性策略
     */
    public boolean isDeterministic() {
        return switch (this) {
            case NATURAL_ORDER -> true;
            case LEAST_CONSTRAINING -> true;
            case MOST_CONSTRAINING -> true;
            case RANDOM_ORDER -> false;
        };
    }

    /**
     * 判断策略是否需要约束信息
     *
     * @return 是否需要约束信息
     */
    public boolean requiresConstraintInfo() {
        return switch (this) {
            case NATURAL_ORDER -> false;
            case RANDOM_ORDER -> false;
            case LEAST_CONSTRAINING -> true;
            case MOST_CONSTRAINING -> true;
        };
    }

    /**
     * 根据问题特征推荐最适合的策略
     *
     * @param searchGoal 搜索目标：FIRST_SOLUTION-找第一个解，ALL_SOLUTIONS-找所有解，BEST_SOLUTION-找最优解
     * @param constraintTightness 约束紧密度
     * @param needsDiversity 是否需要多样性
     * @return 推荐的策略
     */
    public static ValueSelectionStrategy recommendStrategy(SearchGoal searchGoal,
                                                         double constraintTightness,
                                                         boolean needsDiversity) {

        if (needsDiversity) {
            return RANDOM_ORDER;
        }

        return switch (searchGoal) {
            case FIRST_SOLUTION -> {
                // 寻找第一个解时，使用最少约束值保留更多选择
                if (constraintTightness > 0.7) {
                    yield LEAST_CONSTRAINING;
                } else {
                    yield NATURAL_ORDER;
                }
            }
            case ALL_SOLUTIONS -> {
                // 寻找所有解时，使用最多约束值快速剪枝
                yield MOST_CONSTRAINING;
            }
            case BEST_SOLUTION -> {
                // 寻找最优解时，使用最少约束值保持搜索空间
                yield LEAST_CONSTRAINING;
            }
        };
    }

    /**
     * 获取策略的适用场景
     *
     * @return 适用场景描述
     */
    public String getApplicableScenarios() {
        return switch (this) {
            case NATURAL_ORDER -> "简单问题、调试、基准测试、需要确定性结果";
            case LEAST_CONSTRAINING -> "寻找可行解、优化问题、约束紧密的问题";
            case MOST_CONSTRAINING -> "寻找所有解、快速验证无解、约束松散的问题";
            case RANDOM_ORDER -> "需要多样性、多次运行、避免局部模式";
        };
    }

    /**
     * 获取策略的优缺点
     *
     * @return 优缺点描述
     */
    public String getProsAndCons() {
        return switch (this) {
            case NATURAL_ORDER -> "优点：简单快速，结果可重现；缺点：可能效率不高";
            case LEAST_CONSTRAINING -> "优点：提高找解概率，保留选择空间；缺点：计算开销大";
            case MOST_CONSTRAINING -> "优点：快速剪枝，尽早发现冲突；缺点：可能错过解";
            case RANDOM_ORDER -> "优点：增加多样性，避免固定模式；缺点：结果不可重现";
        };
    }

    /**
     * 获取与变量选择策略的最佳组合
     *
     * @param variableStrategy 变量选择策略
     * @return 组合效果描述
     */
    public String getBestCombinationWith(VariableSelectionStrategy variableStrategy) {
        return switch (variableStrategy) {
            case FIRST_UNASSIGNED -> switch (this) {
                case NATURAL_ORDER -> "基础组合，适合简单问题";
                case LEAST_CONSTRAINING -> "提升基础策略效果";
                case MOST_CONSTRAINING -> "快速验证，适合调试";
                case RANDOM_ORDER -> "增加随机性，避免偏见";
            };
            case MINIMUM_REMAINING_VALUES -> switch (this) {
                case NATURAL_ORDER -> "经典组合，平衡效率和效果";
                case LEAST_CONSTRAINING -> "最佳组合，广泛使用";
                case MOST_CONSTRAINING -> "快速剪枝组合";
                case RANDOM_ORDER -> "MRV+随机，适合多样性需求";
            };
            case DEGREE_HEURISTIC -> switch (this) {
                case NATURAL_ORDER -> "度启发式+自然顺序";
                case LEAST_CONSTRAINING -> "强力组合，适合复杂问题";
                case MOST_CONSTRAINING -> "激进策略，快速收敛";
                case RANDOM_ORDER -> "度启发式+随机化";
            };
            case MOST_CONSTRAINING -> switch (this) {
                case NATURAL_ORDER -> "保守的约束优先组合";
                case LEAST_CONSTRAINING -> "平衡的约束处理组合";
                case MOST_CONSTRAINING -> "双重约束优先，极速剪枝";
                case RANDOM_ORDER -> "约束优先+随机化";
            };
        };
    }

    /**
     * 搜索目标枚举
     */
    public enum SearchGoal {
        FIRST_SOLUTION,  // 找第一个解
        ALL_SOLUTIONS,   // 找所有解
        BEST_SOLUTION    // 找最优解
    }
}

