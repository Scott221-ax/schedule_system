package com.scott.schedule.strategy.impl;

import com.scott.schedule.algorithm.ScheduleChromosome;
import com.scott.schedule.config.GreedyConfig;
import com.scott.schedule.model.Course;
import com.scott.schedule.model.TimeSlot;
import com.scott.schedule.service.DataService;
import com.scott.schedule.strategy.AbstractGreedyStrategy;

import java.util.Comparator;
import java.util.List;

/**
 * 最大约束优先贪心策略
 * 综合考虑多种约束因素，优先安排约束最复杂的课程
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public class MostConstrainedFirstStrategy extends AbstractGreedyStrategy {

    public MostConstrainedFirstStrategy(GreedyConfig config, DataService dataService) {
        super(config, dataService);
    }

    @Override
    public ScheduleChromosome execute() {
        System.out.println("🎯 执行最大约束优先贪心算法...");

        ScheduleChromosome solution = new ScheduleChromosome();

        // 计算综合约束分数
        List<Course> courses = calculateConstraintScores();

        // 按约束分数降序排序
        courses.sort(Comparator.comparingDouble((Course course) -> course.getDegree()).reversed());

        // 逐个安排课程的所有课时
        for (Course course : courses) {
            int scheduledHours = 0;
            int targetHours = course.getHoursPerWeek();

            System.out.printf("📚 安排课程: %s，需要安排 %d 课时%n", course.getName(), targetHours);

            // 为每门课程安排所需的课时数
            while (scheduledHours < targetHours) {
                TimeSlot bestSlot = selectOptimalTimeSlot(course, solution);
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
        return "最大约束优先";
    }

    @Override
    public String getStrategyDescription() {
        return "综合考虑多种约束因素，优先安排约束最复杂的课程";
    }

    /**
     * 计算课程优先级
     *
     * @param course 课程信息
     * @return 优先级分数
     */
    @Override
    public double calculatePriority(Course course) {
        return calculateConstraintScore(course);
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
        return selectOptimalTimeSlot(course, solution);
    }

    /**
     * 计算综合约束分数
     */
    private List<Course> calculateConstraintScores() {
        List<Course> courses = getAllCourses();

        for (Course course : courses) {
            double constraintScore = calculateConstraintScore(course);
            course.setDegree(constraintScore);
        }

        return courses;
    }

    /**
     * 计算单门课程的综合约束分数
     */
    private double calculateConstraintScore(Course course) {
        double score = 0.0;

        // 教师约束
        score += calculateTeacherConstraintScore(course) * config.getConstraintWeights().getTeacherWeight();
        
        // 教室约束
        score += calculateClassroomConstraintScore(course) * config.getConstraintWeights().getClassroomWeight();
        
        // 时间约束
        score += calculateTimeConstraintScore(course) * config.getConstraintWeights().getTimeWeight();
        
        // 学生约束
        score += calculateStudentConstraintScore(course) * config.getConstraintWeights().getStudentWeight();
        
        // 课程间约束
        score += calculateInterCourseConstraintScore(course) * config.getConstraintWeights().getContinuityWeight();

        return score;
    }

    /**
     * 计算教师约束分数
     */
    private double calculateTeacherConstraintScore(Course course) {
        // 实现教师约束计算
        // 考虑教师可用时间、专业匹配度等
        return 0.0;
    }

    /**
     * 计算教室约束分数
     */
    private double calculateClassroomConstraintScore(Course course) {
        // 实现教室约束计算
        // 考虑教室容量、设备要求等
        return 0.0;
    }

    /**
     * 计算时间约束分数
     */
    private double calculateTimeConstraintScore(Course course) {
        // 实现时间约束计算
        // 考虑课程时长、时间偏好等
        return 0.0;
    }

    /**
     * 计算学生约束分数
     */
    private double calculateStudentConstraintScore(Course course) {
        // 实现学生约束计算
        // 考虑学生数量、专业要求等
        return 0.0;
    }

    /**
     * 计算课程间约束分数
     */
    private double calculateInterCourseConstraintScore(Course course) {
        // 实现课程间约束计算
        // 考虑前置课程、冲突课程等
        return 0.0;
    }

    /**
     * 选择最优时间段
     */
    private TimeSlot selectOptimalTimeSlot(Course course, ScheduleChromosome solution) {
        List<TimeSlot> availableSlots = getAvailableTimeSlots(course, solution);

        if (availableSlots.isEmpty()) {
            return null;
        }

        // 使用更复杂的评估函数选择最优时间段
        return availableSlots.stream()
                .min(Comparator.comparingDouble(slot -> evaluateOptimalTimeSlot(course, slot, solution)))
                .orElse(null);
    }

    /**
     * 评估最优时间段
     */
    private double evaluateOptimalTimeSlot(Course course, TimeSlot slot, ScheduleChromosome solution) {
        double score = 0.0;

        // 基础评估
        score += evaluateTimeSlot(course, slot, solution);

        // 额外考虑因素
        score += evaluateSlotEfficiency(course, slot, solution);
        score += evaluateSlotFlexibility(course, slot, solution);
        score += evaluateSlotConflict(course, slot, solution);

        return score;
    }

    /**
     * 评估时间段效率
     */
    private double evaluateSlotEfficiency(Course course, TimeSlot slot, ScheduleChromosome solution) {
        // 实现时间段效率评估
        return 0.0;
    }

    /**
     * 评估时间段灵活性
     */
    private double evaluateSlotFlexibility(Course course, TimeSlot slot, ScheduleChromosome solution) {
        // 实现时间段灵活性评估
        return 0.0;
    }

    /**
     * 评估时间段冲突
     */
    private double evaluateSlotConflict(Course course, TimeSlot slot, ScheduleChromosome solution) {
        // 实现时间段冲突评估
        return 0.0;
    }
}
