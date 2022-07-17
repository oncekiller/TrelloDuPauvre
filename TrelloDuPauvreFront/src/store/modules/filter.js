export default {
    namespaced: true,
    state: {
        filterKeyWord: "",
        filterNature: undefined,
        filterStatus: undefined,
        filterDelay: false,
        filterDeadline: undefined,
        filterPriorityRangeActif: false,
        filterPriorityRange: [2,8],
        sortType: "ordre"
    },
    getters: {
        draggableDisabled(state) {
            return state.sortType != "ordre"
        }
    },
    mutations: {
        setFilterKeyWord(state, filterKeyWord){
            state.filterKeyWord = filterKeyWord
        },
        setFilterNature(state, filterNature){
            state.filterNature = filterNature
        },
        setFilterStatus(state, filterStatus){
            state.filterStatus = filterStatus
        },
        setFilterDelay(state, filterDelay){
            state.filterDelay = filterDelay
        },
        setFilterDeadline(state, filterDeadline){
            state.filterDeadline = filterDeadline
        },
        setFilterPriorityRangeActif(state, filterPriorityRangeActif){
            state.filterPriorityRangeActif = filterPriorityRangeActif
        },
        setFilterPriorityRange(state, filterPriorityRange){
            state.filterPriorityRange = filterPriorityRange
        },
        setSortType(state, sortType){
            state.sortType = sortType
        },
    },
    actions: {
        filterProjectTickets({state, rootState, commit}) {
            let projectTickets = rootState.ticketStore.projectTickets

            //KeyWords filter
            if(state.filterKeyWord != "") {
                projectTickets = projectTickets.map((projectStory) => {
                    const include = projectStory.tickets.some((ticket) => {
                        return ticket.name.includes(state.filterKeyWord) //|| ticket.description.includes(state.filterKeyWord)
                    })
                    if(include) {
                        const tickets = projectStory.tickets.map((ticket) => {
                            if(ticket.name.includes(state.filterKeyWord) /*|| ticket.description.includes(state.filterKeyWord)*/){
                                return {... ticket}
                            }
                        }).filter((ticket) => {
                            return ticket != undefined
                        })
                        return {...projectStory, tickets: tickets }
                    }
                })
                .filter((projectStory) => {
                    return projectStory != undefined
                })
            }

            //Nature filter
            if(state.filterNature) {
                projectTickets = projectTickets.map((projectStory) => {
                    const include = projectStory.tickets.some((ticket) => {
                        return ticket.natureId == state.filterNature
                    })
                    if(include) {
                        const tickets = projectStory.tickets.map((ticket) => {
                            if(ticket.natureId == state.filterNature){
                                return {... ticket}
                            }
                        }).filter((ticket) => {
                            return ticket != undefined
                        })
                        return {...projectStory, tickets: tickets }
                    }
                })
                .filter((projectStory) => {
                    return projectStory != undefined
                })
            }

            //status filter
            if(state.filterStatus) {
                projectTickets = projectTickets.map((projectStory) => {
                    const include = projectStory.tickets.some((ticket) => {
                        return ticket.statusId == state.filterStatus
                    })
                    if(include) {
                        const tickets = projectStory.tickets.map((ticket) => {
                            if(ticket.statusId == state.filterStatus){
                                return {... ticket}
                            }
                        }).filter((ticket) => {
                            return ticket != undefined
                        })
                        return {...projectStory, tickets: tickets }
                    }
                })
                .filter((projectStory) => {
                    return projectStory != undefined
                })
            }

            //Delay and deadline filter
            if(state.filterDelay){
                const actualDate = new Date()
                projectTickets = projectTickets.map((projectStory) => {
                    const include = projectStory.tickets.some((ticket) => {
                        return ticket.deadLine != null && new Date(ticket.deadLine) < actualDate
                    })
                    if(include) {
                        console.log(projectStory)
                        const tickets = projectStory.tickets.map((ticket) => {
                            if(ticket.deadLine != null && new Date(ticket.deadLine) < actualDate){
                                return {... ticket}
                            }
                        }).filter((ticket) => {
                            return ticket != undefined
                        })
                        return {...projectStory, tickets: tickets }
                    }
                })
                .filter((projectStory) => {
                    return projectStory != undefined
                })
            }else {
                if(state.filterDeadline) {
                    const comparedFilterDeadline = new Date(state.filterDeadline)
                    projectTickets = projectTickets.map((projectStory) => {
                        const include = projectStory.tickets.some((ticket) => {
                            const ticketDate = new Date(ticket.deadLine)
                            return ticket.deadLine != null && ticketDate.toString() == comparedFilterDeadline.toString()
                        })
                        if(include) {
                            const tickets = projectStory.tickets.map((ticket) => {
                                const ticketDate = new Date(ticket.deadLine)
                                if(ticket.deadLine != null && ticketDate.toString() == comparedFilterDeadline.toString()){
                                    return {... ticket}
                                }
                            }).filter((ticket) => {
                                return ticket != undefined
                            })
                            return {...projectStory, tickets: tickets }
                        }
                    })
                    .filter((projectStory) => {
                        return projectStory != undefined
                    })
                }
            }

            //Filter priority
            if(state.filterPriorityRangeActif){
                projectTickets = projectTickets.map((projectStory) => {
                    const include = projectStory.tickets.some((ticket) => {
                        return state.filterPriorityRange[0] <= ticket.priority 
                        && state.filterPriorityRange[1] >= ticket.priority
                    })
                    if(include) {
                        const tickets = projectStory.tickets.map((ticket) => {
                            if(state.filterPriorityRange[0] <= ticket.priority 
                                && state.filterPriorityRange[1] >= ticket.priority){
                                return {... ticket}
                            }
                        }).filter((ticket) => {
                            return ticket != undefined
                        })
                        return {...projectStory, tickets: tickets }
                    }
                })
                .filter((projectStory) => {
                    return projectStory != undefined
                })
            }

            commit("ticketStore/setFilteredProjectTickets", projectTickets, { root: true })
        }
    }
}