import { useState } from "react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { PlusCircle, Trash2, Save, Calendar, Users, BookOpen, Clock, Settings, Clock3 } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { fetchCourses, fetchTeachers, fetchClasses, fetchSchedule } from "../api/scheduleApi";
import ScheduleGrid from "../components/ScheduleGrid";
import CourseForm from "../components/CourseForm";
import TeacherForm from "../components/TeacherForm";
import ClassForm from "../components/ClassForm";
import ConstraintForm from "../components/ConstraintForm";
import { Switch } from "@/components/ui/switch";
import { TimeSettingsForm } from "../components/TimeSettingsForm";

const Index = () => {
  const [activeTab, setActiveTab] = useState("schedule");
  const [constraints, setConstraints] = useState([
    { id: 1, type: "教师时间冲突", content: "同一教师不能在同一时间上多节课", priority: "高", enabled: true },
    { id: 2, type: "教室冲突", content: "同一教室不能在同一时间安排多节课", priority: "高", enabled: true },
    { id: 3, type: "课程连续性", content: "同一课程尽量安排在连续的时间段", priority: "中", enabled: true },
  ]);

  const { data: courses = [] } = useQuery({
    queryKey: ['courses'],
    queryFn: fetchCourses,
  });

  const { data: teachers = [] } = useQuery({
    queryKey: ['teachers'],
    queryFn: fetchTeachers,
  });

  const { data: classes = [] } = useQuery({
    queryKey: ['classes'],
    queryFn: fetchClasses,
  });

  const { data: schedule = [] } = useQuery({
    queryKey: ['schedule'],
    queryFn: fetchSchedule,
  });

  const toggleConstraint = (id) => {
    setConstraints(prev => 
      prev.map(constraint => 
        constraint.id === id 
          ? { ...constraint, enabled: !constraint.enabled } 
          : constraint
      )
    );
  };

  return (
    <div className="container mx-auto py-8">
      <h1 className="text-3xl font-bold mb-6">学校排课系统</h1>
      
      <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
        <TabsList className="grid grid-cols-6 mb-8">
          <TabsTrigger value="schedule" className="flex items-center gap-2">
            <Calendar className="h-4 w-4" />
            <span>课程表</span>
          </TabsTrigger>
          <TabsTrigger value="courses" className="flex items-center gap-2">
            <BookOpen className="h-4 w-4" />
            <span>课程管理</span>
          </TabsTrigger>
          <TabsTrigger value="teachers" className="flex items-center gap-2">
            <Users className="h-4 w-4" />
            <span>教师管理</span>
          </TabsTrigger>
          <TabsTrigger value="classes" className="flex items-center gap-2">
            <Clock className="h-4 w-4" />
            <span>班级管理</span>
          </TabsTrigger>
          <TabsTrigger value="constraints" className="flex items-center gap-2">
            <Settings className="h-4 w-4" />
            <span>排课约束</span>
          </TabsTrigger>
          <TabsTrigger value="time-settings" className="flex items-center gap-2">
            <Clock3 className="h-4 w-4" />
            <span>时间设置</span>
          </TabsTrigger>
        </TabsList>
        
        <TabsContent value="schedule">
          <Card>
            <CardHeader>
              <CardTitle>课程表</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="mb-4 flex justify-between items-center">
                <div className="flex gap-4">
                  <div className="w-48">
                    <Label htmlFor="class-select">选择班级</Label>
                    <Select defaultValue="all">
                      <SelectTrigger id="class-select">
                        <SelectValue placeholder="选择班级" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="all">全部班级</SelectItem>
                        {classes.map(cls => (
                          <SelectItem key={cls.id} value={cls.id}>{cls.name}</SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="w-48">
                    <Label htmlFor="week-select">选择周次</Label>
                    <Select defaultValue="current">
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
                <Button>
                  <Save className="h-4 w-4 mr-2" />
                  保存排课
                </Button>
              </div>
              <ScheduleGrid schedule={schedule} courses={courses} teachers={teachers} classes={classes} />
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="courses">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>课程管理</CardTitle>
              <CourseForm />
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
                  {courses.map((course) => (
                    <TableRow key={course.id}>
                      <TableCell>{course.name}</TableCell>
                      <TableCell>{course.code}</TableCell>
                      <TableCell>{course.hours}</TableCell>
                      <TableCell>{course.grade}</TableCell>
                      <TableCell>
                        <Button variant="ghost" size="icon">
                          <Trash2 className="h-4 w-4 text-red-500" />
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="teachers">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>教师管理</CardTitle>
              <TeacherForm />
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
                  {teachers.map((teacher) => (
                    <TableRow key={teacher.id}>
                      <TableCell>{teacher.name}</TableCell>
                      <TableCell>{teacher.code}</TableCell>
                      <TableCell>{teacher.courses.join(', ')}</TableCell>
                      <TableCell>{teacher.contact}</TableCell>
                      <TableCell>
                        <Button variant="ghost" size="icon">
                          <Trash2 className="h-4 w-4 text-red-500" />
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="classes">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>班级管理</CardTitle>
              <ClassForm />
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
                  {classes.map((cls) => (
                    <TableRow key={cls.id}>
                      <TableCell>{cls.name}</TableCell>
                      <TableCell>{cls.grade}</TableCell>
                      <TableCell>{cls.headTeacher}</TableCell>
                      <TableCell>{cls.studentCount}</TableCell>
                      <TableCell>
                        <Button variant="ghost" size="icon">
                          <Trash2 className="h-4 w-4 text-red-500" />
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="constraints">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>排课约束条件</CardTitle>
              <ConstraintForm />
            </CardHeader>
            <CardContent>
              <div className="space-y-6">
                <div className="flex justify-end">
                  <Button className="bg-green-600 hover:bg-green-700">
                    开始自动排课
                  </Button>
                </div>
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>约束类型</TableHead>
                      <TableHead>约束内容</TableHead>
                      <TableHead>优先级</TableHead>
                      <TableHead>启用状态</TableHead>
                      <TableHead>操作</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {constraints.map((constraint) => (
                      <TableRow key={constraint.id}>
                        <TableCell>{constraint.type}</TableCell>
                        <TableCell>{constraint.content}</TableCell>
                        <TableCell>{constraint.priority}</TableCell>
                        <TableCell>
                          <Switch
                            checked={constraint.enabled}
                            onCheckedChange={() => toggleConstraint(constraint.id)}
                          />
                        </TableCell>
                        <TableCell>
                          <Button variant="ghost" size="icon">
                            <Trash2 className="h-4 w-4 text-red-500" />
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="time-settings">
          <Card>
            <CardHeader>
              <CardTitle>课程时间安排设置</CardTitle>
            </CardHeader>
            <CardContent>
              <TimeSettingsForm />
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
};

export default Index;
