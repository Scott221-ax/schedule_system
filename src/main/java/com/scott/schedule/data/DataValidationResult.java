package com.scott.schedule.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据验证结果类
 *
 * @author mazhenpeng02
 * @since 2025/8/29
 */
@Data
public class DataValidationResult {

    /**
     * 课程数量
     */
    private int courseCount;

    /**
     * 教师数量
     */
    private int teacherCount;

    /**
     * 教室数量
     */
    private int classroomCount;

    /**
     * 时间段数量
     */
    private int timeSlotCount;

    /**
     * 学生数量
     */
    private int studentCount;

    /**
     * 错误列表
     */
    private List<String> errors = new ArrayList<>();

    /**
     * 警告列表
     */
    private List<String> warnings = new ArrayList<>();

    /**
     * 添加错误
     *
     * @param error 错误信息
     */
    public void addError(String error) {
        errors.add(error);
    }

    /**
     * 添加警告
     *
     * @param warning 警告信息
     */
    public void addWarning(String warning) {
        warnings.add(warning);
    }

    /**
     * 是否验证通过
     *
     * @return 验证结果
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * 获取验证摘要
     *
     * @return 验证摘要
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("数据验证结果:\n");
        sb.append(String.format("- 课程: %d, 教师: %d, 教室: %d, 时间段: %d, 学生: %d\n",
                courseCount, teacherCount, classroomCount, timeSlotCount, studentCount));
        sb.append(String.format("- 错误: %d, 警告: %d\n", errors.size(), warnings.size()));

        if (!errors.isEmpty()) {
            sb.append("错误详情:\n");
            for (String error : errors) {
                sb.append("  - ").append(error).append("\n");
            }
        }

        if (!warnings.isEmpty()) {
            sb.append("警告详情:\n");
            for (String warning : warnings) {
                sb.append("  - ").append(warning).append("\n");
            }
        }

        return sb.toString();
    }
}

