<template>
    <v-container class="moveDropDown" v-click-outside="handleClose">
        <v-row>
            <v-col>
                <v-row class="headerRow">
                    <span>Déplacer la story</span>
                    <v-icon @click="handleClose()">mdi-close</v-icon>
                </v-row>
                <v-row class="positionSelectorRow">
                    <v-col>
                        <v-row v-if="displayProjectDropList">
                            <span>Projet</span>
                            <ActionButtonDropList 
                                :width="150" 
                                :elementsList="getProjectsList"
                                :selectedElementIndex="getProjectIndex" 
                                @handleSelectionAction="handleSelectProject" 
                            />
                        </v-row>
                        <v-row v-if="displayStoryDropList">
                            <span>Story</span>
                            <ActionButtonDropList
                                :width="150" 
                                :elementsList="getStoriesList"
                                :selectedElementIndex="getStoryIndex" 
                                @handleSelectionAction="handleSelectStory" 
                            />
                        </v-row>
                        <v-row>
                            <span>Index</span>
                            <ActionButtonDropList
                                :width="150" 
                                :elementsList="getIndexList"
                                :selectedElementIndex="getIndex" 
                                @handleSelectionAction="handleSelectIndex" 
                            />
                        </v-row>
                    </v-col>
                </v-row>
                <v-row class="footerRow">
                    <button @click="handleMoveClick">Déplacer</button>
                </v-row>
            </v-col>
        </v-row>
    </v-container>
</template>
<script>
import { mapActions, mapState } from 'vuex'
import ActionButtonDropList from './common/ActionButtonDropList.vue'
    export default {
        components: {
            ActionButtonDropList
        },
        props:{
            projectId: {type: Number},
            storyId: {type: Number},
            ticketId: {type: Number},
            type: {type: String, required: true, default: "story"}
        },
        data() {
            return {
                storiesList: [],
                ticketsList: [],
                selectedProjectId: this.projectId,
                selectedStoryId: this.storyId,
                selectedFrontIndex: 0
            }
        },
        async mounted(){
            await this.getStoriesListData()
            await this.getTicketsListData()
            this.selectedFrontIndex = this.type == "ticket"
                ? this.ticketsList?.find(ticket => ticket.ticketId == this.ticketId)?.frontIndex
                : this.type == "story" 
                    ? this.storiesList?.find(story => story.storyId == this.storyId)?.frontIndex
                    : 0
        },
        methods: {
            ...mapActions("storyStore", ["getStoriesByProjectId"]),
            ...mapActions("ticketStore", ["getTicketsFromStoryId"]),
            resetState(){
                this.storiesList= [],
                this.ticketsList= []
            },
            async getStoriesListData(){
                if(this.projectId){
                    this.storiesList = await this.getStoriesByProjectId(this.projectId)
                    return
                }
                this.storiesList = []
            },
            async getTicketsListData(){
                if(this.storyId && this.type == "ticket"){
                    this.ticketsList = await this.getTicketsFromStoryId(this.storyId)
                    return
                }
                this.ticketsList = []
            },
            async handleSelectProject(projectIndex){
                this.selectedProjectId = this.allProjects[projectIndex]?.projectId
                this.storiesList = await this.getStoriesByProjectId(this.selectedProjectId) 
                this.selectedFrontIndex = 0
            },
            async handleSelectStory(storyIndex){
                this.selectedStoryId = this.storiesList[storyIndex]?.storyId
                this.ticketsList = await this.getTicketsFromStoryId(this.selectedStoryId) 
                this.selectedFrontIndex = 0
            },
            handleSelectIndex(index){
                this.selectedFrontIndex = index
            },
            handleClose(){
                this.resetState()
                this.$emit("handleCloseAction")
            },
            handleMoveClick(){
                if(this.type == "ticket"){
                    this.$emit("handleMoveTicketAction", {
                        oldStoryId: this.storyId,
                        newStoryId: this.selectedStoryId,
                        ticketId: this.ticketId,
                        newFrontIndex: this.selectedFrontIndex
                    })
                    return
                }
                if(this.type == "story"){
                    this.$emit("handleMoveStoryAction", {
                        oldProjectId: this.projectId,
                        newProjectId: this.selectedProjectId,
                        storyId: this.storyId,
                        newFrontIndex: this.selectedFrontIndex
                    })
                    return
                }
                this.$emit("handleCloseAction")
            }
        },
        computed: {
            ...mapState("projectStore", ["allProjects"]),
            displayStoryDropList(){
                return this.type == "ticket"
            },
            displayProjectDropList(){
                return this.type == "story"
            },
            getProjectsList(){
                return this.allProjects.map(project => project.name)
            },
            getProjectIndex(){
                const index = this.allProjects?.findIndex(project => project.projectId == this.selectedProjectId)
                return index >= 0 ? index : 0
            },
            getStoriesList(){
                return this.storiesList.map(story => story.name)
            },
            getStoryIndex(){
                const index = this.storiesList?.findIndex(story => story.storyId == this.selectedStoryId)
                return index >= 0 ? index : 0
            },
            getIndexList(){
                if(this.type == "ticket"){
                    const listLength = this.selectedStoryId == this.storyId ? this.ticketsList?.length : this.ticketsList?.length + 1
                    return new Array(listLength > 0 ? listLength : 1).fill().map((x,i)=>i)
                }
                if(this.type == "story"){
                    const listLength = this.selectedProjectId == this.projectId ? this.storiesList?.length : this.storiesList?.length + 1
                    return new Array(listLength > 0 ? listLength : 1).fill().map((x,i)=>i)
                }
                return [0]
            },
            getIndex(){
                return this.selectedFrontIndex
            }
        }
    }
</script>
<style scoped>
    .moveDropDown {
        padding: 0;
        background-color: #fff;
        color: #000;
        border-radius: 5px;
        position: absolute;
        box-shadow: 0 8px 16px -4px #091e4240, 0 0 0 1px #091e4214;
        padding-top: 5px;
        padding-bottom: 2px;
        z-index: 2;
        width: 250px;
    }
        .moveDropDown .v-col {
            padding: 0;
        }

        .moveDropDown .v-row {
            margin: 0;
        }
        .moveDropDown .headerRow{
            display: inherit;
            border-bottom: 1px solid #aaa;
            margin-left: 7px;
            margin-right: 7px;
            text-align: center;
            margin-bottom: 5px;
            padding-bottom: 5px;
        }
            .moveDropDown .headerRow span{
                font-size: 16px;
                margin-left: 10px;
                width: 100%;
            }
            .moveDropDown .headerRow i{
                float: right
            }
        .moveDropDown .positionSelectorRow {
            margin-top: 10px;
            padding-left: 15px;
            padding-right: 15px;
        }
            .moveDropDown .positionSelectorRow span {
                width: 50px;
                margin-bottom: 15px;
            }
        .moveDropDown .footerRow {
            margin-top: 10px;
            margin-bottom: 10px;
            justify-content: center;
        }
        .moveDropDown .footerRow button{
            padding: 5px 20px;
            border-radius: 5px;
            color: #fff;
            background-color: #FFDC84;
        }
        
</style>