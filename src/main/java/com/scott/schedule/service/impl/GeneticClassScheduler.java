package com.scott.schedule.service.impl;

import com.scott.schedule.algorithm.*;
import com.scott.schedule.config.GeneticAlgorithmConfig;
import com.scott.schedule.service.ClassScheduler;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 基于遗传算法的排课服务实现
 * 使用JDK 21的新特性：模式匹配、switch表达式、虚拟线程、文本块等
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Service
public class GeneticClassScheduler implements ClassScheduler {

    /**
     * 遗传算法配置参数
     * 包含种群大小、最大代数、交叉率、变异率、精英比例等核心参数
     */
    private final GeneticAlgorithmConfig config;

    /**
     * 随机数生成器
     * 用于遗传算法中的随机操作，如初始化、选择、交叉、变异等
     */
    private final Random random = new Random();

    /**
     * 适应度计算器
     * 负责评估每个染色体（排课方案）的适应度值，适应度越高表示排课方案越优
     */
    private final FitnessCalculator fitnessCalculator;

    /**
     * 选择操作器
     * 实现遗传算法中的选择操作，从种群中选择优秀个体进行繁殖
     * 常用方法包括轮盘赌选择、锦标赛选择等
     */
    private final SelectionOperator selectionOperator;

    /**
     * 交叉操作器
     * 实现遗传算法中的交叉操作，将两个父代个体的基因进行交换产生子代
     * 用于产生新的排课方案组合
     */
    private final CrossoverOperator crossoverOperator;

    /**
     * 变异操作器
     * 实现遗传算法中的变异操作，对个体基因进行随机改变以增加种群多样性
     * 防止算法陷入局部最优解
     */
    private final MutationOperator mutationOperator;

    /**
     * 虚拟线程执行器
     * 使用JDK 21的虚拟线程特性，用于并行计算适应度，提高算法执行效率
     * 虚拟线程相比传统线程更轻量级，可以创建大量并发任务
     */
    private final ExecutorService executor;

    /**
     * 构造函数，初始化遗传算法排课服务
     *
     * @param config 遗传算法配置参数，包含种群大小、代数、概率等设置
     * @param fitnessCalculator 适应度计算器，用于评估排课方案的质量
     * @param selectionOperator 选择操作器，用于从种群中选择优秀个体
     * @param crossoverOperator 交叉操作器，用于生成新的子代个体
     * @param mutationOperator 变异操作器，用于增加种群多样性
     */
    public GeneticClassScheduler(GeneticAlgorithmConfig config,
                                 FitnessCalculator fitnessCalculator,
                                 SelectionOperator selectionOperator,
                                 CrossoverOperator crossoverOperator,
                                 MutationOperator mutationOperator) {
        this.config = config;
        this.fitnessCalculator = fitnessCalculator;
        this.selectionOperator = selectionOperator;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        // 使用虚拟线程执行器，提高并发性能
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * 执行遗传算法排课
     * 这是排课服务的主入口方法，完整执行遗传算法的所有步骤：
     * 1. 初始化种群 - 随机生成初始排课方案
     * 2. 计算适应度 - 评估每个方案的质量
     * 3. 进化过程 - 通过选择、交叉、变异操作优化方案
     * 4. 应用结果 - 将最优方案转换为实际的课程安排
     *
     * 使用JDK 21的文本块特性美化输出，虚拟线程提高并发性能
     */
    @Override
    public void schedule() {
        System.out.printf(
                """
                        🚀 开始执行遗传算法排课
                        📊 种群大小: %d
                        🔄 最大代数: %d
                        ✂️ 交叉概率: %.2f
                        🧬 变异概率: %.2f
                        🏆 精英比例: %.2f
                        %n""",
                config.getPopulationSize(),
                config.getMaxGenerations(),
                config.getCrossoverRate(),
                config.getMutationRate(),
                config.getEliteRate()
        );

        try {
            // 1. 初始化种群
            List<ScheduleChromosome> population = initializePopulation();

            // 2. 计算初始适应度（并行计算）
            calculateFitnessParallel(population);

            // 3. 主循环：进化过程
            var evolutionResult = evolvePopulation(population);

            // 4. 输出最优解
            applySchedule(evolutionResult.bestSolution());

            System.out.printf(
                    """
                            ✅ 遗传算法排课完成
                            🏆 最优解适应度: %.4f
                            🔄 总进化代数: %d
                            ⚠️ 约束违反数: %d
                            %n""", evolutionResult.bestSolution().getFitness(),
            evolutionResult.generations(),
            evolutionResult.bestSolution().getConstraintViolations()
    );

        } finally {
            executor.close(); // 关闭虚拟线程执行器
        }
    }

    /**
     * 初始化种群
     * 创建指定大小的初始种群，每个个体（染色体）代表一个排课方案
     * 使用随机化方法生成多样化的初始解，为后续进化提供基础
     *
     * @return 初始化的种群列表，包含随机生成的排课方案
     */
    private List<ScheduleChromosome> initializePopulation() {
        return new ArrayList<>(config.getPopulationSize()) {{
            for (int i = 0; i < config.getPopulationSize(); i++) {
                var chromosome = new ScheduleChromosome();
                chromosome.randomize(); // 随机初始化染色体的基因
                add(chromosome);
            }
        }};
    }

    /**
     * 并行计算种群适应度
     * 使用虚拟线程并行计算种群中每个个体的适应度值，显著提高计算效率
     * 适应度值反映了排课方案的质量，包括时间冲突、教室容量、教师偏好等因素
     *
     * @param population 需要计算适应度的种群
     */
    private void calculateFitnessParallel(List<ScheduleChromosome> population) {
        // 为每个染色体创建异步计算任务
        var futures = population.stream()
                .map(chromosome -> CompletableFuture.runAsync(() -> {
                    double fitness = fitnessCalculator.calculate(chromosome);
                    chromosome.setFitness(fitness);
                }, executor))
                .toList();

        // 等待所有适应度计算完成
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }

    /**
     * 进化种群
     * 执行遗传算法的核心进化过程，通过多代迭代优化排课方案
     * 包含选择、交叉、变异、精英保留等操作，直到找到满意解或达到终止条件
     *
     * @param population 初始种群
     * @return 进化结果，包含最终代数和最优解
     */
    private EvolutionResult evolvePopulation(List<ScheduleChromosome> population) {
        int generation = 0;
        int generationsWithoutImprovement = 0;
        double bestFitness = 0.0;

        while (generation < config.getMaxGenerations()) {
            // 选择
            List<ScheduleChromosome> selected = selectionOperator.select(population);

            // 交叉
            List<ScheduleChromosome> offspring = crossoverOperator.crossover(selected);

            // 变异
            mutationOperator.mutate(offspring);

            // 精英保留
            elitePreservation(population, offspring);

            // 更新种群
            population = offspring;

            // 重新计算适应度
            calculateFitnessParallel(population);

            // 检查改进情况
            var currentBest = getBestSolution(population);
            if (currentBest.getFitness() > bestFitness) {
                bestFitness = currentBest.getFitness();
                generationsWithoutImprovement = 0;
            } else {
                generationsWithoutImprovement++;
            }

            // 检查是否达到目标或收敛
            if (isOptimalSolutionFound(population) ||
                    isConverged(population) ||
                    generationsWithoutImprovement >= config.getMaxGenerationsWithoutImprovement()) {
                break;
            }

            generation++;

            // 每100代输出一次进度
            if (generation % 100 == 0) {
                System.out.printf("🔄 第%d代完成，当前最优适应度: %.4f%n",
                        generation, bestFitness);
            }
        }

        return new EvolutionResult(generation, getBestSolution(population));
    }

    /**
     * 精英保留策略
     * 保留上一代种群中适应度最高的个体到新一代种群中
     * 确保优秀的排课方案不会在进化过程中丢失，提高算法收敛性
     *
     * @param oldPopulation 上一代种群
     * @param newPopulation 新一代种群
     */
    private void elitePreservation(List<ScheduleChromosome> oldPopulation,
                                   List<ScheduleChromosome> newPopulation) {
        int eliteCount = (int) (config.getPopulationSize() * config.getEliteRate());

        // 按适应度排序并保留最优个体
        var sortedPopulation = oldPopulation.stream()
                .sorted((a, b) -> Double.compare(b.getFitness(), a.getFitness()))
                .limit(eliteCount)
                .map(ScheduleChromosome::clone)
                .toList();

        // 替换新种群中的前几个个体
        for (int i = 0; i < Math.min(eliteCount, newPopulation.size()); i++) {
            newPopulation.set(i, sortedPopulation.get(i));
        }
    }

    /**
     * 检查是否找到最优解
     * 判断种群中是否存在适应度达到1.0（完美解）的个体
     *
     * @param population 当前种群
     * @return 如果找到最优解返回true，否则返回false
     */
    private boolean isOptimalSolutionFound(List<ScheduleChromosome> population) {
        return population.stream()
                .anyMatch(chromosome -> chromosome.getFitness() >= 1.0);
    }

    /**
     * 检查种群是否收敛
     * 通过计算种群适应度的方差来判断是否收敛
     * 当方差小于阈值时认为种群已收敛，继续进化意义不大
     *
     * @param population 当前种群
     * @return 如果种群已收敛返回true，否则返回false
     */
    private boolean isConverged(List<ScheduleChromosome> population) {
        var fitnesses = population.stream()
                .mapToDouble(ScheduleChromosome::getFitness)
                .toArray();

        double avg = Arrays.stream(fitnesses).average().orElse(0.0);
        double variance = Arrays.stream(fitnesses)
                .map(f -> Math.pow(f - avg, 2))
                .average()
                .orElse(0.0);

        return Math.sqrt(variance) < config.getConvergenceThreshold();
    }

    /**
     * 获取种群中的最优解
     * 从当前种群中找出适应度最高的个体作为最优解
     *
     * @param population 当前种群
     * @return 适应度最高的染色体（排课方案）
     * @throws IllegalStateException 如果种群为空
     */
    private ScheduleChromosome getBestSolution(List<ScheduleChromosome> population) {
        return population.stream()
                .max(Comparator.comparingDouble(ScheduleChromosome::getFitness))
                .orElseThrow(() -> new IllegalStateException("种群为空"));
    }

    /**
     * 应用排课结果
     * 将遗传算法找到的最优染色体转换为实际的课程安排
     * 并将结果保存到数据库中供系统使用
     *
     * @param solution 最优排课方案（染色体）
     */
    private void applySchedule(ScheduleChromosome solution) {
        // TODO: 将染色体转换为实际的课程安排并保存到数据库
        System.out.println("🏆 最优解适应度: " + solution.getFitness());
    }

    /**
     * 进化结果记录类
     * 使用JDK 21的record特性，封装遗传算法的执行结果
     * 包含实际进化的代数和找到的最优解
     */
    public record EvolutionResult(
            /**
             * 实际进化代数
             * 算法实际执行的迭代次数，可能小于配置的最大代数（提前收敛或找到最优解）
             */
            int generations,

            /**
             * 最优解
             * 遗传算法找到的适应度最高的排课方案
             */
            ScheduleChromosome bestSolution
    ) {
    }
}
