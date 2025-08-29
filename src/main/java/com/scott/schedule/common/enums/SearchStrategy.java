package com.scott.schedule.common.enums;

/**
 * 搜索策略枚举
 * 定义回溯算法的整体搜索策略
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public enum SearchStrategy {

    /**
     * 深度优先搜索 (Depth-First Search)
     * 标准的回溯搜索策略，深入搜索直到找到解或无解
     *
     * 特点：
     * - 内存需求小
     * - 适合寻找第一个解
     * - 可能陷入深层无解分支
     */
    DEPTH_FIRST("深度优先搜索", "深入搜索直到找到解或无解"),

    /**
     * 广度优先搜索 (Breadth-First Search)
     * 按层次搜索，保证找到最短路径的解
     *
     * 特点：
     * - 找到最优解（如果存在）
     * - 内存需求大
     * - 适合寻找最短解
     */
    BREADTH_FIRST("广度优先搜索", "按层次搜索，保证找到最短路径解"),

    /**
     * 限制深度搜索 (Depth-Limited Search)
     * 限制搜索深度的深度优先搜索
     *
     * 特点：
     * - 避免无限深入
     * - 可能错过深层解
     * - 适合有深度限制的问题
     */
    DEPTH_LIMITED("限制深度搜索", "限制搜索深度的深度优先搜索"),

    /**
     * 迭代加深搜索 (Iterative Deepening)
     * 逐步增加深度限制的搜索
     *
     * 特点：
     * - 结合深度和广度优先的优点
     * - 找到最短解且内存需求小
     * - 有重复计算开销
     */
    ITERATIVE_DEEPENING("迭代加深搜索", "逐步增加深度限制的搜索");

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
    SearchStrategy(String name, String description) {
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
     * 获取策略的内存需求等级
     *
     * @return 内存需求等级 (1-4, 4需求最大)
     */
    public int getMemoryRequirement() {
        return switch (this) {
            case DEPTH_FIRST -> 1;
            case DEPTH_LIMITED -> 1;
            case ITERATIVE_DEEPENING -> 2;
            case BREADTH_FIRST -> 4;
        };
    }

    /**
     * 获取策略的时间复杂度等级
     *
     * @return 时间复杂度等级 (1-4, 4最复杂)
     */
    public int getTimeComplexity() {
        return switch (this) {
            case DEPTH_FIRST -> 2;
            case DEPTH_LIMITED -> 2;
            case BREADTH_FIRST -> 3;
            case ITERATIVE_DEEPENING -> 4;
        };
    }

    /**
     * 判断策略是否保证找到最优解
     *
     * @return 是否保证最优解
     */
    public boolean guaranteesOptimalSolution() {
        return switch (this) {
            case DEPTH_FIRST -> false;
            case DEPTH_LIMITED -> false;
            case BREADTH_FIRST -> true;
            case ITERATIVE_DEEPENING -> true;
        };
    }

    /**
     * 获取策略的适用场景
     *
     * @return 适用场景描述
     */
    public String getApplicableScenarios() {
        return switch (this) {
            case DEPTH_FIRST -> "寻找任意解、内存受限、深度有限的问题";
            case BREADTH_FIRST -> "寻找最优解、解的深度较浅的问题";
            case DEPTH_LIMITED -> "已知解的大致深度、避免无限搜索";
            case ITERATIVE_DEEPENING -> "寻找最优解且内存受限、不知道解的深度";
        };
    }
}

