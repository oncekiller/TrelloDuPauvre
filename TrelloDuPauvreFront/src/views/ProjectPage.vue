<template>
    <div class="projectPage" :style="getStyleContainer">
        <ModalTicket ref="modalTicket" v-model="modalOpenData" :modalType="modalType"
            @handleCloseAction="handleModalClose" />
        <CreateHeaderDropDown :elements="[
                {
                    name: 'Story',
                    emit: 'createStoryAction'
                },
            ]" @createStoryAction="handleAddStory" />
        <div :style="getSubMenuStyle" v-if="subMenuDisplay" class="subMenu"
            v-click-outside="() =>  {this.subMenuDisplay = false}">
            <div class="subMenuTitle">
                <span>Actions</span>
                <v-icon @click="() =>  {this.subMenuDisplay = false}">mdi-close</v-icon>
            </div>
            <p @click="handleEditSubMenu()">Modifier</p>
            <p @click="handleDeleteSubMenu()">Supprimer</p>
            <p v-if="!draggableDisabled" @click="handleMoveSubMenu">Déplacer</p>
        </div>
        <MoveDropDown :style="getSubMenuStyle" v-if="moveDropDownOpen && !draggableDisabled"
            :projectId="selectedProject?.projectId" :storyId="getMoveDropDownStoryId"
            :ticketId="getMoveDropDownTicketId" :type="subMenuItemType" @handleCloseAction="handleCloseMoveDropDown"
            @handleMoveTicketAction="handleMoveTicketDropDown" @handleMoveStoryAction="handleMoveStoryDropDown" />
        <v-container class="projectPageContainer">
            <div class="filterRow">
                <FiltersDropDown />
                <ActionButtonDropList class="projectDropList" :elementsList="getProjectsName"
                    :selectedElementIndex="getProjectElementIndex" :width="130"
                    @handleSelectionAction="handleSelectProject" />
                <ActionButtonDropList class="sortDropList" :elementsList="sortElementsList"
                    :selectedElementIndex="getSortElementsListIndex" :width="130"
                    @handleSelectionAction="handleSelectSort" />
            </div>
            <v-row class="storiesRow">
                <draggable v-model="filteredProjectTicketsData" :disabled="draggableDisabled" group="stories"
                    @end="handleDragStory" item-key="storyId" class="draggable">
                    <StoryPanel v-for="(projectStory, key) in filteredProjectTicketsData" :key="key"
                        class="storyPanelCol" :ref="'story' + projectStory.storyId" :style="'width:270px'"
                        :storyId="projectStory.storyId" :name="projectStory.name" :tickets="projectStory.tickets"
                        @handleEditTicketAction="handleEditTicket" @handleAddTicketAction="handleAddTicket"
                        @handleSubMenuClickAction="handleSubMenuClick" @handleUpdateStoryAction="handleUpdateStory"
                        @handleDragAction="handleDragTicket" />
                </draggable>

                <div>
                    <button class="addStoryButton" v-if="!editFieldStoryOpen" @click="handleAddStory()">Ajouter une
                        story</button>
                    <EditField class="editField" ref="editField" :type="variantBlue"
                        v-click-outside="() => {this.editFieldStoryOpen = false}" v-if="editFieldStoryOpen"
                        :validationButtonLabel="'Ajouter'" @handleValidationAction="handleValidationEditFieldStory"
                        @handleCloseAction="handleCloseEditFieldStory" />
                </div>
            </v-row>
        </v-container>
    </div>
</template>

<script>
import { mapActions, mapMutations, mapState, mapGetters } from 'vuex'
import StoryPanel from '@/components/StoryPanel.vue'
import ModalTicket from '@/components/ModalTicket.vue'
import EditField from '@/components/common/EditField.vue'
import FiltersDropDown from '@/components/FiltersDropDown.vue'
import { VueDraggableNext } from 'vue-draggable-next'
import ActionButtonDropList from '@/components/common/ActionButtonDropList.vue'
import CreateHeaderDropDown from '@/components/CreateHeaderDropDown.vue'
import utils from "@/utils/utils"
import MoveDropDown from '@/components/MoveDropDown.vue'
    export default {
        components: {
            ActionButtonDropList,
            StoryPanel,
            ModalTicket,
            EditField,
            FiltersDropDown,
            draggable: VueDraggableNext,
            CreateHeaderDropDown,
            MoveDropDown
        },
        data() {
            return {
                modalOpenData: false,
                modalType: "create",
                editFieldStoryOpen: false,
                subMenuDisplay: false,
                subMenuItemId: -1,
                subMenuStoryId: -1,
                subMenuItemType: "",
                subMenuX: 0,
                subMenuY: 0,
                sortElementsList: [
                    "ordre",
                    "date"
                ],
                moveDropDownOpen: false
            }
        },
        async mounted() {
            this.resetState()
            await Promise.all([this.getTicketsFromProjectIdMappedByStory(this.selectedProject?.projectId), this.getAllNatures(), this.getAllStatus()])
            await this.filterProjectTickets()

            //update last consultation date
            const newDate =  utils.formatDateTime(new Date())
            this.updateFieldSelectedProject({fieldName: "lastConsultationDate", fieldValue: newDate})
            await this.createUpdateSelectedProject("update")
        },
        methods: {
            ...mapActions("ticketStore", [
                "getTicketsFromProjectIdMappedByStory",
                "getTicketById", 
                "deleteTicketById", 
                "updateTickets",
            ]),
            ...mapActions("storyStore", ["deleteStoryById", "createStory", "updateStory", "updateStories", "getStoriesByProjectId"]),
            ...mapActions("natureStore", ["getAllNatures"]),
            ...mapActions("statusStore", ["getAllStatus"]),
            ...mapActions("filterStore", ["filterProjectTickets"]),
            ...mapActions("projectStore",["createUpdateSelectedProject", "updateFieldSelectedProject"]),
            ...mapMutations("ticketStore", ["setSelectedTicket", "setProjectTickets", "setFilteredProjectTickets"]),
            ...mapMutations("projectStore", ["setSelectedProject"]),
            ...mapMutations("filterStore", ["setSortType"]),
            async refreshData() {
                await this.getTicketsFromProjectIdMappedByStory(this.selectedProject?.projectId)
                await this.filterProjectTickets()
            },
            async handleEditTicket(ticketId) {
                this.setSelectedTicket(await this.getTicketById(ticketId))
                this.modalType = "update"
                this.modalOpenData = true
                //Focus input text of Edit field element on next render
                this.$nextTick(() => {
                    this.$refs.modalTicket.$refs.ticketName.focus()
                })
            },
            handleAddTicket(storyId){
                const frontIndex = this.projectTickets?.find(story => story.storyId == storyId)?.tickets?.length
                this.setSelectedTicket(
                    {
                        ticketId: null,
                        name: "",
                        description: "",
                        priority: 5,
                        natureId: this.natureList[0]?.natureId,
                        statusId: this.statusList[0]?.statusId,
                        deadLine: "",
                        creationDate: "",
                        modificationDate: "",
                        projectId: this.selectedProject?.projectId,
                        storyId: storyId,
                        checkListItems: [],
                        frontIndex: frontIndex
                    }
                )
                this.modalType = "create"
                this.modalOpenData = true
                //Focus input text of Edit field element on next render
                this.$nextTick(() => {
                    this.$refs.modalTicket.$refs.ticketName.focus()
                })
            },
            handleAddStory(){
                this.editFieldStoryOpen = true
                //Focus input text of Edit field element on next render
                this.$nextTick(() => {
                    this.$refs.editField.$refs.fieldValue.focus()
                })
            },
            async handleDeleteTicket(ticketId){
                const story = this.projectTickets?.find(story => story.tickets?.some(ticket => ticket.ticketId == ticketId ))
                if(story){
                    const frontIndex = story?.tickets?.find(ticket => ticket.ticketId === ticketId)?.frontIndex
                    let listUpdatedTicket = []
                    await this.deleteTicketById(ticketId)

                    //Reindex tickets
                    for (let index = frontIndex + 1 ; index <= story?.tickets?.length - 1; index++) {
                        const updatedTicket = story?.tickets?.find(ticket => ticket.frontIndex === index)
                        if(updatedTicket){
                            listUpdatedTicket = [
                                ...listUpdatedTicket,
                                this.generateNewIndexTicket(updatedTicket, index - 1, story.storyId)
                            ]
                        }
                    }
                    if(listUpdatedTicket.length > 0){
                        await this.updateTickets(listUpdatedTicket)
                    }

                    //Refresh Data
                    await this.refreshData()
                }
            },
            async handleDeleteStory(storyId){
                const frontIndex = this.projectTickets?.find(story => story.storyId === storyId)?.frontIndex
                let listUpdatedStory = []
                await this.deleteStoryById(storyId)

                //Reindex stories
                for (let index = frontIndex + 1 ; index <= this.projectTickets.length ; index++) {
                    const updatedStory = this.filteredProjectTickets?.find(story => story.frontIndex === index)
                    if(updatedStory){
                        listUpdatedStory = [
                            ...listUpdatedStory,
                            {
                                storyId: updatedStory.storyId,
                                name: updatedStory.name,
                                frontIndex: index - 1,
                                creationDate: updatedStory.creationDate,
                                project : {
                                    projectId: updatedStory.projectId
                                }
                            }
                        ]
                    }
                }
                if(listUpdatedStory.length > 0){
                    await this.updateStories(listUpdatedStory)
                }
                //Refresh Data
                await this.refreshData()
            },
            handleSubMenuClick(data){
                if(!this.subMenuDisplay){
                    if(data.type == "story"){
                        const subMenuButtonCords = this.$refs["story" + data.storyId][0].$refs["subMenuButton"].$el.getBoundingClientRect()
                        this.subMenuItemId= data.storyId
                        this.subMenuStoryId = -1
                        this.subMenuItemType= "story"
                        this.subMenuX = subMenuButtonCords.x
                        this.subMenuY = subMenuButtonCords.y + 20
                    }
                    if(data.type == "ticket"){
                        const subMenuButtonCords = this.$refs["story" + data.storyId][0].$refs["ticket" + data.ticketId][0].$refs["subMenuButton"].$el.getBoundingClientRect()
                        this.subMenuItemId= data.ticketId
                        this.subMenuStoryId = data.storyId
                        this.subMenuItemType= "ticket"
                        this.subMenuX = subMenuButtonCords.x + 7
                        this.subMenuY = subMenuButtonCords.y + 25
                    }
                    this.subMenuDisplay = true
                }
            },
            async handleModalClose(){
                //Refresh Data
                await this.refreshData()
                this.setSelectedTicket({})
                this.modalOpenData = false
            },
            resetState() {
                this.setSelectedTicket({})
                this.setProjectTickets([])
                this.setFilteredProjectTickets([])
            },
            async handleValidationEditFieldStory(storyName){
                if(storyName != "") {
                    await this.createStory({
                        name: storyName, 
                        projectId: this.selectedProject?.projectId,
                        frontIndex: this.projectTickets.length
                    })
                    //Refresh Data
                    await this.refreshData()
                    this.editFieldStoryOpen = false
                } else {
                    this.$refs.editField.$refs.fieldValue.focus()
                }
            },
            async handleUpdateStory(data) {
                const updatedStory = this.projectTickets.find(story => story.storyId === data.storyId)
                await this.updateStory({
                    name: data.storyName, 
                    storyId: data.storyId, 
                    projectId: this.selectedProject?.projectId, 
                    creationDate: updatedStory.creationDate,
                    frontIndex: updatedStory.frontIndex
                })
                //Refresh Data
                await this.refreshData()
            },
            handleCloseEditFieldStory(){
                this.editFieldStoryOpen = false
            },
            async handleEditSubMenu() {
                if(this.subMenuItemType == "story"){
                    this.$refs["story" + this.subMenuItemId][0].textAreaNameOpen = true
                    this.$nextTick(() => {
                        this.$refs["story" + this.subMenuItemId][0].$refs.textAreaName.focus()
                    })
                }
                if(this.subMenuItemType == "ticket"){
                    await this.handleEditTicket(this.subMenuItemId)
                }
                this.subMenuDisplay = false
            },
            async handleDeleteSubMenu() {
                if(this.subMenuItemType == "story"){
                    await this.handleDeleteStory(this.subMenuItemId)
                }
                if(this.subMenuItemType == "ticket"){
                    await this.handleDeleteTicket(this.subMenuItemId)
                }
                this.subMenuDisplay = false
            },
            handleMoveSubMenu() {
                this.subMenuDisplay = false
                this.moveDropDownOpen = true
            },
            async handleDragStory(event) {
                const oldIndex = event.oldIndex
                const newIndex = event.newIndex
                const movedStory = this.filteredProjectTickets?.find(story => story.frontIndex === oldIndex)
                await this.moveStory(movedStory, this.selectedProject?.projectId, this.selectedProject?.projectId, newIndex)
                await this.refreshData()
            },
            generateNewIndexTicket(updatedTicket, frontIndex, storyId){
                return {   
                    ticketId: updatedTicket.ticketId,
                    frontIndex: frontIndex,
                    name : updatedTicket.name,
                    description : updatedTicket.description,
                    priority: updatedTicket.priority,
                    deadLine : updatedTicket.deadLine,
                    creationDate: updatedTicket.creationDate,
                    modificationDate: utils.formatDateTime(new Date()),
                    nature: {
                        natureId: updatedTicket.natureId
                    },
                    status: {
                        statusId: updatedTicket.statusId
                    },
                    project: {
                        projectId: this.selectedProject?.projectId
                    },
                    story: {
                        storyId: storyId
                    },
                }
            }, 
            async handleDragTicket(event){
                const oldIndex = event.oldIndex
                const newIndex = event.newIndex
                const oldStoryId = event.from.getAttribute("item-key")
                const newStoryId = event.to.getAttribute("item-key")
                const oldStory = this.projectTickets?.find(story => story.storyId == oldStoryId)
                const movedTicket =  oldStory?.tickets?.find(ticket => ticket.frontIndex === oldIndex)

                await this.moveTicket(movedTicket, oldStoryId, newStoryId, newIndex)
                await this.refreshData()
            },
            async handleSelectProject(projectIndex) {
                this.setSelectedProject(this.allProjects?.[projectIndex])
            },
            async handleSelectSort(sortIndex) {
                const sortType = this.sortElementsList?.[sortIndex]
                this.setSortType(sortType)
                await this.refreshData()
            },
            async handleCloseMoveDropDown(){
                this.moveDropDownOpen = false
                await this.refreshData()
            },
            async handleMoveTicketDropDown(data){
                const oldStory = this.projectTickets?.find(story => story.storyId == data.oldStoryId)
                const movedTicket =  oldStory?.tickets?.find(ticket => ticket.ticketId === data.ticketId)
                await this.moveTicket(movedTicket, data.oldStoryId, data.newStoryId, data.newFrontIndex)
                await this.handleCloseMoveDropDown()
            },
            async handleMoveStoryDropDown(data){
                const movedStory = this.filteredProjectTickets?.find(story => story.storyId === data.storyId)
                await this.moveStory(movedStory, data.oldProjectId, data.newProjectId, data.newFrontIndex)
                await this.handleCloseMoveDropDown()
            },
            async moveStory(movedStory, oldProjectId, newProjectId, newFrontIndex){
                let listUpdatedStory = []
                const oldFrontIndex = movedStory?.frontIndex
                if(oldProjectId == newProjectId){
                    //Move in the same project
                    if(oldFrontIndex != newFrontIndex){
                        if(oldFrontIndex > newFrontIndex) {
                            for (let index = newFrontIndex; index < oldFrontIndex; index++) {
                                const updatedStory = this.filteredProjectTickets?.find(story => story.frontIndex == index)
                                if(updatedStory){
                                    listUpdatedStory = [
                                        ...listUpdatedStory, 
                                        {   
                                            storyId: updatedStory.storyId,
                                            name: updatedStory.name, 
                                            frontIndex: updatedStory.frontIndex + 1,
                                            creationDate: updatedStory.creationDate,
                                            project: {
                                                projectId: updatedStory.projectId
                                            }
                                        }
                                    ]
                                }
                            }
                        }
                        if(oldFrontIndex < newFrontIndex) {
                            for (let index = oldFrontIndex + 1 ; index < newFrontIndex + 1; index++) {
                                const updatedStory = this.filteredProjectTickets?.find(story => story.frontIndex == index)
                                if(updatedStory){
                                    listUpdatedStory = [
                                        ...listUpdatedStory, 
                                        {   
                                            storyId: updatedStory.storyId,
                                            name: updatedStory.name, 
                                            frontIndex: updatedStory.frontIndex - 1,
                                            creationDate: updatedStory.creationDate,
                                            project: {
                                                projectId: updatedStory.projectId
                                            }
                                        }
                                    ]
                                }
                            }
                        }
                    }
                }else{
                    //Move story to another project

                    //Move stories of the previous project
                    for (let index = oldFrontIndex + 1; index < this.filteredProjectTickets.length; index++) {
                        const updatedStory = this.filteredProjectTickets?.find(story => story.frontIndex == index)
                        if (updatedStory) {
                            listUpdatedStory = [
                                ...listUpdatedStory,
                                {
                                    storyId: updatedStory.storyId,
                                    name: updatedStory.name,
                                    frontIndex: updatedStory.frontIndex - 1,
                                    creationDate: updatedStory.creationDate,
                                    project: {
                                        projectId: updatedStory.projectId
                                    }
                                }
                            ]
                        }
                    }
                    //Move stories of the new project
                    const listStoriesNewProject = await this.getStoriesByProjectId(newProjectId)
                    for (let index = newFrontIndex; index < listStoriesNewProject.length; index++) {
                        const updatedStory = listStoriesNewProject?.find(story => story.frontIndex == index)
                        if (updatedStory) {
                            listUpdatedStory = [
                                ...listUpdatedStory,
                                {
                                    storyId: updatedStory.storyId,
                                    name: updatedStory.name,
                                    frontIndex: updatedStory.frontIndex + 1,
                                    creationDate: updatedStory.creationDate,
                                    project: {
                                        projectId: updatedStory.projectId
                                    }
                                }
                            ]
                        }
                    }
                }

                //Add moved story
                listUpdatedStory = [
                    ...listUpdatedStory,
                    {
                        storyId: movedStory.storyId,
                        name: movedStory.name,
                        frontIndex: newFrontIndex,
                        creationDate: movedStory.creationDate,
                        project: {
                            projectId: newProjectId
                        }
                    }
                ]
                if (listUpdatedStory.length > 0) {
                    await this.updateStories(listUpdatedStory)
                }
            },
            async moveTicket(movedTicket, oldStoryId, newStoryId, newFrontIndex){
                let listUpdatedTicket = []
                const oldFrontIndex = movedTicket?.frontIndex
                const oldStory = this.projectTickets?.find(story => story.storyId == oldStoryId)
                const newStory = this.projectTickets?.find(story => story.storyId == newStoryId)
                if(oldStoryId == newStoryId){
                    //Move in the same story
                    if (oldFrontIndex != newFrontIndex) {
                        console.log(movedTicket, oldStoryId, newStoryId,oldFrontIndex, newFrontIndex)
                        if (oldFrontIndex > newFrontIndex) {
                            for (let index = newFrontIndex; index < oldFrontIndex; index++) {
                                const updatedTicket = oldStory?.tickets?.find(ticket => ticket.frontIndex == index)
                                if (updatedTicket) {
                                    listUpdatedTicket = [
                                        ...listUpdatedTicket,
                                        this.generateNewIndexTicket(updatedTicket, index + 1, oldStoryId)
                                    ]
                                }
                            }
                        }
                        if (oldFrontIndex < newFrontIndex) {
                            for (let index = oldFrontIndex + 1; index < newFrontIndex + 1; index++) {
                                const updatedTicket = oldStory?.tickets?.find(ticket => ticket.frontIndex == index)
                                if (updatedTicket) {
                                    listUpdatedTicket = [
                                        ...listUpdatedTicket,
                                        this.generateNewIndexTicket(updatedTicket, index - 1, oldStoryId)
                                    ]
                                }
                            }
                        }
                    }
                }else{
                    //Move in another story

                    //Reindex old story tickets
                    for (let index = oldFrontIndex + 1 ; index < oldStory?.tickets?.length ; index++) {
                        const updatedTicket = oldStory?.tickets?.find(ticket => ticket.frontIndex == index)
                        if (updatedTicket) {
                            listUpdatedTicket = [
                                ...listUpdatedTicket,
                                this.generateNewIndexTicket(updatedTicket, index - 1, oldStoryId)
                            ]
                        }
                    }
                    //Reindex new story tickets
                    for (let index = newFrontIndex ; index < newStory?.tickets?.length ; index++) {
                        const updatedTicket = newStory?.tickets?.find(ticket => ticket.frontIndex == index)
                        if (updatedTicket) {
                            listUpdatedTicket = [
                                ...listUpdatedTicket,
                                this.generateNewIndexTicket(updatedTicket, index + 1, newStoryId)
                            ]
                        }
                    }
                }
                //Update moved ticket
                listUpdatedTicket = [
                    ...listUpdatedTicket,
                    this.generateNewIndexTicket(movedTicket, newFrontIndex, newStoryId)
                ]

                await this.updateTickets(listUpdatedTicket)
            }
        },
        computed: {
            ...mapState("projectStore", ["selectedProject", "allProjects", "selectedProjectImage"]),
            ...mapState("ticketStore", ["projectTickets", "filteredProjectTickets"]),
            ...mapState("natureStore", ["natureList"]),
            ...mapState("statusStore", ["statusList"]),
            ...mapState("filterStore", ["sortType"]),
            ...mapGetters("filterStore", ["draggableDisabled"]),
            filteredProjectTicketsData: {
                get() {
                    return this.filteredProjectTickets
                },
                set(value) {
                    this.setFilteredProjectTickets(value)
                }
            },
            getSelectedProjectId(){
                return this.selectedProject?.projectId
            },
            getProjectsName() {
                return this.allProjects?.map(project => project.name)
            },
            getProjectElementIndex() {
                return this.allProjects?.findIndex(project => project.projectId == this.selectedProject?.projectId)
            },
            getSortElementsListIndex(){
                 return this.sortElementsList?.findIndex(sortElement => sortElement == this.sortType)
            },
            getSubMenuStyle(){
                return `left: ${window.scrollX  + this.subMenuX}px; top: ${window.scrollY + this.subMenuY }px;`
            },
            getStyleContainer(){
                if(this.selectedProject?.image){
                    const imgUrl = `data:image/png;base64,${this.selectedProject?.image?.data}`
                    return `background: url(${imgUrl})` 
                }
                if(this.selectedProject?.bgColor){
                    return `background-color: ${this.selectedProject?.bgColor}`
                }
                return ''
            },
            getMoveDropDownTicketId(){
                return this.subMenuItemType == "ticket" ? this.subMenuItemId : null
            },
            getMoveDropDownStoryId(){
                return this.subMenuItemType == "story" 
                    ? this.subMenuItemId 
                    : this.subMenuItemType == "ticket" 
                        ? this.subMenuStoryId
                        : null
            }
        },
        watch: {
            async getSelectedProjectId(){
                await this.refreshData()
                const newDate =  utils.formatDateTime(new Date())
                this.updateFieldSelectedProject({fieldName: "lastConsultationDate", fieldValue: newDate})
                await this.createUpdateSelectedProject("update")
            }
        }
    }
</script>

<style scoped>
    .projectPage {
        height: 100%;
        background-size: cover !important;
    }
        .projectPage .subMenu{
            background-color: #fff;
            color: #000;
            border-radius: 5px;
            position: absolute;
            box-shadow: 0 8px 16px -4px #091e4240, 0 0 0 1px #091e4214;
            padding-top: 5px;
            padding-bottom: 2px;
            z-index: 2;
            width: 150px;
        }

        .projectPage .v-row{
            padding: 0;
        }
        .projectPage .v-col{
            padding: 0;
        }
    
        .projectPage .subMenu p:hover{
            cursor: pointer;
            background-color: #D9EBF1;
        }
        .projectPage .subMenu p {
            padding-left: 7px;
            padding-right: 7px;
            padding-top: 2px;
            text-align: left;
        }

        .projectPage .subMenu .subMenuTitle {
            border-bottom: 1px solid #aaa;
            margin-left: 7px;
            margin-right: 7px;
            text-align: center;
            margin-bottom: 5px;
            padding-bottom: 5px;
        }

            .projectPage .subMenu .subMenuTitle i{
                float: right
            }
            .projectPage .subMenu .subMenuTitle span{
                margin-left: 10px;
            }
    .projectPage .projectPageContainer{
        padding-left: 30px;
        padding-right: 30px;
        max-width: 100%;
        height: 100%;
    }
        .projectPage .projectPageContainer .filterRow {
            width: fit-content;
            display: flex;
            flex-wrap: wrap ;
            z-index: 2;
            height: 70px;
            margin-bottom: 20px;
        }
            .projectPage .projectPageContainer .filterRow .v-container {
                margin-left: 0px;
                margin-right: 15px;
            }
        .projectPage .projectPageContainer .sortCol {
            z-index: 2;
        }
        .projectPage .projectPageContainer .storiesRow {
            flex-wrap: unset;
            overflow-x:scroll;
            height: calc(100% - 60px)
        }
            /* .projectPage .projectPageContainer .storiesRow::-webkit-scrollbar {
                display: none;
            } */
            .projectPage .projectPageContainer .storiesRow .storyPanelCol{
                margin-left: 10px;
                margin-right: 10px;
            }
            .projectPage .projectPageContainer .storiesRow .addStoryButton {
                padding: 10px;
                border-radius: 5px;
                background-color: #409DBB;
                font-weight: 700;
                font-size: 14px !important;
                color: #fff;
                min-width: 150px;
            }

            .projectPage .projectPageContainer .storiesRow .draggable{
                display: flex
            }
</style>