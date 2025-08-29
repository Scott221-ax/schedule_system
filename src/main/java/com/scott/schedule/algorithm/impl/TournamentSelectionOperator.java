package com.scott.schedule.algorithm.impl;

import com.scott.schedule.algorithm.SelectionOperator;
import com.scott.schedule.algorithm.ScheduleChromosome;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * 锦标赛选择操作实现
 * 使用JDK 21的新特性：函数式编程、var关键字、Stream API等
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Component
public class TournamentSelectionOperator implements SelectionOperator {
    
    private static final int TOURNAMENT_SIZE = 3;
    private final Random random = new Random();
    
    @Override
    public List<ScheduleChromosome> select(List<ScheduleChromosome> population) {
        // 使用Stream API和函数式编程特性
        return population.stream()
            .map(chromosome -> tournamentSelect(population))
            .map(ScheduleChromosome::clone)
            .toList();
    }
    
    /**
     * 锦标赛选择
     * 使用JDK 21的函数式编程特性
     */
    private ScheduleChromosome tournamentSelect(List<ScheduleChromosome> population) {
        // 随机选择TOURNAMENT_SIZE个个体进行比较
        var tournamentParticipants = random.ints(TOURNAMENT_SIZE, 0, population.size())
            .mapToObj(population::get)
            .toList();
        
        // 使用Stream API找到最优个体
        return tournamentParticipants.stream()
            .max((a, b) -> Double.compare(a.getFitness(), b.getFitness()))
            .orElseThrow(() -> new IllegalStateException("锦标赛选择失败"));
    }
}
