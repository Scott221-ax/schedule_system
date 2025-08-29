// 模拟API调用

// 获取课程列表
export const fetchCourses = async () => {
  // 模拟API延迟
  await new Promise(resolve => setTimeout(resolve, 500))
  
  return [
    { id: "1", name: "语文", code: "YW", hours: 5, grade: "高一" },
    { id: "2", name: "数学", code: "SX", hours: 5, grade: "高一" },
    { id: "3", name: "英语", code: "YY", hours: 4, grade: "高一" },
    { id: "4", name: "物理", code: "WL", hours: 3, grade: "高一" },
    { id: "5", name: "化学", code: "HX", hours: 3, grade: "高一" },
    { id: "6", name: "生物", code: "SW", hours: 2, grade: "高一" },
    { id: "7", name: "历史", code: "LS", hours: 2, grade: "高一" },
    { id: "8", name: "地理", code: "DL", hours: 2, grade: "高一" },
    { id: "9", name: "政治", code: "ZZ", hours: 2, grade: "高一" },
  ]
}

// 获取教师列表
export const fetchTeachers = async () => {
  // 模拟API延迟
  await new Promise(resolve => setTimeout(resolve, 500))
  
  return [
    { id: "1", name: "张老师", code: "T001", courses: ["语文"], contact: "13800138001" },
    { id: "2", name: "李老师", code: "T002", courses: ["数学"], contact: "13800138002" },
    { id: "3", name: "王老师", code: "T003", courses: ["英语"], contact: "13800138003" },
    { id: "4", name: "赵老师", code: "T004", courses: ["物理"], contact: "13800138004" },
    { id: "5", name: "刘老师", code: "T005", courses: ["化学"], contact: "13800138005" },
    { id: "6", name: "陈老师", code: "T006", courses: ["生物"], contact: "13800138006" },
    { id: "7", name: "杨老师", code: "T007", courses: ["历史"], contact: "13800138007" },
    { id: "8", name: "周老师", code: "T008", courses: ["地理"], contact: "13800138008" },
    { id: "9", name: "吴老师", code: "T009", courses: ["政治"], contact: "13800138009" },
  ]
}

// 获取班级列表
export const fetchClasses = async () => {
  // 模拟API延迟
  await new Promise(resolve => setTimeout(resolve, 500))
  
  return [
    { id: "1", name: "高一(1)班", grade: "高一", headTeacher: "张老师", studentCount: 45 },
    { id: "2", name: "高一(2)班", grade: "高一", headTeacher: "李老师", studentCount: 42 },
    { id: "3", name: "高一(3)班", grade: "高一", headTeacher: "王老师", studentCount: 40 },
    { id: "4", name: "高一(4)班", grade: "高一", headTeacher: "赵老师", studentCount: 43 },
    { id: "5", name: "高一(5)班", grade: "高一", headTeacher: "刘老师", studentCount: 41 },
  ]
}

// 获取课程表
export const fetchSchedule = async () => {
  // 模拟API延迟
  await new Promise(resolve => setTimeout(resolve, 500))
  
  return [
    { id: "1", day: 0, period: 0, courseId: "1", teacherId: "1", classId: "1" },
    { id: "2", day: 0, period: 1, courseId: "2", teacherId: "2", classId: "1" },
    { id: "3", day: 1, period: 0, courseId: "3", teacherId: "3", classId: "1" },
    { id: "4", day: 1, period: 1, courseId: "4", teacherId: "4", classId: "1" },
    { id: "5", day: 2, period: 0, courseId: "5", teacherId: "5", classId: "1" },
    { id: "6", day: 2, period: 1, courseId: "1", teacherId: "1", classId: "1" },
    { id: "7", day: 3, period: 0, courseId: "2", teacherId: "2", classId: "1" },
    { id: "8", day: 3, period: 1, courseId: "3", teacherId: "3", classId: "1" },
    { id: "9", day: 4, period: 0, courseId: "4", teacherId: "4", classId: "1" },
    { id: "10", day: 4, period: 1, courseId: "5", teacherId: "5", classId: "1" },
  ]
}
