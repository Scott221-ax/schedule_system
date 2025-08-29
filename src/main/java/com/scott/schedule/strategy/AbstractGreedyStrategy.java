package com.scott.schedule.strategy;

import com.scott.schedule.algorithm.ScheduleChromosome;
import com.scott.schedule.config.GreedyConfig;
import com.scott.schedule.model.Course;
import com.scott.schedule.model.TimeSlot;
import com.scott.schedule.model.Classroom;
import com.scott.schedule.model.Teacher;
import com.scott.schedule.service.DataService;
import lombok.Getter;

import java.util.*;

/**
 * 抽象贪心策略基类
 * 提供通用的功能实现，子类只需要实现特定的策略逻辑
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public abstract class AbstractGreedyStrategy implements GreedyStrategy {

    @Getter
    protected final GreedyConfig config;
    
    @Getter
    protected final DataService dataService;
    
    protected final Random random = new Random();

    public AbstractGreedyStrategy(GreedyConfig config, DataService dataService) {
        this.config = config;
        this.dataService = dataService;
    }

    /**
     * 获取所有课程信息
     */
    protected List<Course> getAllCourses() {
        // 从数据服务获取课程信息
        return dataService.getAllCourses();
    }

    /**
     * 获取可用时间段
     */
    protected List<TimeSlot> getAvailableTimeSlots(Course course, ScheduleChromosome solution) {
        // 从数据服务获取所有时间段，然后过滤出可用的
        List<TimeSlot> allTimeSlots = dataService.getAllTimeSlots();
        
        // 实现时间段可用性检查逻辑
        return allTimeSlots.stream()
                .filter(slot -> isTimeSlotAvailable(course, slot, solution))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 检查时间段是否可用
     */
    protected boolean isTimeSlotAvailable(Course course, TimeSlot slot, ScheduleChromosome solution) {
        // 检查时间段是否已被占用
        if (!slot.isAvailable()) {
            return false;
        }
        
        // 检查教师在该时间段是否可用
        if (!isTeacherAvailable(course.getTeacherId(), slot, solution)) {
            return false;
        }
        
        // 检查是否有合适的教室
        if (!hasAvailableClassroom(course, slot, solution)) {
            return false;
        }
        
        // 检查班级在该时间段是否可用
        return areClassesAvailable(course.getClassIds(), slot, solution);
    }

    /**
     * 检查教师是否在指定时间段可用
     */
    protected boolean isTeacherAvailable(String teacherId, TimeSlot slot, ScheduleChromosome solution) {
        // 检查教师是否在该时间段已被安排其他课程
        for (ScheduleChromosome.CourseSchedule schedule : solution.getGenes().values()) {
            if (schedule.teacherId().equals(Long.valueOf(teacherId)) && 
                schedule.timeSlotId().equals(Long.valueOf(slot.getId()))) {
                return false; // 教师在该时间段已被安排
            }
        }
        
        // 检查教师本身的不可用时间段约束
        Teacher teacher = dataService.getTeacherById(teacherId);
        if (teacher != null && teacher.getUnavailableTimeSlots() != null) {
            // 检查当前时间段是否在教师的不可用时间段列表中
            String currentTimeSlotId = slot.getId();
            if (teacher.getUnavailableTimeSlots().contains(currentTimeSlotId)) {
                return false; // 教师在该时间段不可用
            }
        }
        
        return true;
    }

    /**
     * 检查是否有可用的教室
     */
    protected boolean hasAvailableClassroom(Course course, TimeSlot slot, ScheduleChromosome solution) {
        // 实现教室可用性检查
        // 这里需要检查是否有容量足够且设备合适的教室
        
        List<Classroom> classrooms = dataService.getAllClassrooms();
        
        // 检查是否有容量足够的教室
        boolean hasCapacity = classrooms.stream()
                .anyMatch(classroom -> classroom.getCapacity() >= course.getStudentCount());
        
        if (!hasCapacity) {
            return false;
        }
        
        // 检查是否有教室在该时间段未被占用
        for (ScheduleChromosome.CourseSchedule schedule : solution.getGenes().values()) {
            if (schedule.timeSlotId().equals(Long.valueOf(slot.getId()))) {
                // 该时间段已被占用，检查是否有其他可用教室
                boolean hasOtherClassroom = classrooms.stream()
                        .anyMatch(classroom -> !classroom.getId().equals(schedule.classroomId().toString()) &&
                                             classroom.getCapacity() >= course.getStudentCount());
                if (!hasOtherClassroom) {
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * 检查班级是否在指定时间段可用
     */
    protected boolean areClassesAvailable(List<String> classIds, TimeSlot slot, ScheduleChromosome solution) {
        // 实现班级时间冲突检查
        // 这里需要检查班级是否在该时间段已被安排其他课程
        
        if (classIds == null || classIds.isEmpty()) {
            return true;
        }
        
        // 检查是否有班级在该时间段已被安排其他课程
        for (ScheduleChromosome.CourseSchedule schedule : solution.getGenes().values()) {
            if (schedule.timeSlotId().equals(Long.valueOf(slot.getId()))) {
                // 该时间段已被占用，检查是否与当前课程的班级冲突
                for (String classId : classIds) {
                    if (schedule.classId().equals(Long.valueOf(classId))) {
                        return false; // 班级在该时间段已被安排
                    }
                }
            }
        }
        
        return true;
    }

    /**
     * 分配课程到时间段
     */
    protected void assignCourseToSlot(ScheduleChromosome solution, Course course, TimeSlot slot) {
        assignCourseToSlot(solution, course, slot, 1);
    }

    /**
     * 分配课程到时间段（指定课时）
     */
    protected void assignCourseToSlot(ScheduleChromosome solution, Course course, TimeSlot slot, int hourIndex) {
        // 实现课程分配逻辑
        // 这里需要根据ScheduleChromosome的实际结构来实现
        
        // 创建课程安排记录
        ScheduleChromosome.CourseSchedule courseSchedule = new ScheduleChromosome.CourseSchedule(
                Long.valueOf(course.getId()),
                Long.valueOf(course.getTeacherId()),
                selectBestClassroom(course, slot), // 选择最佳教室
                Long.valueOf(slot.getId()),
                selectBestClass(course, slot) // 选择最佳班级
        );
        
        // 将课程安排添加到染色体中
        solution.getGenes().put(Long.valueOf(course.getId()), courseSchedule);
        
        // 标记时间段为不可用
        slot.setAvailable(false);
        
        System.out.printf("分配课程 %s 到时间段 %s (第%d课时)%n", course.getName(), slot.getId(), hourIndex);
    }

    /**
     * 选择最佳教室
     */
    protected Long selectBestClassroom(Course course, TimeSlot slot) {
        // 实现教室选择逻辑
        // 这里应该根据课程需求、教室容量、设备等因素选择最佳教室
        
        List<Classroom> availableClassrooms = dataService.getAllClassrooms();
        
        // 过滤出容量足够的教室
        List<Classroom> suitableClassrooms = availableClassrooms.stream()
                .filter(classroom -> classroom.getCapacity() >= course.getStudentCount())
                .collect(java.util.stream.Collectors.toList());
        
        if (suitableClassrooms.isEmpty()) {
            return null;
        }
        
        // 选择容量最接近课程需求的教室（避免浪费）
        Classroom bestClassroom = suitableClassrooms.stream()
                .min(Comparator.comparingInt(classroom -> 
                    Math.abs(classroom.getCapacity() - course.getStudentCount())))
                .orElse(suitableClassrooms.get(0));
        
        return Long.valueOf(bestClassroom.getId());
    }

    /**
     * 选择最佳班级
     */
    protected Long selectBestClass(Course course, TimeSlot slot) {
        // 实现班级选择逻辑
        // 这里应该根据课程类型、班级特点等因素选择最佳班级
        
        if (course.getClassIds() == null || course.getClassIds().isEmpty()) {
            return null;
        }
        
        // 如果有多个班级，选择第一个（可以根据具体需求优化选择策略）
        // TODO: 可以根据班级特点、课程类型等因素进行更智能的选择
        return Long.valueOf(course.getClassIds().get(0));
    }

    /**
     * 处理无法安排的课程
     */
    protected void handleUnassignableCourse(Course course) {
        System.out.printf("⚠️ 课程 %s 无法安排，记录冲突%n", course.getName());
        // 记录冲突信息
    }

    /**
     * 评估时间段质量
     */
    protected double evaluateTimeSlot(Course course, TimeSlot slot, ScheduleChromosome solution) {
        // 实现时间段评估逻辑
        double score = 0.0;
        
        // 考虑各种因素
        score += evaluateTeacherAvailability(course, slot);
        score += evaluateClassroomAvailability(course, slot);
        score += evaluateTimePreference(course, slot);
        
        return score;
    }

    /**
     * 评估教师可用性
     */
    protected double evaluateTeacherAvailability(Course course, TimeSlot slot) {
        // 实现教师可用性评估
        return 0.0;
    }

    /**
     * 评估教室可用性
     */
    protected double evaluateClassroomAvailability(Course course, TimeSlot slot) {
        // 实现教室可用性评估
        return 0.0;
    }

    /**
     * 评估时间偏好
     */
    protected double evaluateTimePreference(Course course, TimeSlot slot) {
        // 实现时间偏好评估
        return 0.0;
    }

    /**
     * 获取候选时间段
     */
    protected List<TimeSlot> getCandidateTimeSlots(Course course, ScheduleChromosome solution) {
        List<TimeSlot> availableSlots = getAvailableTimeSlots(course, solution);
        
        if (availableSlots.isEmpty()) {
            return Collections.emptyList();
        }

        // 根据评估函数排序
        availableSlots.sort(Comparator.comparingDouble(slot -> evaluateTimeSlot(course, slot, solution)));
        
        return availableSlots;
    }

    /**
     * 随机化选择时间段
     */
    protected TimeSlot selectRandomizedSlot(List<TimeSlot> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
        
        // 引入随机化因子
        double randomFactor = config.getRandomizationFactor();
        if (random.nextDouble() < randomFactor) {
            // 随机选择
            return candidates.get(random.nextInt(candidates.size()));
        } else {
            // 选择最优的
            return candidates.get(0);
        }
    }
}
