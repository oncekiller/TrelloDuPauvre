import { createStore } from 'vuex'
import commonStore from './modules/common'
import ticketStore from './modules/ticket'
import storyStore from './modules/story'
import projectStore from './modules/project'
import statusStore from './modules/status'
import natureStore from './modules/nature'
import filterStore from './modules/filter'
import imageStore from './modules/image'
import workspaceStore from './modules/workspace'

const store = createStore({
    modules: {
        commonStore,
        ticketStore,
        storyStore,
        projectStore,
        statusStore,
        natureStore,
        filterStore,
        imageStore,
        workspaceStore
    }
})

export default store