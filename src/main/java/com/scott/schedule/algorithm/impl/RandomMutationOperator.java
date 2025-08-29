package com.scott.schedule.algorithm.impl;

import com.scott.schedule.algorithm.MutationOperator;
import com.scott.schedule.algorithm.ScheduleChromosome;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.Map;

/**
 * 随机变异操作实现
 * 使用JDK 21的模式匹配和switch表达式特性
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Component
public class RandomMutationOperator implements MutationOperator {
    
    private static final double MUTATION_RATE = 0.1;
    private final Random random = new Random();
    
    @Override
    public void mutate(List<ScheduleChromosome> population) {
        population.stream()
            .filter(chromosome -> random.nextDouble() < MUTATION_RATE)
            .forEach(this::performMutation);
    }
    
    /**
     * 执行变异操作
     * 使用JDK 21的模式匹配和switch表达式
     */
    private void performMutation(ScheduleChromosome chromosome) {
        Map<Long, ScheduleChromosome.CourseSchedule> genes = chromosome.getGenes();
        
        // 随机选择一个基因进行变异
        if (!genes.isEmpty()) {
            Long[] courseIds = genes.keySet().toArray(Long[]::new);
            Long randomCourseId = courseIds[random.nextInt(courseIds.length)];
            
            ScheduleChromosome.CourseSchedule schedule = genes.get(randomCourseId);
            
            // 使用JDK 21的switch表达式和模式匹配
            var mutationType = random.nextInt(4);
            var newSchedule = switch (mutationType) {
                case 0 -> schedule.withTeacherId(generateRandomTeacherId());
                case 1 -> schedule.withClassroomId(generateRandomClassroomId());
                case 2 -> schedule.withTimeSlotId(generateRandomTimeSlotId());
                case 3 -> schedule.withClassId(generateRandomClassId());
                default -> throw new IllegalStateException("Unexpected mutation type: " + mutationType);
            };
            
            // 更新基因
            genes.put(randomCourseId, newSchedule);
        }
    }
    
    /**
     * 生成随机教师ID
     * 使用JDK 21的文本块特性
     */
    private Long generateRandomTeacherId() {
        // TODO: 根据实际数据生成随机教师ID
        // 这里使用模拟数据，实际应该从数据库查询
        var teacherIds = List.of(1L, 2L, 3L, 4L, 5L);
        return teacherIds.get(random.nextInt(teacherIds.size()));
    }
    
    /**
     * 生成随机教室ID
     */
    private Long generateRandomClassroomId() {
        // TODO: 根据实际数据生成随机教室ID
        var classroomIds = List.of(101L, 102L, 103L, 201L, 202L, 203L);
        return classroomIds.get(random.nextInt(classroomIds.size()));
    }
    
    /**
     * 生成随机时间段ID
     */
    private Long generateRandomTimeSlotId() {
        // TODO: 根据实际数据生成随机时间段ID
        // 假设每天8节课，每周5天
        var timeSlotIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L);
        return timeSlotIds.get(random.nextInt(timeSlotIds.size()));
    }
    
    /**
     * 生成随机班级ID
     */
    private Long generateRandomClassId() {
        // TODO: 根据实际数据生成随机班级ID
        var classIds = List.of(1001L, 1002L, 1003L, 2001L, 2002L, 2003L);
        return classIds.get(random.nextInt(classIds.size()));
    }
}
