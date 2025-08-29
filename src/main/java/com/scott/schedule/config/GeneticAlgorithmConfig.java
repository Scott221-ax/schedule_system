package com.scott.schedule.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 遗传算法配置类
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Configuration
@ConfigurationProperties(prefix = "genetic.algorithm")
public class GeneticAlgorithmConfig {
    
    /**
     * 种群大小
     */
    private int populationSize = 100;
    
    /**
     * 最大进化代数
     */
    private int maxGenerations = 1000;
    
    /**
     * 交叉概率
     */
    private double crossoverRate = 0.8;
    
    /**
     * 变异概率
     */
    private double mutationRate = 0.1;
    
    /**
     * 精英保留比例
     */
    private double eliteRate = 0.1;
    
    /**
     * 锦标赛大小
     */
    private int tournamentSize = 3;
    
    /**
     * 收敛阈值
     */
    private double convergenceThreshold = 0.001;
    
    /**
     * 最大无改进代数
     */
    private int maxGenerationsWithoutImprovement = 100;
    
    // Getters and Setters
    public int getPopulationSize() {
        return populationSize;
    }
    
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }
    
    public int getMaxGenerations() {
        return maxGenerations;
    }
    
    public void setMaxGenerations(int maxGenerations) {
        this.maxGenerations = maxGenerations;
    }
    
    public double getCrossoverRate() {
        return crossoverRate;
    }
    
    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }
    
    public double getMutationRate() {
        return mutationRate;
    }
    
    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }
    
    public double getEliteRate() {
        return eliteRate;
    }
    
    public void setEliteRate(double eliteRate) {
        this.eliteRate = eliteRate;
    }
    
    public int getTournamentSize() {
        return tournamentSize;
    }
    
    public void setTournamentSize(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }
    
    public double getConvergenceThreshold() {
        return convergenceThreshold;
    }
    
    public void setConvergenceThreshold(double convergenceThreshold) {
        this.convergenceThreshold = convergenceThreshold;
    }
    
    public int getMaxGenerationsWithoutImprovement() {
        return maxGenerationsWithoutImprovement;
    }
    
    public void setMaxGenerationsWithoutImprovement(int maxGenerationsWithoutImprovement) {
        this.maxGenerationsWithoutImprovement = maxGenerationsWithoutImprovement;
    }
}
