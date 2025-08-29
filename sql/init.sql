-- 排班排课系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS schedule_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS schedule_system_dev DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE schedule_system;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_username` (`username`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 部门表
CREATE TABLE IF NOT EXISTS `department` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '部门ID',
    `name` VARCHAR(100) NOT NULL COMMENT '部门名称',
    `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '部门编码',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父部门ID',
    `level` INT DEFAULT 1 COMMENT '部门层级',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `description` TEXT COMMENT '部门描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_parent_id` (`parent_id`),
    INDEX `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 排班表
CREATE TABLE IF NOT EXISTS `schedule` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '排班ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `department_id` BIGINT NOT NULL COMMENT '部门ID',
    `schedule_date` DATE NOT NULL COMMENT '排班日期',
    `shift_type` VARCHAR(20) NOT NULL COMMENT '班次类型：MORNING-早班，AFTERNOON-午班，EVENING-晚班',
    `start_time` TIME NOT NULL COMMENT '开始时间',
    `end_time` TIME NOT NULL COMMENT '结束时间',
    `status` VARCHAR(20) DEFAULT 'NORMAL' COMMENT '状态：NORMAL-正常，LEAVE-请假，SUBSTITUTE-代班',
    `remark` TEXT COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY `uk_user_date_shift` (`user_id`, `schedule_date`, `shift_type`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_department_id` (`department_id`),
    INDEX `idx_schedule_date` (`schedule_date`),
    INDEX `idx_shift_type` (`shift_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排班表';

-- 课程表
CREATE TABLE IF NOT EXISTS `course` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课程ID',
    `name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '课程编码',
    `teacher_id` BIGINT NOT NULL COMMENT '授课教师ID',
    `classroom` VARCHAR(50) COMMENT '教室',
    `course_date` DATE NOT NULL COMMENT '上课日期',
    `start_time` TIME NOT NULL COMMENT '开始时间',
    `end_time` TIME NOT NULL COMMENT '结束时间',
    `duration` INT NOT NULL COMMENT '课程时长（分钟）',
    `capacity` INT DEFAULT 0 COMMENT '容量',
    `enrolled` INT DEFAULT 0 COMMENT '已报名人数',
    `status` VARCHAR(20) DEFAULT 'NORMAL' COMMENT '状态：NORMAL-正常，CANCELLED-取消',
    `description` TEXT COMMENT '课程描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) COMMENT '创建人',
    `update_by` VARCHAR(50) COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_teacher_id` (`teacher_id`),
    INDEX `idx_course_date` (`course_date`),
    INDEX `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 初始化数据
INSERT INTO `user` (`username`, `password`, `real_name`, `email`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '系统管理员', 'admin@meituan.com', 'ADMIN'),
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '张老师', 'teacher1@meituan.com', 'USER'),
('teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '李老师', 'teacher2@meituan.com', 'USER');

INSERT INTO `department` (`name`, `code`, `parent_id`, `level`) VALUES
('技术部', 'TECH', 0, 1),
('产品部', 'PRODUCT', 0, 1),
('运营部', 'OPERATION', 0, 1);

