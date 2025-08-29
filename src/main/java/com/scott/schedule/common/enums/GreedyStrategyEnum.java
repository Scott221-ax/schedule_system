package com.scott.schedule.common.enums;

import lombok.Getter;

/**
 * 贪心算法策略枚举
 * 定义了各种贪心算法的策略类型
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Getter
public enum GreedyStrategyEnum {

    /**
     * 最大度优先策略
     * 优先安排约束最多（度最大）的课程
     * 特点：
     * - 优先处理最难安排的课程
     * - 减少后续选择的复杂性
     * - 适合约束密集的问题
     */
    LARGEST_DEGREE_FIRST("最大度优先", "优先安排约束最多的课程"),

    /**
     * 最小剩余值策略
     * 优先安排剩余可选时间段最少的课程
     * 特点：
     * - 避免课程无法安排的情况
     * - 动态调整优先级
     * - 适合时间段紧张的问题
     */
    MINIMUM_REMAINING_VALUES("最小剩余值", "优先安排剩余可选时间段最少的课程"),

    /**
     * 最早截止时间优先策略
     * 优先安排截止时间最早的课程
     * 特点：
     * - 确保紧急课程优先安排
     * - 适合有时间要求的排课
     * - 类似于任务调度中的EDF算法
     */
    EARLIEST_DEADLINE_FIRST("最早截止时间优先", "优先安排截止时间最早的课程"),

    /**
     * 最大约束优先策略
     * 综合考虑多种约束因素，优先安排约束最复杂的课程
     * <p>
     * 特点：
     * - 综合评估多种约束
     * - 平衡各种因素
     * - 适合复杂约束环境
     */
    MOST_CONSTRAINED_FIRST("最大约束优先", "综合考虑多种约束，优先安排约束最复杂的课程"),

    /**
     * 随机贪心策略
     * 在贪心选择中引入随机性，增加解的多样性
     * <p>
     * 特点：
     * - 增加解的多样性
     * - 避免陷入固定模式
     * - 适合需要多样化解的场景
     */
    RANDOM_GREEDY("随机贪心", "在贪心选择中引入随机性，增加解的多样性"),

    /**
     * 混合贪心策略
     * 结合多种贪心策略的优势，选择最优结果
     * <p>
     * 特点：
     * - 综合多种策略优势
     * - 提高解的质量
     * - 计算时间较长但效果更好
     */
    HYBRID_GREEDY("混合贪心", "结合多种贪心策略的优势，选择最优结果");

    /**
     * 策略名称
     */
    private final String name;

    /**
     * 策略描述
     *
     */
    private final String description;

    /**
     * 构造函数
     *
     * @param name        策略名称
     * @param description 策略描述
     */
    GreedyStrategyEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * 根据问题特征推荐最适合的策略
     *
     * @param courseCount       课程数量
     * @param constraintDensity 约束密度 (0.0-1.0)
     * @param timeSlotCount     时间段数量
     * @param hasDeadlines      是否有截止时间要求
     * @return 推荐的策略
     */
    public static GreedyStrategyEnum recommendStrategy(int courseCount, double constraintDensity,
                                                       int timeSlotCount, boolean hasDeadlines) {

        // 计算时间段紧张程度
        double timeSlotPressure = (double) courseCount / timeSlotCount;

        // 根据不同条件推荐策略
        if (hasDeadlines) {
            return EARLIEST_DEADLINE_FIRST;
        } else if (constraintDensity > 0.7) {
            return LARGEST_DEGREE_FIRST;
        } else if (timeSlotPressure > 0.8) {
            return MINIMUM_REMAINING_VALUES;
        } else if (courseCount > 200) {
            return MOST_CONSTRAINED_FIRST;
        } else {
            return HYBRID_GREEDY;
        }
    }

    /**
     * 获取策略的计算复杂度等级
     *
     * @return 复杂度等级 (1-5, 5最复杂)
     */
    public int getComplexityLevel() {
        return switch (this) {
            case LARGEST_DEGREE_FIRST -> 2;
            case MINIMUM_REMAINING_VALUES -> 4;
            case EARLIEST_DEADLINE_FIRST -> 1;
            case MOST_CONSTRAINED_FIRST -> 3;
            case RANDOM_GREEDY -> 1;
            case HYBRID_GREEDY -> 5;
        };
    }

    /**
     * 获取策略的预期解质量等级
     *
     * @return 解质量等级 (1-5, 5质量最高)
     */
    public int getQualityLevel() {
        return switch (this) {
            case LARGEST_DEGREE_FIRST -> 3;
            case MINIMUM_REMAINING_VALUES -> 4;
            case EARLIEST_DEADLINE_FIRST -> 2;
            case MOST_CONSTRAINED_FIRST -> 4;
            case RANDOM_GREEDY -> 2;
            case HYBRID_GREEDY -> 5;
        };
    }

    /**
     * 判断策略是否适合并行执行
     *
     * @return 是否适合并行执行
     */
    public boolean isParallelizable() {
        return switch (this) {
            case LARGEST_DEGREE_FIRST -> true;
            // 需要动态更新，不适合并行
            case MINIMUM_REMAINING_VALUES -> false;
            case EARLIEST_DEADLINE_FIRST -> true;
            case MOST_CONSTRAINED_FIRST -> true;
            case RANDOM_GREEDY -> true;
            // 可以并行执行多种策略
            case HYBRID_GREEDY -> true;
        };
    }

    /**
     * 获取策略的适用场景描述
     *
     * @return 适用场景描述
     */
    public String getApplicableScenarios() {
        return switch (this) {
            case LARGEST_DEGREE_FIRST -> "约束密集、课程间冲突较多的场景";
            case MINIMUM_REMAINING_VALUES -> "时间段紧张、需要精确安排的场景";
            case EARLIEST_DEADLINE_FIRST -> "有明确时间要求、存在截止时间的场景";
            case MOST_CONSTRAINED_FIRST -> "多种约束并存、需要综合考虑的复杂场景";
            case RANDOM_GREEDY -> "需要多样化解、避免固定模式的场景";
            case HYBRID_GREEDY -> "对解质量要求高、可接受较长计算时间的场景";
        };
    }
}

