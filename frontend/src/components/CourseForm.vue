<template>
  <Dialog v-model:open="open">
    <DialogTrigger as-child>
      <Button>
        <PlusCircle class="h-4 w-4 mr-2" />
        添加课程
      </Button>
    </DialogTrigger>
    <DialogContent class="sm:max-w-[425px]">
      <DialogHeader>
        <DialogTitle>添加新课程</DialogTitle>
      </DialogHeader>
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="space-y-2">
          <Label for="name">课程名称</Label>
          <Input
            id="name"
            v-model="formData.name"
            required
          />
        </div>
        <div class="space-y-2">
          <Label for="code">课程代码</Label>
          <Input
            id="code"
            v-model="formData.code"
            required
          />
        </div>
        <div class="space-y-2">
          <Label for="hours">课时数</Label>
          <Input
            id="hours"
            v-model="formData.hours"
            type="number"
            required
          />
        </div>
        <div class="space-y-2">
          <Label for="grade">适用年级</Label>
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

const emit = defineEmits(['add-course'])

const open = ref(false)
const formData = ref({
  name: '',
  code: '',
  hours: '',
  grade: ''
})

const handleSubmit = () => {
  emit('add-course', { ...formData.value, id: Date.now().toString() })
  open.value = false
  formData.value = {
    name: '',
    code: '',
    hours: '',
    grade: ''
  }
}
</script>
