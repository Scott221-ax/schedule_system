package com.scott.schedule.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.scott.schedule.model.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON数据管理器
 * 负责从本地JSON文件读取和写入排课相关数据
 * 用于开发阶段的测试，后续可替换为数据库操作
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Component
public class JsonDataManager {

    /**
     * JSON对象映射器
     */
    private final ObjectMapper objectMapper;

    /**
     * 数据文件根目录
     */
    private final String dataDirectory = "data";

    /**
     * 构造函数
     */
    public JsonDataManager() {
        this.objectMapper = new ObjectMapper();
        initializeDataDirectory();
    }

    /**
     * 初始化数据目录
     */
    private void initializeDataDirectory() {
        try {
            Path dataPath = Paths.get(dataDirectory);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                System.out.println("📁 创建数据目录: " + dataPath.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("无法创建数据目录", e);
        }
    }

    // ==================== 课程数据操作 ====================

    /**
     * 读取所有课程数据
     *
     * @return 课程列表
     */
    public List<Course> loadCourses() {
        return loadDataFromFile("courses.json", Course.class);
    }

    /**
     * 保存课程数据
     *
     * @param courses 课程列表
     */
    public void saveCourses(List<Course> courses) {
        saveDataToFile("courses.json", courses);
    }

    // ==================== 教师数据操作 ====================

    /**
     * 读取所有教师数据
     *
     * @return 教师列表
     */
    public List<Teacher> loadTeachers() {
        return loadDataFromFile("teachers.json", Teacher.class);
    }

    /**
     * 保存教师数据
     *
     * @param teachers 教师列表
     */
    public void saveTeachers(List<Teacher> teachers) {
        saveDataToFile("teachers.json", teachers);
    }

    // ==================== 教室数据操作 ====================

    /**
     * 读取所有教室数据
     *
     * @return 教室列表
     */
    public List<Classroom> loadClassrooms() {
        return loadDataFromFile("classrooms.json", Classroom.class);
    }

    /**
     * 保存教室数据
     *
     * @param classrooms 教室列表
     */
    public void saveClassrooms(List<Classroom> classrooms) {
        saveDataToFile("classrooms.json", classrooms);
    }

    // ==================== 时间段数据操作 ====================

    /**
     * 读取所有时间段数据
     *
     * @return 时间段列表
     */
    public List<TimeSlot> loadTimeSlots() {
        return loadDataFromFile("timeslots.json", TimeSlot.class);
    }

    /**
     * 保存时间段数据
     *
     * @param timeSlots 时间段列表
     */
    public void saveTimeSlots(List<TimeSlot> timeSlots) {
        saveDataToFile("timeslots.json", timeSlots);
    }

    // ==================== 学生数据操作 ====================

    /**
     * 读取所有学生数据
     *
     * @return 学生列表
     */
    public List<Student> loadStudents() {
        return loadDataFromFile("students.json", Student.class);
    }

    /**
     * 保存学生数据
     *
     * @param students 学生列表
     */
    public void saveStudents(List<Student> students) {
        saveDataToFile("students.json", students);
    }

    // ==================== 约束数据操作 ====================

    /**
     * 读取约束配置数据
     *
     * @return 约束配置
     */
    public ScheduleConstraints loadConstraints() {
        List<ScheduleConstraints> constraints = loadDataFromFile("constraints.json", ScheduleConstraints.class);
        return constraints.isEmpty() ? new ScheduleConstraints() : constraints.get(0);
    }

    /**
     * 保存约束配置数据
     *
     * @param constraints 约束配置
     */
    public void saveConstraints(ScheduleConstraints constraints) {
        saveDataToFile("constraints.json", List.of(constraints));
    }

    // ==================== 排课结果数据操作 ====================

    /**
     * 读取排课结果数据
     *
     * @return 排课结果列表
     */
    public List<ScheduleResult> loadScheduleResults() {
        return loadDataFromFile("schedule_results.json", ScheduleResult.class);
    }

    /**
     * 保存排课结果数据
     *
     * @param results 排课结果列表
     */
    public void saveScheduleResults(List<ScheduleResult> results) {
        saveDataToFile("schedule_results.json", results);
    }

    /**
     * 保存单个排课结果
     *
     * @param result 排课结果
     */
    public void saveScheduleResult(ScheduleResult result) {
        List<ScheduleResult> results = loadScheduleResults();
        results.add(result);
        saveScheduleResults(results);
    }

    // ==================== 通用数据操作方法 ====================

    /**
     * 从文件加载数据
     *
     * @param filename 文件名
     * @param clazz 数据类型
     * @param <T> 泛型类型
     * @return 数据列表
     */
    private <T> List<T> loadDataFromFile(String filename, Class<T> clazz) {
        File file = new File(dataDirectory, filename);

        if (!file.exists()) {
            System.out.printf("⚠️ 数据文件不存在: %s，返回空列表%n", filename);
            return new ArrayList<>();
        }

        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return objectMapper.readValue(file,
                typeFactory.constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            System.err.printf("❌ 读取数据文件失败: %s - %s%n", filename, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 保存数据到文件
     *
     * @param filename 文件名
     * @param data 数据列表
     * @param <T> 泛型类型
     */
    private <T> void saveDataToFile(String filename, List<T> data) {
        File file = new File(dataDirectory, filename);

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
            System.out.printf("✅ 数据已保存到文件: %s (%d条记录)%n", filename, data.size());
        } catch (IOException e) {
            System.err.printf("❌ 保存数据文件失败: %s - %s%n", filename, e.getMessage());
            throw new RuntimeException("保存数据失败", e);
        }
    }

    // ==================== 数据初始化和验证 ====================

    /**
     * 初始化测试数据
     * 创建一套完整的测试数据用于开发和测试
     */
    public void initializeTestData() {
        System.out.println("🔄 开始初始化测试数据...");

        // 初始化教室数据
        if (loadClassrooms().isEmpty()) {
            saveClassrooms(createTestClassrooms());
        }

        // 初始化时间段数据
        if (loadTimeSlots().isEmpty()) {
            saveTimeSlots(createTestTimeSlots());
        }

        // 初始化教师数据
        if (loadTeachers().isEmpty()) {
            saveTeachers(createTestTeachers());
        }

        // 初始化学生数据
        if (loadStudents().isEmpty()) {
            saveStudents(createTestStudents());
        }

        // 初始化课程数据
        if (loadCourses().isEmpty()) {
            saveCourses(createTestCourses());
        }

        // 初始化约束配置
        if (loadConstraints().getHardConstraints().isEmpty()) {
            saveConstraints(createTestConstraints());
        }

        System.out.println("✅ 测试数据初始化完成");
    }

    /**
     * 验证数据完整性
     *
     * @return 验证结果
     */
    public DataValidationResult validateData() {
        DataValidationResult result = new DataValidationResult();

        // 验证各类数据是否存在
        List<Course> courses = loadCourses();
        List<Teacher> teachers = loadTeachers();
        List<Classroom> classrooms = loadClassrooms();
        List<TimeSlot> timeSlots = loadTimeSlots();
        List<Student> students = loadStudents();

        result.setCourseCount(courses.size());
        result.setTeacherCount(teachers.size());
        result.setClassroomCount(classrooms.size());
        result.setTimeSlotCount(timeSlots.size());
        result.setStudentCount(students.size());

        // 检查数据完整性
        if (courses.isEmpty()) result.addError("课程数据为空");
        if (teachers.isEmpty()) result.addError("教师数据为空");
        if (classrooms.isEmpty()) result.addError("教室数据为空");
        if (timeSlots.isEmpty()) result.addError("时间段数据为空");

        // 检查数据关联性
        validateDataRelations(courses, teachers, classrooms, result);

        return result;
    }

    /**
     * 验证数据关联性
     */
    private void validateDataRelations(List<Course> courses, List<Teacher> teachers,
                                     List<Classroom> classrooms, DataValidationResult result) {
        // 检查课程是否有对应的教师
        for (Course course : courses) {
            if (course.getTeacherId() != null) {
                boolean teacherExists = teachers.stream()
                    .anyMatch(t -> t.getId().equals(course.getTeacherId()));
                if (!teacherExists) {
                    result.addWarning("课程 " + course.getName() + " 的教师ID不存在: " + course.getTeacherId());
                }
            }
        }

        System.out.printf("📊 数据验证完成: %s%n", result.isValid() ? "通过" : "存在问题");
    }

    // ==================== 测试数据创建方法 ====================

    private List<Classroom> createTestClassrooms() {
        List<Classroom> classrooms = new ArrayList<>();

        // 普通教室
        for (int i = 1; i <= 20; i++) {
            Classroom classroom = new Classroom();
            classroom.setId("CR" + String.format("%03d", i));
            classroom.setName("教室" + i);
            classroom.setCapacity(50 + (i % 3) * 20); // 容量50-90
            classroom.setType(ClassroomType.NORMAL);
            classroom.setBuilding("教学楼A");
            classroom.setFloor(i % 5 + 1);
            classroom.setRoomNumber(String.valueOf(i));
            classroom.setAvailable(true);
            classroom.setEquipment(List.of("投影仪", "黑板"));
            classroom.setMaintenanceTimeSlots(new ArrayList<>());
            classroom.setNotes("普通教室");
            classrooms.add(classroom);
        }

        // 实验室
        for (int i = 1; i <= 5; i++) {
            Classroom classroom = new Classroom();
            classroom.setId("LAB" + String.format("%02d", i));
            classroom.setName("实验室" + i);
            classroom.setCapacity(30);
            classroom.setType(ClassroomType.LAB);
            classroom.setBuilding("实验楼B");
            classroom.setFloor(i % 3 + 1);
            classroom.setRoomNumber("L" + i);
            classroom.setAvailable(true);
            classroom.setEquipment(List.of("实验设备", "计算机"));
            classroom.setMaintenanceTimeSlots(new ArrayList<>());
            classroom.setNotes("实验室");
            classrooms.add(classroom);
        }

        // 多媒体教室
        for (int i = 1; i <= 3; i++) {
            Classroom classroom = new Classroom();
            classroom.setId("MM" + String.format("%02d", i));
            classroom.setName("多媒体教室" + i);
            classroom.setCapacity(100);
            classroom.setType(ClassroomType.MULTIMEDIA);
            classroom.setBuilding("教学楼C");
            classroom.setFloor(i + 1);
            classroom.setRoomNumber("M" + i);
            classroom.setAvailable(true);
            classroom.setEquipment(List.of("投影仪", "音响", "麦克风", "电脑"));
            classroom.setMaintenanceTimeSlots(new ArrayList<>());
            classroom.setNotes("多媒体教室");
            classrooms.add(classroom);
        }

        return classrooms;
    }

    private List<TimeSlot> createTestTimeSlots() {
        List<TimeSlot> timeSlots = new ArrayList<>();
        String[] days = {"周一", "周二", "周三", "周四", "周五"};
        String[] periods = {"08:00-09:40", "10:00-11:40", "14:00-15:40", "16:00-17:40", "19:00-20:40"};

        int id = 1;
        for (int day = 0; day < days.length; day++) {
            for (int period = 0; period < periods.length; period++) {
                TimeSlot timeSlot = new TimeSlot();
                timeSlot.setId("TS" + String.format("%03d", id++));
                timeSlot.setDayOfWeek(days[day]);
                timeSlot.setPeriod(period + 1);
                timeSlot.setTimeRange(periods[period]);
                timeSlot.setDayNumber(day + 1);
                timeSlot.setAvailable(true);
                timeSlot.setEvening(period == 4); // 第5节是晚上
                timeSlot.setNotes("正常时间段");

                // 计算开始和结束时间（分钟数）
                String[] times = periods[period].split("-");
                timeSlot.setStartMinutes(parseTimeToMinutes(times[0]));
                timeSlot.setEndMinutes(parseTimeToMinutes(times[1]));

                timeSlots.add(timeSlot);
            }
        }

        return timeSlots;
    }

    /**
     * 将时间字符串转换为分钟数
     *
     * @param time 时间字符串，格式为 "HH:mm"
     * @return 从00:00开始计算的分钟数
     */
    private int parseTimeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    private List<Teacher> createTestTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        String[] names = {"张教授", "李老师", "王博士", "刘副教授", "陈老师", "杨教授", "赵老师", "孙博士"};
        String[] departments = {"计算机学院", "数学学院", "物理学院", "化学学院"};

        for (int i = 0; i < names.length; i++) {
            Teacher teacher = new Teacher();
            teacher.setId("T" + String.format("%03d", i + 1));
            teacher.setName(names[i]);
            teacher.setDepartment(departments[i % departments.length]);
            teacher.setTitle("副教授");
            teacher.setEmail("teacher" + (i + 1) + "@university.edu");
            teacher.setPhone("138" + String.format("%08d", i + 1));
            teacher.setMaxHoursPerWeek(16);
            teacher.setPreferredTimeSlots(new ArrayList<>());
            teacher.setUnavailableTimeSlots(new ArrayList<>());
            teacher.setSpecializations(new ArrayList<>());
            teacher.setPartTime(false);
            teachers.add(teacher);
        }

        return teachers;
    }

    private List<Student> createTestStudents() {
        List<Student> students = new ArrayList<>();

        // 创建几个班级的学生
        String[] classes = {"计算机2021-1班", "计算机2021-2班", "数学2021-1班"};

        int studentId = 1;
        for (String className : classes) {
            for (int i = 1; i <= 30; i++) {
                Student student = new Student();
                student.setId("S" + String.format("%06d", studentId));
                student.setName("学生" + studentId);
                student.setClassName(className);
                student.setGrade("2021");
                student.setEmail("student" + studentId + "@university.edu");
                student.setMajor("计算机科学与技术");
                student.setStudentNumber("2021" + String.format("%06d", studentId));
                student.setElectiveCourseIds(new ArrayList<>());
                student.setUnavailableTimeSlots(new ArrayList<>());
                students.add(student);
                studentId++;
            }
        }

        return students;
    }

    private List<Course> createTestCourses() {
        List<Course> courses = new ArrayList<>();
        List<Teacher> teachers = loadTeachers();

        String[] courseNames = {
            "高等数学", "线性代数", "概率论", "数据结构", "算法设计",
            "操作系统", "计算机网络", "数据库原理", "软件工程", "人工智能"
        };

        for (int i = 0; i < courseNames.length; i++) {
            Course course = new Course();
            course.setId("C" + String.format("%03d", i + 1));
            course.setName(courseNames[i]);
            course.setCredits(4); // 学分
            course.setHoursPerWeek(2); // 每周课时
            course.setTeacherId(teachers.get(i % teachers.size()).getId());
            course.setStudentCount(45); // 学生人数
            course.setCourseType(CourseType.THEORY);
            course.setRequired(true);
            course.setDifficultyLevel(3);
            course.setClassIds(List.of("CL001", "CL002"));
            course.setPrerequisiteIds(new ArrayList<>());
            courses.add(course);
        }

        return courses;
    }

    private ScheduleConstraints createTestConstraints() {
        ScheduleConstraints constraints = new ScheduleConstraints();

        // 硬约束
        constraints.addHardConstraint("教师时间冲突", "同一教师不能在同一时间段上多门课");
        constraints.addHardConstraint("教室容量限制", "教室容量必须满足课程学生人数");
        constraints.addHardConstraint("教室时间冲突", "同一教室不能在同一时间段安排多门课");

        // 软约束
        constraints.addSoftConstraint("教师偏好时间", "尽量安排在教师偏好的时间段", 0.8);
        constraints.addSoftConstraint("课程分散性", "同一门课程的不同节次尽量分散", 0.6);
        constraints.addSoftConstraint("教室利用率", "提高教室利用率", 0.4);

        return constraints;
    }

    // ==================== 数据统计方法 ====================

    /**
     * 获取数据统计信息
     *
     * @return 数据统计
     */
    public DataStatistics getDataStatistics() {
        DataStatistics stats = new DataStatistics();

        stats.setCourseCount(loadCourses().size());
        stats.setTeacherCount(loadTeachers().size());
        stats.setClassroomCount(loadClassrooms().size());
        stats.setTimeSlotCount(loadTimeSlots().size());
        stats.setStudentCount(loadStudents().size());

        return stats;
    }

    /**
     * 打印数据统计信息
     */
    public void printDataStatistics() {
        DataStatistics stats = getDataStatistics();

        System.out.printf(
                """
                        📊 数据统计信息:
                        ├── 课程数量: %d
                        ├── 教师数量: %d
                        ├── 教室数量: %d
                        ├── 时间段数量: %d
                        └── 学生数量: %d
                        %n""",
                stats.getCourseCount(),
                stats.getTeacherCount(),
                stats.getClassroomCount(),
                stats.getTimeSlotCount(),
                stats.getStudentCount()
        );
    }
}

