import trelloDuPauvreApiService from "@/services/api/trelloDuPauvreApiService"
export default {
    namespaced: true,
    state: {
        statusList: [],
    },
    getters: {

    },
    mutations: {
        setStatusList(state, statusList){
            state.statusList = statusList
        },
    },
    actions: {
        async getAllStatus({commit}) {
            commit("setStatusList", await trelloDuPauvreApiService.get("/status" ))
        },
    },  
}
