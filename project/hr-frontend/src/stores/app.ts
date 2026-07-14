import { defineStore } from 'pinia'

interface TagItem {
  path: string
  title: string
  name: string
}

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebarCollapsed: false,
    activeMenu: '',
    activeSubMenu: '',
    tagsViewList: [] as TagItem[]
  }),

  actions: {
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },

    addTagView(tag: TagItem) {
      if (!this.tagsViewList.find(t => t.path === tag.path)) {
        this.tagsViewList.push(tag)
      }
    },

    removeTagView(path: string) {
      const index = this.tagsViewList.findIndex(t => t.path === path)
      if (index > -1) {
        this.tagsViewList.splice(index, 1)
      }
    },

    clearTagView() {
      this.tagsViewList = []
    }
  }
})