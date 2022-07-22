export default {
    namespaced: true,
    state: {
        createHeaderDropDownOpen: false,
        alertDisplay: false,
        reloadPage: false,
    },
    mutations: {
        setCreateHeaderDropDownOpen(state, createHeaderDropDownOpen){
            state.createHeaderDropDownOpen = createHeaderDropDownOpen
        },
        setAlertDisplay(state, alertDisplay){
            state.alertDisplay = alertDisplay
        },
        setReloadPage(state, reloadPage){
            state.reloadPage = reloadPage
        }
    }
}