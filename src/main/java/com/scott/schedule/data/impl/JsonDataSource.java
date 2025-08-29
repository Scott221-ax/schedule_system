package com.scott.schedule.data.impl;

import com.scott.schedule.data.DataSource;
import com.scott.schedule.data.DataStatistics;
import com.scott.schedule.data.DataValidationResult;
import com.scott.schedule.data.JsonDataManager;
import com.scott.schedule.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * JSON文件数据源实现
 * 基于本地JSON文件的数据存储和访问
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Component("jsonDataSource")
public class JsonDataSource implements DataSource {

    private final JsonDataManager jsonDataManager;

    public JsonDataSource(JsonDataManager jsonDataManager) {
        this.jsonDataManager = jsonDataManager;
    }

    @Override
    public String getDataSourceType() {
        return "JSON";
    }

    // ==================== 课程数据操作 ====================

    @Override
    public List<Course> getAllCourses() {
        return jsonDataManager.loadCourses();
    }

    @Override
    public Course getCourseById(String courseId) {
        return getAllCourses().stream()
                .filter(course -> course.getId().equals(courseId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Course saveCourse(Course course) {
        List<Course> courses = getAllCourses();

        // 如果ID为空，生成新ID
        if (course.getId() == null || course.getId().isEmpty()) {
            course.setId("C" + String.format("%03d", courses.size() + 1));
        }

        // 更新或添加课程
        courses.removeIf(c -> c.getId().equals(course.getId()));
        courses.add(course);

        jsonDataManager.saveCourses(courses);
        return course;
    }

    @Override
    public List<Course> saveCourses(List<Course> courses) {
        jsonDataManager.saveCourses(courses);
        return courses;
    }

    @Override
    public boolean deleteCourse(String courseId) {
        List<Course> courses = getAllCourses();
        boolean removed = courses.removeIf(course -> course.getId().equals(courseId));
        if (removed) {
            jsonDataManager.saveCourses(courses);
        }
        return removed;
    }

    // ==================== 教师数据操作 ====================

    @Override
    public List<Teacher> getAllTeachers() {
        return jsonDataManager.loadTeachers();
    }

    @Override
    public Teacher getTeacherById(String teacherId) {
        return getAllTeachers().stream()
                .filter(teacher -> teacher.getId().equals(teacherId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Teacher saveTeacher(Teacher teacher) {
        List<Teacher> teachers = getAllTeachers();

        if (teacher.getId() == null || teacher.getId().isEmpty()) {
            teacher.setId("T" + String.format("%03d", teachers.size() + 1));
        }

        teachers.removeIf(t -> t.getId().equals(teacher.getId()));
        teachers.add(teacher);

        jsonDataManager.saveTeachers(teachers);
        return teacher;
    }

    @Override
    public List<Teacher> saveTeachers(List<Teacher> teachers) {
        jsonDataManager.saveTeachers(teachers);
        return teachers;
    }

    @Override
    public boolean deleteTeacher(String teacherId) {
        List<Teacher> teachers = getAllTeachers();
        boolean removed = teachers.removeIf(teacher -> teacher.getId().equals(teacherId));
        if (removed) {
            jsonDataManager.saveTeachers(teachers);
        }
        return removed;
    }

    // ==================== 教室数据操作 ====================

    @Override
    public List<Classroom> getAllClassrooms() {
        return jsonDataManager.loadClassrooms();
    }

    @Override
    public Classroom getClassroomById(String classroomId) {
        return getAllClassrooms().stream()
                .filter(classroom -> classroom.getId().equals(classroomId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Classroom saveClassroom(Classroom classroom) {
        List<Classroom> classrooms = getAllClassrooms();

        if (classroom.getId() == null || classroom.getId().isEmpty()) {
            classroom.setId("CR" + String.format("%03d", classrooms.size() + 1));
        }

        classrooms.removeIf(c -> c.getId().equals(classroom.getId()));
        classrooms.add(classroom);

        jsonDataManager.saveClassrooms(classrooms);
        return classroom;
    }

    @Override
    public List<Classroom> saveClassrooms(List<Classroom> classrooms) {
        jsonDataManager.saveClassrooms(classrooms);
        return classrooms;
    }

    @Override
    public boolean deleteClassroom(String classroomId) {
        List<Classroom> classrooms = getAllClassrooms();
        boolean removed = classrooms.removeIf(classroom -> classroom.getId().equals(classroomId));
        if (removed) {
            jsonDataManager.saveClassrooms(classrooms);
        }
        return removed;
    }

    // ==================== 时间段数据操作 ====================

    @Override
    public List<TimeSlot> getAllTimeSlots() {
        return jsonDataManager.loadTimeSlots();
    }

    @Override
    public TimeSlot getTimeSlotById(String timeSlotId) {
        return getAllTimeSlots().stream()
                .filter(timeSlot -> timeSlot.getId().equals(timeSlotId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public TimeSlot saveTimeSlot(TimeSlot timeSlot) {
        List<TimeSlot> timeSlots = getAllTimeSlots();

        if (timeSlot.getId() == null || timeSlot.getId().isEmpty()) {
            timeSlot.setId("TS" + String.format("%03d", timeSlots.size() + 1));
        }

        timeSlots.removeIf(ts -> ts.getId().equals(timeSlot.getId()));
        timeSlots.add(timeSlot);

        jsonDataManager.saveTimeSlots(timeSlots);
        return timeSlot;
    }

    @Override
    public List<TimeSlot> saveTimeSlots(List<TimeSlot> timeSlots) {
        jsonDataManager.saveTimeSlots(timeSlots);
        return timeSlots;
    }

    @Override
    public boolean deleteTimeSlot(String timeSlotId) {
        List<TimeSlot> timeSlots = getAllTimeSlots();
        boolean removed = timeSlots.removeIf(timeSlot -> timeSlot.getId().equals(timeSlotId));
        if (removed) {
            jsonDataManager.saveTimeSlots(timeSlots);
        }
        return removed;
    }

    // ==================== 学生数据操作 ====================

    @Override
    public List<Student> getAllStudents() {
        return jsonDataManager.loadStudents();
    }

    @Override
    public Student getStudentById(String studentId) {
        return getAllStudents().stream()
                .filter(student -> student.getId().equals(studentId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Student saveStudent(Student student) {
        List<Student> students = getAllStudents();

        if (student.getId() == null || student.getId().isEmpty()) {
            student.setId("S" + String.format("%06d", students.size() + 1));
        }

        students.removeIf(s -> s.getId().equals(student.getId()));
        students.add(student);

        jsonDataManager.saveStudents(students);
        return student;
    }

    @Override
    public List<Student> saveStudents(List<Student> students) {
        jsonDataManager.saveStudents(students);
        return students;
    }

    @Override
    public boolean deleteStudent(String studentId) {
        List<Student> students = getAllStudents();
        boolean removed = students.removeIf(student -> student.getId().equals(studentId));
        if (removed) {
            jsonDataManager.saveStudents(students);
        }
        return removed;
    }

    // ==================== 约束数据操作 ====================

    @Override
    public ScheduleConstraints getConstraints() {
        return jsonDataManager.loadConstraints();
    }

    @Override
    public ScheduleConstraints saveConstraints(ScheduleConstraints constraints) {
        jsonDataManager.saveConstraints(constraints);
        return constraints;
    }

    // ==================== 排课结果数据操作 ====================

    @Override
    public List<ScheduleResult> getAllScheduleResults() {
        return jsonDataManager.loadScheduleResults();
    }

    @Override
    public ScheduleResult getScheduleResultById(String resultId) {
        return getAllScheduleResults().stream()
                .filter(result -> result.getId().equals(resultId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public ScheduleResult saveScheduleResult(ScheduleResult result) {
        List<ScheduleResult> results = getAllScheduleResults();

        if (result.getId() == null || result.getId().isEmpty()) {
            result.setId("SR" + UUID.randomUUID().toString().substring(0, 8));
        }

        results.removeIf(r -> r.getId().equals(result.getId()));
        results.add(result);

        jsonDataManager.saveScheduleResults(results);
        return result;
    }

    @Override
    public List<ScheduleResult> saveScheduleResults(List<ScheduleResult> results) {
        jsonDataManager.saveScheduleResults(results);
        return results;
    }

    @Override
    public boolean deleteScheduleResult(String resultId) {
        List<ScheduleResult> results = getAllScheduleResults();
        boolean removed = results.removeIf(result -> result.getId().equals(resultId));
        if (removed) {
            jsonDataManager.saveScheduleResults(results);
        }
        return removed;
    }

    // ==================== 数据管理操作 ====================

    @Override
    public void initializeTestData() {
        jsonDataManager.initializeTestData();
    }

    @Override
    public DataValidationResult validateData() {
        return jsonDataManager.validateData();
    }

    @Override
    public DataStatistics getDataStatistics() {
        DataStatistics stats = jsonDataManager.getDataStatistics();
        stats.setScheduleResultCount(getAllScheduleResults().size());
        return stats;
    }

    @Override
    public void clearAllData() {
        jsonDataManager.saveCourses(List.of());
        jsonDataManager.saveTeachers(List.of());
        jsonDataManager.saveClassrooms(List.of());
        jsonDataManager.saveTimeSlots(List.of());
        jsonDataManager.saveStudents(List.of());
        jsonDataManager.saveScheduleResults(List.of());
        jsonDataManager.saveConstraints(new ScheduleConstraints());
        System.out.println("🗑️ 已清空所有JSON数据");
    }
}

