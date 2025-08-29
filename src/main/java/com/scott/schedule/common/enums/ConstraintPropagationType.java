package com.scott.schedule.common.enums;

/**
 * 约束传播类型枚举
 * 定义回溯算法中使用的不同约束传播算法
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public enum ConstraintPropagationType {

    /**
     * AC-3算法 (Arc Consistency 3)
     * 经典的弧一致性算法，确保每个约束的弧都是一致的
     *
     * 特点：
     * - 最常用的约束传播算法
     * - 时间复杂度O(ed³)，其中e是约束数，d是域大小
     * - 适合大多数约束满足问题
     */
    AC3("AC-3算法", "弧一致性算法，确保约束弧的一致性"),

    /**
     * AC-4算法 (Arc Consistency 4)
     * 改进的弧一致性算法，使用更精细的数据结构
     *
     * 特点：
     * - 在某些情况下比AC-3更高效
     * - 时间复杂度O(ed²)
     * - 空间复杂度较高
     */
    AC4("AC-4算法", "改进的弧一致性算法，使用精细数据结构"),

    /**
     * 前向检查 (Forward Checking)
     * 在赋值后立即检查相邻变量的域
     *
     * 特点：
     * - 简单高效的约束传播
     * - 只检查直接相关的变量
     * - 计算开销小
     */
    FORWARD_CHECKING("前向检查", "赋值后检查相邻变量域的一致性"),

    /**
     * 路径一致性 (Path Consistency)
     * 确保任意两个变量之间的路径都是一致的
     *
     * 特点：
     * - 比弧一致性更强的约束传播
     * - 计算开销较大
     * - 适合复杂约束网络
     */
    PATH_CONSISTENCY("路径一致性", "确保变量间路径的一致性"),

    /**
     * 无约束传播
     * 不进行任何约束传播，纯回溯搜索
     *
     * 特点：
     * - 最简单的策略
     * - 计算开销最小
     * - 搜索空间最大
     */
    NONE("无约束传播", "不进行约束传播的纯回溯搜索");

    /**
     * 算法名称
     */
    private final String name;

    /**
     * 算法描述
     */
    private final String description;

    /**
     * 构造函数
     *
     * @param name 算法名称
     * @param description 算法描述
     */
    ConstraintPropagationType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * 获取算法名称
     *
     * @return 算法名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取算法描述
     *
     * @return 算法描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取算法的计算复杂度等级
     *
     * @return 复杂度等级 (1-5, 5最复杂)
     */
    public int getComplexity() {
        return switch (this) {
            case NONE -> 1;
            case FORWARD_CHECKING -> 2;
            case AC3 -> 3;
            case AC4 -> 4;
            case PATH_CONSISTENCY -> 5;
        };
    }

    /**
     * 获取算法的约束传播强度
     *
     * @return 传播强度等级 (1-5, 5最强)
     */
    public int getPropagationStrength() {
        return switch (this) {
            case NONE -> 1;
            case FORWARD_CHECKING -> 2;
            case AC3 -> 3;
            case AC4 -> 3;
            case PATH_CONSISTENCY -> 5;
        };
    }

    /**
     * 获取算法的适用场景
     *
     * @return 适用场景描述
     */
    public String getApplicableScenarios() {
        return switch (this) {
            case NONE -> "简单问题、快速原型、性能敏感场景";
            case FORWARD_CHECKING -> "中等复杂度问题、平衡性能和效果";
            case AC3 -> "一般约束满足问题、标准选择";
            case AC4 -> "大域问题、需要高效传播的场景";
            case PATH_CONSISTENCY -> "复杂约束网络、高质量解需求";
        };
    }
}

