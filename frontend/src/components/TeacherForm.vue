<template>
  <Dialog v-model:open="open">
    <DialogTrigger as-child>
      <Button>
        <PlusCircle class="h-4 w-4 mr-2" />
        添加教师
      </Button>
    </DialogTrigger>
    <DialogContent class="sm:max-w-[425px]">
      <DialogHeader>
        <DialogTitle>添加新教师</DialogTitle>
      </DialogHeader>
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="space-y-2">
          <Label for="name">姓名</Label>
          <Input
            id="name"
            v-model="formData.name"
            required
          />
        </div>
        <div class="space-y-2">
          <Label for="code">工号</Label>
          <Input
            id="code"
            v-model="formData.code"
            required
          />
        </div>
        <div class="space-y-2">
          <Label for="courses">教授课程</Label>
          <Input
            id="courses"
            v-model="coursesInput"
            placeholder="用逗号分隔多个课程"
            required
          />
        </div>
        <div class="space-y-2">
          <Label for="contact">联系方式</Label>
          <Input
            id="contact"
            v-model="formData.contact"
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
import { ref, computed } from 'vue'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
import { PlusCircle } from 'lucide-vue-next'

const emit = defineEmits(['add-teacher'])

const open = ref(false)
const formData = ref({
  name: '',
  code: '',
  courses: [],
  contact: ''
})

const coursesInput = computed({
  get: () => formData.value.courses.join(', '),
  set: (value) => {
    formData.value.courses = value.split(',').map(c => c.trim())
  }
})

const handleSubmit = () => {
  emit('add-teacher', { ...formData.value, id: Date.now().toString() })
  open.value = false
  formData.value = {
    name: '',
    code: '',
    courses: [],
    contact: ''
  }
}
</script>
