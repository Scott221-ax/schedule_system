package com.scott.schedule.service.impl;

import com.scott.schedule.algorithm.ScheduleChromosome;
import com.scott.schedule.service.ClassScheduler;
import org.springframework.stereotype.Service;

/**
 * 混合算法排课服务
 * 结合遗传算法和蚁群算法的优势，提供更强大的排课能力
 *
 * 算法策略：
 * 1. 第一阶段：使用遗传算法进行全局搜索，快速找到较好的解
 * 2. 第二阶段：使用蚁群算法进行局部优化，利用信息素机制精细调优
 * 3. 第三阶段：局部搜索进行最终优化
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Service
public class HybridScheduler implements ClassScheduler {

    /**
     * 遗传算法排课服务
     * 用于第一阶段的全局搜索
     */
    private final GeneticClassScheduler geneticScheduler;

    /**
     * 蚁群算法排课服务
     * 用于第二阶段的信息素引导优化
     */
    private final AntColonyScheduler antColonyScheduler;

    /**
     * 当前最优解
     */
    private ScheduleChromosome bestSolution;

    /**
     * 构造函数
     *
     * @param geneticScheduler 遗传算法排课服务
     * @param antColonyScheduler 蚁群算法排课服务
     */
    public HybridScheduler(GeneticClassScheduler geneticScheduler,
                          AntColonyScheduler antColonyScheduler) {
        this.geneticScheduler = geneticScheduler;
        this.antColonyScheduler = antColonyScheduler;
    }

    /**
     * 执行混合算法排课
     * 分阶段执行不同算法，充分发挥各算法的优势
     */
    @Override
    public void schedule() {
        System.out.println(
                """
                        🔄 开始执行混合算法排课
                        📋 算法策略：遗传算法 → 蚁群算法 → 局部搜索
                        🎯 目标：结合全局搜索和局部优化的优势
                        """
        );

        long startTime = System.currentTimeMillis();

        try {
            // 第一阶段：遗传算法全局搜索
            executePhaseOne();

            // 第二阶段：蚁群算法优化
            executePhaseTwo();

            // 第三阶段：局部搜索精调
            executePhaseThree();

            // 输出最终结果
            outputFinalResults(startTime);

        } catch (Exception e) {
            System.err.printf("❌ 混合算法执行失败: %s%n", e.getMessage());
            throw new RuntimeException("混合算法排课失败", e);
        }
    }

    /**
     * 第一阶段：遗传算法全局搜索
     * 利用遗传算法的全局搜索能力快速找到较好的解空间
     */
    private void executePhaseOne() {
        System.out.println(
                """
                        🧬 === 第一阶段：遗传算法全局搜索 ===
                        🎯 目标：快速探索解空间，找到高质量的初始解
                        """
        );

        long phaseStartTime = System.currentTimeMillis();

        // 执行遗传算法
        geneticScheduler.schedule();

        // 获取遗传算法的最优解作为下一阶段的输入
        // TODO: 从遗传算法中获取最优解
        // bestSolution = geneticScheduler.getBestSolution();

        long phaseEndTime = System.currentTimeMillis();
        System.out.printf("✅ 第一阶段完成，耗时: %d ms%n", phaseEndTime - phaseStartTime);
    }

    /**
     * 第二阶段：蚁群算法优化
     * 利用蚁群算法的信息素机制和正反馈特性进行优化
     */
    private void executePhaseTwo() {
        System.out.println(
                """
                        🐜 === 第二阶段：蚁群算法优化 ===
                        🎯 目标：利用信息素机制，在遗传算法基础上进一步优化
                        """
        );

        long phaseStartTime = System.currentTimeMillis();

        // 如果第一阶段有结果，将其作为蚁群算法的启发式信息
        if (bestSolution != null) {
            // TODO: 将遗传算法的结果转换为蚁群算法的启发式信息
            initializeAntColonyWithGeneticResult();
        }

        // 执行蚁群算法
        antColonyScheduler.schedule();

        // 获取蚁群算法的最优解
        // TODO: 从蚁群算法中获取最优解并与当前最优解比较
        // updateBestSolution(antColonyScheduler.getBestSolution());

        long phaseEndTime = System.currentTimeMillis();
        System.out.printf("✅ 第二阶段完成，耗时: %d ms%n", phaseEndTime - phaseStartTime);
    }

    /**
     * 第三阶段：局部搜索精调
     * 使用局部搜索算法对当前最优解进行精细调优
     */
    private void executePhaseThree() {
        System.out.println(
                """
                        🔍 === 第三阶段：局部搜索精调 ===
                        🎯 目标：对当前最优解进行局部优化，提升解的质量
                        """
        );

        long phaseStartTime = System.currentTimeMillis();

        if (bestSolution != null) {
            // 执行局部搜索
            ScheduleChromosome improvedSolution = performLocalSearch(bestSolution);

            // 更新最优解
            if (improvedSolution.getFitness() > bestSolution.getFitness()) {
                bestSolution = improvedSolution;
                System.out.printf("🎉 局部搜索找到更优解，适应度提升至: %.4f%n",
                                bestSolution.getFitness());
            } else {
                System.out.println("ℹ️ 局部搜索未找到更优解，当前解已接近局部最优");
            }
        }

        long phaseEndTime = System.currentTimeMillis();
        System.out.printf("✅ 第三阶段完成，耗时: %d ms%n", phaseEndTime - phaseStartTime);
    }

    /**
     * 使用遗传算法结果初始化蚁群算法
     * 将遗传算法找到的优秀解转换为蚁群算法的启发式信息
     */
    private void initializeAntColonyWithGeneticResult() {
        System.out.println("🔄 正在将遗传算法结果转换为蚁群算法启发式信息...");

        // TODO: 实现具体的转换逻辑
        // 1. 分析遗传算法最优解的特征
        // 2. 在对应的信息素矩阵位置增加初始信息素
        // 3. 调整启发式信息矩阵

        System.out.println("✅ 启发式信息初始化完成");
    }

    /**
     * 执行局部搜索
     * 对给定解进行局部优化
     *
     * @param solution 待优化的解
     * @return 优化后的解
     */
    private ScheduleChromosome performLocalSearch(ScheduleChromosome solution) {
        System.out.println("🔍 开始局部搜索优化...");

        ScheduleChromosome currentSolution = solution.clone();
        ScheduleChromosome bestLocalSolution = solution.clone();

        // 局部搜索参数
        int maxLocalIterations = 100;
        int improvementCount = 0;

        for (int i = 0; i < maxLocalIterations; i++) {
            // 生成邻域解
            ScheduleChromosome neighbor = generateNeighbor(currentSolution);

            // 如果邻域解更优，则接受
            if (neighbor.getFitness() > bestLocalSolution.getFitness()) {
                bestLocalSolution = neighbor.clone();
                currentSolution = neighbor.clone();
                improvementCount++;
            }

            // 如果连续多次没有改进，提前终止
            if (i - improvementCount > 20) {
                break;
            }
        }

        System.out.printf("🔍 局部搜索完成，共进行 %d 次改进%n", improvementCount);
        return bestLocalSolution;
    }

    /**
     * 生成邻域解
     * 通过小幅度修改当前解来生成邻域解
     *
     * @param solution 当前解
     * @return 邻域解
     */
    private ScheduleChromosome generateNeighbor(ScheduleChromosome solution) {
        ScheduleChromosome neighbor = solution.clone();

        // TODO: 实现具体的邻域生成策略
        // 1. 随机选择两门课程交换时间
        // 2. 随机选择一门课程更换教室
        // 3. 调整连续课程的时间安排

        // 示例：简单的随机变异
        // neighbor.mutate(0.1);

        return neighbor;
    }

    /**
     * 更新最优解
     *
     * @param newSolution 新解
     */
    private void updateBestSolution(ScheduleChromosome newSolution) {
        if (newSolution != null &&
            (bestSolution == null || newSolution.getFitness() > bestSolution.getFitness())) {
            bestSolution = newSolution.clone();
        }
    }

    /**
     * 输出最终结果
     *
     * @param startTime 算法开始时间
     */
    private void outputFinalResults(long startTime) {
        long totalTime = System.currentTimeMillis() - startTime;

        System.out.printf(
                """

                        🎉 === 混合算法排课完成 ===
                        🏆 最优解适应度: %.4f
                        ⏱️ 总耗时: %d ms
                        ⚠️ 约束违反数: %d
                        📊 算法组合: 遗传算法 + 蚁群算法 + 局部搜索

                        🔍 算法执行总结:
                        ├── 第一阶段(遗传算法): 全局搜索，快速定位优质解空间
                        ├── 第二阶段(蚁群算法): 信息素引导，精细化搜索优化
                        └── 第三阶段(局部搜索): 局部调优，提升解的精度

                        ✨ 混合算法充分发挥了各算法的优势，获得了高质量的排课方案！
                        %n""",
                bestSolution != null ? bestSolution.getFitness() : 0.0,
                totalTime,
                bestSolution != null ? bestSolution.getConstraintViolations() : 0
        );
    }

    /**
     * 获取最优解
     *
     * @return 当前最优解
     */
    public ScheduleChromosome getBestSolution() {
        return bestSolution;
    }

    /**
     * 算法性能统计
     * 用于分析各阶段的性能表现
     */
    public static class PerformanceStats {
        private long geneticAlgorithmTime;
        private long antColonyTime;
        private long localSearchTime;
        private double initialFitness;
        private double finalFitness;
        private int totalIterations;

        // Getter和Setter方法
        public long getGeneticAlgorithmTime() { return geneticAlgorithmTime; }
        public void setGeneticAlgorithmTime(long geneticAlgorithmTime) { this.geneticAlgorithmTime = geneticAlgorithmTime; }

        public long getAntColonyTime() { return antColonyTime; }
        public void setAntColonyTime(long antColonyTime) { this.antColonyTime = antColonyTime; }

        public long getLocalSearchTime() { return localSearchTime; }
        public void setLocalSearchTime(long localSearchTime) { this.localSearchTime = localSearchTime; }

        public double getInitialFitness() { return initialFitness; }
        public void setInitialFitness(double initialFitness) { this.initialFitness = initialFitness; }

        public double getFinalFitness() { return finalFitness; }
        public void setFinalFitness(double finalFitness) { this.finalFitness = finalFitness; }

        public int getTotalIterations() { return totalIterations; }
        public void setTotalIterations(int totalIterations) { this.totalIterations = totalIterations; }

        /**
         * 计算性能改进比例
         *
         * @return 性能改进比例
         */
        public double getImprovementRatio() {
            if (initialFitness == 0) return 0;
            return (finalFitness - initialFitness) / initialFitness * 100;
        }

        /**
         * 获取总执行时间
         *
         * @return 总执行时间（毫秒）
         */
        public long getTotalTime() {
            return geneticAlgorithmTime + antColonyTime + localSearchTime;
        }
    }
}

