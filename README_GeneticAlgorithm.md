# 遗传算法排课系统框架 (JDK 21)

## 概述

本项目实现了一个基于遗传算法的智能排课系统框架，**使用JDK 21的最新特性**，包括模式匹配、记录类、虚拟线程、文本块等，用于解决学校课程安排问题。

## 🚀 JDK 21 特性使用

### 1. **记录类 (Record Classes)**
- `CourseSchedule` - 课程安排信息，自动生成构造函数、getter、equals、hashCode、toString
- `EvolutionResult` - 进化结果，包含代数和最优解
- `ConstraintResult` - 约束检查结果
- `PopulationStats` - 种群统计信息
- `EvolutionProgress` - 进化进度

### 2. **模式匹配 (Pattern Matching)**
- Switch表达式中的模式匹配
- 类型模式匹配 (`case ScheduleChromosome c when ...`)
- 空值检查模式匹配

### 3. **Switch表达式 (Switch Expressions)**
- 使用箭头语法 (`->`)
- 支持模式匹配
- 支持yield语句

### 4. **虚拟线程 (Virtual Threads)**
- 使用 `Executors.newVirtualThreadPerTaskExecutor()` 创建虚拟线程执行器
- 并行计算种群适应度，提高性能
- 适合I/O密集型操作

### 5. **文本块 (Text Blocks)**
- 多行字符串格式化
- 支持插值表达式
- 提高代码可读性

### 6. **var关键字和类型推断**
- 局部变量类型推断
- 减少冗余代码
- 提高代码简洁性

### 7. **Stream API增强**
- `toList()` 方法
- 函数式编程风格
- 并行流处理

## 架构设计

### 核心组件

1. **ClassScheduler** - 排课服务接口
2. **GeneticClassScheduler** - 遗传算法排课实现（使用JDK 21特性）
3. **ScheduleChromosome** - 染色体（课程安排方案）
4. **FitnessCalculator** - 适应度计算器
5. **SelectionOperator** - 选择操作
6. **CrossoverOperator** - 交叉操作
7. **MutationOperator** - 变异操作
8. **GeneticAlgorithmUtils** - 工具类（使用JDK 21特性）

### 遗传算法流程

```
初始化种群 → 并行计算适应度 → 选择 → 交叉 → 变异 → 精英保留 → 更新种群
     ↑                                                                    ↓
     ←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←
```

## 使用方法

### 1. 配置参数

在 `application.yml` 中配置遗传算法参数：

```yaml
genetic:
  algorithm:
    population-size: 100
    max-generations: 1000
    crossover-rate: 0.8
    mutation-rate: 0.1
    elite-rate: 0.1
    tournament-size: 3
    convergence-threshold: 0.001
    max-generations-without-improvement: 100
```

### 2. 调用排课服务

```java
@Autowired
private ClassScheduler classScheduler;

public void scheduleClasses() {
    classScheduler.schedule(); // 自动执行遗传算法排课
}
```

### 3. 使用工具类

```java
// 计算种群统计信息
var stats = GeneticAlgorithmUtils.calculatePopulationStats(population);
System.out.println(GeneticAlgorithmUtils.generateStatsReport(stats));

// 检查种群是否收敛
boolean converged = GeneticAlgorithmUtils.isPopulationConverged(population, 0.001);

// 获取精英个体
var elite = GeneticAlgorithmUtils.getEliteIndividuals(population, 0.1);
```

## 🆕 JDK 21 特性示例

### 记录类示例

```java
public record CourseSchedule(
    Long courseId,      // 课程ID
    Long teacherId,     // 教师ID
    Long classroomId,   // 教室ID
    Long timeSlotId,    // 时间段ID
    Long classId        // 班级ID
) {
    public boolean isValid() {
        return courseId != null && teacherId != null && 
               classroomId != null && timeSlotId != null && classId != null;
    }
    
    // 不可变对象，使用with方法创建新实例
    public CourseSchedule withTeacherId(Long newTeacherId) {
        return new CourseSchedule(courseId, newTeacherId, classroomId, timeSlotId, classId);
    }
}
```

### 模式匹配示例

```java
public static boolean isValidChromosome(ScheduleChromosome chromosome) {
    return switch (chromosome) {
        case null -> false;
        case ScheduleChromosome c when c.getGenes() == null -> false;
        case ScheduleChromosome c when c.getGenes().isEmpty() -> false;
        case ScheduleChromosome c -> c.getGenes().values().stream()
            .allMatch(ScheduleChromosome.CourseSchedule::isValid);
    };
}
```

### Switch表达式示例

```java
var newSchedule = switch (mutationType) {
    case 0 -> schedule.withTeacherId(generateRandomTeacherId());
    case 1 -> schedule.withClassroomId(generateRandomClassroomId());
    case 2 -> schedule.withTimeSlotId(generateRandomTimeSlotId());
    case 3 -> schedule.withClassId(generateRandomClassId());
    default -> throw new IllegalStateException("Unexpected mutation type: " + mutationType);
};
```

### 文本块示例

```java
System.out.println("""
    🚀 开始执行遗传算法排课
    📊 种群大小: %d
    🔄 最大代数: %d
    ✂️ 交叉概率: %.2f
    🧬 变异概率: %.2f
    🏆 精英比例: %.2f
    """.formatted(
        config.getPopulationSize(),
        config.getMaxGenerations(),
        config.getCrossoverRate(),
        config.getMutationRate(),
        config.getEliteRate()
    ));
```

### 虚拟线程示例

```java
// 使用虚拟线程执行器，提高并发性能
private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

// 并行计算种群适应度
private void calculateFitnessParallel(List<ScheduleChromosome> population) {
    var futures = population.stream()
        .map(chromosome -> CompletableFuture.runAsync(() -> {
            double fitness = fitnessCalculator.calculate(chromosome);
            chromosome.setFitness(fitness);
        }, executor))
        .toList();
    
    // 等待所有计算完成
    CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
}
```

## 需要完善的部分

### 1. 数据初始化

- 在 `ScheduleChromosome.randomize()` 方法中实现随机初始化逻辑
- 根据实际的课程、教师、教室、时间段数据进行初始化

### 2. 约束检查

- 在 `DefaultFitnessCalculator` 中实现具体的约束检查逻辑
- 硬约束：教师时间冲突、教室时间冲突、班级时间冲突等
- 软约束：教师偏好、教室容量匹配、课程连续性等

### 3. 随机数据生成

- 在 `RandomMutationOperator` 中实现真实的随机ID生成
- 根据数据库中的实际数据范围生成随机值

### 4. 结果持久化

- 在 `GeneticClassScheduler.applySchedule()` 方法中实现结果保存逻辑
- 将染色体转换为实际的课程安排记录并保存到数据库

## 扩展建议

### 1. 多目标优化

- 支持多个适应度目标（如时间冲突最小化、教师满意度最大化等）
- 实现帕累托最优解集

### 2. 自适应参数

- 根据进化过程动态调整交叉率和变异率
- 实现自适应种群大小

### 3. 局部搜索

- 在遗传算法基础上增加局部搜索优化
- 实现爬山算法或模拟退火等局部优化方法

### 4. 并行化增强

- 利用虚拟线程的轻量级特性
- 实现岛屿模型等并行遗传算法
- 使用结构化并发 (Structured Concurrency)

## 注意事项

1. **JDK版本要求**: 必须使用JDK 21或更高版本
2. **性能优势**: 虚拟线程适合I/O密集型操作，CPU密集型操作建议使用传统线程池
3. **内存管理**: 记录类是不可变的，有助于减少内存分配和GC压力
4. **代码简洁性**: 模式匹配和switch表达式使代码更加清晰易读
5. **遗传算法特性**: 遗传算法是启发式算法，不保证找到全局最优解
6. **参数调优**: 参数调优对算法性能影响很大，需要根据实际问题进行调整
7. **约束条件**: 约束条件的设计直接影响解的质量和可行性
8. **测试验证**: 建议在正式使用前进行充分的测试和验证

## 系统要求

- **JDK版本**: 21.0.0 或更高版本
- **Spring Boot**: 3.0.0 或更高版本
- **内存**: 建议8GB以上
- **CPU**: 建议多核处理器，支持虚拟线程
