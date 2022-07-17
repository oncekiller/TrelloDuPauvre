import trelloDuPauvreApiService from "@/services/api/trelloDuPauvreApiService"
import utils from "@/utils/utils"
export default {
    namespaced: true,
    state: {
       projectStories: []
    },
    getters: {

    },
    mutations: {
        setProjectStories(state, projectStories) {
            return state.projectStories = projectStories
        }
    },
    actions: {
        async getStoriesByProjectId({commit, rootState}, projectId) {
            let projectStories = await trelloDuPauvreApiService.get("/project/"+ projectId + "/stories")
            projectStories = projectStories.map(story => {
                return {
                    storyId: story.storyId,
                    name: story.name,
                    frontIndex: story.frontIndex,
                    creationDate: story.creationDate,
                    projectId: story.project?.projectId
                }
            })
            if(rootState.filterStore?.sortType == "date"){
                projectStories.sort((a, b) => {return a.creationDate - b.creationDate})
            }else {
                projectStories.sort((a, b) => {return a.frontIndex - b.frontIndex})
            }
            commit("setProjectStories", projectStories)
            return projectStories
        },
        async deleteStoryById(none, storyId ){
            await trelloDuPauvreApiService.delete("/stories/" + storyId)
        },
        async createStory(none, {name, projectId, frontIndex}) {
            const newStory = {
                name: name,
                frontIndex: frontIndex,
                creationDate: utils.formatDateTime(new Date()),
                project: {
                    projectId: projectId
                }
            }
            await trelloDuPauvreApiService.post("/stories", newStory)
        },
        async updateStory(none, {name, projectId, storyId, frontIndex, creationDate}) {
            const newStory = {
                name: name,
                frontIndex: frontIndex,
                creationDate: creationDate,
                project: {
                    projectId: projectId
                }
            }
            await trelloDuPauvreApiService.put("/stories/" + storyId , newStory)
        },
        async moveStory(none, {storyId, projectId, frontIndex}) {
            await trelloDuPauvreApiService.put("/stories/" + storyId + "/move?projectId=" + projectId + "&frontIndex=" + frontIndex)
        },
        async updateStories(none, stories) { 
            await trelloDuPauvreApiService.put("/stories/multiple" , stories)
        }
    },  
}
