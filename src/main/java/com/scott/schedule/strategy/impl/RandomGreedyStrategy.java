package com.scott.schedule.strategy.impl;

import com.scott.schedule.algorithm.ScheduleChromosome;
import com.scott.schedule.config.GreedyConfig;
import com.scott.schedule.model.Course;
import com.scott.schedule.model.TimeSlot;
import com.scott.schedule.service.DataService;
import com.scott.schedule.strategy.AbstractGreedyStrategy;

import java.util.Collections;
import java.util.List;

/**
 * 随机贪心策略
 * 在贪心选择中引入随机性，增加解的多样性
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public class RandomGreedyStrategy extends AbstractGreedyStrategy {

    public RandomGreedyStrategy(GreedyConfig config, DataService dataService) {
        super(config, dataService);
    }

    @Override
    public ScheduleChromosome execute() {
        System.out.println("🎯 执行随机贪心算法...");

        ScheduleChromosome solution = new ScheduleChromosome();
        List<Course> courses = getAllCourses();

        // 随机打乱课程顺序
        Collections.shuffle(courses, random);

        // 逐个安排课程的所有课时
        for (Course course : courses) {
            int scheduledHours = 0;
            int targetHours = course.getHoursPerWeek();

            System.out.printf("📚 安排课程: %s，需要安排 %d 课时%n", course.getName(), targetHours);

            // 为每门课程安排所需的课时数
            while (scheduledHours < targetHours) {
                // 获取候选时间段
                List<TimeSlot> candidates = getCandidateTimeSlots(course, solution);

                if (!candidates.isEmpty()) {
                    // 使用随机化选择
                    TimeSlot selectedSlot = selectRandomizedSlot(candidates);
                    assignCourseToSlot(solution, course, selectedSlot, scheduledHours + 1);
                    scheduledHours++;
                    System.out.printf("   ✅ 第 %d 课时已安排到时间段 %s%n", scheduledHours, selectedSlot.getId());
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
        return "随机贪心";
    }

    @Override
    public String getStrategyDescription() {
        return "在贪心选择中引入随机性，增加解的多样性";
    }

    /**
     * 计算课程优先级
     *
     * @param course 课程信息
     * @return 优先级分数
     */
    @Override
    public double calculatePriority(Course course) {
        return 0;
    }

    @Override
    public TimeSlot selectBestTimeSlot(Course course, ScheduleChromosome solution) {
        List<TimeSlot> candidates = getCandidateTimeSlots(course, solution);
        
        if (candidates.isEmpty()) {
            return null;
        }

        // 随机化选择
        return selectRandomizedSlot(candidates);
    }

    /**
     * 增强的随机化选择
     * 结合贪心选择和随机选择
     */
    protected TimeSlot selectRandomizedSlotEnhanced(List<TimeSlot> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }

        double randomFactor = config.getRandomizationFactor();
        
        // 根据随机化因子决定选择策略
        if (random.nextDouble() < randomFactor) {
            // 完全随机选择
            return candidates.get(random.nextInt(candidates.size()));
        } else if (random.nextDouble() < 0.7) {
            // 70% 概率选择前3个最优的
            int topCount = Math.min(3, candidates.size());
            return candidates.get(random.nextInt(topCount));
        } else {
            // 30% 概率选择最优的
            return candidates.get(0);
        }
    }
}
