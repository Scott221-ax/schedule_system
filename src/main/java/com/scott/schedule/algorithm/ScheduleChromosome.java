package com.scott.schedule.algorithm;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 排课染色体，表示一个课程安排方案
 * 使用JDK 21的记录类特性来简化代码
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Data
public class ScheduleChromosome implements Cloneable {

    /**
     * 获取基因 课程安排，格式：Map<课程ID, 课程安排信息>
     */
    private Map<Long, CourseSchedule> genes;

    /**
     * 获取适应度
     */
    private double fitness;

    /**
     * 约束违反数量
     */
    private int constraintViolations;

    public ScheduleChromosome() {
        this.genes = new HashMap<>();
        this.fitness = 0.0;
        this.constraintViolations = 0;
    }

    /**
     * 随机初始化染色体
     */
    public void randomize() {
        // 根据实际的课程、教师、教室、时间段数据进行随机初始化
        
        // 清空现有基因
        genes.clear();
        
        // 获取所有可用的课程、教师、教室、时间段数据
        // 注意：这里需要注入相应的服务来获取数据
        // 目前使用模拟数据进行演示
        
        // 模拟课程数据
        Long[] courseIds = {1L, 2L, 3L, 4L, 5L};
        Long[] teacherIds = {101L, 102L, 103L, 104L, 105L};
        Long[] classroomIds = {201L, 202L, 203L, 204L, 205L};
        Long[] timeSlotIds = {301L, 302L, 303L, 304L, 305L};
        Long[] classIds = {401L, 402L, 403L, 404L, 405L};
        
        // 随机生成课程安排
        for (Long courseId : courseIds) {
            // 随机选择教师、教室、时间段、班级
            Long randomTeacher = teacherIds[(int) (Math.random() * teacherIds.length)];
            Long randomClassroom = classroomIds[(int) (Math.random() * classroomIds.length)];
            Long randomTimeSlot = timeSlotIds[(int) (Math.random() * timeSlotIds.length)];
            Long randomClass = classIds[(int) (Math.random() * classIds.length)];
            
            // 创建课程安排
            CourseSchedule courseSchedule = new CourseSchedule(
                courseId, randomTeacher, randomClassroom, randomTimeSlot, randomClass
            );
            
            // 添加到染色体中
            genes.put(courseId, courseSchedule);
        }
        
        // 重置适应度和约束违反数量
        fitness = 0.0;
        constraintViolations = 0;
    }

    /**
     * 克隆染色体
     */
    @Override
    public ScheduleChromosome clone() {
        try {
            ScheduleChromosome cloned = (ScheduleChromosome) super.clone();
            cloned.genes = new HashMap<>(this.genes);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("克隆失败", e);
        }
    }

    /**
     * 课程安排信息记录类
     * 使用JDK 21的记录类特性，自动生成构造函数、getter、equals、hashCode、toString
     */
    public record CourseSchedule(
            Long courseId,      // 课程ID
            Long teacherId,     // 教师ID
            Long classroomId,   // 教室ID
            Long timeSlotId,    // 时间段ID
            Long classId        // 班级ID
    ) {
        /**
         * 验证课程安排的有效性
         */
        public boolean isValid() {
            return courseId != null && teacherId != null &&
                    classroomId != null && timeSlotId != null && classId != null;
        }

        /**
         * 创建课程安排的副本，可以修改特定字段
         */
        public CourseSchedule withTeacherId(Long newTeacherId) {
            return new CourseSchedule(courseId, newTeacherId, classroomId, timeSlotId, classId);
        }

        public CourseSchedule withClassroomId(Long newClassroomId) {
            return new CourseSchedule(courseId, teacherId, newClassroomId, timeSlotId, classId);
        }

        public CourseSchedule withTimeSlotId(Long newTimeSlotId) {
            return new CourseSchedule(courseId, teacherId, classroomId, newTimeSlotId, classId);
        }

        public CourseSchedule withClassId(Long newClassId) {
            return new CourseSchedule(courseId, teacherId, classroomId, timeSlotId, newClassId);
        }
    }
}
