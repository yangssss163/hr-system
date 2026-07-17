<template>
  <div class="schedule-manage">
    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button-group>
            <el-button :type="viewMode === 'month' ? 'primary' : ''" @click="switchView('month')">月</el-button>
            <el-button :type="viewMode === 'week' ? 'primary' : ''" @click="switchView('week')">周</el-button>
          </el-button-group>
          <el-button @click="goToday">今天</el-button>
          <el-button @click="goPrev"><el-icon><ArrowLeft /></el-icon></el-button>
          <el-button @click="goNext"><el-icon><ArrowRight /></el-icon></el-button>
          <span class="title-text">{{ currentTitle }}</span>
        </div>
        <el-button v-permission="'office:schedule:create'" type="primary" @click="handleAdd">新建日程</el-button>
      </div>

      <div v-loading="scheduleLoading" class="calendar-wrapper">
        <!-- 月视图 -->
        <div v-if="viewMode === 'month'" class="month-calendar">
          <div class="calendar-header">
            <div v-for="d in 7" :key="d" class="header-cell">{{ ['一','二','三','四','五','六','日'][d-1] }}</div>
          </div>
          <div class="calendar-grid">
            <div v-for="(cell, idx) in monthCells" :key="idx" :class="['day-cell', { 'is-other': !cell.isCurrentMonth, 'is-today': cell.isToday }]" @click="handleDayClick(cell)">
              <div class="day-num">{{ cell.day }}</div>
              <div class="day-events">
                <div
                  v-for="evt in cell.events.slice(0, 3)"
                  :key="evt.id"
                  :class="['event-chip']"
                  :style="{ background: evt.color || '#409EFF' }"
                  @click.stop="handleEditEvent(evt)"
                  :title="evt.title"
                >{{ evt.title }}</div>
                <div v-if="cell.events.length > 3" class="event-more">+{{ cell.events.length - 3 }} 更多</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 周视图 -->
        <div v-else class="week-calendar">
          <div class="week-header">
            <div class="week-time-col" />
            <div v-for="d in weekDays" :key="d.day" :class="['week-header-cell', { 'is-today': d.isToday }]">
              <div class="week-day-name">{{ d.name }}</div>
              <div class="week-day-num">{{ d.day }}</div>
            </div>
          </div>
          <div class="week-body">
            <div v-for="h in 24" :key="h" class="hour-row">
              <div class="hour-label">{{ String(h - 1).padStart(2, '0') }}:00</div>
              <div v-for="(d, di) in weekDays" :key="di" class="hour-cell" @click="handleWeekCellClick(d.date, h - 1)" />
            </div>
            <div class="week-events-layer">
              <div
                v-for="evt in weekEvents"
                :key="'we'+evt.id"
                class="week-event"
                :style="getWeekEventStyle(evt)"
                @click="handleEditEvent(evt)"
              >
                <div class="week-event-title">{{ evt.title }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 新建/编辑 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑日程' : '新建日程'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="开始时间" prop="startTime"><el-date-picker v-model="form.startTime" type="datetime" style="width:100%" /></el-form-item>
        <el-form-item label="结束时间" prop="endTime"><el-date-picker v-model="form.endTime" type="datetime" style="width:100%" /></el-form-item>
        <el-form-item label="全天"><el-switch v-model="form.allDay" :active-value="1" :inactive-value="0" /></el-form-item>
        <el-form-item label="颜色标识"><el-color-picker v-model="form.color" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button v-if="isEdit" type="danger" plain @click="handleDeleteFromEdit">删除</el-button>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { scheduleApi } from '@/api/modules/office'
import type { Schedule, ScheduleForm } from '@/api/types'

interface CalendarCell { date: string; day: number; isCurrentMonth: boolean; isToday: boolean; events: Schedule[] }
interface WeekDay { name: string; day: number; date: string; isToday: boolean }

const userStore = useUserStore()
const viewMode = ref<'month' | 'week'>('month')
const currentDate = ref(new Date())
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editId = ref(0)
const scheduleLoading = ref(false)
const schedules = ref<Schedule[]>([])
const weekNames = ['日','一','二','三','四','五','六']

const defaultForm = (): ScheduleForm => ({ userId: userStore.userInfo?.userId || 0, title: '', content: '', startTime: '', endTime: '', allDay: 0, color: '#409EFF' })
const form = reactive<ScheduleForm>(defaultForm())
const rules = {
  title: [{ required: true, message: '请输入日程标题', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const currentTitle = computed(() => {
  const y = currentDate.value.getFullYear(); const m = currentDate.value.getMonth() + 1
  if (viewMode.value === 'month') return `${y}年${m}月`
  const start = weekDays.value[0]; const end = weekDays.value[6]
  return `${start.date?.slice(5)} ~ ${end.date?.slice(5)}`
})

const switchView = (mode: 'month' | 'week') => { viewMode.value = mode; loadSchedules() }
const goToday = () => { currentDate.value = new Date(); loadSchedules() }
const goPrev = () => {
  if (viewMode.value === 'month') currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1, 1)
  else currentDate.value = new Date(currentDate.value.getTime() - 7 * 86400000)
  loadSchedules()
}
const goNext = () => {
  if (viewMode.value === 'month') currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 1)
  else currentDate.value = new Date(currentDate.value.getTime() + 7 * 86400000)
  loadSchedules()
}

const padDate = (d: Date) => `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
const padDateTime = (d: Date) => `${padDate(d)} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}:${String(d.getSeconds()).padStart(2,'0')}`

// 月视图
const monthCells = computed<CalendarCell[]>(() => {
  const y = currentDate.value.getFullYear(); const m = currentDate.value.getMonth()
  const firstDay = new Date(y, m, 1).getDay() || 7
  const daysInMonth = new Date(y, m + 1, 0).getDate()
  const daysInPrevMonth = new Date(y, m, 0).getDate()
  const today = padDate(new Date())
  const cells: CalendarCell[] = []
  for (let i = firstDay - 1; i > 0; i--) {
    const date = padDate(new Date(y, m - 1, daysInPrevMonth - i + 1))
    cells.push({ date, day: daysInPrevMonth - i + 1, isCurrentMonth: false, isToday: date === today, events: getEventsForDate(date) })
  }
  for (let d = 1; d <= daysInMonth; d++) {
    const date = padDate(new Date(y, m, d))
    cells.push({ date, day: d, isCurrentMonth: true, isToday: date === today, events: getEventsForDate(date) })
  }
  const remaining = 42 - cells.length
  for (let d = 1; d <= remaining; d++) {
    const date = padDate(new Date(y, m + 1, d))
    cells.push({ date, day: d, isCurrentMonth: false, isToday: date === today, events: getEventsForDate(date) })
  }
  return cells
})

const getEventsForDate = (date: string) => schedules.value.filter(s => {
  const start = s.startTime?.slice(0, 10) || ''; const end = s.endTime?.slice(0, 10) || ''
  return date >= start && date <= (end || start)
})

// 周视图
const weekDays = computed<WeekDay[]>(() => {
  const today = padDate(new Date())
  const days: WeekDay[] = []
  const startOfWeek = new Date(currentDate.value)
  const dayOfWeek = startOfWeek.getDay()
  startOfWeek.setDate(startOfWeek.getDate() - (dayOfWeek === 0 ? 6 : dayOfWeek - 1))
  for (let i = 0; i < 7; i++) {
    const d = new Date(startOfWeek.getTime() + i * 86400000)
    const date = padDate(d)
    days.push({ name: weekNames[d.getDay()], day: d.getDate(), date, isToday: date === today })
  }
  return days
})

const weekEvents = computed(() => schedules.value.filter(s => {
  const start = s.startTime?.slice(0, 10) || ''; const end = s.endTime?.slice(0, 10) || ''
  return start <= weekDays.value[6].date && (end || start) >= weekDays.value[0].date
}))

const getWeekEventStyle = (evt: Schedule) => {
  const startDate = evt.startTime?.slice(0, 10) || ''
  const startHour = parseInt(evt.startTime?.slice(11, 13) || '0')
  const startMin = parseInt(evt.startTime?.slice(14, 16) || '0')
  const topPct = ((startHour * 60 + startMin) / 1440) * 100
  const dayIdx = weekDays.value.findIndex(d => d.date === startDate)
  const leftPct = dayIdx >= 0 ? (60 / 900) * 100 + (dayIdx / 7) * ((900 - 60) / 900) * 100 : 0
  return {
    top: `${topPct}%`,
    left: `calc(60px + ${dayIdx >= 0 ? (dayIdx / 7) * 100 : 0}%)`,
    width: `calc(${100 / 7}% - 2px)`,
    background: evt.color || '#409EFF'
  }
}

// 数据加载
const loadSchedules = async () => {
  scheduleLoading.value = true
  try {
    const isAdmin = userStore.permissions?.includes('admin')
    const params: Record<string, any> = { pageSize: 9999 }
    if (viewMode.value === 'month') {
      const y = currentDate.value.getFullYear(); const m = currentDate.value.getMonth()
      params.startTime = padDate(new Date(y, m - 1, 25))
      params.endTime = padDate(new Date(y, m + 1, 7))
    }
    if (!isAdmin) params.userId = userStore.userInfo?.userId
    const res = await scheduleApi.list(params)
    schedules.value = res.data?.records || []
  } catch { schedules.value = [] }
  finally { scheduleLoading.value = false }
}

const handleDayClick = (cell: CalendarCell) => {
  Object.assign(form, { ...defaultForm(), startTime: `${cell.date} 09:00:00`, endTime: `${cell.date} 10:00:00` })
  isEdit.value = false; editId.value = 0; dialogVisible.value = true
}
const handleWeekCellClick = (date: string, hour: number) => {
  Object.assign(form, { ...defaultForm(), startTime: `${date} ${String(hour).padStart(2,'0')}:00:00`, endTime: `${date} ${String(hour + 1).padStart(2,'0')}:00:00` })
  isEdit.value = false; editId.value = 0; dialogVisible.value = true
}
const handleAdd = () => { Object.assign(form, defaultForm()); isEdit.value = false; editId.value = 0; dialogVisible.value = true }
const handleEditEvent = (evt: Schedule) => {
  isEdit.value = true; editId.value = evt.id
  Object.assign(form, { userId: evt.userId, title: evt.title, content: evt.content, startTime: evt.startTime, endTime: evt.endTime, allDay: evt.allDay, color: evt.color })
  dialogVisible.value = true
}
const handleDeleteFromEdit = async () => {
  if (!editId.value) return
  await ElMessageBox.confirm('确定删除此日程？')
  await scheduleApi.delete(editId.value)
  ElMessage.success('删除成功'); dialogVisible.value = false; loadSchedules()
}
const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    if (isEdit.value) await scheduleApi.update(editId.value, form as any)
    else await scheduleApi.create(form as any)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadSchedules()
  })
}

onMounted(() => loadSchedules())
</script>

<style lang="scss" scoped>
.schedule-manage {
  .toolbar { margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 8px; }
  .toolbar-left { display: flex; align-items: center; gap: 8px; }
  .title-text { font-size: 16px; font-weight: 600; margin-left: 8px; }
  .calendar-wrapper { min-height: 200px; }

  .month-calendar { border: 1px solid #e5e7eb; border-radius: 8px; overflow: hidden; }
  .calendar-header, .calendar-grid { display: grid; grid-template-columns: repeat(7, 1fr); }
  .header-cell { padding: 10px 0; text-align: center; font-weight: 600; font-size: 13px; background: #f8fafc; border-bottom: 1px solid #e5e7eb; border-right: 1px solid #f0f0f0; &:last-child { border-right: none; } }
  .day-cell {
    min-height: 100px; padding: 4px 6px; border-right: 1px solid #f0f0f0; border-bottom: 1px solid #f0f0f0; cursor: pointer;
    &:nth-child(7n) { border-right: none; }
    &:hover { background: #f8fafc; }
    &.is-other { background: #fafafa; .day-num { color: #ccc; } }
    &.is-today { background: #eff6ff; .day-num { background: #1d4ed8; color: #fff; border-radius: 50%; width: 24px; height: 24px; display: inline-flex; align-items: center; justify-content: center; } }
  }
  .day-num { font-size: 13px; font-weight: 500; margin-bottom: 2px; display: inline-block; padding: 2px 4px; }
  .day-events { display: flex; flex-direction: column; gap: 2px; margin-top: 4px; }
  .event-chip { padding: 2px 6px; border-radius: 3px; color: #fff; font-size: 11px; line-height: 1.3; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; cursor: pointer; }
  .event-more { font-size: 11px; color: #999; padding: 0 4px; }

  .week-calendar { border: 1px solid #e5e7eb; border-radius: 8px; overflow: hidden; }
  .week-header { display: grid; grid-template-columns: 60px repeat(7, 1fr); background: #f8fafc; border-bottom: 1px solid #e5e7eb; }
  .week-time-col { border-right: 1px solid #e5e7eb; }
  .week-header-cell { padding: 8px 0; text-align: center; font-size: 12px; border-right: 1px solid #e5e7eb;
    &:last-child { border-right: none; }
    &.is-today { background: #eff6ff; .week-day-num { background: #1d4ed8; color: #fff; border-radius: 50%; } }
  }
  .week-day-name { font-weight: 500; }
  .week-day-num { display: inline-block; width: 28px; height: 28px; line-height: 28px; font-size: 14px; font-weight: 600; margin-top: 2px; }
  .week-body { position: relative; max-height: 650px; overflow-y: auto; }
  .hour-row { display: grid; grid-template-columns: 60px repeat(7, 1fr); border-bottom: 1px solid #f0f0f0; position: relative; }
  .hour-label { padding: 0 8px; font-size: 11px; color: #999; text-align: right; line-height: 40px; border-right: 1px solid #f0f0f0; }
  .hour-cell { height: 40px; border-right: 1px solid #f0f0f0; cursor: pointer;
    &:hover { background: #f0f4ff; }
    &:last-child { border-right: none; }
  }
  .week-events-layer { position: absolute; top: 0; left: 60px; right: 0; bottom: 0; pointer-events: none; }
  .week-event { position: absolute; z-index: 2; padding: 2px 6px; border-radius: 3px; color: #fff; font-size: 11px; overflow: hidden; cursor: pointer; opacity: 0.85; pointer-events: auto;
    &:hover { opacity: 1; z-index: 3; }
  }
  .week-event-title { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
}
</style>
