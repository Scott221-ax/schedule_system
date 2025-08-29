package com.scott.schedule.service;

import com.scott.schedule.config.DataSourceConfig;
import com.scott.schedule.data.DataSource;
import com.scott.schedule.data.DataSourceFactory;
import com.scott.schedule.data.DataStatistics;
import com.scott.schedule.data.DataValidationResult;
import com.scott.schedule.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据服务类
 * 作为业务层的统一数据访问接口
 * 使用工厂模式管理数据源
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Service
public class DataService {

    private final DataSourceConfig dataSourceConfig;
    private final DataSourceFactory dataSourceFactory;

    private DataSource currentDataSource;

    @Autowired
    public DataService(DataSourceConfig dataSourceConfig, DataSourceFactory dataSourceFactory) {
        this.dataSourceConfig = dataSourceConfig;
        this.dataSourceFactory = dataSourceFactory;
    }

    /**
     * 初始化数据服务
     */
    @PostConstruct
    public void initialize() {
        // 验证配置
        dataSourceConfig.validate();

        // 创建数据源
        currentDataSource = dataSourceFactory.createDataSource();

        // 打印配置信息
        dataSourceConfig.printConfig();
        System.out.printf("✅ 数据服务初始化完成，使用数据源: %s%n", currentDataSource.getDataSourceType());

        // 初始化测试数据（如果配置了）
        if (dataSourceConfig.isInitializeTestData()) {
            currentDataSource.initializeTestData();
        }

        // 验证数据完整性（如果配置了）
        if (dataSourceConfig.isValidateOnStartup()) {
            DataValidationResult validationResult = currentDataSource.validateData();
            if (!validationResult.isValid()) {
                System.err.println("⚠️ 数据验证失败:");
                validationResult.getErrors().forEach(error -> System.err.println("  - " + error));
            }
        }
    }

    // ==================== 数据源管理方法 ====================

    /**
     * 切换数据源
     *
     * @param dataSourceType 数据源类型
     */
    public void switchDataSource(String dataSourceType) {
        if (!dataSourceFactory.isSupported(dataSourceType)) {
            throw new IllegalArgumentException("不支持的数据源类型: " + dataSourceType);
        }

        String oldType = currentDataSource.getDataSourceType();
        System.out.printf("🔄 切换数据源: %s -> %s%n", oldType, dataSourceType.toUpperCase());

        // 创建新的数据源
        DataSource newDataSource = dataSourceFactory.createDataSource(dataSourceType);

        // 更新配置
        dataSourceConfig.setType(dataSourceType);

        // 切换数据源
        currentDataSource = newDataSource;

        System.out.printf("✅ 数据源切换完成: %s%n", currentDataSource.getDataSourceType());
    }

    /**
     * 数据迁移
     * 从当前数据源迁移数据到目标数据源
     *
     * @param targetDataSourceType 目标数据源类型
     */
    public void migrateData(String targetDataSourceType) {
        if (targetDataSourceType.equalsIgnoreCase(currentDataSource.getDataSourceType())) {
            System.out.println("⚠️ 目标数据源与当前数据源相同，无需迁移");
            return;
        }

        if (!dataSourceFactory.isSupported(targetDataSourceType)) {
            throw new IllegalArgumentException("不支持的数据源类型: " + targetDataSourceType);
        }

        System.out.printf("🔄 开始数据迁移: %s -> %s%n",
                currentDataSource.getDataSourceType(), targetDataSourceType.toUpperCase());

        // 备份当前数据源
        DataSource sourceDataSource = currentDataSource;

        // 创建目标数据源
        DataSource targetDataSource = dataSourceFactory.createDataSource(targetDataSourceType);

        try {
            // 执行数据迁移
            migrateAllData(sourceDataSource, targetDataSource);

            // 切换到目标数据源
            currentDataSource = targetDataSource;
            dataSourceConfig.setType(targetDataSourceType);

            System.out.println("✅ 数据迁移完成");
        } catch (Exception e) {
            System.err.printf("❌ 数据迁移失败: %s%n", e.getMessage());
            throw new RuntimeException("数据迁移失败", e);
        }
    }

    /**
     * 迁移所有数据
     */
    private void migrateAllData(DataSource source, DataSource target) {
        // 迁移课程
        List<Course> courses = source.getAllCourses();
        if (!courses.isEmpty()) {
            target.saveCourses(courses);
            System.out.printf("📚 迁移课程数据: %d条%n", courses.size());
        }

        // 迁移教师
        List<Teacher> teachers = source.getAllTeachers();
        if (!teachers.isEmpty()) {
            target.saveTeachers(teachers);
            System.out.printf("👨‍🏫 迁移教师数据: %d条%n", teachers.size());
        }

        // 迁移教室
        List<Classroom> classrooms = source.getAllClassrooms();
        if (!classrooms.isEmpty()) {
            target.saveClassrooms(classrooms);
            System.out.printf("🏫 迁移教室数据: %d条%n", classrooms.size());
        }

        // 迁移时间段
        List<TimeSlot> timeSlots = source.getAllTimeSlots();
        if (!timeSlots.isEmpty()) {
            target.saveTimeSlots(timeSlots);
            System.out.printf("⏰ 迁移时间段数据: %d条%n", timeSlots.size());
        }

        // 迁移学生
        List<Student> students = source.getAllStudents();
        if (!students.isEmpty()) {
            target.saveStudents(students);
            System.out.printf("👨‍🎓 迁移学生数据: %d条%n", students.size());
        }

        // 迁移约束配置
        ScheduleConstraints constraints = source.getConstraints();
        if (constraints != null && !constraints.getHardConstraints().isEmpty()) {
            target.saveConstraints(constraints);
            System.out.println("⚖️ 迁移约束配置");
        }

        // 迁移排课结果
        List<ScheduleResult> results = source.getAllScheduleResults();
        if (!results.isEmpty()) {
            target.saveScheduleResults(results);
            System.out.printf("📊 迁移排课结果: %d条%n", results.size());
        }
    }

    /**
     * 获取当前数据源类型
     *
     * @return 数据源类型
     */
    public String getCurrentDataSourceType() {
        return currentDataSource.getDataSourceType();
    }

    /**
     * 获取支持的数据源类型
     *
     * @return 支持的数据源类型数组
     */
    public String[] getSupportedDataSourceTypes() {
        return dataSourceFactory.getSupportedTypes();
    }

    // ==================== 委托给当前数据源的方法 ====================

    // 课程相关方法
    public List<Course> getAllCourses() {
        return currentDataSource.getAllCourses();
    }

    public Course getCourseById(String courseId) {
        return currentDataSource.getCourseById(courseId);
    }

    public Course saveCourse(Course course) {
        return currentDataSource.saveCourse(course);
    }

    public List<Course> saveCourses(List<Course> courses) {
        return currentDataSource.saveCourses(courses);
    }

    public boolean deleteCourse(String courseId) {
        return currentDataSource.deleteCourse(courseId);
    }

    // 教师相关方法
    public List<Teacher> getAllTeachers() {
        return currentDataSource.getAllTeachers();
    }

    public Teacher getTeacherById(String teacherId) {
        return currentDataSource.getTeacherById(teacherId);
    }

    public Teacher saveTeacher(Teacher teacher) {
        return currentDataSource.saveTeacher(teacher);
    }

    public List<Teacher> saveTeachers(List<Teacher> teachers) {
        return currentDataSource.saveTeachers(teachers);
    }

    public boolean deleteTeacher(String teacherId) {
        return currentDataSource.deleteTeacher(teacherId);
    }

    // 教室相关方法
    public List<Classroom> getAllClassrooms() {
        return currentDataSource.getAllClassrooms();
    }

    public Classroom getClassroomById(String classroomId) {
        return currentDataSource.getClassroomById(classroomId);
    }

    public Classroom saveClassroom(Classroom classroom) {
        return currentDataSource.saveClassroom(classroom);
    }

    public List<Classroom> saveClassrooms(List<Classroom> classrooms) {
        return currentDataSource.saveClassrooms(classrooms);
    }

    public boolean deleteClassroom(String classroomId) {
        return currentDataSource.deleteClassroom(classroomId);
    }

    // 时间段相关方法
    public List<TimeSlot> getAllTimeSlots() {
        return currentDataSource.getAllTimeSlots();
    }

    public TimeSlot getTimeSlotById(String timeSlotId) {
        return currentDataSource.getTimeSlotById(timeSlotId);
    }

    public TimeSlot saveTimeSlot(TimeSlot timeSlot) {
        return currentDataSource.saveTimeSlot(timeSlot);
    }

    public List<TimeSlot> saveTimeSlots(List<TimeSlot> timeSlots) {
        return currentDataSource.saveTimeSlots(timeSlots);
    }

    public boolean deleteTimeSlot(String timeSlotId) {
        return currentDataSource.deleteTimeSlot(timeSlotId);
    }

    // 学生相关方法
    public List<Student> getAllStudents() {
        return currentDataSource.getAllStudents();
    }

    public Student getStudentById(String studentId) {
        return currentDataSource.getStudentById(studentId);
    }

    public Student saveStudent(Student student) {
        return currentDataSource.saveStudent(student);
    }

    public List<Student> saveStudents(List<Student> students) {
        return currentDataSource.saveStudents(students);
    }

    public boolean deleteStudent(String studentId) {
        return currentDataSource.deleteStudent(studentId);
    }

    // 约束相关方法
    public ScheduleConstraints getConstraints() {
        return currentDataSource.getConstraints();
    }

    public ScheduleConstraints saveConstraints(ScheduleConstraints constraints) {
        return currentDataSource.saveConstraints(constraints);
    }

    // 排课结果相关方法
    public List<ScheduleResult> getAllScheduleResults() {
        return currentDataSource.getAllScheduleResults();
    }

    public ScheduleResult getScheduleResultById(String resultId) {
        return currentDataSource.getScheduleResultById(resultId);
    }

    public ScheduleResult saveScheduleResult(ScheduleResult result) {
        return currentDataSource.saveScheduleResult(result);
    }

    public List<ScheduleResult> saveScheduleResults(List<ScheduleResult> results) {
        return currentDataSource.saveScheduleResults(results);
    }

    public boolean deleteScheduleResult(String resultId) {
        return currentDataSource.deleteScheduleResult(resultId);
    }

    // 数据管理方法
    public void initializeTestData() {
        currentDataSource.initializeTestData();
    }

    public DataValidationResult validateData() {
        return currentDataSource.validateData();
    }

    public DataStatistics getDataStatistics() {
        return currentDataSource.getDataStatistics();
    }

    public void clearAllData() {
        currentDataSource.clearAllData();
    }

    /**
     * 打印数据服务状态
     */
    public void printStatus() {
        System.out.printf(
                """
                        📊 数据服务状态信息:
                        ├── 当前数据源: %s
                        ├── 配置类型: %s
                        ├── 支持的数据源: %s
                        ├── 初始化测试数据: %s
                        ├── 启动验证: %s
                        └── 数据统计: %s
                        %n""",
                currentDataSource.getDataSourceType(),
                dataSourceConfig.getType().toUpperCase(),
                String.join(", ", getSupportedDataSourceTypes()),
                dataSourceConfig.isInitializeTestData() ? "是" : "否",
                dataSourceConfig.isValidateOnStartup() ? "是" : "否",
                getDataStatistics().getSummary().replace("\n", "\n    ")
        );
    }
}

