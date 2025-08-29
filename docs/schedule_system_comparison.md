# 排课系统对比文档

## 概述

本项目包含两套完整的排课系统设计：
1. **大学排课系统** - 适用于高等教育机构
2. **初高中排课系统** - 适用于中等教育机构

## 系统对比

### 1. 大学排课系统

#### 特点
- **选课制度**: 学生可以选择不同的课程和教师
- **灵活性高**: 课程时间、地点相对灵活
- **跨专业选课**: 支持不同专业学生选修同一门课程
- **学分制**: 基于学分进行课程管理

#### 核心实体
- 学院 (College)
- 专业 (Major)
- 班级 (SchoolClass)
- 教师 (Teacher)
- 课程 (Course)
- 教室 (Classroom)
- 学期 (Semester)
- 时间段 (TimeSlot)
- 课程安排 (CourseSchedule)

#### 数据库设计特点
```sql
-- 大学课程安排表
CREATE TABLE course_schedule (
    semester_id BIGINT,     -- 学期
    course_id BIGINT,       -- 课程
    teacher_id BIGINT,      -- 教师
    classroom_id BIGINT,    -- 教室
    class_id BIGINT,        -- 班级
    day_of_week TINYINT,    -- 星期
    time_slot_id BIGINT,    -- 时间段
    -- 支持灵活的周次安排
    start_week INT,
    end_week INT,
    week_type VARCHAR(20)   -- 全部/单周/双周
);
```

### 2. 初高中排课系统

#### 特点
- **固定班级**: 学生在固定班级上课
- **固定教室**: 每个班级有固定的教室
- **统一课表**: 全班学生统一课程安排
- **分科教学**: 按年级和学科进行教学安排

#### 核心实体
- 学校 (School)
- 年级 (Grade)
- 班级 (MiddleHighClass)
- 教师 (Teacher)
- 学科 (Subject)
- 教室 (Classroom)
- 学期 (Semester)
- 时间段 (TimeSlot)
- 班级课程表 (ClassSchedule)
- 教师任课表 (TeacherSubject)

#### 数据库设计特点
```sql
-- 初高中班级课程表
CREATE TABLE class_schedule (
    semester_id BIGINT,     -- 学期
    class_id BIGINT,        -- 班级（核心）
    subject_id BIGINT,      -- 学科
    teacher_id BIGINT,      -- 教师
    classroom_id BIGINT,    -- 教室（可选，使用班级固定教室）
    day_of_week TINYINT,    -- 星期
    time_slot_id BIGINT,    -- 时间段
    -- 约束：同一班级同一时间只能有一门课
    UNIQUE KEY uk_class_day_time (class_id, day_of_week, time_slot_id, semester_id)
);
```

## 主要差异对比

| 维度 | 大学排课系统 | 初高中排课系统 |
|------|-------------|----------------|
| **组织结构** | 学院 → 专业 → 班级 | 学校 → 年级 → 班级 |
| **课程模式** | 选课制，学分制 | 固定课程，学科制 |
| **教室使用** | 灵活分配，按需使用 | 固定教室为主 |
| **排课单位** | 以课程为中心 | 以班级为中心 |
| **时间安排** | 灵活的周次安排 | 相对固定的时间表 |
| **学生管理** | 跨班级选课 | 固定班级制 |
| **教师安排** | 一门课可多个教师 | 一个教师教多个班级 |

## 使用场景

### 大学排课系统适用于：
- 高等院校
- 职业技术学院
- 成人教育机构
- 培训机构（选课制）

### 初高中排课系统适用于：
- 初级中学
- 高级中学
- 完全中学
- 职业中学
- 技工学校

## 技术实现

### 共同特性
- 基于Spring Boot 3.2.0
- 使用MyBatis Plus进行数据访问
- 支持Redis缓存
- 统一的异常处理
- 完善的日志管理

### 扩展性设计
- 枚举类型便于扩展
- 基础实体类统一管理
- 模块化的包结构
- 支持多租户（通过school_id区分）

## 部署建议

### 单独部署
- 根据实际需求选择对应的系统
- 只部署相关的数据库表和实体类

### 统一部署
- 可以在同一个系统中支持两种模式
- 通过配置或参数区分不同的排课模式
- 适合教育集团或多层次教育机构

## 总结

两套排课系统各有特色，分别针对不同的教育阶段和管理模式设计。大学系统注重灵活性和选择性，初高中系统注重规范性和统一性。在实际应用中，可以根据具体需求选择合适的系统，或者进行定制化开发。

