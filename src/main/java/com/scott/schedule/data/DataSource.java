package com.scott.schedule.data;

import com.scott.schedule.model.*;

import java.util.List;

/**
 * 数据源接口
 * 定义统一的数据访问接口，支持多种数据源实现
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
public interface DataSource {

    // ==================== 课程数据操作 ====================
    List<Course> getAllCourses();
    Course getCourseById(String courseId);
    Course saveCourse(Course course);
    List<Course> saveCourses(List<Course> courses);
    boolean deleteCourse(String courseId);

    // ==================== 教师数据操作 ====================
    List<Teacher> getAllTeachers();
    Teacher getTeacherById(String teacherId);
    Teacher saveTeacher(Teacher teacher);
    List<Teacher> saveTeachers(List<Teacher> teachers);
    boolean deleteTeacher(String teacherId);

    // ==================== 教室数据操作 ====================
    List<Classroom> getAllClassrooms();
    Classroom getClassroomById(String classroomId);
    Classroom saveClassroom(Classroom classroom);
    List<Classroom> saveClassrooms(List<Classroom> classrooms);
    boolean deleteClassroom(String classroomId);

    // ==================== 时间段数据操作 ====================
    List<TimeSlot> getAllTimeSlots();
    TimeSlot getTimeSlotById(String timeSlotId);
    TimeSlot saveTimeSlot(TimeSlot timeSlot);
    List<TimeSlot> saveTimeSlots(List<TimeSlot> timeSlots);
    boolean deleteTimeSlot(String timeSlotId);

    // ==================== 学生数据操作 ====================
    List<Student> getAllStudents();
    Student getStudentById(String studentId);
    Student saveStudent(Student student);
    List<Student> saveStudents(List<Student> students);
    boolean deleteStudent(String studentId);

    // ==================== 约束数据操作 ====================
    ScheduleConstraints getConstraints();
    ScheduleConstraints saveConstraints(ScheduleConstraints constraints);

    // ==================== 排课结果数据操作 ====================
    List<ScheduleResult> getAllScheduleResults();
    ScheduleResult getScheduleResultById(String resultId);
    ScheduleResult saveScheduleResult(ScheduleResult result);
    List<ScheduleResult> saveScheduleResults(List<ScheduleResult> results);
    boolean deleteScheduleResult(String resultId);

    // ==================== 数据管理操作 ====================
    void initializeTestData();
    DataValidationResult validateData();
    DataStatistics getDataStatistics();
    void clearAllData();
    String getDataSourceType();
}

