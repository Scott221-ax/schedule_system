package com.scott.schedule.controller;

import com.scott.schedule.data.DataStatistics;
import com.scott.schedule.data.DataValidationResult;
import com.scott.schedule.model.*;
import com.scott.schedule.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据管理控制器
 * 提供数据源管理和数据操作的REST API
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*")
public class DataController {

    private final DataService dataService;

    @Autowired
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    // ==================== 数据源管理接口 ====================

    /**
     * 获取数据源状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getDataSourceStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("dataSourceType", dataService.getCurrentDataSourceType());
        status.put("supportedTypes", dataService.getSupportedDataSourceTypes());
        status.put("statistics", dataService.getDataStatistics());

        return ResponseEntity.ok(status);
    }

    /**
     * 切换数据源
     */
    @PostMapping("/switch/{dataSourceType}")
    public ResponseEntity<Map<String, String>> switchDataSource(@PathVariable String dataSourceType) {
        try {
            dataService.switchDataSource(dataSourceType);

            Map<String, String> response = new HashMap<>();
            response.put("message", "数据源切换成功");
            response.put("currentDataSource", dataService.getCurrentDataSourceType());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "数据源切换失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 数据迁移
     */
    @PostMapping("/migrate/{targetDataSourceType}")
    public ResponseEntity<Map<String, String>> migrateData(@PathVariable String targetDataSourceType) {
        try {
            dataService.migrateData(targetDataSourceType);

            Map<String, String> response = new HashMap<>();
            response.put("message", "数据迁移成功");
            response.put("targetDataSource", targetDataSourceType.toUpperCase());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "数据迁移失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 初始化测试数据
     */
    @PostMapping("/initialize")
    public ResponseEntity<Map<String, String>> initializeTestData() {
        try {
            dataService.initializeTestData();

            Map<String, String> response = new HashMap<>();
            response.put("message", "测试数据初始化成功");
            response.put("dataSource", dataService.getCurrentDataSourceType());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "测试数据初始化失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 验证数据完整性
     */
    @GetMapping("/validate")
    public ResponseEntity<DataValidationResult> validateData() {
        DataValidationResult result = dataService.validateData();
        return ResponseEntity.ok(result);
    }

    /**
     * 获取数据统计
     */
    @GetMapping("/statistics")
    public ResponseEntity<DataStatistics> getDataStatistics() {
        DataStatistics statistics = dataService.getDataStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * 清空所有数据
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearAllData() {
        try {
            dataService.clearAllData();

            Map<String, String> response = new HashMap<>();
            response.put("message", "所有数据已清空");
            response.put("dataSource", dataService.getCurrentDataSourceType());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "清空数据失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ==================== 课程数据接口 ====================

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(dataService.getAllCourses());
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable String courseId) {
        Course course = dataService.getCourseById(courseId);
        return course != null ? ResponseEntity.ok(course) : ResponseEntity.notFound().build();
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> saveCourse(@RequestBody Course course) {
        Course savedCourse = dataService.saveCourse(course);
        return ResponseEntity.ok(savedCourse);
    }

    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable String courseId) {
        boolean deleted = dataService.deleteCourse(courseId);

        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "课程删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "课程删除失败");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== 教师数据接口 ====================

    @GetMapping("/teachers")
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        return ResponseEntity.ok(dataService.getAllTeachers());
    }

    @GetMapping("/teachers/{teacherId}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable String teacherId) {
        Teacher teacher = dataService.getTeacherById(teacherId);
        return teacher != null ? ResponseEntity.ok(teacher) : ResponseEntity.notFound().build();
    }

    @PostMapping("/teachers")
    public ResponseEntity<Teacher> saveTeacher(@RequestBody Teacher teacher) {
        Teacher savedTeacher = dataService.saveTeacher(teacher);
        return ResponseEntity.ok(savedTeacher);
    }

    @DeleteMapping("/teachers/{teacherId}")
    public ResponseEntity<Map<String, String>> deleteTeacher(@PathVariable String teacherId) {
        boolean deleted = dataService.deleteTeacher(teacherId);

        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "教师删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "教师删除失败");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== 教室数据接口 ====================

    @GetMapping("/classrooms")
    public ResponseEntity<List<Classroom>> getAllClassrooms() {
        return ResponseEntity.ok(dataService.getAllClassrooms());
    }

    @GetMapping("/classrooms/{classroomId}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable String classroomId) {
        Classroom classroom = dataService.getClassroomById(classroomId);
        return classroom != null ? ResponseEntity.ok(classroom) : ResponseEntity.notFound().build();
    }

    @PostMapping("/classrooms")
    public ResponseEntity<Classroom> saveClassroom(@RequestBody Classroom classroom) {
        Classroom savedClassroom = dataService.saveClassroom(classroom);
        return ResponseEntity.ok(savedClassroom);
    }

    @DeleteMapping("/classrooms/{classroomId}")
    public ResponseEntity<Map<String, String>> deleteClassroom(@PathVariable String classroomId) {
        boolean deleted = dataService.deleteClassroom(classroomId);

        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "教室删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "教室删除失败");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== 时间段数据接口 ====================

    @GetMapping("/timeslots")
    public ResponseEntity<List<TimeSlot>> getAllTimeSlots() {
        return ResponseEntity.ok(dataService.getAllTimeSlots());
    }

    @GetMapping("/timeslots/{timeSlotId}")
    public ResponseEntity<TimeSlot> getTimeSlotById(@PathVariable String timeSlotId) {
        TimeSlot timeSlot = dataService.getTimeSlotById(timeSlotId);
        return timeSlot != null ? ResponseEntity.ok(timeSlot) : ResponseEntity.notFound().build();
    }

    @PostMapping("/timeslots")
    public ResponseEntity<TimeSlot> saveTimeSlot(@RequestBody TimeSlot timeSlot) {
        TimeSlot savedTimeSlot = dataService.saveTimeSlot(timeSlot);
        return ResponseEntity.ok(savedTimeSlot);
    }

    @DeleteMapping("/timeslots/{timeSlotId}")
    public ResponseEntity<Map<String, String>> deleteTimeSlot(@PathVariable String timeSlotId) {
        boolean deleted = dataService.deleteTimeSlot(timeSlotId);

        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "时间段删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "时间段删除失败");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== 学生数据接口 ====================

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(dataService.getAllStudents());
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<Student> getStudentById(@PathVariable String studentId) {
        Student student = dataService.getStudentById(studentId);
        return student != null ? ResponseEntity.ok(student) : ResponseEntity.notFound().build();
    }

    @PostMapping("/students")
    public ResponseEntity<Student> saveStudent(@RequestBody Student student) {
        Student savedStudent = dataService.saveStudent(student);
        return ResponseEntity.ok(savedStudent);
    }

    @DeleteMapping("/students/{studentId}")
    public ResponseEntity<Map<String, String>> deleteStudent(@PathVariable String studentId) {
        boolean deleted = dataService.deleteStudent(studentId);

        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "学生删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "学生删除失败");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== 约束配置接口 ====================

    @GetMapping("/constraints")
    public ResponseEntity<ScheduleConstraints> getConstraints() {
        return ResponseEntity.ok(dataService.getConstraints());
    }

    @PostMapping("/constraints")
    public ResponseEntity<ScheduleConstraints> saveConstraints(@RequestBody ScheduleConstraints constraints) {
        ScheduleConstraints savedConstraints = dataService.saveConstraints(constraints);
        return ResponseEntity.ok(savedConstraints);
    }

    // ==================== 排课结果接口 ====================

    @GetMapping("/results")
    public ResponseEntity<List<ScheduleResult>> getAllScheduleResults() {
        return ResponseEntity.ok(dataService.getAllScheduleResults());
    }

    @GetMapping("/results/{resultId}")
    public ResponseEntity<ScheduleResult> getScheduleResultById(@PathVariable String resultId) {
        ScheduleResult result = dataService.getScheduleResultById(resultId);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @PostMapping("/results")
    public ResponseEntity<ScheduleResult> saveScheduleResult(@RequestBody ScheduleResult result) {
        ScheduleResult savedResult = dataService.saveScheduleResult(result);
        return ResponseEntity.ok(savedResult);
    }

    @DeleteMapping("/results/{resultId}")
    public ResponseEntity<Map<String, String>> deleteScheduleResult(@PathVariable String resultId) {
        boolean deleted = dataService.deleteScheduleResult(resultId);

        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "排课结果删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "排课结果删除失败");
            return ResponseEntity.badRequest().body(response);
        }
    }
}

