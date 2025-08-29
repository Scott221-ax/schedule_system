package com.scott.schedule.common.enums;

/**
 * 平局打破策略枚举
 * 当多个选择具有相同评分时，用于决定如何打破平局
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public enum TieBreakingStrategy {

    /**
     * 随机选择
     * 从平局的选项中随机选择一个
     *
     * 特点：
     * - 简单快速
     * - 增加解的多样性
     * - 避免固定偏好
     */
    RANDOM("随机选择"),

    /**
     * 选择第一个
     * 总是选择平局选项中的第一个
     *
     * 特点：
     * - 确定性选择
     * - 执行速度最快
     * - 结果可重现
     */
    FIRST("选择第一个"),

    /**
     * 选择最后一个
     * 总是选择平局选项中的最后一个
     *
     * 特点：
     * - 确定性选择
     * - 与FIRST相反的偏好
     * - 结果可重现
     */
    LAST("选择最后一个"),

    /**
     * 最小索引优先
     * 选择索引最小的选项
     *
     * 特点：
     * - 偏向较早创建的对象
     * - 确定性选择
     * - 适合有优先级概念的场景
     */
    MIN_INDEX("最小索引优先"),

    /**
     * 最大索引优先
     * 选择索引最大的选项
     *
     * 特点：
     * - 偏向较晚创建的对象
     * - 确定性选择
     * - 适合需要新颖性的场景
     */
    MAX_INDEX("最大索引优先"),

    /**
     * 最小ID优先
     * 选择ID最小的选项
     *
     * 特点：
     * - 基于对象标识符
     * - 确定性选择
     * - 适合有自然排序的场景
     */
    MIN_ID("最小ID优先"),

    /**
     * 最大ID优先
     * 选择ID最大的选项
     *
     * 特点：
     * - 基于对象标识符
     * - 确定性选择
     * - 与MIN_ID相反的偏好
     */
    MAX_ID("最大ID优先"),

    /**
     * 最小约束优先
     * 选择剩余约束最少的选项
     *
     * 特点：
     * - 基于约束数量
     * - 倾向于选择更自由的选项
     * - 适合约束满足问题
     */
    MIN_CONSTRAINTS("最小约束优先"),

    /**
     * 最大约束优先
     * 选择剩余约束最多的选项
     *
     * 特点：
     * - 基于约束数量
     * - 倾向于选择更受限的选项
     * - 适合需要谨慎选择的场景
     */
    MAX_CONSTRAINTS("最大约束优先"),

    /**
     * 最小度数优先
     * 选择度数（连接数）最小的选项
     *
     * 特点：
     * - 基于图论概念
     * - 倾向于选择连接较少的节点
     * - 适合图着色等问题
     */
    MIN_DEGREE("最小度数优先"),

    /**
     * 最大度数优先
     * 选择度数（连接数）最大的选项
     *
     * 特点：
     * - 基于图论概念
     * - 倾向于选择连接较多的节点
     * - 适合需要处理复杂关系的场景
     */
    MAX_DEGREE("最大度数优先");

    /**
     * 策略描述
     */
    private final String description;

    /**
     * 构造函数
     *
     * @param description 策略描述
     */
    TieBreakingStrategy(String description) {
        this.description = description;
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
     * 判断策略是否为随机策略
     *
     * @return 是否为随机策略
     */
    public boolean isRandom() {
        return this == RANDOM;
    }

    /**
     * 判断策略是否为确定性策略
     *
     * @return 是否为确定性策略
     */
    public boolean isDeterministic() {
        return !isRandom();
    }

    /**
     * 获取策略的计算复杂度
     *
     * @return 复杂度等级 (1-3, 3最复杂)
     */
    public int getComplexity() {
        return switch (this) {
            case RANDOM, FIRST, LAST -> 1;
            case MIN_INDEX, MAX_INDEX, MIN_ID, MAX_ID -> 2;
            case MIN_CONSTRAINTS, MAX_CONSTRAINTS, MIN_DEGREE, MAX_DEGREE -> 3;
        };
    }

    /**
     * 根据问题特征推荐最适合的平局打破策略
     *
     * @param needsDiversity 是否需要多样性
     * @param needsDeterminism 是否需要确定性
     * @param hasConstraints 是否有约束信息
     * @param hasGraphStructure 是否有图结构
     * @return 推荐的策略
     */
    public static TieBreakingStrategy recommendStrategy(boolean needsDiversity,
                                                       boolean needsDeterminism,
                                                       boolean hasConstraints,
                                                       boolean hasGraphStructure) {

        if (needsDeterminism) {
            if (hasGraphStructure) {
                return MAX_DEGREE; // 图结构问题优先处理复杂节点
            } else if (hasConstraints) {
                return MIN_CONSTRAINTS; // 约束问题优先选择自由度高的选项
            } else {
                return FIRST; // 简单确定性选择
            }
        } else if (needsDiversity) {
            return RANDOM; // 需要多样性时使用随机策略
        } else {
            return MIN_INDEX; // 默认使用最小索引策略
        }
    }

    /**
     * 获取策略的适用场景
     *
     * @return 适用场景描述
     */
    public String getApplicableScenarios() {
        return switch (this) {
            case RANDOM -> "需要解的多样性、避免偏见的场景";
            case FIRST -> "需要快速决策、结果可重现的场景";
            case LAST -> "偏好后来选项、需要确定性的场景";
            case MIN_INDEX -> "有自然顺序、偏好早期选项的场景";
            case MAX_INDEX -> "偏好新颖性、后期选项的场景";
            case MIN_ID -> "基于标识符排序、需要一致性的场景";
            case MAX_ID -> "偏好高ID对象、需要确定性的场景";
            case MIN_CONSTRAINTS -> "约束满足问题、偏好自由度高的场景";
            case MAX_CONSTRAINTS -> "需要谨慎选择、处理复杂约束的场景";
            case MIN_DEGREE -> "图着色问题、偏好简单节点的场景";
            case MAX_DEGREE -> "网络问题、需要处理关键节点的场景";
        };
    }
}

