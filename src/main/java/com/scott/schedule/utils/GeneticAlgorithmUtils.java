package com.scott.schedule.utils;

import com.scott.schedule.algorithm.ScheduleChromosome;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 遗传算法工具类
 * 使用JDK 21的新特性：模式匹配、switch表达式、记录类、虚拟线程等
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public class GeneticAlgorithmUtils {
    
    /**
     * 种群统计信息记录类
     * 使用JDK 21的记录类特性
     */
    public record PopulationStats(
        int size,                    // 种群大小
        double bestFitness,          // 最优适应度
        double worstFitness,         // 最差适应度
        double averageFitness,       // 平均适应度
        double fitnessVariance,      // 适应度方差
        int bestConstraintViolations // 最优解的约束违反数
    ) {}
    
    /**
     * 进化进度记录类
     */
    public record EvolutionProgress(
        int generation,              // 当前代数
        double bestFitness,          // 当前最优适应度
        double averageFitness,       // 当前平均适应度
        boolean improved,            // 是否有所改进
        String status                // 状态描述
    ) {}
    
    /**
     * 计算种群统计信息
     * 使用JDK 21的Stream API和函数式编程
     */
    public static PopulationStats calculatePopulationStats(List<ScheduleChromosome> population) {
        if (population.isEmpty()) {
            return new PopulationStats(0, 0.0, 0.0, 0.0, 0.0, 0);
        }
        
        var fitnesses = population.stream()
            .mapToDouble(ScheduleChromosome::getFitness)
            .toArray();
        
        var bestFitness = Arrays.stream(fitnesses).max().orElse(0.0);
        var worstFitness = Arrays.stream(fitnesses).min().orElse(0.0);
        var averageFitness = Arrays.stream(fitnesses).average().orElse(0.0);
        
        var variance = Arrays.stream(fitnesses)
            .map(f -> Math.pow(f - averageFitness, 2))
            .average()
            .orElse(0.0);
        
        var bestConstraintViolations = population.stream()
            .filter(chromosome -> chromosome.getFitness() == bestFitness)
            .mapToInt(ScheduleChromosome::getConstraintViolations)
            .min()
            .orElse(0);
        
        return new PopulationStats(
            population.size(),
            bestFitness,
            worstFitness,
            averageFitness,
            variance,
            bestConstraintViolations
        );
    }
    
    /**
     * 并行计算种群适应度
     * 使用JDK 21的虚拟线程和CompletableFuture
     */
    public static void calculateFitnessParallel(
            List<ScheduleChromosome> population,
            Function<ScheduleChromosome, Double> fitnessFunction,
            ExecutorService executor) {
        
        var futures = population.stream()
            .map(chromosome -> CompletableFuture.runAsync(() -> {
                var fitness = fitnessFunction.apply(chromosome);
                chromosome.setFitness(fitness);
            }, executor))
            .toList();
        
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }
    
    /**
     * 检查种群是否收敛
     * 使用JDK 21的模式匹配
     */
    public static boolean isPopulationConverged(List<ScheduleChromosome> population, double threshold) {
        return switch (population.size()) {
            case 0, 1 -> true;
            default -> {
                var stats = calculatePopulationStats(population);
                yield Math.sqrt(stats.fitnessVariance()) < threshold;
            }
        };
    }
    
    /**
     * 生成进化进度报告
     * 使用JDK 21的文本块和格式化
     */
    public static String generateProgressReport(EvolutionProgress progress) {
        return """
            🔄 第%d代进化进度
            🏆 最优适应度: %.4f
            📊 平均适应度: %.4f
            📈 状态: %s
            """.formatted(
                progress.generation(),
                progress.bestFitness(),
                progress.averageFitness(),
                progress.status()
            );
    }
    
    /**
     * 生成种群统计报告
     * 使用JDK 21的文本块和格式化
     */
    public static String generateStatsReport(PopulationStats stats) {
        return """
            📊 种群统计信息
            👥 种群大小: %d
            🏆 最优适应度: %.4f
            📉 最差适应度: %.4f
            📊 平均适应度: %.4f
            📈 适应度方差: %.4f
            ⚠️ 最优解约束违反: %d
            """.formatted(
                stats.size(),
                stats.bestFitness(),
                stats.worstFitness(),
                stats.averageFitness(),
                stats.fitnessVariance(),
                stats.bestConstraintViolations()
            );
    }
    
    ///**
    // * 检查染色体是否有效
    // * 使用JDK 21的模式匹配
    // */
    //public static boolean isValidChromosome(ScheduleChromosome chromosome) {
    //    return switch (chromosome) {
    //        case null -> false;
    //        case ScheduleChromosome c when c.getGenes() == null -> false;
    //        case ScheduleChromosome c when c.getGenes().isEmpty() -> false;
    //        case ScheduleChromosome c -> c.getGenes().values().stream();
    //
    //    };
    //}
    
    /**
     * 获取种群中的精英个体
     * 使用JDK 21的Stream API
     */
    public static List<ScheduleChromosome> getEliteIndividuals(
            List<ScheduleChromosome> population, 
            double eliteRate) {
        
        var eliteCount = (int) (population.size() * eliteRate);
        
        return population.stream()
            .sorted((a, b) -> Double.compare(b.getFitness(), a.getFitness()))
            .limit(eliteCount)
            .toList();
    }
    
    /**
     * 计算种群多样性
     * 使用JDK 21的Stream API
     */
    public static double calculatePopulationDiversity(List<ScheduleChromosome> population) {
        if (population.size() < 2) {
            return 0.0;
        }
        
        var uniqueGenes = population.stream()
            .flatMap(chromosome -> chromosome.getGenes().values().stream())
            .collect(Collectors.toSet());
        
        var totalGenes = population.stream()
            .mapToInt(chromosome -> chromosome.getGenes().size())
            .sum();
        
        return (double) uniqueGenes.size() / totalGenes;
    }
}
