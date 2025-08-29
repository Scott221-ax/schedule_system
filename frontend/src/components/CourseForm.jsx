import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { PlusCircle } from "lucide-react";

const CourseForm = () => {
  const [open, setOpen] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    code: "",
    hours: "",
    grade: ""
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSelectChange = (name, value) => {
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // 这里可以添加提交逻辑
    console.log("提交课程数据:", formData);
    setOpen(false);
    // 重置表单
    setFormData({
      name: "",
      code: "",
      hours: "",
      grade: ""
    });
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <PlusCircle className="h-4 w-4 mr-2" />
          添加课程
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>添加新课程</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="name">课程名称</Label>
            <Input
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="code">课程代码</Label>
            <Input
              id="code"
              name="code"
              value={formData.code}
              onChange={handleChange}
              required
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="hours">课时数</Label>
            <Input
              id="hours"
              name="hours"
              type="number"
              value={formData.hours}
              onChange={handleChange}
              required
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="grade">适用年级</Label>
            <Select
              value={formData.grade}
              onValueChange={(value) => handleSelectChange("grade", value)}
            >
              <SelectTrigger>
                <SelectValue placeholder="选择年级" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="高一">高一</SelectItem>
                <SelectItem value="高二">高二</SelectItem>
                <SelectItem value="高三">高三</SelectItem>
                <SelectItem value="初一">初一</SelectItem>
                <SelectItem value="初二">初二</SelectItem>
                <SelectItem value="初三">初三</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="flex justify-end">
            <Button type="submit">保存</Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default CourseForm;
