package com.scott.schedule.common.enums;

/**
 * 变量选择策略枚举
 * 定义回溯算法中选择下一个变量的不同策略
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public enum VariableSelectionStrategy {

    /**
     * 第一个未赋值变量
     * 按照变量的自然顺序选择第一个未赋值的变量
     *
     * 特点：
     * - 实现简单，执行速度快
     * - 不考虑启发式信息
     * - 适合简单问题或快速原型
     */
    FIRST_UNASSIGNED("第一个未赋值", "按自然顺序选择第一个未赋值变量"),

    /**
     * 最小剩余值 (Minimum Remaining Values, MRV)
     * 选择域大小最小的变量，即可选值最少的变量
     * 特点：
     * - 优先处理最受约束的变量
     * - 能够尽早发现冲突
     * - 减少搜索空间
     * - 是最常用和最有效的启发式之一
     */
    MINIMUM_REMAINING_VALUES("最小剩余值", "选择可选值最少的变量"),

    /**
     * 度启发式 (Degree Heuristic)
     * 选择与最多未赋值变量相关联的变量
     * 特点：
     * - 优先处理影响最多其他变量的变量
     * - 常与MRV结合使用作为平局打破器
     * - 适合约束图密集的问题
     */
    DEGREE_HEURISTIC("度启发式", "选择与最多未赋值变量相关的变量"),

    /**
     * 最约束变量 (Most Constraining Variable)
     * 选择对其他变量约束最多的变量
     * 特点：
     * - 综合考虑变量的约束影响
     * - 优先解决最复杂的约束关系
     * - 适合复杂约束网络
     */
    MOST_CONSTRAINING("最约束变量", "选择对其他变量约束最多的变量");

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
    VariableSelectionStrategy(String name, String description) {
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
     * @return 复杂度等级 (1-4, 4最复杂)
     */
    public int getComplexity() {
        return switch (this) {
            case FIRST_UNASSIGNED -> 1;
            case MINIMUM_REMAINING_VALUES -> 2;
            case DEGREE_HEURISTIC -> 3;
            case MOST_CONSTRAINING -> 4;
        };
    }

    /**
     * 获取策略的预期效果
     *
     * @return 效果等级 (1-4, 4效果最好)
     */
    public int getEffectiveness() {
        return switch (this) {
            case FIRST_UNASSIGNED -> 1;
            case MINIMUM_REMAINING_VALUES -> 4;
            case DEGREE_HEURISTIC -> 3;
            case MOST_CONSTRAINING -> 3;
        };
    }

    /**
     * 判断策略是否需要动态信息
     *
     * @return 是否需要动态信息
     */
    public boolean requiresDynamicInfo() {
        return switch (this) {
            case FIRST_UNASSIGNED -> false;
            case MINIMUM_REMAINING_VALUES -> true;
            case DEGREE_HEURISTIC -> true;
            case MOST_CONSTRAINING -> true;
        };
    }

    /**
     * 根据问题特征推荐最适合的策略
     *
     * @param variableCount 变量数量
     * @param constraintDensity 约束密度
     * @param domainSize 平均域大小
     * @return 推荐的策略
     */
    public static VariableSelectionStrategy recommendStrategy(int variableCount,
                                                            double constraintDensity,
                                                            int domainSize) {

        // 小规模问题可以使用简单策略
        if (variableCount < 20) {
            return FIRST_UNASSIGNED;
        }

        // 约束密集的问题适合使用度启发式
        if (constraintDensity > 0.7) {
            return DEGREE_HEURISTIC;
        }

        // 域大小差异较大的问题适合MRV
        if (domainSize > 10) {
            return MINIMUM_REMAINING_VALUES;
        }

        // 复杂问题使用最约束变量策略
        if (variableCount > 100) {
            return MOST_CONSTRAINING;
        }

        // 默认使用MRV，这是最通用的策略
        return MINIMUM_REMAINING_VALUES;
    }

    /**
     * 获取策略的适用场景
     *
     * @return 适用场景描述
     */
    public String getApplicableScenarios() {
        return switch (this) {
            case FIRST_UNASSIGNED -> "简单问题、快速原型、基准测试";
            case MINIMUM_REMAINING_VALUES -> "一般约束满足问题、域大小变化大的问题";
            case DEGREE_HEURISTIC -> "约束密集问题、图着色问题、作为MRV的平局打破器";
            case MOST_CONSTRAINING -> "复杂约束网络、多类型约束、大规模问题";
        };
    }

    /**
     * 获取与其他策略的组合建议
     *
     * @return 组合建议
     */
    public String getCombinationAdvice() {
        return switch (this) {
            case FIRST_UNASSIGNED -> "可与随机化结合增加多样性";
            case MINIMUM_REMAINING_VALUES -> "常与度启发式结合作为平局打破器";
            case DEGREE_HEURISTIC -> "常作为MRV的辅助策略，处理平局情况";
            case MOST_CONSTRAINING -> "可与动态重排序结合，适应搜索过程中的变化";
        };
    }
}

