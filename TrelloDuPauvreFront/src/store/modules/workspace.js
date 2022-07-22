import trelloDuPauvreApiService from "@/services/api/trelloDuPauvreApiService"
import utils from "@/utils/utils"
export default {
    namespaced: true,
    state: {
        allWorkspaces: [],
    },
    getters: {

    },
    mutations: {
        setAllWorkspaces(state, allWorkspaces){
            state.allWorkspaces = allWorkspaces
        },
    },
    actions: {
        async getAllWorkspaces({commit}) {
            commit("setAllWorkspaces", await trelloDuPauvreApiService.get("/workspaces" ))
        },
        async deleteWorkspaceById(none, workspaceId ){
            await trelloDuPauvreApiService.delete("/workspaces/" + workspaceId)
        },
        async createWorkspace(none, data) {
            const newWorkspace = {
                name: data.name,
                iconName: data.iconName,
                creationDate: utils.formatDateTime(new Date()),
            }
            await trelloDuPauvreApiService.post("/workspaces", newWorkspace)
        },
        async updateStory(none, {workspaceId, name, creationDate}) {
            const newWorkspace = {
                name: name,
                creationDate: creationDate,
            }
            await trelloDuPauvreApiService.put("/workspaces/" + workspaceId , newWorkspace)
        },
    },  
}
