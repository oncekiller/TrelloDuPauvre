import trelloDuPauvreApiService from "@/services/api/trelloDuPauvreApiService"
import utils from "@/utils/utils"
export default {
    namespaced: true,
    state: {
        projectTickets: [],
        filteredProjectTickets:[],
        selectedTicket: {},
    },
    getters: {

    },
    mutations: {
        setProjectTickets(state, projectTickets){
            state.projectTickets = projectTickets
        },
        setFilteredProjectTickets(state, filteredProjectTickets){
            state.filteredProjectTickets = filteredProjectTickets
        },
        setSelectedTicket(state, selectedTicket){
            state.selectedTicket = selectedTicket
        },
    },
    actions: {
        updateFieldSelectedTicket({state, commit}, {fieldName, fieldValue}){
            commit("setSelectedTicket", {...state.selectedTicket, [fieldName]: fieldValue} )
        },
        async getTicketsFromProjectIdMappedByStory({commit, rootState}, projectId) {
            let projectTickets = await trelloDuPauvreApiService.get("/project/" + projectId + "/tickets" )

            //Sort Stories
            if(rootState.filterStore?.sortType == "date"){
                projectTickets?.sort((a, b) => {return a.creationDate - b.creationDate})
            }else {
                projectTickets?.sort((a, b) => {return a.frontIndex - b.frontIndex})
            }

            //Map tickets and sort stories
            projectTickets = projectTickets?.map(story => {
                let tickets = story.tickets
                tickets = tickets.map(ticket => {
                    return {
                        ticketId: ticket.ticketId,
                        name: ticket.name,
                        description: ticket.description,
                        priority: ticket.priority,
                        natureId: ticket.nature?.natureId,
                        statusId: ticket.status?.statusId,
                        deadLine: ticket.deadLine,
                        checkListItems: ticket.checkListItems,
                        frontIndex: ticket.frontIndex,
                        creationDate: ticket.creationDate
                    }
                })
                if(rootState.filterStore?.sortType == "date"){
                    tickets?.sort((a, b) => {return a.creationDate - b.creationDate})
                }else {
                    tickets?.sort((a, b) => {return a.frontIndex - b.frontIndex})
                }
                return {...story, tickets: tickets }
            })
            commit("setProjectTickets", projectTickets )
        },
        async getTicketsFromStoryId( none, storyId){
            let tickets = await trelloDuPauvreApiService.get("/story/" + storyId + "/tickets" )
            tickets=  tickets.map(ticket => {
                    return {
                        ticketId: ticket.ticketId,
                        name: ticket.name,
                        description: ticket.description,
                        priority: ticket.priority,
                        natureId: ticket.nature?.natureId,
                        statusId: ticket.status?.statusId,
                        deadLine: ticket.deadLine,
                        checkListItems: ticket.checkListItems,
                        frontIndex: ticket.frontIndex,
                        creationDate: ticket.creationDate
                    }
            })
            return tickets
        },
        async createUpdateSelectedTicket({state}, type){
            const newCreationDate = type == "create" ? utils.formatDateTime(new Date()) : state.selectedTicket?.creationDate
            let ticketId = state.selectedTicket?.ticketId
            const newTicket =  {
                    name : state.selectedTicket?.name,
                    description : state.selectedTicket?.description,
                    priority: state.selectedTicket?.priority,
                    deadLine : state.selectedTicket?.deadLine,
                    creationDate: newCreationDate,
                    modificationDate: utils.formatDateTime(new Date()),
                    nature: {
                        natureId: state.selectedTicket?.natureId
                    },
                    status: {
                        statusId: state.selectedTicket?.statusId
                    },
                    project: {
                        projectId: state.selectedTicket?.projectId
                    },
                    story: {
                        storyId: state.selectedTicket?.storyId
                    },
                    frontIndex: state.selectedTicket?.frontIndex
                }
            
            //Create or Update Ticket
            if(type == "create"){
                const ticket = await trelloDuPauvreApiService.post("/tickets", newTicket )
                //Retrieve ticketId of the created ticket
                ticketId = ticket.ticketId
            }
            if(type == "update"){
                await trelloDuPauvreApiService.put("/tickets/" + ticketId, newTicket ) 
            }
            
            //Delete Update or Create CheckListItems
            await Promise.all([
                await state.selectedTicket?.checkListItems.map(async (checkListItem) => {
                    const newCheckListItem = {
                        checkListItemId: checkListItem.checkListItemId,
                        label: checkListItem.label,
                        checked: checkListItem.checked,
                        ticket: {
                            ticketId: ticketId
                        }
                    }
                    if(checkListItem.updatedStatus == "deleted"){
                        //Deleted
                        await trelloDuPauvreApiService.delete("/checkListItems/" + checkListItem.checkListItemId)
                    }
                    if(checkListItem.updatedStatus == "updated"){
                        //Updated
                        await trelloDuPauvreApiService.put("/checkListItems/" + checkListItem.checkListItemId, newCheckListItem )
                    }
                    if(checkListItem.updatedStatus == "created"){
                        //Created
                        await trelloDuPauvreApiService.post("/checkListItems", newCheckListItem )
                    } 
                }),
                
            ])
        },
        async getTicketById(none, ticketId){
            const ticket = await trelloDuPauvreApiService.get("/tickets/" + ticketId)
            return {
                ticketId: ticket.ticketId,
                name: ticket.name,
                description: ticket.description,
                priority: ticket.priority,
                natureId: ticket.nature?.natureId,
                statusId: ticket.status?.statusId,
                deadLine: ticket.deadLine,
                creationDate: ticket.creationDate,
                modificationDate: ticket.modificationDate,
                projectId: ticket.project?.projectId,
                storyId: ticket.story?.storyId,
                checkListItems: ticket.checkListItems.map(checkListItem => {return {...checkListItem, updatedStatus: null}})
            }
        },
        async deleteTicketById(none, ticketId ){
            await trelloDuPauvreApiService.delete("/tickets/" + ticketId)
        },
        async updateTickets(none, tickets) { 
            await trelloDuPauvreApiService.put("/tickets/multiple" , tickets)
        },
    },  
}
