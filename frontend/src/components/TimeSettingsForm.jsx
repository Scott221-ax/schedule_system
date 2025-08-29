import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { PlusCircle, Trash2, Save } from "lucide-react";
import { toast } from "sonner";

export const TimeSettingsForm = () => {
  const [periods, setPeriods] = useState([
    { id: 1, name: "第一节", startTime: "08:00", endTime: "08:45" },
    { id: 2, name: "第二节", startTime: "08:55", endTime: "09:40" },
    { id: 3, name: "第三节", startTime: "10:00", endTime: "10:45" },
    { id: 4, name: "第四节", startTime: "10:55", endTime: "11:40" },
    { id: 5, name: "第五节", startTime: "14:00", endTime: "14:45" },
    { id: 6, name: "第六节", startTime: "14:55", endTime: "15:40" },
    { id: 7, name: "第七节", startTime: "16:00", endTime: "16:45" },
    { id: 8, name: "第八节", startTime: "16:55", endTime: "17:40" },
  ]);

  const [newPeriod, setNewPeriod] = useState({
    name: "",
    startTime: "",
    endTime: ""
  });

  const handlePeriodChange = (id, field, value) => {
    setPeriods(prev => 
      prev.map(period => 
        period.id === id 
          ? { ...period, [field]: value } 
          : period
      )
    );
  };

  const handleNewPeriodChange = (field, value) => {
    setNewPeriod(prev => ({ ...prev, [field]: value }));
  };

  const addPeriod = () => {
    if (!newPeriod.name || !newPeriod.startTime || !newPeriod.endTime) {
      toast.error("请填写完整的课程信息");
      return;
    }

    const newId = Math.max(...periods.map(p => p.id)) + 1;
    setPeriods(prev => [...prev, { ...newPeriod, id: newId }]);
    setNewPeriod({ name: "", startTime: "", endTime: "" });
    toast.success("添加课程成功");
  };

  const deletePeriod = (id) => {
    setPeriods(prev => prev.filter(period => period.id !== id));
    toast.success("删除课程成功");
  };

  const saveSettings = () => {
    // 这里可以添加保存逻辑
    console.log("保存课程时间安排:", periods);
    toast.success("课程时间安排已保存");
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-end">
        <Button onClick={saveSettings}>
          <Save className="h-4 w-4 mr-2" />
          保存设置
        </Button>
      </div>
      
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>课程名称</TableHead>
            <TableHead>开始时间</TableHead>
            <TableHead>结束时间</TableHead>
            <TableHead>操作</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {periods.map((period) => (
            <TableRow key={period.id}>
              <TableCell>
                <Input
                  value={period.name}
                  onChange={(e) => handlePeriodChange(period.id, "name", e.target.value)}
                />
              </TableCell>
              <TableCell>
                <Input
                  type="time"
                  value={period.startTime}
                  onChange={(e) => handlePeriodChange(period.id, "startTime", e.target.value)}
                />
              </TableCell>
              <TableCell>
                <Input
                  type="time"
                  value={period.endTime}
                  onChange={(e) => handlePeriodChange(period.id, "endTime", e.target.value)}
                />
              </TableCell>
              <TableCell>
                <Button
                  variant="ghost"
                  size="icon"
                  onClick={() => deletePeriod(period.id)}
                >
                  <Trash2 className="h-4 w-4 text-red-500" />
                </Button>
              </TableCell>
            </TableRow>
          ))}
          <TableRow>
            <TableCell>
              <Input
                placeholder="课程名称"
                value={newPeriod.name}
                onChange={(e) => handleNewPeriodChange("name", e.target.value)}
              />
            </TableCell>
            <TableCell>
              <Input
                type="time"
                value={newPeriod.startTime}
                onChange={(e) => handleNewPeriodChange("startTime", e.target.value)}
              />
            </TableCell>
            <TableCell>
              <Input
                type="time"
                value={newPeriod.endTime}
                onChange={(e) => handleNewPeriodChange("endTime", e.target.value)}
              />
            </TableCell>
            <TableCell>
              <Button
                variant="ghost"
                size="icon"
                onClick={addPeriod}
              >
                <PlusCircle className="h-4 w-4 text-green-500" />
              </Button>
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>
  );
};
