import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { PlusCircle } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { fetchClasses } from "../api/scheduleApi";
import ClassTreeSelect from "./ClassTreeSelect";

const TeacherForm = () => {
  const [open, setOpen] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    courses: [],
    contact: "",
    teachableClasses: []
  });

  const { data: classes = [] } = useQuery({
    queryKey: ['classes'],
    queryFn: fetchClasses,
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleCoursesChange = (value) => {
    setFormData(prev => ({ ...prev, courses: value.split(',').map(c => c.trim()) }));
  };

  const handleTeachableClassesChange = (selectedClasses) => {
    setFormData(prev => ({ ...prev, teachableClasses: selectedClasses }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // 这里可以添加提交逻辑
    console.log("提交教师数据:", formData);
    setOpen(false);
    // 重置表单
    setFormData({
      name: "",
      courses: [],
      contact: "",
      teachableClasses: []
    });
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <PlusCircle className="h-4 w-4 mr-2" />
          添加教师
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>添加新教师</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="name">姓名</Label>
            <Input
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="courses">教授课程</Label>
            <Input
              id="courses"
              name="courses"
              value={formData.courses.join(', ')}
              onChange={(e) => handleCoursesChange(e.target.value)}
              placeholder="用逗号分隔多个课程"
              required
            />
          </div>
          <div className="space-y-2">
            <Label>可教班级</Label>
            <ClassTreeSelect
              classes={classes}
              selectedClasses={formData.teachableClasses}
              onSelectionChange={handleTeachableClassesChange}
            />
            <p className="text-xs text-gray-500">可选，留空表示可教授所有班级</p>
          </div>
          <div className="space-y-2">
            <Label htmlFor="contact">联系方式</Label>
            <Input
              id="contact"
              name="contact"
              value={formData.contact}
              onChange={handleChange}
              required
            />
          </div>
          <div className="flex justify-end">
            <Button type="submit">保存</Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default TeacherForm;
