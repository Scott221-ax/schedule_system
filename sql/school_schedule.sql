-- 学校排课系统数据库表结构

-- 创建数据库
CREATE DATABASE IF NOT EXISTS school_schedule_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE school_schedule_system;

-- 1. 学院表
CREATE TABLE IF NOT EXISTS `college` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学院ID',
    `name` VARCHAR(100) NOT NULL COMMENT '学院名称',
    `code` VARCHAR(20) NOT NULL UNIQUE COMMENT '学院编码',
    `dean` VARCHAR(50) COMMENT '院长姓名',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `address` VARCHAR(200) COMMENT '学院地址',
    `description` TEXT COMMENT '学院描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学院表';

-- 2. 专业表
CREATE TABLE IF NOT EXISTS `major` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '专业ID',
    `college_id` BIGINT NOT NULL COMMENT '所属学院ID',
    `name` VARCHAR(100) NOT NULL COMMENT '专业名称',
    `code` VARCHAR(20) NOT NULL UNIQUE COMMENT '专业编码',
    `degree_type` VARCHAR(20) NOT NULL COMMENT '学位类型：BACHELOR-本科，MASTER-硕士，DOCTOR-博士',
    `duration` INT DEFAULT 4 COMMENT '学制年限',
    `description` TEXT COMMENT '专业描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_college_id` (`college_id`),
    INDEX `idx_code` (`code`),
    FOREIGN KEY (`college_id`) REFERENCES `college`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业表';

-- 3. 班级表
CREATE TABLE IF NOT EXISTS `class` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '班级ID',
    `major_id` BIGINT NOT NULL COMMENT '所属专业ID',
    `name` VARCHAR(100) NOT NULL COMMENT '班级名称',
    `code` VARCHAR(20) NOT NULL UNIQUE COMMENT '班级编码',
    `grade` INT NOT NULL COMMENT '年级',
    `student_count` INT DEFAULT 0 COMMENT '学生人数',
    `class_teacher` VARCHAR(50) COMMENT '班主任',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_major_id` (`major_id`),
    INDEX `idx_code` (`code`),
    INDEX `idx_grade` (`grade`),
    FOREIGN KEY (`major_id`) REFERENCES `major`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 4. 教师表
CREATE TABLE IF NOT EXISTS `teacher` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '教师ID',
    `college_id` BIGINT NOT NULL COMMENT '所属学院ID',
    `employee_no` VARCHAR(20) NOT NULL UNIQUE COMMENT '工号',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` CHAR(1) COMMENT '性别：M-男，F-女',
    `birth_date` DATE COMMENT '出生日期',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `title` VARCHAR(20) COMMENT '职称：PROFESSOR-教授，ASSOCIATE_PROFESSOR-副教授，LECTURER-讲师，ASSISTANT-助教',
    `degree` VARCHAR(20) COMMENT '学历：BACHELOR-本科，MASTER-硕士，DOCTOR-博士',
    `hire_date` DATE COMMENT '入职日期',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-离职，1-在职',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_college_id` (`college_id`),
    INDEX `idx_employee_no` (`employee_no`),
    INDEX `idx_name` (`name`),
    FOREIGN KEY (`college_id`) REFERENCES `college`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- 5. 课程表
CREATE TABLE IF NOT EXISTS `course` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课程ID',
    `college_id` BIGINT NOT NULL COMMENT '开课学院ID',
    `name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `code` VARCHAR(20) NOT NULL UNIQUE COMMENT '课程编码',
    `credits` DECIMAL(3,1) NOT NULL COMMENT '学分',
    `hours` INT NOT NULL COMMENT '总学时',
    `theory_hours` INT DEFAULT 0 COMMENT '理论学时',
    `practice_hours` INT DEFAULT 0 COMMENT '实践学时',
    `course_type` VARCHAR(20) NOT NULL COMMENT '课程类型：REQUIRED-必修，ELECTIVE-选修，PUBLIC-公共课',
    `description` TEXT COMMENT '课程描述',
    `prerequisite` TEXT COMMENT '先修课程',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_college_id` (`college_id`),
    INDEX `idx_code` (`code`),
    INDEX `idx_course_type` (`course_type`),
    FOREIGN KEY (`college_id`) REFERENCES `college`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 6. 教室表
CREATE TABLE IF NOT EXISTS `classroom` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '教室ID',
    `building` VARCHAR(50) NOT NULL COMMENT '教学楼',
    `room_number` VARCHAR(20) NOT NULL COMMENT '教室号',
    `name` VARCHAR(100) NOT NULL COMMENT '教室名称',
    `capacity` INT NOT NULL COMMENT '容量',
    `room_type` VARCHAR(20) NOT NULL COMMENT '教室类型：NORMAL-普通教室，LAB-实验室，MULTIMEDIA-多媒体教室，AMPHITHEATER-阶梯教室',
    `equipment` TEXT COMMENT '设备描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY `uk_building_room` (`building`, `room_number`),
    INDEX `idx_room_type` (`room_type`),
    INDEX `idx_capacity` (`capacity`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教室表';

-- 7. 学期表
CREATE TABLE IF NOT EXISTS `semester` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学期ID',
    `name` VARCHAR(50) NOT NULL COMMENT '学期名称',
    `academic_year` VARCHAR(20) NOT NULL COMMENT '学年：如2023-2024',
    `semester_type` VARCHAR(20) NOT NULL COMMENT '学期类型：SPRING-春季学期，AUTUMN-秋季学期，SUMMER-夏季学期',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `weeks` INT NOT NULL COMMENT '总周数',
    `status` VARCHAR(20) DEFAULT 'PLANNING' COMMENT '状态：PLANNING-计划中，ACTIVE-进行中，FINISHED-已结束',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_academic_year` (`academic_year`),
    INDEX `idx_semester_type` (`semester_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学期表';

-- 8. 时间段表
CREATE TABLE IF NOT EXISTS `time_slot` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '时间段ID',
    `name` VARCHAR(50) NOT NULL COMMENT '时间段名称：如第1节课',
    `period` INT NOT NULL COMMENT '节次',
    `start_time` TIME NOT NULL COMMENT '开始时间',
    `end_time` TIME NOT NULL COMMENT '结束时间',
    `duration` INT NOT NULL COMMENT '时长（分钟）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY `uk_period` (`period`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间段表';

-- 9. 课程安排表（核心排课表）
CREATE TABLE IF NOT EXISTS `course_schedule` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课程安排ID',
    `semester_id` BIGINT NOT NULL COMMENT '学期ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `teacher_id` BIGINT NOT NULL COMMENT '授课教师ID',
    `classroom_id` BIGINT NOT NULL COMMENT '教室ID',
    `class_id` BIGINT NOT NULL COMMENT '上课班级ID',
    `day_of_week` TINYINT NOT NULL COMMENT '星期几：1-周一，2-周二...7-周日',
    `time_slot_id` BIGINT NOT NULL COMMENT '时间段ID',
    `start_week` INT NOT NULL COMMENT '开始周次',
    `end_week` INT NOT NULL COMMENT '结束周次',
    `week_type` VARCHAR(20) DEFAULT 'ALL' COMMENT '周次类型：ALL-全部，ODD-单周，EVEN-双周',
    `status` VARCHAR(20) DEFAULT 'NORMAL' COMMENT '状态：NORMAL-正常，CANCELLED-取消，ADJUSTED-调课',
    `remark` TEXT COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_semester_id` (`semester_id`),
    INDEX `idx_course_id` (`course_id`),
    INDEX `idx_teacher_id` (`teacher_id`),
    INDEX `idx_classroom_id` (`classroom_id`),
    INDEX `idx_class_id` (`class_id`),
    INDEX `idx_day_time` (`day_of_week`, `time_slot_id`),
    INDEX `idx_week_range` (`start_week`, `end_week`),
    FOREIGN KEY (`semester_id`) REFERENCES `semester`(`id`),
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`),
    FOREIGN KEY (`teacher_id`) REFERENCES `teacher`(`id`),
    FOREIGN KEY (`classroom_id`) REFERENCES `classroom`(`id`),
    FOREIGN KEY (`class_id`) REFERENCES `class`(`id`),
    FOREIGN KEY (`time_slot_id`) REFERENCES `time_slot`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程安排表';

-- 10. 调课记录表
CREATE TABLE IF NOT EXISTS `schedule_adjustment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '调课记录ID',
    `original_schedule_id` BIGINT NOT NULL COMMENT '原课程安排ID',
    `new_schedule_id` BIGINT COMMENT '新课程安排ID',
    `adjustment_type` VARCHAR(20) NOT NULL COMMENT '调课类型：RESCHEDULE-改时间，CHANGE_ROOM-换教室，SUBSTITUTE-代课，CANCEL-停课',
    `reason` TEXT NOT NULL COMMENT '调课原因',
    `adjustment_date` DATE NOT NULL COMMENT '调课日期',
    `applicant` VARCHAR(50) NOT NULL COMMENT '申请人',
    `approver` VARCHAR(50) COMMENT '审批人',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待审批，APPROVED-已批准，REJECTED-已拒绝',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_original_schedule` (`original_schedule_id`),
    INDEX `idx_adjustment_date` (`adjustment_date`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`original_schedule_id`) REFERENCES `course_schedule`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调课记录表';

-- 初始化基础数据

-- 插入时间段数据
INSERT INTO `time_slot` (`name`, `period`, `start_time`, `end_time`, `duration`) VALUES
('第1节课', 1, '08:00:00', '08:45:00', 45),
('第2节课', 2, '08:55:00', '09:40:00', 45),
('第3节课', 3, '10:00:00', '10:45:00', 45),
('第4节课', 4, '10:55:00', '11:40:00', 45),
('第5节课', 5, '14:00:00', '14:45:00', 45),
('第6节课', 6, '14:55:00', '15:40:00', 45),
('第7节课', 7, '16:00:00', '16:45:00', 45),
('第8节课', 8, '16:55:00', '17:40:00', 45),
('第9节课', 9, '19:00:00', '19:45:00', 45),
('第10节课', 10, '19:55:00', '20:40:00', 45);

-- 插入学院数据
INSERT INTO `college` (`name`, `code`, `dean`) VALUES
('计算机科学与技术学院', 'CS', '张教授'),
('软件工程学院', 'SE', '李教授'),
('数学与统计学院', 'MATH', '王教授'),
('外国语学院', 'FL', '刘教授');

-- 插入教室数据
INSERT INTO `classroom` (`building`, `room_number`, `name`, `capacity`, `room_type`) VALUES
('教学楼A', '101', 'A101教室', 60, 'NORMAL'),
('教学楼A', '102', 'A102教室', 60, 'NORMAL'),
('教学楼A', '201', 'A201多媒体教室', 80, 'MULTIMEDIA'),
('教学楼B', '101', 'B101实验室', 40, 'LAB'),
('教学楼B', '301', 'B301阶梯教室', 120, 'AMPHITHEATER');

