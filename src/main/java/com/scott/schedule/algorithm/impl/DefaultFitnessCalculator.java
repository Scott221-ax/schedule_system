package com.scott.schedule.algorithm.impl;

import com.scott.schedule.algorithm.FitnessCalculator;
import com.scott.schedule.algorithm.ScheduleChromosome;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 默认适应度计算器实现
 * 使用JDK 21的新特性：模式匹配、switch表达式、记录类等
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Component
public class DefaultFitnessCalculator implements FitnessCalculator {
    
    @Override
    public double calculate(ScheduleChromosome chromosome) {
        double fitness = 1.0;
        int violations = 0;
        
        // 使用JDK 21的模式匹配和switch表达式
        var constraintResult = switch (chromosome.getGenes().size()) {
            case 0 -> new ConstraintResult(0, "无课程安排");
            case 1 -> new ConstraintResult(1, "单课程安排");
            default -> checkAllConstraints(chromosome);
        };
        
        violations = constraintResult.violations();
        
        // 计算最终适应度
        if (violations > 0) {
            fitness = 1.0 / (1.0 + violations);
        }
        
        chromosome.setConstraintViolations(violations);
        return fitness;
    }
    
    /**
     * 检查所有约束
     * 使用JDK 21的函数式编程特性
     */
    private ConstraintResult checkAllConstraints(ScheduleChromosome chromosome) {
        var genes = chromosome.getGenes();
        
        // 硬约束检查
        var hardViolations = checkHardConstraints(genes);
        
        // 软约束检查
        var softViolations = checkSoftConstraints(genes);
        
        var totalViolations = hardViolations + softViolations;
        var description = "硬约束违反: %d, 软约束违反: %d".formatted(hardViolations, softViolations);
        
        return new ConstraintResult(totalViolations, description);
    }
    
    /**
     * 检查硬约束
     * 使用JDK 21的Stream API和函数式编程
     */
    private int checkHardConstraints(Map<Long, ScheduleChromosome.CourseSchedule> genes) {
        var violations = 0;
        
        // 检查教师时间冲突
        violations += checkTeacherTimeConflicts(genes);
        
        // 检查教室时间冲突
        violations += checkClassroomTimeConflicts(genes);
        
        // 检查班级时间冲突
        violations += checkClassTimeConflicts(genes);
        
        // 检查课程时间冲突
        violations += checkCourseTimeConflicts(genes);
        
        return violations;
    }
    
    /**
     * 检查软约束
     */
    private int checkSoftConstraints(Map<Long, ScheduleChromosome.CourseSchedule> genes) {
        var violations = 0;
        
        // 检查教师偏好时间
        violations += checkTeacherPreferences(genes);
        
        // 检查教室容量匹配
        violations += checkClassroomCapacity(genes);
        
        // 检查课程连续性
        violations += checkCourseContinuity(genes);
        
        // 检查休息时间安排
        violations += checkBreakTimeArrangement(genes);
        
        return violations;
    }
    
    /**
     * 检查教师时间冲突
     */
    private int checkTeacherTimeConflicts(Map<Long, ScheduleChromosome.CourseSchedule> genes) {
        // 实现具体的教师时间冲突检查逻辑
        int violations = 0;
        
        // 按教师ID分组，检查每个教师的时间安排
        Map<Long, List<ScheduleChromosome.CourseSchedule>> teacherSchedules = genes.values().stream()
                .collect(Collectors.groupingBy(ScheduleChromosome.CourseSchedule::teacherId));
        
        for (Map.Entry<Long, List<ScheduleChromosome.CourseSchedule>> entry : teacherSchedules.entrySet()) {
            List<ScheduleChromosome.CourseSchedule> schedules = entry.getValue();
            
            // 检查同一教师是否有多个课程在同一时间段
            Map<Long, List<ScheduleChromosome.CourseSchedule>> timeSlotGroups = schedules.stream()
                    .collect(Collectors.groupingBy(ScheduleChromosome.CourseSchedule::timeSlotId));
            
            for (List<ScheduleChromosome.CourseSchedule> timeSlotSchedules : timeSlotGroups.values()) {
                if (timeSlotSchedules.size() > 1) {
                    violations += timeSlotSchedules.size() - 1; // 每个额外课程算一次冲突
                }
            }
        }
        
        return violations;
    }
    
    /**
     * 检查教室时间冲突
     */
    private int checkClassroomTimeConflicts(Map<Long, ScheduleChromosome.CourseSchedule> genes) {
        // 实现具体的教室时间冲突检查逻辑
        int violations = 0;
        
        // 按教室ID分组，检查每个教室的时间安排
        Map<Long, List<ScheduleChromosome.CourseSchedule>> classroomSchedules = genes.values().stream()
                .collect(Collectors.groupingBy(ScheduleChromosome.CourseSchedule::classroomId));
        
        for (Map.Entry<Long, List<ScheduleChromosome.CourseSchedule>> entry : classroomSchedules.entrySet()) {
            List<ScheduleChromosome.CourseSchedule> schedules = entry.getValue();
            
            // 检查同一教室是否有多个课程在同一时间段
            Map<Long, List<ScheduleChromosome.CourseSchedule>> timeSlotGroups = schedules.stream()
                    .collect(Collectors.groupingBy(ScheduleChromosome.CourseSchedule::timeSlotId));
            
            for (List<ScheduleChromosome.CourseSchedule> timeSlotSchedules : timeSlotGroups.values()) {
                if (timeSlotSchedules.size() > 1) {
                    violations += timeSlotSchedules.size() - 1; // 每个额外课程算一次冲突
                }
            }
        }
        
        return violations;
    }
    
    /**
     * 检查班级时间冲突
     */
    private int checkClassTimeConflicts(Map<Long, ScheduleChromosome.CourseSchedule> genes) {
        // 实现具体的班级时间冲突检查逻辑
        int violations = 0;
        
        // 按班级ID分组，检查每个班级的时间安排
        Map<Long, List<ScheduleChromosome.CourseSchedule>> classSchedules = genes.values().stream()
                .collect(Collectors.groupingBy(ScheduleChromosome.CourseSchedule::classId));
        
        for (Map.Entry<Long, List<ScheduleChromosome.CourseSchedule>> entry : classSchedules.entrySet()) {
            List<ScheduleChromosome.CourseSchedule> schedules = entry.getValue();
            
            // 检查同一班级是否有多个课程在同一时间段
            Map<Long, List<ScheduleChromosome.CourseSchedule>> timeSlotGroups = schedules.stream()
                    .collect(Collectors.groupingBy(ScheduleChromosome.CourseSchedule::timeSlotId));
            
            for (List<ScheduleChromosome.CourseSchedule> timeSlotSchedules : timeSlotGroups.values()) {
                if (timeSlotSchedules.size() > 1) {
                    violations += timeSlotSchedules.size() - 1; // 每个额外课程算一次冲突
                }
            }
        }
        
        return violations;
    }
    
    /**
     * 检查课程时间冲突
     */
    private int checkCourseTimeConflicts(Map<Long, ScheduleChromosome.CourseSchedule> genes) {
        // 实现具体的课程时间冲突检查逻辑
        int violations = 0;
        
        // 检查是否有课程被安排到多个时间段（这通常是不合理的）
        Map<Long, List<ScheduleChromosome.CourseSchedule>> courseSchedules = genes.values().stream()
                .collect(Collectors.groupingBy(ScheduleChromosome.CourseSchedule::courseId));
        
        for (List<ScheduleChromosome.CourseSchedule> schedules : courseSchedules.values()) {
            if (schedules.size() > 1) {
                // 检查是否在同一时间段有重复安排
                Map<Long, List<ScheduleChromosome.CourseSchedule>> timeSlotGroups = schedules.stream()
                        .collect(Collectors.groupingBy(ScheduleChromosome.CourseSchedule::timeSlotId));
                
                for (List<ScheduleChromosome.CourseSchedule> timeSlotSchedules : timeSlotGroups.values()) {
                    if (timeSlotSchedules.size() > 1) {
                        violations += timeSlotSchedules.size() - 1; // 每个额外安排算一次冲突
                    }
                }
            }
        }
        
        return violations;
    }
    
    /**
     * 检查教师偏好
     */
    private int checkTeacherPreferences(Map<Long, ScheduleChromosome.CourseSchedule> genes) {
        // 实现具体的教师偏好检查逻辑
        int violations = 0;
        
        // TODO: 这里需要从数据库或配置中获取教师的偏好时间
        // 目前返回基础值，实际实现时需要：
        // 1. 获取教师偏好时间段
        // 2. 检查当前安排是否符合偏好
        // 3. 计算违反偏好的惩罚分数
        
        return violations;
    }
    
    /**
     * 检查教室容量
     */
    private int checkClassroomCapacity(Map<Long, ScheduleChromosome.CourseSchedule> genes) {
        // 实现具体的教室容量检查逻辑
        int violations = 0;
        
        // TODO: 这里需要从数据库获取课程的学生人数和教室容量信息
        // 目前返回基础值，实际实现时需要：
        // 1. 获取每门课程的学生人数
        // 2. 获取每个教室的容量
        // 3. 检查容量是否匹配
        // 4. 计算容量不匹配的惩罚分数
        
        return violations;
    }
    
    /**
     * 检查课程连续性
     */
    private int checkCourseContinuity(Map<Long, ScheduleChromosome.CourseSchedule> genes) {
        // TODO: 实现具体的课程连续性检查逻辑
        return 0;
    }
    
    /**
     * 检查休息时间安排
     */
    private int checkBreakTimeArrangement(Map<Long, ScheduleChromosome.CourseSchedule> genes) {
        // TODO: 实现具体的休息时间安排检查逻辑
        return 0;
    }
    
    /**
     * 约束检查结果记录类
     * 使用JDK 21的记录类特性
     */
    public record ConstraintResult(
        int violations,     // 违反约束的数量
        String description  // 约束检查结果描述
    ) {}
}
