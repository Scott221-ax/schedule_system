package com.scott.schedule.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 排课约束配置模型
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Data
@NoArgsConstructor
public class ScheduleConstraints {

    /**
     * 硬约束列表
     * 必须满足的约束条件
     */
    @JsonProperty("hardConstraints")
    private Map<String, String> hardConstraints = new HashMap<>();

    /**
     * 软约束列表
     * 尽量满足的约束条件，带权重
     */
    @JsonProperty("softConstraints")
    private Map<String, SoftConstraint> softConstraints = new HashMap<>();

    /**
     * 添加硬约束
     *
     * @param name 约束名称
     * @param description 约束描述
     */
    public void addHardConstraint(String name, String description) {
        hardConstraints.put(name, description);
    }

    /**
     * 添加软约束
     *
     * @param name 约束名称
     * @param description 约束描述
     * @param weight 权重 (0.0-1.0)
     */
    public void addSoftConstraint(String name, String description, double weight) {
        softConstraints.put(name, new SoftConstraint(description, weight));
    }

    /**
     * 软约束内部类
     */
    @Data
    @NoArgsConstructor
    public static class SoftConstraint {
        @JsonProperty("description")
        private String description;

        @JsonProperty("weight")
        private double weight;

        @JsonProperty("enabled")
        private boolean enabled = true;

        public SoftConstraint(String description, double weight) {
            this.description = description;
            this.weight = weight;
        }
    }
}

