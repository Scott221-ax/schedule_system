import { useState, useRef } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { PlusCircle, Edit, Trash2, ArrowLeftRight, Check, Clock } from "lucide-react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { toast } from "sonner";
import { motion, AnimatePresence } from "framer-motion";
import { TimeSettingsForm } from "./TimeSettingsForm";

const days = ["周一", "周二", "周三", "周四", "周五"];
const periods = [
  { id: 1, name: "第一节", time: "08:00-08:45" },
  { id: 2, name: "第二节", time: "08:55-09:40" },
  { id: 3, name: "第三节", time: "10:00-10:45" },
  { id: 4, name: "第四节", time: "10:55-11:40" },
  { id: 5, name: "第五节", time: "14:00-14:45" },
  { id: 6, name: "第六节", time: "14:55-15:40" },
  { id: 7, name: "第七节", time: "16:00-16:45" },
  { id: 8, name: "第八节", time: "16:55-17:40" },
];

const ScheduleGrid = ({ schedule, courses, teachers, classes }) => {
  const [selectedClass, setSelectedClass] = useState("all");
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [formData, setFormData] = useState({
    courseId: "",
    teacherId: "",
    classId: ""
  });
  const [scheduleData, setScheduleData] = useState([
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
  ]);
  const [swapMode, setSwapMode] = useState(false);
  const [firstSlot, setFirstSlot] = useState(null);
  const [secondSlot, setSecondSlot] = useState(null);
  const [showConfirmDialog, setShowConfirmDialog] = useState(false);
  const [animateSwap, setAnimateSwap] = useState(false);
  const [flipAnimation, setFlipAnimation] = useState(false);
  const [showTimeSettings, setShowTimeSettings] = useState(false);

  const getCourseName = (courseId) => {
    const course = courses.find(c => c.id === courseId);
    return course ? course.name : "未安排";
  };

  const getTeacherName = (teacherId) => {
    const teacher = teachers.find(t => t.id === teacherId);
    return teacher ? teacher.name : "";
  };

  const getClassRoom = (classId) => {
    const cls = classes.find(c => c.id === classId);
    return cls ? cls.name : "";
  };

  const getScheduleItem = (day, period) => {
    return scheduleData.find(item => item.day === day && item.period === period);
  };

  const handleAddClick = (day, period) => {
    setSelectedSlot({ day, period });
    setFormData({
      courseId: "",
      teacherId: "",
      classId: ""
    });
    setOpenDialog(true);
  };

  const handleEditClick = (day, period, item) => {
    setSelectedSlot({ day, period });
    setFormData({
      courseId: item.courseId,
      teacherId: item.teacherId,
      classId: item.classId
    });
    setOpenDialog(true);
  };

  const handleDeleteClick = (day, period) => {
    setScheduleData(prev => prev.filter(item => !(item.day === day && item.period === period)));
  };

  const handleFormChange = (name, value) => {
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const { day, period } = selectedSlot;
    
    // 检查是否已存在该时间段的数据
    const existingIndex = scheduleData.findIndex(item => item.day === day && item.period === period);
    
    if (existingIndex >= 0) {
      // 更新现有数据
      const newData = [...scheduleData];
      newData[existingIndex] = { ...formData, day, period };
      setScheduleData(newData);
    } else {
      // 添加新数据
      setScheduleData(prev => [...prev, { ...formData, day, period }]);
    }
    
    setOpenDialog(false);
  };

  const handleSlotClick = (day, period) => {
    if (!swapMode) {
      // 进入交换模式，选择第一个课程
      setSwapMode(true);
      setFirstSlot({ day, period });
      setSecondSlot(null);
      toast.info("请选择要交换的课程");
    } else if (!secondSlot) {
      // 选择第二个课程
      if (firstSlot.day === day && firstSlot.period === period) {
        // 选择了同一个课程，取消交换
        setSwapMode(false);
        setFirstSlot(null);
        setSecondSlot(null);
        toast.info("已取消交换");
        return;
      }
      
      setSecondSlot({ day, period });
      setShowConfirmDialog(true);
    }
  };

  const confirmSwap = () => {
    if (!firstSlot || !secondSlot) return;
    
    // 立即关闭确认对话框
    setShowConfirmDialog(false);
    
    // 执行翻转动画
    setFlipAnimation(true);
    
    // 动画完成后执行实际交换
    setTimeout(() => {
      const firstItem = getScheduleItem(firstSlot.day, firstSlot.period);
      const secondItem = getScheduleItem(secondSlot.day, secondSlot.period);
      
      if (firstItem && secondItem) {
        // 两个时间段都有课程，交换它们
        const newData = [...scheduleData];
        
        // 找到并更新第一个课程
        const firstIndex = newData.findIndex(item => item.day === firstSlot.day && item.period === firstSlot.period);
        if (firstIndex >= 0) {
          newData[firstIndex] = { ...secondItem, day: firstSlot.day, period: firstSlot.period };
        }
        
        // 找到并更新第二个课程
        const secondIndex = newData.findIndex(item => item.day === secondSlot.day && item.period === secondSlot.period);
        if (secondIndex >= 0) {
          newData[secondIndex] = { ...firstItem, day: secondSlot.day, period: secondSlot.period };
        }
        
        setScheduleData(newData);
      } else if (firstItem) {
        // 只有第一个时间段有课程，移动到第二个时间段
        const newData = [...scheduleData];
        const firstIndex = newData.findIndex(item => item.day === firstSlot.day && item.period === firstSlot.period);
        if (firstIndex >= 0) {
          newData[firstIndex] = { ...firstItem, day: secondSlot.day, period: secondSlot.period };
        }
        setScheduleData(newData);
      } else if (secondItem) {
        // 只有第二个时间段有课程，移动到第一个时间段
        const newData = [...scheduleData];
        const secondIndex = newData.findIndex(item => item.day === secondSlot.day && item.period === secondSlot.period);
        if (secondIndex >= 0) {
          newData[secondIndex] = { ...secondItem, day: firstSlot.day, period: firstSlot.period };
        }
        setScheduleData(newData);
      }
      
      setFlipAnimation(false);
      setSwapMode(false);
      setFirstSlot(null);
      setSecondSlot(null);
      toast.success("课程交换成功");
    }, 1000); // 动画持续时间
  };

  const cancelSwap = () => {
    setSwapMode(false);
    setFirstSlot(null);
    setSecondSlot(null);
    setShowConfirmDialog(false);
    toast.info("已取消交换");
  };

  const handleTimeClick = (periodIndex) => {
    setShowTimeSettings(true);
  };

  return (
    <div className="overflow-x-auto">
      {swapMode && (
        <div className="mb-4 p-2 bg-blue-50 border border-blue-200 rounded-md flex items-center justify-between">
          <span className="text-blue-700">
            交换模式: 已选择 {days[firstSlot.day]} {periods[firstSlot.period].name}，请选择要交换的课程
          </span>
          <Button variant="outline" size="sm" onClick={cancelSwap}>
            取消交换
          </Button>
        </div>
      )}
      
      <table className="w-full border-collapse">
        <thead>
          <tr>
            <th className="border p-2 bg-gray-100">时间</th>
            {days.map((day, index) => (
              <th key={index} className="border p-2 bg-gray-100">{day}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {periods.map((period, periodIndex) => (
            <tr key={period.id}>
              <td 
                className="border p-2 bg-gray-50 cursor-pointer hover:bg-gray-100"
                onClick={() => handleTimeClick(periodIndex)}
              >
                <div className="flex items-center justify-between">
                  <div>
                    <div className="font-medium">{period.name}</div>
                    <div className="text-xs text-gray-500">{period.time}</div>
                  </div>
                  <Clock className="h-4 w-4 text-gray-400" />
                </div>
              </td>
              {days.map((_, dayIndex) => {
                const scheduleItem = getScheduleItem(dayIndex, periodIndex);
                const isFirstSlot = swapMode && firstSlot && firstSlot.day === dayIndex && firstSlot.period === periodIndex;
                const isSecondSlot = swapMode && secondSlot && secondSlot.day === dayIndex && secondSlot.period === periodIndex;
                const isAnimating = flipAnimation && (
                  (firstSlot && firstSlot.day === dayIndex && firstSlot.period === periodIndex) ||
                  (secondSlot && secondSlot.day === dayIndex && secondSlot.period === periodIndex)
                );
                
                return (
                  <td 
                    key={dayIndex} 
                    className={`border p-1 h-20 ${isFirstSlot ? 'bg-blue-100' : ''} ${isSecondSlot ? 'bg-green-100' : ''}`}
                  >
                    <AnimatePresence mode="wait">
                      {scheduleItem ? (
                        <motion.div
                          key={`${dayIndex}-${periodIndex}-${scheduleItem.courseId}`}
                          initial={{ opacity: 0, scale: 0.8 }}
                          animate={{ 
                            opacity: 1, 
                            scale: 1,
                            rotateY: isAnimating ? 180 : 0
                          }}
                          exit={{ opacity: 0, scale: 0.8 }}
                          transition={{ 
                            duration: isAnimating ? 1 : 0.3,
                            type: "spring",
                            stiffness: 200,
                            damping: 25
                          }}
                          className="h-full"
                        >
                          <Card 
                            className={`h-full ${isFirstSlot ? 'bg-blue-100 border-blue-300' : isSecondSlot ? 'bg-green-100 border-green-300' : 'bg-blue-50 border-blue-200'} cursor-pointer`}
                            onClick={() => handleSlotClick(dayIndex, periodIndex)}
                          >
                            <CardContent className="p-2 h-full flex flex-col justify-between">
                              <div>
                                <div className="font-medium text-sm">{getCourseName(scheduleItem.courseId)}</div>
                                <div className="text-xs text-gray-600">{getTeacherName(scheduleItem.teacherId)}</div>
                              </div>
                              <div className="text-xs text-gray-500">{getClassRoom(scheduleItem.classId)}</div>
                              <div className="flex justify-end gap-1 mt-1">
                                <Button 
                                  variant="ghost" 
                                  size="icon" 
                                  className="h-6 w-6 p-0"
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    handleEditClick(dayIndex, periodIndex, scheduleItem);
                                  }}
                                >
                                  <Edit className="h-3 w-3" />
                                </Button>
                                <Button 
                                  variant="ghost" 
                                  size="icon" 
                                  className="h-6 w-6 p-0"
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    handleDeleteClick(dayIndex, periodIndex);
                                  }}
                                >
                                  <Trash2 className="h-3 w-3 text-red-500" />
                                </Button>
                              </div>
                            </CardContent>
                          </Card>
                        </motion.div>
                      ) : (
                        <motion.div 
                          key={`empty-${dayIndex}-${periodIndex}`}
                          initial={{ opacity: 0 }}
                          animate={{ opacity: 1 }}
                          exit={{ opacity: 0 }}
                          transition={{ duration: 0.3 }}
                          className="h-full flex items-center justify-center cursor-pointer hover:bg-gray-50"
                          onClick={() => handleSlotClick(dayIndex, periodIndex)}
                        >
                          <Button 
                            variant="ghost" 
                            size="sm" 
                            className="h-8 w-8 p-0"
                            onClick={(e) => {
                              e.stopPropagation();
                              handleAddClick(dayIndex, periodIndex);
                            }}
                          >
                            <PlusCircle className="h-4 w-4" />
                          </Button>
                        </motion.div>
                      )}
                    </AnimatePresence>
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>

      <Dialog open={openDialog} onOpenChange={setOpenDialog}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>{selectedSlot && getScheduleItem(selectedSlot.day, selectedSlot.period) ? "编辑课程" : "添加课程"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="course">课程</Label>
              <Select
                value={formData.courseId}
                onValueChange={(value) => handleFormChange("courseId", value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="选择课程" />
                </SelectTrigger>
                <SelectContent>
                  {courses.map(course => (
                    <SelectItem key={course.id} value={course.id}>{course.name}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="teacher">教师</Label>
              <Select
                value={formData.teacherId}
                onValueChange={(value) => handleFormChange("teacherId", value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="选择教师" />
                </SelectTrigger>
                <SelectContent>
                  {teachers.map(teacher => (
                    <SelectItem key={teacher.id} value={teacher.id}>{teacher.name}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="class">班级</Label>
              <Select
                value={formData.classId}
                onValueChange={(value) => handleFormChange("classId", value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="选择班级" />
                </SelectTrigger>
                <SelectContent>
                  {classes.map(cls => (
                    <SelectItem key={cls.id} value={cls.id}>{cls.name}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="flex justify-end">
              <Button type="submit">保存</Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>

      <Dialog open={showConfirmDialog} onOpenChange={setShowConfirmDialog}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>确认交换</DialogTitle>
          </DialogHeader>
          <div className="py-4">
            <p className="mb-4">您确定要交换以下两个课程吗？</p>
            <div className="space-y-2">
              <div className="p-2 bg-blue-50 rounded-md">
                <p className="font-medium">{days[firstSlot?.day]} {periods[firstSlot?.period]?.name}</p>
                <p>{getCourseName(getScheduleItem(firstSlot?.day, firstSlot?.period)?.courseId)}</p>
              </div>
              <div className="flex justify-center">
                <ArrowLeftRight className="h-6 w-6 text-gray-500" />
              </div>
              <div className="p-2 bg-green-50 rounded-md">
                <p className="font-medium">{days[secondSlot?.day]} {periods[secondSlot?.period]?.name}</p>
                <p>{getCourseName(getScheduleItem(secondSlot?.day, secondSlot?.period)?.courseId)}</p>
              </div>
            </div>
          </div>
          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={cancelSwap}>取消</Button>
            <Button onClick={confirmSwap}>
              <Check className="h-4 w-4 mr-2" />
              确认交换
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      <Dialog open={showTimeSettings} onOpenChange={setShowTimeSettings}>
        <DialogContent className="sm:max-w-[600px]">
          <DialogHeader>
            <DialogTitle>课程时间安排设置</DialogTitle>
          </DialogHeader>
          <TimeSettingsForm />
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default ScheduleGrid;
