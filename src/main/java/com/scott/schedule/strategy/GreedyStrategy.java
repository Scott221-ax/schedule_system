package com.scott.schedule.strategy;

import com.scott.schedule.algorithm.ScheduleChromosome;
import com.scott.schedule.model.Course;
import com.scott.schedule.model.TimeSlot;

/**
 * 贪心策略接口
 * 定义所有贪心策略需要实现的核心方法
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public interface GreedyStrategy {

    /**
     * 执行贪心算法
     *
     * @return 排课解
     */
    ScheduleChromosome execute();

    /**
     * 获取策略名称
     *
     * @return 策略名称
     */
    String getStrategyName();

    /**
     * 获取策略描述
     *
     * @return 策略描述
     */
    String getStrategyDescription();

    /**
     * 计算课程优先级
     *
     * @param course 课程信息
     * @return 优先级分数
     */
    double calculatePriority(Course course);

    /**
     * 选择最佳时间段
     *
     * @param course 课程信息
     * @param solution 当前解
     * @return 最佳时间段
     */
    TimeSlot selectBestTimeSlot(Course course, ScheduleChromosome solution);
}
