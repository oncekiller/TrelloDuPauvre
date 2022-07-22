import trelloDuPauvreApiService from "@/services/api/trelloDuPauvreApiService"
import utils from "@/utils/utils"

export default {
    namespaced: true,
    state: {
        selectedProject: {},
        allProjects: []
    },
    getters: {

    },
    mutations: {
        setSelectedProject(state, selectedProject){
            state.selectedProject = selectedProject
        },
        setAllProjects(state, allProjects){
            state.allProjects = allProjects
        }
    },
    actions: {
        async getAllProjects({commit}) {
            let projects =  await trelloDuPauvreApiService.get("/projects")
            projects = projects.map(project => {
                return {
                    projectId: project.projectId,
                    name: project.name,
                    isFavorite: project.isFavorite,
                    creationDate: project.creationDate,
                    lastConsultationDate: project.lastConsultationDate,
                    bgColor: project.bgColor,
                    bgImageId: project.bgImage?.imageId,
                    workspaceId: project.workspace?.workspaceId
                }
            })
            commit("setAllProjects", projects)
        },
        updateFieldSelectedProject({state, commit}, {fieldName, fieldValue}){
            commit("setSelectedProject", {...state.selectedProject, [fieldName]: fieldValue} )
        },
        async createUpdateSelectedProject({state}, type) {
            const newCreationDate = type == "create" ? utils.formatDateTime(new Date()) : state.selectedProject?.creationDate
            const newLastConsultationDate = type == "create" ? utils.formatDateTime(new Date()) : state.selectedProject?.lastConsultationDate
            const workspace = state.selectedProject?.workspaceId 
                ? 
                    {
                        workspaceId: state.selectedProject?.workspaceId 
                    }
                : undefined
            let newProject = {
                name: state.selectedProject?.name,
                isFavorite: state.selectedProject?.isFavorite,
                creationDate: newCreationDate,
                lastConsultationDate: newLastConsultationDate,
                bgColor: state.selectedProject?.bgColor,
                workspace: workspace
            }
            const bgImageId = state.selectedProject?.bgImageId
            if(bgImageId) {
                newProject = {...newProject, bgImage: {
                    imageId: bgImageId
                }}
            }
            //Create or Update Ticket
            if(type == "create"){
                await trelloDuPauvreApiService.post("/projects", newProject)
            }
            if(type == "update"){
                await trelloDuPauvreApiService.put("/projects/" + state.selectedProject?.projectId, newProject)
            }
        },
        async updateProjectFavorite(none, {projectId, isFavorite}){
            await trelloDuPauvreApiService.put("/projects/" + projectId + "/favorite?isFavorite=" + isFavorite)
        },
        async deleteProjectById(none, projectId ){
            await trelloDuPauvreApiService.delete("/projects/" + projectId)
        },
    },  
}
