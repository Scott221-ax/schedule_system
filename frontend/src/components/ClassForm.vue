<template>
  <Dialog v-model:open="open">
    <DialogTrigger as-child>
      <Button>
        <PlusCircle class="h-4 w-4 mr-2" />
        添加班级
      </Button>
    </DialogTrigger>
    <DialogContent class="sm:max-w-[425px]">
      <DialogHeader>
        <DialogTitle>添加新班级</DialogTitle>
      </DialogHeader>
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="space-y-2">
          <Label for="name">班级名称</Label>
          <Input
            id="name"
            v-model="formData.name"
            required
          />
        </div>
        <div class="space-y-2">
          <Label for="grade">年级</Label>
          <Select v-model="formData.grade">
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
        <div class="space-y-2">
          <Label for="headTeacher">班主任</Label>
          <Input
            id="headTeacher"
            v-model="formData.headTeacher"
            required
          />
        </div>
        <div class="space-y-2">
          <Label for="studentCount">学生人数</Label>
          <Input
            id="studentCount"
            v-model="formData.studentCount"
            type="number"
            required
          />
        </div>
        <div class="flex justify-end">
          <Button type="submit">保存</Button>
        </div>
      </form>
    </DialogContent>
  </Dialog>
</template>

<script setup>
import { ref } from 'vue'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
import { PlusCircle } from 'lucide-vue-next'

const emit = defineEmits(['add-class'])

const open = ref(false)
const formData = ref({
  name: '',
  grade: '',
  headTeacher: '',
  studentCount: ''
})

const handleSubmit = () => {
  emit('add-class', { ...formData.value, id: Date.now().toString() })
  open.value = false
  formData.value = {
    name: '',
    grade: '',
    headTeacher: '',
    studentCount: ''
  }
}
</script>
