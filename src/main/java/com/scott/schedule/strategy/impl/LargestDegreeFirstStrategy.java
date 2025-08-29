package com.scott.schedule.strategy.impl;

import com.scott.schedule.algorithm.ScheduleChromosome;
import com.scott.schedule.config.GreedyConfig;
import com.scott.schedule.model.Course;
import com.scott.schedule.model.TimeSlot;
import com.scott.schedule.model.Classroom;
import com.scott.schedule.service.DataService;
import com.scott.schedule.strategy.AbstractGreedyStrategy;

import java.util.Comparator;
import java.util.List;

/**
 * 最大度优先贪心策略
 * 优先安排约束数量最多的课程，减少后续安排的复杂性
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public class LargestDegreeFirstStrategy extends AbstractGreedyStrategy {

    public LargestDegreeFirstStrategy(GreedyConfig config, DataService dataService) {
        super(config, dataService);
    }

    @Override
    public ScheduleChromosome execute() {
        System.out.println("🎯 执行最大度优先贪心算法...");

        ScheduleChromosome solution = new ScheduleChromosome();

        // 计算每门课程的度（约束数量）
        List<Course> courses = calculateCourseDegrees();

        // 按度降序排序
        courses.sort(Comparator.comparingDouble(Course::getDegree).reversed());

        // 逐个安排课程的所有课时
        int totalTargetHours = 0;
        int totalScheduledHours = 0;
        
        for (Course course : courses) {
            int scheduledHours = 0;
            int targetHours = course.getHoursPerWeek();
            totalTargetHours += targetHours;

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
            
            totalScheduledHours += scheduledHours;

            if (scheduledHours == targetHours) {
                System.out.printf("   🎉 课程 %s 全部 %d 课时安排完成%n", course.getName(), targetHours);
            } else {
                System.out.printf("   ❌ 课程 %s 仅安排了 %d/%d 课时%n", course.getName(), scheduledHours, targetHours);
            }
        }
        
        // 打印总体排课统计信息
        System.out.println("📊 排课完成统计:");
        System.out.printf("   - 总课程数: %d%n", courses.size());
        System.out.printf("   - 总需排课时数: %d%n", totalTargetHours);
        System.out.printf("   - 总已排课时数: %d%n", totalScheduledHours);
        System.out.printf("   - 排课成功率: %.1f%%%n", (double) totalScheduledHours / totalTargetHours * 100);
        System.out.printf("   - 染色体基因数: %d%n", solution.getGenes().size());

        return solution;
    }

    @Override
    public String getStrategyName() {
        return "最大度优先";
    }

    @Override
    public String getStrategyDescription() {
        return "优先安排约束数量最多的课程，减少后续安排的复杂性";
    }

    @Override
    public double calculatePriority(Course course) {
        return calculateDegreeForCourse(course.getId());
    }

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
     * 计算课程度数（约束数量）
     */
    private List<Course> calculateCourseDegrees() {
        List<Course> courses = getAllCourses();

        for (Course course : courses) {
            double degree = calculateDegreeForCourse(course.getId());
            course.setDegree(degree);
        }

        return courses;
    }

    /**
     * 计算单门课程的度数
     */
    private double calculateDegreeForCourse(String courseId) {
        double degree = 0.0;

        // 考虑各种约束因素
        degree += getTeacherConstraints(courseId) * config.getConstraintWeights().getTeacherWeight();
        degree += getClassroomConstraints(courseId) * config.getConstraintWeights().getClassroomWeight();
        degree += getTimeConstraints(courseId) * config.getConstraintWeights().getTimeWeight();
        degree += getStudentConstraints(courseId) * config.getConstraintWeights().getStudentWeight();

        return degree;
    }

    /**
     * 获取教师约束
     */
    private double getTeacherConstraints(String courseId) {
        // 实现教师约束计算
        Course course = dataService.getCourseById(courseId);
        if (course == null) {
            return 0.0;
        }
        
        double constraints = 0.0;
        
        // 检查教师是否有其他课程在同一时间段
        List<TimeSlot> allTimeSlots = dataService.getAllTimeSlots();
        for (TimeSlot slot : allTimeSlots) {
            // TODO: 这里需要检查教师在该时间段是否已被安排其他课程
            // 目前返回基础约束值
        }
        
        // 教师偏好约束
        constraints += 1.0; // 基础约束值
        
        return constraints;
    }

    /**
     * 获取教室约束
     */
    private double getClassroomConstraints(String courseId) {
        // 实现教室约束计算
        Course course = dataService.getCourseById(courseId);
        if (course == null) {
            return 0.0;
        }
        
        double constraints = 0.0;
        
        // 检查教室容量是否满足学生人数要求
        if (course.getStudentCount() > 0) {
            List<Classroom> classrooms = dataService.getAllClassrooms();
            boolean hasSuitableClassroom = classrooms.stream()
                    .anyMatch(classroom -> classroom.getCapacity() >= course.getStudentCount());
            
            if (!hasSuitableClassroom) {
                constraints += 2.0; // 教室容量不足的惩罚
            }
        }
        
        // 检查教室设备是否满足课程需求
        if (course.getCourseType() != null) {
            // 根据课程类型检查教室设备是否合适
            switch (course.getCourseType().toString()) {
                case "LAB":
                    // 实验课需要实验室设备
                    constraints += 1.5;
                    break;
                case "PRACTICE":
                    // 实践课需要实践设备
                    constraints += 1.0;
                    break;
                case "LECTURE":
                    // 理论课对设备要求较低
                    constraints += 0.3;
                    break;
                default:
                    constraints += 0.5; // 基础设备约束
            }
        }
        
        return constraints;
    }

    /**
     * 获取时间约束
     */
    private double getTimeConstraints(String courseId) {
        // 实现时间约束计算
        Course course = dataService.getCourseById(courseId);
        if (course == null) {
            return 0.0;
        }
        
        double constraints = 0.0;
        
        // 课程时间偏好约束
        if (course.getCourseType() != null) {
            switch (course.getCourseType().toString()) {
                case "LECTURE":
                    // 理论课偏好上午
                    constraints += 0.5;
                    break;
                case "LAB":
                    // 实验课偏好下午
                    constraints += 0.5;
                    break;
                case "PRACTICE":
                    // 实践课偏好下午
                    constraints += 0.5;
                    break;
                default:
                    constraints += 0.3;
            }
        }
        
        // 课程连续性约束
        if (course.getHoursPerWeek() > 1) {
            constraints += 1.0; // 多课时课程需要连续安排
        }
        
        return constraints;
    }

    /**
     * 获取学生约束
     */
    private double getStudentConstraints(String courseId) {
        // 实现学生约束计算
        Course course = dataService.getCourseById(courseId);
        if (course == null) {
            return 0.0;
        }
        
        double constraints = 0.0;
        
        // 学生人数约束
        if (course.getStudentCount() > 100) {
            constraints += 1.5; // 大班课的约束
        } else if (course.getStudentCount() > 50) {
            constraints += 1.0; // 中班课的约束
        } else {
            constraints += 0.5; // 小班课的约束
        }
        
        // 班级冲突约束
        if (course.getClassIds() != null && course.getClassIds().size() > 1) {
            constraints += 0.8; // 多班级课程的约束
        }
        
        // 先修课程约束
        if (course.getPrerequisiteIds() != null && !course.getPrerequisiteIds().isEmpty()) {
            constraints += 1.2; // 有先修课程要求的约束
        }
        
        return constraints;
    }
}
