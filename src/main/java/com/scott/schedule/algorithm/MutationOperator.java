package com.scott.schedule.algorithm;

import java.util.List;

/**
 * 变异操作接口
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public interface MutationOperator {
    
    /**
     * 对个体进行变异操作
     * 
     * @param population 待变异的种群
     */
    void mutate(List<ScheduleChromosome> population);
}
