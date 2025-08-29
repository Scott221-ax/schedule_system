package com.scott.schedule.service.impl;

import com.scott.schedule.algorithm.ScheduleChromosome;
import com.scott.schedule.config.AntColonyConfig;
import com.scott.schedule.service.ClassScheduler;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * 基于蚁群算法的排课服务实现
 * 模拟蚂蚁觅食行为，通过信息素机制优化排课方案
 * 使用JDK 21的新特性：虚拟线程、Stream API、文本块等
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Service
public class AntColonyScheduler implements ClassScheduler {

    /**
     * 蚁群算法配置参数
     * 包含蚂蚁数量、迭代次数、信息素参数等核心配置
     */
    private final AntColonyConfig config;

    /**
     * 随机数生成器
     * 用于蚂蚁路径选择中的随机决策
     */
    private final Random random = new Random();

    /**
     * 信息素矩阵
     * 存储课程-时间-教室组合的信息素浓度
     * 维度：[课程数][时间段数][教室数]
     */
    private double[][][] pheromoneMatrix;

    /**
     * 启发式信息矩阵
     * 存储课程安排的启发式价值（如教室容量匹配度、时间偏好等）
     * 维度：[课程数][时间段数][教室数]
     */
    private double[][][] heuristicMatrix;

    /**
     * 蚂蚁群体
     * 每只蚂蚁代表一个独立的排课方案构建者
     */
    private List<Ant> ants;

    /**
     * 全局最优解
     * 记录到目前为止找到的最好排课方案
     */
    private ScheduleChromosome globalBestSolution;

    /**
     * 虚拟线程执行器
     * 使用JDK 21的虚拟线程特性，用于并行执行蚂蚁的路径构建
     */
    private final ExecutorService executor;

    /**
     * 构造函数，初始化蚁群算法排课服务
     *
     * @param config 蚁群算法配置参数
     */
    public AntColonyScheduler(AntColonyConfig config) {
        this.config = config;
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        initializeComponents();
    }

    /**
     * 执行蚁群算法排课
     * 主要流程：
     * 1. 初始化信息素和启发式信息
     * 2. 迭代执行蚂蚁路径构建
     * 3. 更新信息素
     * 4. 记录最优解
     */
    @Override
    public void schedule() {
        System.out.printf(
                """
                        🐜 开始执行蚁群算法排课
                        🔢 蚂蚁数量: %d
                        🔄 最大迭代次数: %d
                        💧 信息素重要性: %.2f
                        🧠 启发式重要性: %.2f
                        💨 信息素挥发率: %.2f
                        %n""",
                config.getAntCount(),
                config.getMaxIterations(),
                config.getAlpha(),
                config.getBeta(),
                config.getEvaporationRate()
        );

        try {
            // 初始化信息素和启发式信息
            initializePheromoneMatrix();
            initializeHeuristicMatrix();

            // 主迭代循环
            for (int iteration = 0; iteration < config.getMaxIterations(); iteration++) {
                // 并行构建蚂蚁解
                constructAntSolutionsParallel();

                // 更新全局最优解
                updateGlobalBest();

                // 更新信息素
                updatePheromone();

                // 信息素挥发
                evaporatePheromone();

                // 每50代输出一次进度
                if (iteration % 50 == 0) {
                    System.out.printf("🔄 第%d代完成，当前最优适应度: %.4f%n",
                            iteration, globalBestSolution != null ? globalBestSolution.getFitness() : 0.0);
                }

                // 检查收敛条件
                if (isConverged()) {
                    System.out.printf("✅ 算法在第%d代收敛%n", iteration);
                    break;
                }
            }

            // 输出最终结果
            System.out.printf(
                    """
                            ✅ 蚁群算法排课完成
                            🏆 最优解适应度: %.4f
                            🐜 参与蚂蚁数量: %d
                            ⚠️ 约束违反数: %d
                            %n""",
                    globalBestSolution.getFitness(),
                    config.getAntCount(),
                    globalBestSolution.getConstraintViolations()
            );

        } finally {
            executor.close(); // 关闭虚拟线程执行器
        }
    }

    /**
     * 初始化算法组件
     * 创建蚂蚁群体和相关数据结构
     */
    private void initializeComponents() {
        // 初始化蚂蚁群体
        this.ants = IntStream.range(0, config.getAntCount())
                .mapToObj(i -> new Ant(i))
                .toList();

        // 初始化矩阵维度（这里使用示例维度，实际应根据具体问题调整）
        int courseCount = 100;  // 课程数量
        int timeSlotCount = 50; // 时间段数量
        int classroomCount = 30; // 教室数量

        this.pheromoneMatrix = new double[courseCount][timeSlotCount][classroomCount];
        this.heuristicMatrix = new double[courseCount][timeSlotCount][classroomCount];
    }

    /**
     * 初始化信息素矩阵
     * 将所有信息素浓度设置为初始值
     */
    private void initializePheromoneMatrix() {
        double initialPheromone = config.getInitialPheromone();
        for (double[][] courseMatrix : pheromoneMatrix) {
            for (double[] timeSlotArray : courseMatrix) {
                Arrays.fill(timeSlotArray, initialPheromone);
            }
        }
    }

    /**
     * 初始化启发式信息矩阵
     * 计算每个课程-时间-教室组合的启发式价值
     */
    private void initializeHeuristicMatrix() {
        for (int course = 0; course < heuristicMatrix.length; course++) {
            for (int timeSlot = 0; timeSlot < heuristicMatrix[course].length; timeSlot++) {
                for (int classroom = 0; classroom < heuristicMatrix[course][timeSlot].length; classroom++) {
                    // 计算启发式价值（示例：基于教室容量、时间偏好等）
                    heuristicMatrix[course][timeSlot][classroom] = calculateHeuristicValue(course, timeSlot, classroom);
                }
            }
        }
    }

    /**
     * 计算启发式价值
     * 根据课程特点、时间偏好、教室适配度等因素计算启发式价值
     *
     * @param course 课程索引
     * @param timeSlot 时间段索引
     * @param classroom 教室索引
     * @return 启发式价值
     */
    private double calculateHeuristicValue(int course, int timeSlot, int classroom) {
        // 示例计算逻辑，实际应根据具体业务规则调整
        double capacityMatch = 1.0; // 教室容量匹配度
        double timePreference = 1.0; // 时间偏好度
        double resourceAvailability = 1.0; // 资源可用性

        return capacityMatch * timePreference * resourceAvailability;
    }

    /**
     * 并行构建蚂蚁解
     * 使用虚拟线程并行执行所有蚂蚁的路径构建过程
     */
    private void constructAntSolutionsParallel() {
        var futures = ants.stream()
                .map(ant -> CompletableFuture.runAsync(() -> {
                    ScheduleChromosome solution = ant.constructSolution(pheromoneMatrix, heuristicMatrix);
                    ant.setSolution(solution);
                }, executor))
                .toList();

        // 等待所有蚂蚁完成路径构建
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }

    /**
     * 更新全局最优解
     * 从当前代的所有蚂蚁中找出最优解，并更新全局记录
     */
    private void updateGlobalBest() {
        ScheduleChromosome iterationBest = ants.stream()
                .map(Ant::getSolution)
                .filter(Objects::nonNull)
                .max(Comparator.comparingDouble(ScheduleChromosome::getFitness))
                .orElse(null);

        if (iterationBest != null) {
            if (globalBestSolution == null || iterationBest.getFitness() > globalBestSolution.getFitness()) {
                globalBestSolution = iterationBest.clone();
            }
        }
    }

    /**
     * 更新信息素
     * 根据蚂蚁找到的解的质量更新信息素浓度
     * 好的解会增强对应路径的信息素
     */
    private void updatePheromone() {
        // 只有最优的蚂蚁才能留下信息素（精英蚂蚁策略）
        List<Ant> eliteAnts = ants.stream()
                .filter(ant -> ant.getSolution() != null)
                .sorted((a, b) -> Double.compare(b.getSolution().getFitness(), a.getSolution().getFitness()))
                .limit(config.getEliteAntCount())
                .toList();

        for (Ant ant : eliteAnts) {
            ScheduleChromosome solution = ant.getSolution();
            double pheromoneDeposit = config.getPheromoneIntensity() * solution.getFitness();

            // 在解对应的路径上增加信息素
            depositPheromone(solution, pheromoneDeposit);
        }
    }

    /**
     * 在指定路径上沉积信息素
     *
     * @param solution 排课解
     * @param amount 信息素沉积量
     */
    private void depositPheromone(ScheduleChromosome solution, double amount) {
        // TODO: 根据具体的解结构在对应的信息素矩阵位置增加信息素
        // 这里需要根据ScheduleChromosome的具体结构来实现
    }

    /**
     * 信息素挥发
     * 模拟自然界中信息素随时间挥发的现象，避免算法过早收敛
     */
    private void evaporatePheromone() {
        double evaporationRate = config.getEvaporationRate();
        double minPheromone = config.getMinPheromone();

        for (double[][] courseMatrix : pheromoneMatrix) {
            for (double[] timeSlotArray : courseMatrix) {
                for (int i = 0; i < timeSlotArray.length; i++) {
                    timeSlotArray[i] *= (1.0 - evaporationRate);
                    // 确保信息素不会低于最小值
                    timeSlotArray[i] = Math.max(timeSlotArray[i], minPheromone);
                }
            }
        }
    }

    /**
     * 检查算法是否收敛
     * 通过分析信息素分布的方差来判断是否收敛
     *
     * @return 如果算法收敛返回true，否则返回false
     */
    private boolean isConverged() {
        // 计算信息素矩阵的方差
        double sum = 0.0;
        double sumSquare = 0.0;
        int count = 0;

        for (double[][] courseMatrix : pheromoneMatrix) {
            for (double[] timeSlotArray : courseMatrix) {
                for (double pheromone : timeSlotArray) {
                    sum += pheromone;
                    sumSquare += pheromone * pheromone;
                    count++;
                }
            }
        }

        double mean = sum / count;
        double variance = (sumSquare / count) - (mean * mean);
        double standardDeviation = Math.sqrt(variance);

        // 如果标准差小于阈值，认为已收敛
        return standardDeviation < config.getConvergenceThreshold();
    }

    /**
     * 蚂蚁类
     * 代表蚁群中的单个蚂蚁，负责构建排课解
     */
    private static class Ant {
        /**
         * 蚂蚁编号
         */
        private final int id;

        /**
         * 蚂蚁构建的排课解
         */
        private ScheduleChromosome solution;

        /**
         * 随机数生成器
         */
        private final Random random = new Random();

        /**
         * 构造函数
         *
         * @param id 蚂蚁编号
         */
        public Ant(int id) {
            this.id = id;
        }

        /**
         * 构建排课解
         * 蚂蚁根据信息素和启发式信息构建完整的排课方案
         *
         * @param pheromoneMatrix 信息素矩阵
         * @param heuristicMatrix 启发式信息矩阵
         * @return 构建的排课解
         */
        public ScheduleChromosome constructSolution(double[][][] pheromoneMatrix, double[][][] heuristicMatrix) {
            ScheduleChromosome chromosome = new ScheduleChromosome();

            // TODO: 实现具体的解构建逻辑
            // 1. 遍历所有课程
            // 2. 为每门课程选择时间段和教室
            // 3. 选择概率基于信息素浓度和启发式价值
            // 4. 使用轮盘赌选择或其他概率选择方法

            // 示例：随机初始化（实际应该基于信息素和启发式信息）
            chromosome.randomize();

            return chromosome;
        }

        /**
         * 概率选择
         * 根据信息素浓度和启发式价值计算选择概率
         *
         * @param pheromone 信息素浓度
         * @param heuristic 启发式价值
         * @param alpha 信息素重要性参数
         * @param beta 启发式重要性参数
         * @return 选择概率
         */
        private double calculateProbability(double pheromone, double heuristic, double alpha, double beta) {
            return Math.pow(pheromone, alpha) * Math.pow(heuristic, beta);
        }

        // Getter和Setter方法
        public int getId() {
            return id;
        }

        public ScheduleChromosome getSolution() {
            return solution;
        }

        public void setSolution(ScheduleChromosome solution) {
            this.solution = solution;
        }
    }
}

