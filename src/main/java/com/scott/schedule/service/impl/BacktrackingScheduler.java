package com.scott.schedule.service.impl;

import com.scott.schedule.algorithm.ScheduleChromosome;
import com.scott.schedule.config.BacktrackingConfig;
import com.scott.schedule.service.ClassScheduler;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 基于回溯算法的排课服务实现
 * 使用深度优先搜索和回溯技术寻找可行的排课方案
 * 适合小到中等规模的排课问题，能够找到精确解
 *
 * 算法特点：
 * 1. 完备性：能够找到所有可行解或证明无解
 * 2. 最优性：可以找到满足所有约束的解
 * 3. 剪枝优化：通过约束传播和启发式剪枝提高效率
 * 4. 灵活性：支持多种变量选择和值选择策略
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Service
public class BacktrackingScheduler implements ClassScheduler {

    /**
     * 回溯算法配置参数
     */
    private final BacktrackingConfig config;

    /**
     * 搜索统计信息
     */
    private SearchStatistics statistics;

    /**
     * 约束检查器
     */
    private ConstraintChecker constraintChecker;

    /**
     * 变量选择器
     */
    private VariableSelector variableSelector;

    /**
     * 值选择器
     */
    private ValueSelector valueSelector;

    /**
     * 当前最优解
     */
    private ScheduleChromosome bestSolution;

    /**
     * 搜索开始时间
     */
    private long searchStartTime;

    /**
     * 构造函数
     *
     * @param config 回溯算法配置参数
     */
    public BacktrackingScheduler(BacktrackingConfig config) {
        this.config = config;
        this.statistics = new SearchStatistics();
        this.constraintChecker = new ConstraintChecker();
        this.variableSelector = new VariableSelector(config);
        this.valueSelector = new ValueSelector(config);
    }

    /**
     * 执行回溯算法排课
     */
    @Override
    public void schedule() {
        System.out.printf(
                """
                        🔍 开始执行回溯算法排课
                        📋 变量选择策略: %s
                        🎯 值选择策略: %s
                        ⏰ 最大搜索时间: %d 秒
                        🌳 最大搜索深度: %d
                        🔧 启用约束传播: %s
                        %n""",
                config.getVariableSelectionStrategy().getDescription(),
                config.getValueSelectionStrategy().getDescription(),
                config.getMaxSearchTimeSeconds(),
                config.getMaxSearchDepth(),
                config.isEnableConstraintPropagation() ? "是" : "否"
        );

        searchStartTime = System.currentTimeMillis();
        statistics.reset();

        try {
            // 初始化搜索状态
            SearchState initialState = initializeSearchState();

            // 执行回溯搜索
            boolean solutionFound = backtrackSearch(initialState, 0);

            // 输出结果
            outputResults(solutionFound);

        } catch (Exception e) {
            System.err.printf("❌ 回溯算法执行失败: %s%n", e.getMessage());
            throw new RuntimeException("回溯算法排课失败", e);
        }
    }

    /**
     * 初始化搜索状态
     *
     * @return 初始搜索状态
     */
    private SearchState initializeSearchState() {
        SearchState state = new SearchState();

        // 初始化变量域
        initializeVariableDomains(state);

        // 如果启用约束传播，进行初始约束传播
        if (config.isEnableConstraintPropagation()) {
            propagateConstraints(state);
        }

        return state;
    }

    /**
     * 回溯搜索主算法
     *
     * @param state 当前搜索状态
     * @param depth 当前搜索深度
     * @return 是否找到解
     */
    private boolean backtrackSearch(SearchState state, int depth) {
        statistics.nodesVisited++;

        // 检查终止条件
        if (isTimeoutExceeded() || depth > config.getMaxSearchDepth()) {
            return false;
        }

        // 检查是否找到完整解
        if (isCompleteAssignment(state)) {
            bestSolution = createSolutionFromState(state);
            statistics.solutionsFound++;
            return true;
        }

        // 选择下一个变量
        Variable nextVariable = variableSelector.selectVariable(state);
        if (nextVariable == null) {
            return false; // 无可选变量
        }

        // 获取变量的可选值
        List<Value> possibleValues = valueSelector.selectValues(nextVariable, state);

        // 尝试每个可能的值
        for (Value value : possibleValues) {
            statistics.assignmentsTried++;

            // 检查赋值是否一致
            if (isConsistentAssignment(nextVariable, value, state)) {
                // 进行赋值
                SearchState newState = makeAssignment(state, nextVariable, value);

                // 约束传播
                if (config.isEnableConstraintPropagation()) {
                    if (!propagateConstraints(newState)) {
                        statistics.constraintPropagationFailures++;
                        continue; // 约束传播失败，尝试下一个值
                    }
                }

                // 递归搜索
                if (backtrackSearch(newState, depth + 1)) {
                    return true; // 找到解
                }

                statistics.backtracks++;
            } else {
                statistics.consistencyCheckFailures++;
            }
        }

        return false; // 所有值都尝试过，无解
    }

    /**
     * 检查是否超时
     *
     * @return 是否超时
     */
    private boolean isTimeoutExceeded() {
        long elapsedTime = (System.currentTimeMillis() - searchStartTime) / 1000;
        return elapsedTime > config.getMaxSearchTimeSeconds();
    }

    /**
     * 检查是否为完整赋值
     *
     * @param state 搜索状态
     * @return 是否为完整赋值
     */
    private boolean isCompleteAssignment(SearchState state) {
        return state.getUnassignedVariables().isEmpty();
    }

    /**
     * 检查赋值是否一致
     *
     * @param variable 变量
     * @param value 值
     * @param state 当前状态
     * @return 是否一致
     */
    private boolean isConsistentAssignment(Variable variable, Value value, SearchState state) {
        return constraintChecker.isConsistent(variable, value, state);
    }

    /**
     * 进行变量赋值
     *
     * @param state 当前状态
     * @param variable 变量
     * @param value 值
     * @return 新的搜索状态
     */
    private SearchState makeAssignment(SearchState state, Variable variable, Value value) {
        SearchState newState = state.clone();
        newState.assign(variable, value);
        return newState;
    }

    /**
     * 约束传播
     *
     * @param state 搜索状态
     * @return 是否成功
     */
    private boolean propagateConstraints(SearchState state) {
        boolean changed = true;
        while (changed) {
            changed = false;

            // AC-3算法进行弧一致性检查
            for (Variable variable : state.getUnassignedVariables()) {
                List<Value> domain = new ArrayList<>(state.getDomain(variable));
                for (Value value : domain) {
                    if (!hasConsistentSupport(variable, value, state)) {
                        state.removeFromDomain(variable, value);
                        changed = true;

                        // 检查域是否为空
                        if (state.getDomain(variable).isEmpty()) {
                            return false; // 无解
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 检查值是否有一致性支持
     *
     * @param variable 变量
     * @param value 值
     * @param state 搜索状态
     * @return 是否有支持
     */
    private boolean hasConsistentSupport(Variable variable, Value value, SearchState state) {
        // TODO: 实现具体的一致性检查逻辑
        return constraintChecker.hasSupport(variable, value, state);
    }

    /**
     * 从搜索状态创建解
     *
     * @param state 搜索状态
     * @return 排课解
     */
    private ScheduleChromosome createSolutionFromState(SearchState state) {
        ScheduleChromosome solution = new ScheduleChromosome();
        // TODO: 根据搜索状态构建具体的排课解
        return solution;
    }

    /**
     * 初始化变量域
     *
     * @param state 搜索状态
     */
    private void initializeVariableDomains(SearchState state) {
        // TODO: 根据具体问题初始化变量和域
        // 示例：为每门课程创建变量，域为可选的时间段和教室组合
    }

    /**
     * 输出搜索结果
     *
     * @param solutionFound 是否找到解
     */
    private void outputResults(boolean solutionFound) {
        long totalTime = System.currentTimeMillis() - searchStartTime;

        System.out.printf(
                """
                        
                        %s 回溯算法搜索完成
                        🏆 找到解: %s
                        ⏱️ 搜索时间: %d ms
                        📊 搜索统计:
                        ├── 访问节点数: %d
                        ├── 尝试赋值数: %d
                        ├── 回溯次数: %d
                        ├── 一致性检查失败: %d
                        ├── 约束传播失败: %d
                        └── 找到解的数量: %d
                        
                        🎯 回溯算法特点: 完备性强，能找到精确解，适合小到中等规模问题
                        %n""",
                solutionFound ? "✅" : "❌",
                solutionFound ? "是" : "否",
                totalTime,
                statistics.nodesVisited,
                statistics.assignmentsTried,
                statistics.backtracks,
                statistics.consistencyCheckFailures,
                statistics.constraintPropagationFailures,
                statistics.solutionsFound
        );
    }

    // ==================== 内部类和数据结构 ====================

    /**
     * 搜索状态类
     * 维护当前的变量赋值和域信息
     */
    private static class SearchState implements Cloneable {
        private Map<Variable, Value> assignments = new HashMap<>();
        private Map<Variable, Set<Value>> domains = new HashMap<>();
        private Set<Variable> unassignedVariables = new HashSet<>();

        public void assign(Variable variable, Value value) {
            assignments.put(variable, value);
            unassignedVariables.remove(variable);
        }

        public Value getAssignment(Variable variable) {
            return assignments.get(variable);
        }

        public Set<Value> getDomain(Variable variable) {
            return domains.getOrDefault(variable, new HashSet<>());
        }

        public void setDomain(Variable variable, Set<Value> domain) {
            domains.put(variable, new HashSet<>(domain));
        }

        public void removeFromDomain(Variable variable, Value value) {
            domains.computeIfAbsent(variable, k -> new HashSet<>()).remove(value);
        }

        public Set<Variable> getUnassignedVariables() {
            return new HashSet<>(unassignedVariables);
        }

        public void addVariable(Variable variable, Set<Value> domain) {
            unassignedVariables.add(variable);
            setDomain(variable, domain);
        }

        @Override
        public SearchState clone() {
            try {
                SearchState cloned = (SearchState) super.clone();
                cloned.assignments = new HashMap<>(this.assignments);
                cloned.domains = new HashMap<>();
                for (Map.Entry<Variable, Set<Value>> entry : this.domains.entrySet()) {
                    cloned.domains.put(entry.getKey(), new HashSet<>(entry.getValue()));
                }
                cloned.unassignedVariables = new HashSet<>(this.unassignedVariables);
                return cloned;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 变量类
     * 表示排课问题中的一个变量（如某门课程的安排）
     */
    private static class Variable {
        private final int id;
        private final String name;
        private final VariableType type;

        public Variable(int id, String name, VariableType type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public VariableType getType() { return type; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Variable variable)) return false;
            return id == variable.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    /**
     * 值类
     * 表示变量的一个可能取值（如时间段和教室的组合）
     */
    private static class Value {
        private final int timeSlot;
        private final int classroom;
        private final int teacher;

        public Value(int timeSlot, int classroom, int teacher) {
            this.timeSlot = timeSlot;
            this.classroom = classroom;
            this.teacher = teacher;
        }

        public int getTimeSlot() { return timeSlot; }
        public int getClassroom() { return classroom; }
        public int getTeacher() { return teacher; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Value value)) return false;
            return timeSlot == value.timeSlot &&
                   classroom == value.classroom &&
                   teacher == value.teacher;
        }

        @Override
        public int hashCode() {
            return Objects.hash(timeSlot, classroom, teacher);
        }
    }

    /**
     * 变量类型枚举
     */
    private enum VariableType {
        COURSE_ASSIGNMENT, // 课程安排
        TEACHER_ASSIGNMENT, // 教师安排
        CLASSROOM_ASSIGNMENT // 教室安排
    }

    /**
     * 搜索统计信息
     */
    private static class SearchStatistics {
        int nodesVisited = 0;
        int assignmentsTried = 0;
        int backtracks = 0;
        int consistencyCheckFailures = 0;
        int constraintPropagationFailures = 0;
        int solutionsFound = 0;

        void reset() {
            nodesVisited = assignmentsTried = backtracks =
            consistencyCheckFailures = constraintPropagationFailures = solutionsFound = 0;
        }
    }

    /**
     * 约束检查器
     */
    private static class ConstraintChecker {
        boolean isConsistent(Variable variable, Value value, SearchState state) {
            // TODO: 实现具体的约束检查逻辑
            return true;
        }

        boolean hasSupport(Variable variable, Value value, SearchState state) {
            // TODO: 实现支持检查逻辑
            return true;
        }
    }

    /**
     * 变量选择器
     */
    private static class VariableSelector {
        private final BacktrackingConfig config;

        VariableSelector(BacktrackingConfig config) {
            this.config = config;
        }

        Variable selectVariable(SearchState state) {
            Set<Variable> unassigned = state.getUnassignedVariables();
            if (unassigned.isEmpty()) {
                return null;
            }

            return switch (config.getVariableSelectionStrategy()) {
                case FIRST_UNASSIGNED -> unassigned.iterator().next();
                case MINIMUM_REMAINING_VALUES -> selectMRVVariable(unassigned, state);
                case DEGREE_HEURISTIC -> selectDegreeVariable(unassigned, state);
                case MOST_CONSTRAINING -> selectMostConstrainingVariable(unassigned, state);
            };
        }

        private Variable selectMRVVariable(Set<Variable> variables, SearchState state) {
            return variables.stream()
                    .min(Comparator.comparingInt(v -> state.getDomain(v).size()))
                    .orElse(null);
        }

        private Variable selectDegreeVariable(Set<Variable> variables, SearchState state) {
            // TODO: 实现度启发式选择
            return variables.iterator().next();
        }

        private Variable selectMostConstrainingVariable(Set<Variable> variables, SearchState state) {
            // TODO: 实现最约束变量选择
            return variables.iterator().next();
        }
    }

    /**
     * 值选择器
     */
    private static class ValueSelector {
        private final BacktrackingConfig config;

        ValueSelector(BacktrackingConfig config) {
            this.config = config;
        }

        List<Value> selectValues(Variable variable, SearchState state) {
            List<Value> values = new ArrayList<>(state.getDomain(variable));

            return switch (config.getValueSelectionStrategy()) {
                case NATURAL_ORDER -> values;
                case LEAST_CONSTRAINING -> sortByLeastConstraining(values, variable, state);
                case MOST_CONSTRAINING -> sortByMostConstraining(values, variable, state);
                case RANDOM_ORDER -> { Collections.shuffle(values); yield values; }
            };
        }

        private List<Value> sortByLeastConstraining(List<Value> values, Variable variable, SearchState state) {
            // TODO: 实现最少约束值排序
            return values;
        }

        private List<Value> sortByMostConstraining(List<Value> values, Variable variable, SearchState state) {
            // TODO: 实现最多约束值排序
            return values;
        }
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
     * 获取搜索统计信息
     *
     * @return 搜索统计信息
     */
    public SearchStatistics getStatistics() {
        return statistics;
    }
}

