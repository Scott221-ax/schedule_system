package com.scott.schedule.algorithm.impl;

import com.scott.schedule.algorithm.CrossoverOperator;
import com.scott.schedule.algorithm.ScheduleChromosome;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * 均匀交叉操作实现
 * 使用JDK 21的新特性：模式匹配、switch表达式、var关键字等
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Component
public class UniformCrossoverOperator implements CrossoverOperator {
    
    private static final double CROSSOVER_RATE = 0.8;
    private final Random random = new Random();
    
    @Override
    public List<ScheduleChromosome> crossover(List<ScheduleChromosome> selected) {
        var offspring = new ArrayList<ScheduleChromosome>();
        
        // 两两配对进行交叉
        for (int i = 0; i < selected.size() - 1; i += 2) {
            var parent1 = selected.get(i);
            var parent2 = selected.get(i + 1);
            
            if (random.nextDouble() < CROSSOVER_RATE) {
                // 执行交叉
                var children = performCrossover(parent1, parent2);
                offspring.addAll(children);
            } else {
                // 不交叉，直接复制父代
                offspring.add(parent1.clone());
                offspring.add(parent2.clone());
            }
        }
        
        // 如果种群大小为奇数，处理最后一个个体
        if (selected.size() % 2 == 1) {
            offspring.add(selected.get(selected.size() - 1).clone());
        }
        
        return offspring;
    }
    
    /**
     * 执行交叉操作
     * 使用JDK 21的模式匹配和函数式编程特性
     */
    private List<ScheduleChromosome> performCrossover(ScheduleChromosome parent1, 
                                                     ScheduleChromosome parent2) {
        var child1 = parent1.clone();
        var child2 = parent2.clone();
        
        var genes1 = child1.getGenes();
        var genes2 = child2.getGenes();
        
        // 对每个基因位进行均匀交叉
        genes1.keySet().forEach(courseId -> {
            if (random.nextBoolean()) {
                // 交换基因
                var temp = genes1.get(courseId);
                genes1.put(courseId, genes2.get(courseId));
                genes2.put(courseId, temp);
            }
        });
        
        return List.of(child1, child2);
    }
}
