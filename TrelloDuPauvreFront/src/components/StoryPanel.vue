<template>
    <v-container class="storyPanel">
        <v-row>
            <v-col>
                <v-row class="headerSection">
                    <v-col @click="handleClickTextInputName()" class="colName" cols="11">
                        <input 
                            rows="1"
                            v-on:keyup.enter="handleEnterTextInputName()"
                            v-click-outside="() => {
                                    this.textInputNameOpen = false,
                                    handleClickOutsideTextInputName()
                                }" 
                            ref="textInputName" 
                            class="textInputName" 
                            v-if="textInputNameOpen" 
                            v-model="getStoryName"/>
                        <div class="textName" v-if="!textInputNameOpen">{{getStoryName}}</div>
                    </v-col>
                    <v-col cols="1" class="text-right colSubMenu">
                        <v-icon ref="subMenuButton" @click="handleSubMenuStoryClick()">mdi-dots-horizontal</v-icon>
                    </v-col>               
                </v-row>
                <v-row class="ticketsSection">
                    <v-col>
                            <draggable 
                                v-model="ticketsData" 
                                :disabled="draggableDisabled"
                                group="tickets" 
                                @add="handleDrag" 
                                @update="handleDrag" 
                                :item-key="storyId"
                                class="draggable"
                            >
                                <TicketPanel 
                                    v-for="(ticket, key) in ticketsData"
                                    :key="key"
                                    :ref="'ticket' + ticket.ticketId"
                                    :ticketId="ticket.ticketId"
                                    :name="ticket.name"
                                    :checkListItemsNumber="ticket.checkListItems.length"
                                    :checkListItemsValidatedNumber="getCheckListItemsValidatedNumber(ticket.checkListItems)"
                                    @click="handleEditTicket(ticket.ticketId)"
                                    @handleSubMenuClickAction="handleSubMenuTicketClick"
                                />
                            </draggable>
                    </v-col>
                </v-row>
                <v-row class="footerSection">
                    <span @click="handleAddTicket()">Créer un ticket</span>
                </v-row>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import TicketPanel from './TicketPanel.vue'
    import { VueDraggableNext } from 'vue-draggable-next'
    import { mapGetters, mapState } from 'vuex'
    export default {
        components: {
            TicketPanel,
            draggable: VueDraggableNext,
        },
        props: {
            storyId: {type: Number, required: true},
            name: {type: String, default: ""},
            tickets: {type: Array, required: true, default:new Array()}
        },
        data() {
            return {
                textInputNameOpen: false,
                storyName: this.name,
                ticketList: this.tickets,
            }
        },
        watch: { 
            name: {
                handler(){this.storyName = this.name}
            },
            tickets: {
                handler(){this.ticketList = this.tickets}
            }
        },
        methods: {
            handleClickTextInputName() {
                this.textInputNameOpen = true
                this.$nextTick(() => {
                    this.$refs.textInputName.focus()
                })
            },
            handleEnterTextInputName() {
                if(this.storyName != ""){
                    this.$emit("handleUpdateStoryAction", {storyId: this.storyId, storyName: this.storyName})
                }else {
                    this.storyName = this.name
                }
                this.textInputNameOpen = false
            },
            handleClickOutsideTextInputName() {
                this.storyName = this.name
            },
            handleAddTicket() {
                this.$emit("handleAddTicketAction", this.storyId)
            },
            handleEditTicket(ticketId) {
                this.$emit("handleEditTicketAction", ticketId)
            },
            handleSubMenuStoryClick() {
                this.$emit("handleSubMenuClickAction", {type: "story", storyId: this.storyId})
            },
            handleSubMenuTicketClick(ticketId) {
                this.$emit("handleSubMenuClickAction",  {type: "ticket", storyId: this.storyId, ticketId: ticketId})
            },
            getCheckListItemsValidatedNumber(checkListItems) {
                return checkListItems.filter(checkListItem => checkListItem.checked).length
            },
            handleDrag(event) {
                this.$emit("handleDragAction", event)
            },
        },
        computed: {
            ...mapState("filterStore", ["sortType"]),
            ...mapGetters("filterStore", ["draggableDisabled"]),
            ticketsData: {
                get() {
                    return this.ticketList
                },
                set(value){
                    this.ticketList = value
                }
            },
            getStoryName: {
                get () {
                    return this.storyName
                },
                set (value) {
                    this.storyName= value
                }
            },
        },
    }
</script>

<style scoped>
    .storyPanel {
        background-color: #D9EBF1;
        border-radius: 10px;
        padding: 15px 10px;
        height: fit-content;
    }
    .storyPanel .v-col {
        padding: 0;
    }
    .storyPanel .v-row {
        margin: 0;
    }
    .storyPanel .headerSection {
        font-size: 20px;
        height: 30px;
    }
    .storyPanel .headerSection .colName {
        padding-right: 5px;
        cursor: pointer;
    }
    .storyPanel .headerSection .colName .textName {
        padding-left: 5px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }
    .storyPanel .headerSection .colName .textInputName {
        background-color: #fff;
        border-style: none;
        resize: none;
        outline: 2px #409DBB solid !important;
        border-radius: 5px;
        height: 30px;
        padding-left: 5px;
        width: 100%;
    }
    .storyPanel .headerSection .colSubMenu{
        font-size: 14px;
        color: #000;
    }    
    .storyPanel .ticketsSection .ticketPanel{
        margin-top: 7px;
        height: fit-content;
        max-height: 60px;
        cursor: pointer;
    }
    .storyPanel .footerSection {
        margin-top: 10px;
        cursor: pointer;
    }
</style>
