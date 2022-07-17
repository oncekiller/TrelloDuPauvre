import trelloDuPauvreApiService from "@/services/api/trelloDuPauvreApiService"
export default {
    namespaced: true,
    state: {
        natureList: [],
    },
    getters: {

    },
    mutations: {
        setNatureList(state, natureList){
            state.natureList = natureList
        },
    },
    actions: {
        async getAllNatures({commit}) {
            commit("setNatureList", await trelloDuPauvreApiService.get("/natures" ))
        },
    },  
}
