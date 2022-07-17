export default {
    namespaced: true,
    state: {
        createHeaderDropDownOpen: false
    },
    mutations: {
        setCreateHeaderDropDownOpen(state, createHeaderDropDownOpen){
            state.createHeaderDropDownOpen = createHeaderDropDownOpen
        }
    }
}