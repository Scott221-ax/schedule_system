<template>
  <div class="container mx-auto py-8">
    <h1 class="text-3xl font-bold mb-6">学校排课系统</h1>
    
    <Tabs v-model="activeTab" class="w-full">
      <TabsList class="grid grid-cols-4 mb-8">
        <TabsTrigger value="schedule" class="flex items-center gap-2">
          <Calendar class="h-4 w-4" />
          <span>课程表</span>
        </TabsTrigger>
        <TabsTrigger value="courses" class="flex items-center gap-2">
          <BookOpen class="h-4 w-4" />
          <span>课程管理</span>
        </TabsTrigger>
        <TabsTrigger value="teachers" class="flex items-center gap-2">
          <Users class="h-4 w-4" />
          <span>教师管理</span>
        </TabsTrigger>
        <TabsTrigger value="classes" class="flex items-center gap-2">
          <Clock class="h-4 w-4" />
          <span>班级管理</span>
        </TabsTrigger>
      </TabsList>
      
      <TabsContent value="schedule">
        <Card>
          <CardHeader>
            <CardTitle>课程表</CardTitle>
          </CardHeader>
          <CardContent>
            <div class="mb-4 flex justify-between items-center">
              <div class="flex gap-4">
                <div class="w-48">
                  <Label for="class-select">选择班级</Label>
                  <Select v-model="selectedClass" default-value="all">
                    <SelectTrigger id="class-select">
                      <SelectValue placeholder="选择班级" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">全部班级</SelectItem>
                      <SelectItem v-for="cls in classes" :key="cls.id" :value="cls.id">{{ cls.name }}</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <div class="w-48">
                  <Label for="week-select">选择周次</Label>
                  <Select v-model="selectedWeek" default-value="current">
                    <SelectTrigger id="week-select">
                      <SelectValue placeholder="选择周次" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="current">本周</SelectItem>
                      <SelectItem value="next">下周</SelectItem>
                      <SelectItem value="week1">第1周</SelectItem>
                      <SelectItem value="week2">第2周</SelectItem>
                      <SelectItem value="week3">第3周</SelectItem>
                      <SelectItem value="week4">第4周</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
              <Button @click="saveSchedule">
                <Save class="h-4 w-4 mr-2" />
                保存排课
              </Button>
            </div>
            <ScheduleGrid :schedule="schedule" :courses="courses" :teachers="teachers" :classes="classes" />
          </CardContent>
        </Card>
      </TabsContent>
      
      <TabsContent value="courses">
        <Card>
          <CardHeader class="flex flex-row items-center justify-between">
            <CardTitle>课程管理</CardTitle>
            <CourseForm @add-course="addCourse" />
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>课程名称</TableHead>
                  <TableHead>课程代码</TableHead>
                  <TableHead>课时数</TableHead>
                  <TableHead>适用年级</TableHead>
                  <TableHead>操作</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow v-for="course in courses" :key="course.id">
                  <TableCell>{{ course.name }}</TableCell>
                  <TableCell>{{ course.code }}</TableCell>
                  <TableCell>{{ course.hours }}</TableCell>
                  <TableCell>{{ course.grade }}</TableCell>
                  <TableCell>
                    <Button variant="ghost" size="icon" @click="deleteCourse(course.id)">
                      <Trash2 class="h-4 w-4 text-red-500" />
                    </Button>
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </TabsContent>
      
      <TabsContent value="teachers">
        <Card>
          <CardHeader class="flex flex-row items-center justify-between">
            <CardTitle>教师管理</CardTitle>
            <TeacherForm @add-teacher="addTeacher" />
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>姓名</TableHead>
                  <TableHead>工号</TableHead>
                  <TableHead>教授课程</TableHead>
                  <TableHead>联系方式</TableHead>
                  <TableHead>操作</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow v-for="teacher in teachers" :key="teacher.id">
                  <TableCell>{{ teacher.name }}</TableCell>
                  <TableCell>{{ teacher.code }}</TableCell>
                  <TableCell>{{ teacher.courses.join(', ') }}</TableCell>
                  <TableCell>{{ teacher.contact }}</TableCell>
                  <TableCell>
                    <Button variant="ghost" size="icon" @click="deleteTeacher(teacher.id)">
                      <Trash2 class="h-4 w-4 text-red-500" />
                    </Button>
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </TabsContent>
      
      <TabsContent value="classes">
        <Card>
          <CardHeader class="flex flex-row items-center justify-between">
            <CardTitle>班级管理</CardTitle>
            <ClassForm @add-class="addClass" />
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>班级名称</TableHead>
                  <TableHead>年级</TableHead>
                  <TableHead>班主任</TableHead>
                  <TableHead>学生人数</TableHead>
                  <TableHead>操作</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <TableRow v-for="cls in classes" :key="cls.id">
                  <TableCell>{{ cls.name }}</TableCell>
                  <TableCell>{{ cls.grade }}</TableCell>
                  <TableCell>{{ cls.headTeacher }}</TableCell>
                  <TableCell>{{ cls.studentCount }}</TableCell>
                  <TableCell>
                    <Button variant="ghost" size="icon" @click="deleteClass(cls.id)">
                      <Trash2 class="h-4 w-4 text-red-500" />
                    </Button>
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </TabsContent>
    </Tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { PlusCircle, Trash2, Save, Calendar, Users, BookOpen, Clock } from 'lucide-vue-next'
import { useQuery } from '@tanstack/vue-query'
import { fetchCourses, fetchTeachers, fetchClasses, fetchSchedule } from '../api/scheduleApi'
import ScheduleGrid from '../components/ScheduleGrid.vue'
import CourseForm from '../components/CourseForm.vue'
import TeacherForm from '../components/TeacherForm.vue'
import ClassForm from '../components/ClassForm.vue'

const activeTab = ref('schedule')
const selectedClass = ref('all')
const selectedWeek = ref('current')

const { data: courses = [] } = useQuery({
  queryKey: ['courses'],
  queryFn: fetchCourses,
})

const { data: teachers = [] } = useQuery({
  queryKey: ['teachers'],
  queryFn: fetchTeachers,
})

const { data: classes = [] } = useQuery({
  queryKey: ['classes'],
  queryFn: fetchClasses,
})

const { data: schedule = [] } = useQuery({
  queryKey: ['schedule'],
  queryFn: fetchSchedule,
})

const saveSchedule = () => {
  console.log('保存排课')
}

const addCourse = (course) => {
  console.log('添加课程:', course)
}

const deleteCourse = (id) => {
  console.log('删除课程:', id)
}

const addTeacher = (teacher) => {
  console.log('添加教师:', teacher)
}

const deleteTeacher = (id) => {
  console.log('删除教师:', id)
}

const addClass = (cls) => {
  console.log('添加班级:', cls)
}

const deleteClass = (id) => {
  console.log('删除班级:', id)
}
</script>
