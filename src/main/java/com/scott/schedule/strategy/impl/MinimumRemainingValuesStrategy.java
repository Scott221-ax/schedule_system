package com.scott.schedule.strategy.impl;

import com.scott.schedule.algorithm.ScheduleChromosome;
import com.scott.schedule.config.GreedyConfig;
import com.scott.schedule.model.Course;
import com.scott.schedule.model.TimeSlot;
import com.scott.schedule.service.DataService;
import com.scott.schedule.strategy.AbstractGreedyStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

/**
 * 最小剩余值贪心策略
 * 优先安排剩余可选时间段最少的课程，减少搜索空间
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public class MinimumRemainingValuesStrategy extends AbstractGreedyStrategy {

    public MinimumRemainingValuesStrategy(GreedyConfig config, DataService dataService) {
        super(config, dataService);
    }

    @Override
    public ScheduleChromosome execute() {
        System.out.println("🎯 执行最小剩余值贪心算法...");

        ScheduleChromosome solution = new ScheduleChromosome();

        // 计算每门课程的约束度
        List<Course> courses = calculateCourseConstraints();

        // 按约束度降序排序
        courses.sort(Comparator.comparingDouble(Course::getDegree).reversed());

        // 逐个安排课程的所有课时
        for (Course course : courses) {
            int scheduledHours = 0;
            int targetHours = course.getHoursPerWeek();

            System.out.printf("📚 安排课程: %s，需要安排 %d 课时%n", course.getName(), targetHours);

            // 为每门课程安排所需的课时数
            while (scheduledHours < targetHours) {
                TimeSlot bestSlot = selectBestTimeSlot(course, solution);
                if (bestSlot != null) {
                    assignCourseToSlot(solution, course, bestSlot, scheduledHours + 1);
                    scheduledHours++;
                    System.out.printf("   ✅ 第 %d 课时已安排到时间段 %s%n", scheduledHours, bestSlot.getId());
                } else {
                    System.out.printf("   ⚠️ 第 %d 课时无法安排%n", scheduledHours + 1);
                    handleUnassignableCourse(course);
                    break; // 无法继续安排，跳出循环
                }
            }

            if (scheduledHours == targetHours) {
                System.out.printf("   🎉 课程 %s 全部 %d 课时安排完成%n", course.getName(), targetHours);
            } else {
                System.out.printf("   ❌ 课程 %s 仅安排了 %d/%d 课时%n", course.getName(), scheduledHours, targetHours);
            }
        }

        return solution;
    }

    @Override
    public String getStrategyName() {
        return "最小剩余值";
    }

    @Override
    public String getStrategyDescription() {
        return "优先安排剩余可选时间段最少的课程，减少搜索空间";
    }

    /**
     * 计算课程优先级
     *
     * @param course 课程信息
     * @return 优先级分数
     */
    @Override
    public double calculatePriority(Course course) {
        // 计算剩余可选时间段数量
        List<TimeSlot> availableSlots = getAvailableTimeSlots(course, new ScheduleChromosome());
        return availableSlots.size();
    }

    /**
     * 选择最佳时间段
     *
     * @param course   课程信息
     * @param solution 当前解
     * @return 最佳时间段
     */
    @Override
    public TimeSlot selectBestTimeSlot(Course course, ScheduleChromosome solution) {
        List<TimeSlot> availableSlots = getAvailableTimeSlots(course, solution);

        if (availableSlots.isEmpty()) {
            return null;
        }

        // 根据评估函数选择最佳时间段
        return availableSlots.stream()
                .min(Comparator.comparingDouble(slot -> evaluateTimeSlot(course, slot, solution)))
                .orElse(null);
    }

    /**
     * 计算课程约束度
     */
    private List<Course> calculateCourseConstraints() {
        List<Course> courses = getAllCourses();

        for (Course course : courses) {
            // 计算剩余可选时间段数量作为约束度
            List<TimeSlot> availableSlots = getAvailableTimeSlots(course, new ScheduleChromosome());
            double constraintDegree = availableSlots.size();
            course.setDegree(constraintDegree);
        }

        return courses;
    }
}
