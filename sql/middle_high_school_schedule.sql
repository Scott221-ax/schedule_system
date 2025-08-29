-- 初高中排课系统数据库表结构

-- 创建数据库
CREATE DATABASE IF NOT EXISTS middle_high_school_schedule DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE middle_high_school_schedule;

-- 1. 学校表
CREATE TABLE IF NOT EXISTS `school` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学校ID',
    `name` VARCHAR(100) NOT NULL COMMENT '学校名称',
    `code` VARCHAR(20) NOT NULL UNIQUE COMMENT '学校编码',
    `school_type` VARCHAR(20) NOT NULL COMMENT '学校类型：MIDDLE-初中，HIGH-高中，COMBINED-完全中学',
    `principal` VARCHAR(50) COMMENT '校长姓名',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `address` VARCHAR(200) COMMENT '学校地址',
    `description` TEXT COMMENT '学校描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_code` (`code`),
    INDEX `idx_school_type` (`school_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校表';

-- 2. 年级表
CREATE TABLE IF NOT EXISTS `grade` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '年级ID',
    `school_id` BIGINT NOT NULL COMMENT '所属学校ID',
    `name` VARCHAR(50) NOT NULL COMMENT '年级名称',
    `level` INT NOT NULL COMMENT '年级级别：7-初一，8-初二，9-初三，10-高一，11-高二，12-高三',
    `grade_type` VARCHAR(20) NOT NULL COMMENT '年级类型：MIDDLE-初中，HIGH-高中',
    `grade_director` VARCHAR(50) COMMENT '年级主任',
    `student_count` INT DEFAULT 0 COMMENT '年级学生总数',
    `class_count` INT DEFAULT 0 COMMENT '班级数量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_school_id` (`school_id`),
    INDEX `idx_level` (`level`),
    INDEX `idx_grade_type` (`grade_type`),
    FOREIGN KEY (`school_id`) REFERENCES `school`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='年级表';

-- 3. 班级表
CREATE TABLE IF NOT EXISTS `class` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '班级ID',
    `grade_id` BIGINT NOT NULL COMMENT '所属年级ID',
    `name` VARCHAR(50) NOT NULL COMMENT '班级名称',
    `class_number` INT NOT NULL COMMENT '班级序号',
    `classroom_id` BIGINT COMMENT '固定教室ID',
    `head_teacher_id` BIGINT COMMENT '班主任ID',
    `student_count` INT DEFAULT 0 COMMENT '学生人数',
    `class_type` VARCHAR(20) DEFAULT 'REGULAR' COMMENT '班级类型：REGULAR-普通班，KEY-重点班，EXPERIMENTAL-实验班，ART-艺术班，SPORTS-体育班',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY `uk_grade_class` (`grade_id`, `class_number`),
    INDEX `idx_grade_id` (`grade_id`),
    INDEX `idx_head_teacher` (`head_teacher_id`),
    INDEX `idx_classroom` (`classroom_id`),
    FOREIGN KEY (`grade_id`) REFERENCES `grade`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 4. 教师表
CREATE TABLE IF NOT EXISTS `teacher` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '教师ID',
    `school_id` BIGINT NOT NULL COMMENT '所属学校ID',
    `employee_no` VARCHAR(20) NOT NULL UNIQUE COMMENT '工号',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` CHAR(1) COMMENT '性别：M-男，F-女',
    `birth_date` DATE COMMENT '出生日期',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `subject_id` BIGINT COMMENT '主教学科ID',
    `title` VARCHAR(20) COMMENT '职称：SENIOR-高级教师，INTERMEDIATE-中级教师，JUNIOR-初级教师',
    `education` VARCHAR(20) COMMENT '学历：BACHELOR-本科，MASTER-硕士，DOCTOR-博士',
    `hire_date` DATE COMMENT '入职日期',
    `teacher_type` VARCHAR(20) DEFAULT 'FULL_TIME' COMMENT '教师类型：FULL_TIME-专职，PART_TIME-兼职',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-离职，1-在职',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_school_id` (`school_id`),
    INDEX `idx_employee_no` (`employee_no`),
    INDEX `idx_subject_id` (`subject_id`),
    INDEX `idx_name` (`name`),
    FOREIGN KEY (`school_id`) REFERENCES `school`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- 5. 学科表
CREATE TABLE IF NOT EXISTS `subject` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学科ID',
    `name` VARCHAR(50) NOT NULL COMMENT '学科名称',
    `code` VARCHAR(20) NOT NULL UNIQUE COMMENT '学科编码',
    `subject_type` VARCHAR(20) NOT NULL COMMENT '学科类型：MAIN-主科，MINOR-副科，ELECTIVE-选修',
    `grade_type` VARCHAR(20) NOT NULL COMMENT '适用年级类型：MIDDLE-初中，HIGH-高中，ALL-通用',
    `weekly_hours` INT DEFAULT 0 COMMENT '周课时数',
    `is_exam_subject` TINYINT DEFAULT 1 COMMENT '是否考试科目：0-否，1-是',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `description` TEXT COMMENT '学科描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_code` (`code`),
    INDEX `idx_subject_type` (`subject_type`),
    INDEX `idx_grade_type` (`grade_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学科表';

-- 6. 教室表
CREATE TABLE IF NOT EXISTS `classroom` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '教室ID',
    `school_id` BIGINT NOT NULL COMMENT '所属学校ID',
    `building` VARCHAR(50) NOT NULL COMMENT '教学楼',
    `floor` INT COMMENT '楼层',
    `room_number` VARCHAR(20) NOT NULL COMMENT '教室号',
    `name` VARCHAR(100) NOT NULL COMMENT '教室名称',
    `capacity` INT NOT NULL COMMENT '容量',
    `room_type` VARCHAR(20) NOT NULL COMMENT '教室类型：CLASSROOM-普通教室，LAB-实验室，COMPUTER-机房，MUSIC-音乐室，ART-美术室，SPORTS-体育馆',
    `equipment` TEXT COMMENT '设备描述',
    `is_fixed` TINYINT DEFAULT 1 COMMENT '是否固定教室：0-否，1-是',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-停用，1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY `uk_school_building_room` (`school_id`, `building`, `room_number`),
    INDEX `idx_school_id` (`school_id`),
    INDEX `idx_room_type` (`room_type`),
    INDEX `idx_capacity` (`capacity`),
    FOREIGN KEY (`school_id`) REFERENCES `school`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教室表';

-- 7. 学期表
CREATE TABLE IF NOT EXISTS `semester` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学期ID',
    `school_id` BIGINT NOT NULL COMMENT '所属学校ID',
    `name` VARCHAR(50) NOT NULL COMMENT '学期名称',
    `academic_year` VARCHAR(20) NOT NULL COMMENT '学年：如2023-2024',
    `semester_type` VARCHAR(20) NOT NULL COMMENT '学期类型：FIRST-第一学期，SECOND-第二学期',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `weeks` INT NOT NULL COMMENT '总周数',
    `status` VARCHAR(20) DEFAULT 'PLANNING' COMMENT '状态：PLANNING-计划中，ACTIVE-进行中，FINISHED-已结束',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_school_id` (`school_id`),
    INDEX `idx_academic_year` (`academic_year`),
    INDEX `idx_semester_type` (`semester_type`),
    FOREIGN KEY (`school_id`) REFERENCES `school`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学期表';

-- 8. 时间段表
CREATE TABLE IF NOT EXISTS `time_slot` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '时间段ID',
    `school_id` BIGINT NOT NULL COMMENT '所属学校ID',
    `name` VARCHAR(50) NOT NULL COMMENT '时间段名称：如第1节课',
    `period` INT NOT NULL COMMENT '节次',
    `time_type` VARCHAR(20) NOT NULL COMMENT '时间类型：MORNING-上午，AFTERNOON-下午，EVENING-晚上',
    `start_time` TIME NOT NULL COMMENT '开始时间',
    `end_time` TIME NOT NULL COMMENT '结束时间',
    `duration` INT NOT NULL COMMENT '时长（分钟）',
    `is_active` TINYINT DEFAULT 1 COMMENT '是否启用：0-否，1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY `uk_school_period` (`school_id`, `period`),
    INDEX `idx_school_id` (`school_id`),
    INDEX `idx_time_type` (`time_type`),
    FOREIGN KEY (`school_id`) REFERENCES `school`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间段表';

-- 9. 课程表（核心排课表）
CREATE TABLE IF NOT EXISTS `class_schedule` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课程表ID',
    `semester_id` BIGINT NOT NULL COMMENT '学期ID',
    `class_id` BIGINT NOT NULL COMMENT '班级ID',
    `subject_id` BIGINT NOT NULL COMMENT '学科ID',
    `teacher_id` BIGINT NOT NULL COMMENT '授课教师ID',
    `classroom_id` BIGINT COMMENT '教室ID（可为空，使用班级固定教室）',
    `day_of_week` TINYINT NOT NULL COMMENT '星期几：1-周一，2-周二...7-周日',
    `time_slot_id` BIGINT NOT NULL COMMENT '时间段ID',
    `start_week` INT DEFAULT 1 COMMENT '开始周次',
    `end_week` INT DEFAULT 20 COMMENT '结束周次',
    `week_type` VARCHAR(20) DEFAULT 'ALL' COMMENT '周次类型：ALL-全部，ODD-单周，EVEN-双周',
    `status` VARCHAR(20) DEFAULT 'NORMAL' COMMENT '状态：NORMAL-正常，CANCELLED-取消，ADJUSTED-调课',
    `remark` TEXT COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY `uk_class_day_time` (`class_id`, `day_of_week`, `time_slot_id`, `semester_id`),
    INDEX `idx_semester_id` (`semester_id`),
    INDEX `idx_class_id` (`class_id`),
    INDEX `idx_subject_id` (`subject_id`),
    INDEX `idx_teacher_id` (`teacher_id`),
    INDEX `idx_classroom_id` (`classroom_id`),
    INDEX `idx_day_time` (`day_of_week`, `time_slot_id`),
    FOREIGN KEY (`semester_id`) REFERENCES `semester`(`id`),
    FOREIGN KEY (`class_id`) REFERENCES `class`(`id`),
    FOREIGN KEY (`subject_id`) REFERENCES `subject`(`id`),
    FOREIGN KEY (`teacher_id`) REFERENCES `teacher`(`id`),
    FOREIGN KEY (`classroom_id`) REFERENCES `classroom`(`id`),
    FOREIGN KEY (`time_slot_id`) REFERENCES `time_slot`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级课程表';

-- 10. 教师任课表
CREATE TABLE IF NOT EXISTS `teacher_subject` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `subject_id` BIGINT NOT NULL COMMENT '学科ID',
    `class_id` BIGINT NOT NULL COMMENT '班级ID',
    `semester_id` BIGINT NOT NULL COMMENT '学期ID',
    `is_head_teacher` TINYINT DEFAULT 0 COMMENT '是否班主任：0-否，1-是',
    `weekly_hours` INT DEFAULT 0 COMMENT '周课时数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY `uk_teacher_subject_class_semester` (`teacher_id`, `subject_id`, `class_id`, `semester_id`),
    INDEX `idx_teacher_id` (`teacher_id`),
    INDEX `idx_subject_id` (`subject_id`),
    INDEX `idx_class_id` (`class_id`),
    INDEX `idx_semester_id` (`semester_id`),
    FOREIGN KEY (`teacher_id`) REFERENCES `teacher`(`id`),
    FOREIGN KEY (`subject_id`) REFERENCES `subject`(`id`),
    FOREIGN KEY (`class_id`) REFERENCES `class`(`id`),
    FOREIGN KEY (`semester_id`) REFERENCES `semester`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师任课表';

-- 11. 调课记录表
CREATE TABLE IF NOT EXISTS `schedule_adjustment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '调课记录ID',
    `original_schedule_id` BIGINT NOT NULL COMMENT '原课程安排ID',
    `new_schedule_id` BIGINT COMMENT '新课程安排ID',
    `adjustment_type` VARCHAR(20) NOT NULL COMMENT '调课类型：RESCHEDULE-改时间，CHANGE_TEACHER-换老师，CHANGE_ROOM-换教室，SUBSTITUTE-代课，CANCEL-停课',
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
    FOREIGN KEY (`original_schedule_id`) REFERENCES `class_schedule`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调课记录表';

-- 初始化基础数据

-- 插入学科数据
INSERT INTO `subject` (`name`, `code`, `subject_type`, `grade_type`, `weekly_hours`, `is_exam_subject`, `sort_order`) VALUES
-- 初中学科
('语文', 'CHINESE_M', 'MAIN', 'MIDDLE', 5, 1, 1),
('数学', 'MATH_M', 'MAIN', 'MIDDLE', 5, 1, 2),
('英语', 'ENGLISH_M', 'MAIN', 'MIDDLE', 4, 1, 3),
('物理', 'PHYSICS_M', 'MAIN', 'MIDDLE', 3, 1, 4),
('化学', 'CHEMISTRY_M', 'MAIN', 'MIDDLE', 3, 1, 5),
('生物', 'BIOLOGY_M', 'MINOR', 'MIDDLE', 2, 1, 6),
('历史', 'HISTORY_M', 'MINOR', 'MIDDLE', 2, 1, 7),
('地理', 'GEOGRAPHY_M', 'MINOR', 'MIDDLE', 2, 1, 8),
('政治', 'POLITICS_M', 'MINOR', 'MIDDLE', 2, 1, 9),
('体育', 'PE_M', 'MINOR', 'MIDDLE', 2, 0, 10),
('音乐', 'MUSIC_M', 'MINOR', 'MIDDLE', 1, 0, 11),
('美术', 'ART_M', 'MINOR', 'MIDDLE', 1, 0, 12),
-- 高中学科
('语文', 'CHINESE_H', 'MAIN', 'HIGH', 4, 1, 1),
('数学', 'MATH_H', 'MAIN', 'HIGH', 5, 1, 2),
('英语', 'ENGLISH_H', 'MAIN', 'HIGH', 4, 1, 3),
('物理', 'PHYSICS_H', 'MAIN', 'HIGH', 4, 1, 4),
('化学', 'CHEMISTRY_H', 'MAIN', 'HIGH', 3, 1, 5),
('生物', 'BIOLOGY_H', 'MAIN', 'HIGH', 3, 1, 6),
('历史', 'HISTORY_H', 'MAIN', 'HIGH', 3, 1, 7),
('地理', 'GEOGRAPHY_H', 'MAIN', 'HIGH', 3, 1, 8),
('政治', 'POLITICS_H', 'MAIN', 'HIGH', 3, 1, 9),
('体育', 'PE_H', 'MINOR', 'HIGH', 2, 0, 10),
('音乐', 'MUSIC_H', 'ELECTIVE', 'HIGH', 1, 0, 11),
('美术', 'ART_H', 'ELECTIVE', 'HIGH', 1, 0, 12);

-- 插入示例学校数据
INSERT INTO `school` (`name`, `code`, `school_type`, `principal`) VALUES
('实验中学', 'SYMS', 'COMBINED', '张校长'),
('第一中学', 'NO1MS', 'HIGH', '李校长'),
('育才中学', 'YCMS', 'MIDDLE', '王校长');

-- 插入时间段数据（以实验中学为例）
INSERT INTO `time_slot` (`school_id`, `name`, `period`, `time_type`, `start_time`, `end_time`, `duration`) VALUES
(1, '第1节课', 1, 'MORNING', '08:00:00', '08:40:00', 40),
(1, '第2节课', 2, 'MORNING', '08:50:00', '09:30:00', 40),
(1, '第3节课', 3, 'MORNING', '09:50:00', '10:30:00', 40),
(1, '第4节课', 4, 'MORNING', '10:40:00', '11:20:00', 40),
(1, '第5节课', 5, 'MORNING', '11:30:00', '12:10:00', 40),
(1, '第6节课', 6, 'AFTERNOON', '14:30:00', '15:10:00', 40),
(1, '第7节课', 7, 'AFTERNOON', '15:20:00', '16:00:00', 40),
(1, '第8节课', 8, 'AFTERNOON', '16:10:00', '16:50:00', 40),
(1, '第9节课', 9, 'EVENING', '19:00:00', '19:40:00', 40),
(1, '第10节课', 10, 'EVENING', '19:50:00', '20:30:00', 40);

