import { ref } from 'vue'

const resumeRefreshKey = ref(0)
const interviewRefreshKey = ref(0)
const blacklistRefreshKey = ref(0)

export function useRecruitmentSync() {
  const triggerResumeRefresh = () => {
    resumeRefreshKey.value++
  }

  const triggerInterviewRefresh = () => {
    interviewRefreshKey.value++
  }

  const triggerBlacklistRefresh = () => {
    blacklistRefreshKey.value++
  }

  const triggerAllRefresh = () => {
    resumeRefreshKey.value++
    interviewRefreshKey.value++
    blacklistRefreshKey.value++
  }

  return {
    resumeRefreshKey,
    interviewRefreshKey,
    blacklistRefreshKey,
    triggerResumeRefresh,
    triggerInterviewRefresh,
    triggerBlacklistRefresh,
    triggerAllRefresh
  }
}
