package com.scott.schedule.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 蚁群算法配置类
 * 包含蚁群算法的所有参数配置
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "algorithm.ant-colony")
public class AntColonyConfig {

    /**
     * 蚂蚁数量
     * 蚁群中蚂蚁的总数，影响算法的搜索广度
     * 建议值：20-100
     */
    private int antCount = 50;

    /**
     * 最大迭代次数
     * 算法运行的最大代数
     * 建议值：100-1000
     */
    private int maxIterations = 500;

    /**
     * 信息素重要性参数 (α)
     * 控制信息素在路径选择中的重要程度
     * 值越大，蚂蚁越倾向于选择信息素浓度高的路径
     * 建议值：1.0-2.0
     */
    private double alpha = 1.0;

    /**
     * 启发式信息重要性参数 (β)
     * 控制启发式信息在路径选择中的重要程度
     * 值越大，蚂蚁越倾向于选择启发式价值高的路径
     * 建议值：2.0-5.0
     */
    private double beta = 2.0;

    /**
     * 信息素挥发率 (ρ)
     * 每次迭代后信息素的挥发比例
     * 防止算法过早收敛，保持探索能力
     * 建议值：0.1-0.3
     */
    private double evaporationRate = 0.2;

    /**
     * 信息素强度系数 (Q)
     * 控制蚂蚁释放信息素的强度
     * 影响信息素更新的幅度
     * 建议值：1.0-100.0
     */
    private double pheromoneIntensity = 10.0;

    /**
     * 初始信息素浓度
     * 算法开始时所有路径的初始信息素值
     * 建议值：0.1-1.0
     */
    private double initialPheromone = 0.5;

    /**
     * 最小信息素浓度
     * 防止信息素完全挥发，保持最小探索概率
     * 建议值：0.01-0.1
     */
    private double minPheromone = 0.01;

    /**
     * 最大信息素浓度
     * 防止信息素过度积累，避免算法过早收敛
     * 建议值：1.0-10.0
     */
    private double maxPheromone = 5.0;

    /**
     * 精英蚂蚁数量
     * 每代中能够更新信息素的最优蚂蚁数量
     * 建议值：antCount的10%-30%
     */
    private int eliteAntCount = 10;

    /**
     * 收敛阈值
     * 判断算法是否收敛的标准差阈值
     * 当信息素分布的标准差小于此值时认为收敛
     * 建议值：0.001-0.01
     */
    private double convergenceThreshold = 0.005;

    /**
     * 局部搜索概率
     * 蚂蚁执行局部搜索的概率
     * 用于增强算法的局部优化能力
     * 建议值：0.1-0.3
     */
    private double localSearchProbability = 0.2;

    /**
     * 是否启用精英策略
     * 是否只允许最优蚂蚁更新信息素
     */
    private boolean eliteStrategy = true;

    /**
     * 是否启用最大最小蚂蚁系统 (MMAS)
     * 限制信息素的最大值和最小值
     */
    private boolean maxMinAntSystem = true;

    /**
     * 验证配置参数的有效性
     * 在配置加载后自动调用，确保参数在合理范围内
     */
    public void validate() {
        if (antCount <= 0) {
            throw new IllegalArgumentException("蚂蚁数量必须大于0");
        }
        if (maxIterations <= 0) {
            throw new IllegalArgumentException("最大迭代次数必须大于0");
        }
        if (alpha < 0) {
            throw new IllegalArgumentException("信息素重要性参数α必须非负");
        }
        if (beta < 0) {
            throw new IllegalArgumentException("启发式重要性参数β必须非负");
        }
        if (evaporationRate < 0 || evaporationRate > 1) {
            throw new IllegalArgumentException("信息素挥发率必须在[0,1]范围内");
        }
        if (pheromoneIntensity <= 0) {
            throw new IllegalArgumentException("信息素强度系数必须大于0");
        }
        if (initialPheromone <= 0) {
            throw new IllegalArgumentException("初始信息素浓度必须大于0");
        }
        if (minPheromone <= 0 || minPheromone >= maxPheromone) {
            throw new IllegalArgumentException("最小信息素浓度必须大于0且小于最大信息素浓度");
        }
        if (eliteAntCount <= 0 || eliteAntCount > antCount) {
            throw new IllegalArgumentException("精英蚂蚁数量必须在(0, antCount]范围内");
        }
        if (convergenceThreshold <= 0) {
            throw new IllegalArgumentException("收敛阈值必须大于0");
        }
        if (localSearchProbability < 0 || localSearchProbability > 1) {
            throw new IllegalArgumentException("局部搜索概率必须在[0,1]范围内");
        }
    }

    /**
     * 获取推荐的配置
     * 根据问题规模返回推荐的参数配置
     *
     * @param problemSize 问题规模（如课程数量）
     * @return 推荐的配置
     */
    public static AntColonyConfig getRecommendedConfig(int problemSize) {
        AntColonyConfig config = new AntColonyConfig();

        if (problemSize < 50) {
            // 小规模问题
            config.setAntCount(20);
            config.setMaxIterations(200);
            config.setEvaporationRate(0.1);
        } else if (problemSize < 200) {
            // 中等规模问题
            config.setAntCount(50);
            config.setMaxIterations(500);
            config.setEvaporationRate(0.2);
        } else {
            // 大规模问题
            config.setAntCount(100);
            config.setMaxIterations(1000);
            config.setEvaporationRate(0.3);
        }

        return config;
    }

    /**
     * 打印配置信息
     * 用于调试和日志记录
     */
    public void printConfig() {
        System.out.printf("""
                🐜 蚁群算法配置信息:
                ├── 蚂蚁数量: %d
                ├── 最大迭代次数: %d
                ├── 信息素重要性(α): %.2f
                ├── 启发式重要性(β): %.2f
                ├── 信息素挥发率(ρ): %.2f
                ├── 信息素强度系数(Q): %.2f
                ├── 初始信息素浓度: %.3f
                ├── 最小信息素浓度: %.3f
                ├── 最大信息素浓度: %.3f
                ├── 精英蚂蚁数量: %d
                ├── 收敛阈值: %.4f
                ├── 局部搜索概率: %.2f
                ├── 精英策略: %s
                └── 最大最小蚂蚁系统: %s
                %n""",
                antCount, maxIterations, alpha, beta, evaporationRate,
                pheromoneIntensity, initialPheromone, minPheromone, maxPheromone,
                eliteAntCount, convergenceThreshold, localSearchProbability,
                eliteStrategy ? "启用" : "禁用",
                maxMinAntSystem ? "启用" : "禁用"
        );
    }
}

