package com.scott.schedule.algorithm;

import java.util.List;

/**
 * 交叉操作接口
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public interface CrossoverOperator {
    
    /**
     * 对选中的个体进行交叉操作
     * 
     * @param selected 选中的个体列表
     * @return 交叉后的后代列表
     */
    List<ScheduleChromosome> crossover(List<ScheduleChromosome> selected);
}
