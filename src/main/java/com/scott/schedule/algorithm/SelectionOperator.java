package com.scott.schedule.algorithm;

import java.util.List;

/**
 * 选择操作接口
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public interface SelectionOperator {
    
    /**
     * 从种群中选择个体
     * 
     * @param population 当前种群
     * @return 选择后的个体列表
     */
    List<ScheduleChromosome> select(List<ScheduleChromosome> population);
}
