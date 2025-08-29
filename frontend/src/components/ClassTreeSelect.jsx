import { useState } from "react";
import { Check, ChevronDown, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

const ClassTreeSelect = ({ classes, selectedClasses, onSelectionChange }) => {
  const [open, setOpen] = useState(false);
  const [expandedGrades, setExpandedGrades] = useState({});

  // 按年级分组班级
  const classesByGrade = classes.reduce((acc, cls) => {
    if (!acc[cls.grade]) {
      acc[cls.grade] = [];
    }
    acc[cls.grade].push(cls);
    return acc;
  }, {});

  const toggleGrade = (grade) => {
    setExpandedGrades(prev => ({
      ...prev,
      [grade]: !prev[grade]
    }));
  };

  const toggleClass = (classId) => {
    const newSelection = selectedClasses.includes(classId)
      ? selectedClasses.filter(id => id !== classId)
      : [...selectedClasses, classId];
    onSelectionChange(newSelection);
  };

  const toggleAllInGrade = (grade) => {
    const gradeClasses = classesByGrade[grade].map(cls => cls.id);
    const allSelected = gradeClasses.every(id => selectedClasses.includes(id));
    
    if (allSelected) {
      // 取消选择该年级所有班级
      onSelectionChange(selectedClasses.filter(id => !gradeClasses.includes(id)));
    } else {
      // 选择该年级所有班级
      const newSelection = [...new Set([...selectedClasses, ...gradeClasses])];
      onSelectionChange(newSelection);
    }
  };

  const getSelectedClassNames = () => {
    return selectedClasses.map(id => {
      const cls = classes.find(c => c.id === id);
      return cls ? cls.name : "";
    }).filter(Boolean);
  };

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          role="combobox"
          aria-expanded={open}
          className="w-full justify-between"
        >
          <span className="truncate">
            {selectedClasses.length > 0
              ? `${selectedClasses.length} 个班级已选择`
              : "选择班级"}
          </span>
          <ChevronDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-[300px] p-0" align="start">
        <ScrollArea className="h-[300px]">
          <div className="p-2">
            {Object.entries(classesByGrade).map(([grade, gradeClasses]) => (
              <div key={grade} className="mb-2">
                <div
                  className="flex items-center cursor-pointer py-1 px-2 hover:bg-gray-100 rounded"
                  onClick={() => toggleGrade(grade)}
                >
                  {expandedGrades[grade] ? (
                    <ChevronDown className="h-4 w-4 mr-1" />
                  ) : (
                    <ChevronRight className="h-4 w-4 mr-1" />
                  )}
                  <div
                    className="flex items-center flex-1"
                    onClick={(e) => {
                      e.stopPropagation();
                      toggleAllInGrade(grade);
                    }}
                  >
                    <div className="mr-2">
                      {gradeClasses.every(cls => selectedClasses.includes(cls.id)) ? (
                        <Check className="h-4 w-4 text-primary" />
                      ) : (
                        <div className="h-4 w-4 border rounded" />
                      )}
                    </div>
                    <span className="font-medium">{grade}</span>
                  </div>
                </div>
                {expandedGrades[grade] && (
                  <div className="ml-6 mt-1 space-y-1">
                    {gradeClasses.map((cls) => (
                      <div
                        key={cls.id}
                        className="flex items-center py-1 px-2 hover:bg-gray-100 rounded cursor-pointer"
                        onClick={() => toggleClass(cls.id)}
                      >
                        <div className="mr-2">
                          {selectedClasses.includes(cls.id) ? (
                            <Check className="h-4 w-4 text-primary" />
                          ) : (
                            <div className="h-4 w-4 border rounded" />
                          )}
                        </div>
                        <span>{cls.name}</span>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        </ScrollArea>
        <div className="p-2 border-t">
          <div className="flex flex-wrap gap-1">
            {getSelectedClassNames().map((name) => (
              <Badge key={name} variant="secondary" className="text-xs">
                {name}
              </Badge>
            ))}
          </div>
        </div>
      </PopoverContent>
    </Popover>
  );
};

export default ClassTreeSelect;
