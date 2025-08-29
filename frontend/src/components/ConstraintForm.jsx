import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { PlusCircle } from "lucide-react";
import { Switch } from "@/components/ui/switch";

const ConstraintForm = () => {
  const [open, setOpen] = useState(false);
  const [formData, setFormData] = useState({
    type: "",
    content: "",
    priority: "中",
    enabled: true
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSelectChange = (name, value) => {
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSwitchChange = (checked) => {
    setFormData(prev => ({ ...prev, enabled: checked }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // 这里可以添加提交逻辑
    console.log("提交约束条件:", formData);
    setOpen(false);
    // 重置表单
    setFormData({
      type: "",
      content: "",
      priority: "中",
      enabled: true
    });
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <PlusCircle className="h-4 w-4 mr-2" />
          添加约束
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>添加排课约束</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="type">约束类型</Label>
            <Select
              value={formData.type}
              onValueChange={(value) => handleSelectChange("type", value)}
            >
              <SelectTrigger>
                <SelectValue placeholder="选择约束类型" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="教师时间冲突">教师时间冲突</SelectItem>
                <SelectItem value="教室冲突">教室冲突</SelectItem>
                <SelectItem value="课程连续性">课程连续性</SelectItem>
                <SelectItem value="教师偏好">教师偏好</SelectItem>
                <SelectItem value="课程间隔">课程间隔</SelectItem>
                <SelectItem value="其他">其他</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="space-y-2">
            <Label htmlFor="content">约束内容</Label>
            <Input
              id="content"
              name="content"
              value={formData.content}
              onChange={handleChange}
              placeholder="详细描述约束条件"
              required
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="priority">优先级</Label>
            <Select
              value={formData.priority}
              onValueChange={(value) => handleSelectChange("priority", value)}
            >
              <SelectTrigger>
                <SelectValue placeholder="选择优先级" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="高">高</SelectItem>
                <SelectItem value="中">中</SelectItem>
                <SelectItem value="低">低</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="flex items-center justify-between">
            <Label htmlFor="enabled">启用约束</Label>
            <Switch
              id="enabled"
              checked={formData.enabled}
              onCheckedChange={handleSwitchChange}
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

export default ConstraintForm;
