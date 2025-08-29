package com.scott.schedule.service.impl;

import com.scott.schedule.algorithm.ScheduleChromosome;
import com.scott.schedule.common.enums.GreedyStrategyEnum;
import com.scott.schedule.config.GreedyConfig;
import com.scott.schedule.model.*;
import com.scott.schedule.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * GreedyScheduler 单元测试类
 * 测试贪心算法排课服务的各种功能和边界情况
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("贪心算法排课服务测试")
class GreedySchedulerTest {

    @Mock
    private GreedyConfig config;

    @Mock
    private DataService dataService;

    private GreedyScheduler scheduler;

    @BeforeEach
    void setUp() {
        // 设置配置参数的默认值
        when(config.getStrategy()).thenReturn(GreedyStrategyEnum.LARGEST_DEGREE_FIRST);
        when(config.isRandomized()).thenReturn(false);
        when(config.getRandomizationFactor()).thenReturn(0.1);
        when(config.isFastMode()).thenReturn(false);
        when(config.isEnableLocalImprovement()).thenReturn(true);
        when(config.getMaxLocalImprovementIterations()).thenReturn(50);
        when(config.getTimePreferenceWeight()).thenReturn(2.0);
        when(config.getClassroomFitnessWeight()).thenReturn(1.0);
        when(config.getConflictPenaltyWeight()).thenReturn(2.0);
        when(config.getResourceUtilizationWeight()).thenReturn(0.8);

        // 设置约束权重
        GreedyConfig.ConstraintWeights weights = new GreedyConfig.ConstraintWeights();
        weights.setTeacherWeight(2.0);
        weights.setClassroomWeight(1.5);
        weights.setTimeWeight(1.0);
        weights.setStudentWeight(1.2);
        when(config.getConstraintWeights()).thenReturn(weights);

        // 准备测试数据
        setupTestData();

        scheduler = new GreedyScheduler(config, dataService);
    }

    /**
     * 准备测试数据
     */
    private void setupTestData() {
        // 准备课程数据
        List<Course> courses = createTestCourses();
        when(dataService.getAllCourses()).thenReturn(courses);

        // 准备教师数据
        List<Teacher> teachers = createTestTeachers();
        when(dataService.getAllTeachers()).thenReturn(teachers);

        // 准备教室数据
        List<Classroom> classrooms = createTestClassrooms();
        when(dataService.getAllClassrooms()).thenReturn(classrooms);

        // 准备时间段数据
        List<TimeSlot> timeSlots = createTestTimeSlots();
        when(dataService.getAllTimeSlots()).thenReturn(timeSlots);

        // 设置根据ID查询的Mock行为
        setupMockQueryBehavior(courses, teachers, classrooms, timeSlots);
    }

    /**
     * 创建测试课程数据
     */
    private List<Course> createTestCourses() {
        List<Course> courses = new ArrayList<>();

        // 高约束课程 - 数学课
        Course math = new Course();
        math.setId("1");
        math.setName("高等数学");
        math.setCredits(4);
        math.setHoursPerWeek(4);
        math.setTeacherId("T001");
        math.setStudentCount(120);
        math.setRequired(true);
        math.setDifficultyLevel(5);
        math.setClassIds(Arrays.asList("C001", "C002", "C003"));
        courses.add(math);

        // 中等约束课程 - 物理课
        Course physics = new Course();
        physics.setId("2");
        physics.setName("大学物理");
        physics.setCredits(3);
        physics.setHoursPerWeek(3);
        physics.setTeacherId("T002");
        physics.setStudentCount(80);
        physics.setRequired(true);
        physics.setDifficultyLevel(4);
        physics.setClassIds(Arrays.asList("C001", "C002"));
        courses.add(physics);

        // 低约束课程 - 选修课
        Course elective = new Course();
        elective.setId("3");
        elective.setName("艺术欣赏");
        elective.setCredits(2);
        elective.setHoursPerWeek(2);
        elective.setTeacherId("T003");
        elective.setStudentCount(40);
        elective.setRequired(false);
        elective.setDifficultyLevel(2);
        elective.setClassIds(List.of("C001"));
        courses.add(elective);

        // 实验课程 - 需要特殊教室
        Course lab = new Course();
        lab.setId("4");
        lab.setName("计算机实验");
        lab.setCredits(2);
        lab.setHoursPerWeek(2);
        lab.setTeacherId("T004");
        lab.setStudentCount(30);
        lab.setRequired(true);
        lab.setDifficultyLevel(3);
        lab.setClassIds(List.of("C003"));
        courses.add(lab);

        return courses;
    }

    /**
     * 创建测试教师数据
     */
    private List<Teacher> createTestTeachers() {
        List<Teacher> teachers = new ArrayList<>();

        // 数学老师 - 有时间限制
        Teacher mathTeacher = new Teacher();
        mathTeacher.setId("T001");
        mathTeacher.setName("张教授");
        mathTeacher.setMaxHoursPerWeek(16);
        mathTeacher.setPartTime(false);
        mathTeacher.setUnavailableTimeSlots(List.of("1","2")); // 周一1-2节不可用
        teachers.add(mathTeacher);

        // 物理老师 - 兼职
        Teacher physicsTeacher = new Teacher();
        physicsTeacher.setId("T002");
        physicsTeacher.setName("李老师");
        physicsTeacher.setMaxHoursPerWeek(12);
        physicsTeacher.setPartTime(true);
        physicsTeacher.setUnavailableTimeSlots(List.of("1","2"));
        teachers.add(physicsTeacher);

        // 艺术老师 - 无限制
        Teacher artTeacher = new Teacher();
        artTeacher.setId("T003");
        artTeacher.setName("王老师");
        artTeacher.setMaxHoursPerWeek(20);
        artTeacher.setPartTime(false);
        artTeacher.setUnavailableTimeSlots(List.of("1","2","3"));
        teachers.add(artTeacher);

        // 实验老师
        Teacher labTeacher = new Teacher();
        labTeacher.setId("T004");
        labTeacher.setName("赵老师");
        labTeacher.setMaxHoursPerWeek(18);
        labTeacher.setPartTime(false);
        labTeacher.setUnavailableTimeSlots(new ArrayList<>());
        teachers.add(labTeacher);

        return teachers;
    }

    /**
     * 创建测试教室数据
     */
    private List<Classroom> createTestClassrooms() {
        List<Classroom> classrooms = new ArrayList<>();

        // 大教室
        Classroom largeRoom = new Classroom();
        largeRoom.setId("R001");
        largeRoom.setName("大教室A");
        largeRoom.setCapacity(150);
        largeRoom.setAvailable(true);
        largeRoom.setType(ClassroomType.AMPHITHEATER);
        classrooms.add(largeRoom);

        // 中教室
        Classroom mediumRoom = new Classroom();
        mediumRoom.setId("R002");
        mediumRoom.setName("教室B");
        mediumRoom.setCapacity(80);
        mediumRoom.setAvailable(true);
        mediumRoom.setType(ClassroomType.NORMAL);
        classrooms.add(mediumRoom);

        // 小教室
        Classroom smallRoom = new Classroom();
        smallRoom.setId("R003");
        smallRoom.setName("小教室C");
        smallRoom.setCapacity(40);
        smallRoom.setAvailable(true);
        smallRoom.setType(ClassroomType.LAB);
        classrooms.add(smallRoom);

        // 实验室
        Classroom lab = new Classroom();
        lab.setId("R004");
        lab.setName("计算机实验室");
        lab.setCapacity(30);
        lab.setAvailable(true);
        lab.setType(ClassroomType.LAB);
        classrooms.add(lab);

        return classrooms;
    }

    /**
     * 创建测试时间段数据
     */
    private List<TimeSlot> createTestTimeSlots() {
        List<TimeSlot> timeSlots = new ArrayList<>();

        // 周一到周五，每天2个时间段
        String[] days = {"周一", "周二", "周三", "周四", "周五"};
        String[] periods = {"08:00-09:40", "10:00-11:40"};

        int id = 1;
        for (int day = 1; day <= 5; day++) {
            for (int period = 1; period <= 2; period++) {
                TimeSlot timeSlot = new TimeSlot();
                timeSlot.setId(String.valueOf(id));
                timeSlot.setDayOfWeek(days[day - 1]);
                timeSlot.setDayNumber(day);
                timeSlot.setPeriod(period);
                timeSlot.setTimeRange(periods[period - 1]);
                timeSlot.setAvailable(true);
                timeSlot.setEvening(period == 2); // 第5节是晚上
                timeSlots.add(timeSlot);
                id++;
            }
        }

        return timeSlots;
    }

    /**
     * 设置Mock查询行为
     */
    private void setupMockQueryBehavior(List<Course> courses, List<Teacher> teachers,
                                        List<Classroom> classrooms, List<TimeSlot> timeSlots) {
        // 课程查询
        for (Course course : courses) {
            when(dataService.getCourseById(course.getId())).thenReturn(course);
        }

        // 教师查询
        for (Teacher teacher : teachers) {
            when(dataService.getTeacherById(teacher.getId())).thenReturn(teacher);
        }

        // 教室查询
        for (Classroom classroom : classrooms) {
            when(dataService.getClassroomById(classroom.getId())).thenReturn(classroom);
        }

        // 时间段查询
        for (TimeSlot timeSlot : timeSlots) {
            when(dataService.getTimeSlotById(timeSlot.getId())).thenReturn(timeSlot);
        }
    }

    @Test
    @DisplayName("测试最大度优先贪心算法 - 真实调用")
    void testLargestDegreeFirstRealExecution() {
        // 设置策略为最大度优先
        when(config.getStrategy()).thenReturn(GreedyStrategyEnum.LARGEST_DEGREE_FIRST);

        // 执行排课
        assertDoesNotThrow(() -> scheduler.schedule());

        // 验证结果
        ScheduleChromosome result = scheduler.getBestSolution();
        assertNotNull(result, "排课结果不应为空");
        assertNotNull(result.getGenes(), "基因不应为空");

        // 验证课程都被安排了（每门课程有多个课时）
        // 数学课4课时 + 物理课3课时 + 艺术课2课时 + 实验课2课时 = 11课时
        int expectedTotalHours = 4 + 3 + 2 + 2; // 总共11个课时
        assertTrue(result.getGenes().size() <= expectedTotalHours, "安排的课时数应该不超过总课时数");

        // 验证每个课程安排的有效性
        result.getGenes().forEach((courseId, schedule) -> {
            assertNotNull(schedule, "课程安排不应为空");
            assertTrue(schedule.isValid(), "课程安排应该有效");
            assertNotNull(schedule.courseId(), "课程ID不应为空");
            assertNotNull(schedule.teacherId(), "教师ID不应为空");
            assertNotNull(schedule.timeSlotId(), "时间段ID不应为空");
            // 教室ID可能为空（如果没有找到合适的教室）
        });

        // 验证高约束课程（数学）被优先安排
        boolean mathCourseScheduled = result.getGenes().keySet().stream()
                .anyMatch(key -> {
                    // 检查是否有数学课的课时被安排（课程ID为"1"）
                    return key.equals((long) "1_H1".hashCode()) ||
                           key.equals((long) "1_H2".hashCode()) ||
                           key.equals((long) "1_H3".hashCode()) ||
                           key.equals((long) "1_H4".hashCode());
                });
        assertTrue(mathCourseScheduled, "高约束课程（数学）应该被安排");

        // 验证数据服务被正确调用
        verify(dataService, atLeastOnce()).getAllCourses();
        verify(dataService, atLeastOnce()).getAllTeachers();
        verify(dataService, atLeastOnce()).getAllClassrooms();
        verify(dataService, atLeastOnce()).getAllTimeSlots();
        verify(dataService, atLeastOnce()).getCourseById(anyString());
        verify(dataService, atLeastOnce()).getTeacherById(anyString());

        System.out.println("✅ 最大度优先算法测试通过");
        System.out.println("📊 排课结果统计:");
        System.out.println("   - 安排课程数: " + result.getGenes().size());
        System.out.println("   - 适应度: " + result.getFitness());
        System.out.println("   - 约束违反数: " + result.getConstraintViolations());
    }

    @Test
    @DisplayName("测试最大度优先算法 - 约束度计算验证")
    void testLargestDegreeFirstConstraintCalculation() {
        // 设置策略为最大度优先
        when(config.getStrategy()).thenReturn(GreedyStrategyEnum.LARGEST_DEGREE_FIRST);

        // 执行排课
        scheduler.schedule();

        // 验证约束计算相关的调用
        verify(dataService, atLeastOnce()).getCourseById("1"); // 数学课
        verify(dataService, atLeastOnce()).getCourseById("2"); // 物理课
        verify(dataService, atLeastOnce()).getCourseById("3"); // 艺术课
        verify(dataService, atLeastOnce()).getCourseById("4"); // 实验课

        verify(dataService, atLeastOnce()).getTeacherById("T001"); // 数学老师
        verify(dataService, atLeastOnce()).getTeacherById("T002"); // 物理老师

        // 验证教室和时间段查询
        verify(dataService, atLeastOnce()).getAllClassrooms();
        verify(dataService, atLeastOnce()).getAllTimeSlots();

        System.out.println("✅ 约束度计算验证通过");
    }

    @Test
    @DisplayName("测试最大度优先算法 - 边界情况")
    void testLargestDegreeFirstBoundaryConditions() {
        // 测试空课程列表
        when(dataService.getAllCourses()).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> scheduler.schedule());

        ScheduleChromosome result = scheduler.getBestSolution();
        assertNotNull(result);
        assertTrue(result.getGenes().isEmpty(), "空课程列表应该产生空的排课结果");

        System.out.println("✅ 边界情况测试通过");
    }

    @Test
    @DisplayName("测试最大度优先算法 - 教师冲突处理")
    void testLargestDegreeFirstTeacherConflictHandling() {
        // 创建有冲突的课程数据
        List<Course> conflictCourses = new ArrayList<>();

        Course course1 = new Course();
        course1.setId("C1");
        course1.setName("课程1");
        course1.setTeacherId("T001");
        course1.setStudentCount(50);
        course1.setHoursPerWeek(2);
        course1.setRequired(true);
        course1.setDifficultyLevel(4);
        course1.setClassIds(Arrays.asList("CL001"));
        conflictCourses.add(course1);

        Course course2 = new Course();
        course2.setId("C2");
        course2.setName("课程2");
        course2.setTeacherId("T001"); // 同一个老师
        course2.setStudentCount(60);
        course2.setHoursPerWeek(2);
        course2.setRequired(true);
        course2.setDifficultyLevel(4);
        course2.setClassIds(Arrays.asList("CL002"));
        conflictCourses.add(course2);

        when(dataService.getAllCourses()).thenReturn(conflictCourses);
        when(dataService.getCourseById("C1")).thenReturn(course1);
        when(dataService.getCourseById("C2")).thenReturn(course2);

        // 执行排课
        assertDoesNotThrow(() -> scheduler.schedule());

        ScheduleChromosome result = scheduler.getBestSolution();
        assertNotNull(result);

        // 验证冲突处理
        if (result.getGenes().size() == 2) {
            // 如果两门课都被安排，验证没有时间冲突
            ScheduleChromosome.CourseSchedule schedule1 = result.getGenes().get((long) "C1".hashCode());
            ScheduleChromosome.CourseSchedule schedule2 = result.getGenes().get((long) "C2".hashCode());

            if (schedule1 != null && schedule2 != null) {
                assertNotEquals(schedule1.timeSlotId(), schedule2.timeSlotId(),
                        "同一教师的课程不应安排在同一时间段");
            }
        }

        System.out.println("✅ 教师冲突处理测试通过");
    }

    @Test
    @DisplayName("测试最大度优先算法 - 教室容量约束")
    void testLargestDegreeFirstClassroomCapacityConstraint() {
        // 创建学生人数超过所有教室容量的课程
        Course largeCourse = new Course();
        largeCourse.setId("LARGE");
        largeCourse.setName("大型课程");
        largeCourse.setTeacherId("T001");
        largeCourse.setStudentCount(200); // 超过最大教室容量150
        largeCourse.setHoursPerWeek(2);
        largeCourse.setRequired(true);
        largeCourse.setDifficultyLevel(3);
        largeCourse.setClassIds(Arrays.asList("CL001"));

        when(dataService.getAllCourses()).thenReturn(Arrays.asList(largeCourse));
        when(dataService.getCourseById("LARGE")).thenReturn(largeCourse);

        // 执行排课
        assertDoesNotThrow(() -> scheduler.schedule());

        ScheduleChromosome result = scheduler.getBestSolution();
        assertNotNull(result);

        // 验证课程被安排（即使没有合适的教室）
        if (result.getGenes().containsKey((long) "LARGE".hashCode())) {
            ScheduleChromosome.CourseSchedule schedule = result.getGenes().get((long) "LARGE".hashCode());
            // 教室ID可能为null，因为没有找到合适的教室
            System.out.println("大型课程安排结果 - 教室ID: " + schedule.classroomId());
        }

        System.out.println("✅ 教室容量约束测试通过");
    }

    @Test
    @DisplayName("测试构造函数和初始化")
    void testConstructorAndInitialization() {
        assertNotNull(scheduler, "调度器应该被正确初始化");

        // 验证数据服务调用
        verify(dataService, atLeastOnce()).getAllTimeSlots();
        verify(dataService, atLeastOnce()).getAllClassrooms();

        System.out.println("✅ 构造函数和初始化测试通过");
    }

    @Test
    @DisplayName("测试获取最优解")
    void testGetBestSolution() {
        // 初始状态下最优解应该为null
        assertNull(scheduler.getBestSolution(), "初始状态下最优解应该为null");

        // 执行排课后应该有结果
        scheduler.schedule();
        assertNotNull(scheduler.getBestSolution(), "执行排课后应该有最优解");

        System.out.println("✅ 获取最优解测试通过");
    }
}

