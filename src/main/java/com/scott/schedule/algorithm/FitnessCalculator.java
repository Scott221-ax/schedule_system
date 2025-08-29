package com.scott.schedule.algorithm;

/**
 * 适应度计算器接口
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public interface FitnessCalculator {
    
    /**
     * 计算染色体的适应度值
     * 
     * @param chromosome 待计算的染色体
     * @return 适应度值，范围[0,1]，值越大表示解越好
     */
    double calculate(ScheduleChromosome chromosome);
}
