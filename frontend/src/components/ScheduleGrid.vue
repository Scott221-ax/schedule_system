<template>
  <div class="overflow-x-auto">
    <table class="w-full border-collapse">
      <thead>
        <tr>
          <th class="border p-2 bg-gray-100">时间</th>
          <th v-for="(day, index) in days" :key="index" class="border p-2 bg-gray-100">{{ day }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(period, periodIndex) in periods" :key="period.id">
          <td class="border p-2 bg-gray-50">
            <div class="font-medium">{{ period.name }}</div>
            <div class="text-xs text-gray-500">{{ period.time }}</div>
          </td>
          <td v-for="(_, dayIndex) in days" :key="dayIndex" class="border p-1 h-20">
            <template v-if="getScheduleItem(dayIndex, periodIndex)">
              <Card class="h-full bg-blue-50 border-blue-200">
                <CardContent class="p-2 h-full flex flex-col justify-between">
                  <div>
                    <div class="font-medium text-sm">{{ getCourseName(scheduleItem.courseId) }}</div>
                    <div class="text-xs text-gray-600">{{ getTeacherName(scheduleItem.teacherId) }}</div>
                  </div>
                  <div class="text-xs text-gray-500">{{ getClassRoom(scheduleItem.classId) }}</div>
                </CardContent>
              </Card>
            </template>
            <template v-else>
              <div class="h-full flex items-center justify-center">
                <Button variant="ghost" size="sm" class="h-8 w-8 p-0">
                  <PlusCircle class="h-4 w-4" />
                </Button>
              </div>
            </template>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { PlusCircle } from 'lucide-vue-next'

const props = defineProps({
  schedule: {
    type: Array,
    default: () => []
  },
  courses: {
    type: Array,
    default: () => []
  },
  teachers: {
    type: Array,
    default: () => []
  },
  classes: {
    type: Array,
    default: () => []
  }
})

const days = ["周一", "周二", "周三", "周四", "周五"]
const periods = [
  { id: 1, name: "第一节", time: "08:00-08:45" },
  { id: 2, name: "第二节", time: "08:55-09:40" },
  { id: 3, name: "第三节", time: "10:00-10:45" },
  { id: 4, name: "第四节", time: "10:55-11:40" },
  { id: 5, name: "第五节", time: "14:00-14:45" },
  { id: 6, name: "第六节", time: "14:55-15:40" },
  { id: 7, name: "第七节", time: "16:00-16:45" },
  { id: 8, name: "第八节", time: "16:55-17:40" },
]

// 模拟课程表数据
const scheduleData = [
  { day: 0, period: 0, courseId: "1", teacherId: "1", classId: "1" },
  { day: 0, period: 1, courseId: "2", teacherId: "2", classId: "1" },
  { day: 1, period: 0, courseId: "3", teacherId: "3", classId: "1" },
  { day: 1, period: 1, courseId: "4", teacherId: "4", classId: "1" },
  { day: 2, period: 0, courseId: "5", teacherId: "5", classId: "1" },
  { day: 2, period: 1, courseId: "1", teacherId: "1", classId: "1" },
  { day: 3, period: 0, courseId: "2", teacherId: "2", classId: "1" },
  { day: 3, period: 1, courseId: "3", teacherId: "3", classId: "1" },
  { day: 4, period: 0, courseId: "4", teacherId: "4", classId: "1" },
  { day: 4, period: 1, courseId: "5", teacherId: "5", classId: "1" },
]

const getCourseName = (courseId) => {
  const course = props.courses.find(c => c.id === courseId)
  return course ? course.name : "未安排"
}

const getTeacherName = (teacherId) => {
  const teacher = props.teachers.find(t => t.id === teacherId)
  return teacher ? teacher.name : ""
}

const getClassRoom = (classId) => {
  const cls = props.classes.find(c => c.id === classId)
  return cls ? cls.name : ""
}

const getScheduleItem = (day, period) => {
  return scheduleData.find(item => item.day === day && item.period === period)
}
</script>
