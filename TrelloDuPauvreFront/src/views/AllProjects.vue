<template>
    <div class="allProjects">
        <div :style="subMenuStyle" v-if="subMenuDisplay" class="subMenu" v-click-outside="() =>  {this.subMenuDisplay = false}">
            <div class="subMenuTitle">
                <span>Actions</span>
                <v-icon @click="() =>  {this.subMenuDisplay = false}">mdi-close</v-icon>
            </div>
            <p @click="handleEditProject()">Modifier</p>
            <p @click="handleDeleteProject()">Supprimer</p>
        </div>
        <CreateHeaderDropDown
            :elements="[
                {
                    name: 'Projet',
                    emit: 'createProjectAction'
                },
                {
                    name: 'Espace de travail',
                    emit: 'createWorkspaceAction'
                }
            ]"
            @createProjectAction="handleAddProject"
            @createWorkspaceAction="handleAddWorkspace"
        />
        <ModalProject
            ref="modalProject"
            v-model="modalProjectOpenData"
            :modalType="modalType"
            @handleCloseAction="handleModalProjectClose"
        />
        <ModalWorkspace
            ref="modalWorkspace"
            v-model="modalWorkspaceOpenData"
            @handleCloseAction="handleModalWorkspaceClose"
        />
        <v-container class="mainContainer">
            <v-row v-if="displayLastConsultedRow" class="lastConsultedProjectRow">
                <v-col>
                    <v-row class="header">
                        <v-icon>mdi-clock-time-five-outline</v-icon>
                        Récemment consultés 
                    </v-row>
                    <v-row>
                        <v-row>
                            <div v-for="(project, key) in getLastConsultedProject" :key="key">
                                <ProjectPanel 
                                        :ref="'lastConsultedProject' + project.projectId"
                                        :projectId="project.projectId"
                                        :name="project.name" 
                                        :isFavorite="project.isFavorite"
                                        :bgImgUrl="getImgUrl(project.image)"
                                        :bgImgColor="getProjectColor(project.bgColor)"
                                        @click="handleSelectProject(project)"
                                        @handleSubMenuClickAction="(projectId) => handleSubMenuProjectClick(projectId, 'lastConsulted')"
                                        @handleFavoriteClickAction="handleFavoriteProjectClick"
                                    />
                            </div>
                        </v-row>
                    </v-row>
                </v-col>
            </v-row>
            <v-row v-if="displayFavoriteRow" class="favoriteProjectRow">
                <v-col>
                    <v-row class="header">
                        <v-icon>mdi-star</v-icon>
                        Favoris
                    </v-row>
                    <v-row>
                        <v-row>
                            <div v-for="(project, key) in getFavoriteProject" :key="key">
                                <ProjectPanel 
                                        :ref="'favoriteProject' + project.projectId"
                                        :projectId="project.projectId"
                                        :name="project.name" 
                                        :isFavorite="project.isFavorite"
                                        :bgImgUrl="getImgUrl(project.image)"
                                        :bgImgColor="getProjectColor(project.bgColor)"
                                        @click="handleSelectProject(project)"
                                        @handleSubMenuClickAction="(projectId) => handleSubMenuProjectClick(projectId, 'favorite')"
                                        @handleFavoriteClickAction="handleFavoriteProjectClick"
                                    />
                            </div>
                        </v-row>
                    </v-row>
                </v-col>
            </v-row>
            <v-row class="allProjectRow">
                <v-col>
                    <v-row class="header">
                        <v-icon>mdi-account</v-icon>
                        Tableaux personnels
                    </v-row>
                    <v-row>
                        <v-row>
                            <div v-for="(project, key) in getPersonalsProjects" :key="key">
                                <ProjectPanel 
                                        :ref="'personnalProject' + project.projectId"
                                        :projectId="project.projectId"
                                        :name="project.name" 
                                        :isFavorite="project.isFavorite"
                                        :bgImgUrl="getImgUrl(project.image)"
                                        :bgImgColor="getProjectColor(project.bgColor)"
                                        @click="handleSelectProject(project)"
                                        @handleSubMenuClickAction="(projectId) => handleSubMenuProjectClick(projectId, 'personnal')"
                                        @handleFavoriteClickAction="handleFavoriteProjectClick"
                                    />
                            </div>
                            <div>
                                <button class="addProjectButton" @click="handleAddProject()">Ajouter un projet</button>
                            </div>
                        </v-row>
                    </v-row>
                </v-col>
            </v-row>
            <v-row class="workspaceProjectsRow">
                <v-col>
                    <v-row class="title">
                        <p>Vos espaces de travail</p>
                    </v-row>
                    <v-row v-for="(workspace, key) in getWorkspacesProjects" :key="key" class="workspaceProjectElementRow">
                        <v-col>
                            <v-row class="header">
                                <v-icon>{{workspace.iconName ? workspace.iconName : "mdi-folder"}}</v-icon>
                                {{workspace.name}}
                                <button @click="handleClickDeleteWorkspace(workspace.workspaceId)">Supprimer</button>
                            </v-row>
                            <v-row>
                                <v-row>
                                    <div v-for="(project, key) in workspace.projects" :key="key">
                                        <ProjectPanel 
                                                :ref="workspace.name + 'Project' + project.projectId"
                                                :projectId="project.projectId"
                                                :name="project.name" 
                                                :isFavorite="project.isFavorite"
                                                :bgImgUrl="getImgUrl(project.image)"
                                                :bgImgColor="getProjectColor(project.bgColor)"
                                                @click="handleSelectProject(project)"
                                                @handleSubMenuClickAction="(projectId) => handleSubMenuProjectClick(projectId, workspace.name)"
                                                @handleFavoriteClickAction="handleFavoriteProjectClick"
                                            />
                                    </div>
                                    <div>
                                        <button class="addProjectButton" @click="handleAddProject(workspace.workspaceId)">Ajouter un projet</button>
                                    </div>
                                </v-row>
                            </v-row>
                        </v-col>
                    </v-row>
                    <v-row>
                        <button class="addWorkspaceButton" @click="handleAddWorkspace()">Créer un espace</button>
                    </v-row>
                </v-col>
            </v-row>
        </v-container>
    </div>
</template>

<script>
    import { mapActions, mapMutations, mapState } from 'vuex';
    import ProjectPanel from '@/components/ProjectPanel.vue';
    import ModalProject from '@/components/ModalProject.vue'
    import ModalWorkspace from '@/components/ModalWorkspace.vue'
    import CreateHeaderDropDown from '@/components/CreateHeaderDropDown.vue';
        export default {
            name: "AllProjects",
            components: {
                ProjectPanel,
                ModalProject,
                ModalWorkspace,
                CreateHeaderDropDown
            },
            data() {
                return { 
                    subMenuDisplay: false,   
                    subMenuItemId: -1,                    
                    modalProjectOpenData: false,
                    modalWorkspaceOpenData: false,
                    subMenuX: 0,
                    subMenuY: 0,
                    modalType: "create",
            }
        },
        async mounted() {
            await this.getAllProjects()
            await this.getAllWorkspaces()
            this.setAllProjects(await Promise.all(this.allProjects.map(async (project) =>  {
                let image = undefined
                if(project?.bgImageId){
                   image = await this.getImageById(project?.bgImageId)
                }
                return {...project, image: image}
            })))
        },
        methods: {
            ...mapActions("projectStore", ["getAllProjects", "deleteProjectById", "updateProjectFavorite"]),
            ...mapActions("imageStore", ["createImage", "getImageById"]),
            ...mapActions("workspaceStore", ["getAllWorkspaces", "deleteWorkspaceById"]),
            ...mapMutations("projectStore", [ "setSelectedProject", "setAllProjects"]),
            ...mapMutations("workspaceStore", [ "setSelectedWorkspace"]),
            handleSelectProject(project) {
                console.log(project)
                this.setSelectedProject(project)
                this.$router.push("projectPage")
            },
            async refreshData() {
                await this.getAllProjects()
                await this.getAllWorkspaces()
                this.setAllProjects(await Promise.all(this.allProjects.map(async (project) =>  {
                    let image = undefined
                    if(project?.bgImageId){
                        image = await this.getImageById(project?.bgImageId)
                    }
                    return {...project, image: image}
                })))
            },
            handleAddProject(workspaceId){
                if(workspaceId){
                    this.setSelectedWorkspace(this.allWorkspaces?.find(workspace => workspace.workspaceId === workspaceId))

                }else {
                    this.setSelectedWorkspace(undefined)
                }
                this.setSelectedProject({
                    projectId: null,
                    name: "",
                    isFavorite: false,
                    bgColor: null,
                    bgImageId: null,
                    workspaceId: workspaceId,
                })
                this.modalType = "create"
                this.modalProjectOpenData = true
                this.$nextTick(() => {
                    this.$refs.modalProject.$refs.fieldValue.focus()
                })
            },
            async handleEditProject(){
                let project = this.allProjects.find(project => 
                    project.projectId == this.subMenuItemId
                )
                this.setSelectedProject(project)
                this.modalType = "update"
                this.modalProjectOpenData = true
                //Focus input text of Edit field element on next render
                this.$nextTick(() => {
                    this.$refs.modalProject.$refs.fieldValue.focus()
                })
                this.subMenuDisplay = false
            },
            async handleDeleteProject(){
                await this.deleteProjectById(this.subMenuItemId)
                this.subMenuDisplay = false
                await this.refreshData()
            }, 
            async handleModalProjectClose(){
                this.modalProjectOpenData = false
                await this.refreshData()
            },
            async handleModalWorkspaceClose(){
                this.modalWorkspaceOpenData = false
                await this.refreshData()
            },
            async handleUpload(event) {
                let files = event.target.files
                await this.createImage(files)
            },
            handleSubMenuProjectClick(projectId, refBasedName){
                if(!this.subMenuDisplay){
                    const subMenuButtonCords = this.$refs[refBasedName + "Project" + projectId]?.[0]?.$refs["subMenuButton"]?.$el?.getBoundingClientRect()
                    this.subMenuItemId= projectId
                    this.subMenuX = subMenuButtonCords.x
                    this.subMenuY = subMenuButtonCords.y + 20
                }
                this.subMenuDisplay = true
            },
            async handleFavoriteProjectClick(data){
                await this.updateProjectFavorite({
                        projectId: data.projectId,
                        isFavorite: data.isFavorite
                })
                await this.refreshData()
            },
            getImgUrl(image){
                return image?.data ? `data:image/png;base64,${image?.data}` : ''
            },
            getProjectColor(bgColor){
                return bgColor ? bgColor : "#FFDC84"
            },
            handleAddWorkspace(){
                this.modalWorkspaceOpenData = true
                //Focus input text of Edit field element on next render
                this.$nextTick(() => {
                    this.$refs.modalWorkspace.$refs.fieldValue.focus()
                })
            },
            handleCloseEditFieldWorkspace(){
                this.modalWorkspaceOpenData = false
            },
            async handleClickDeleteWorkspace(workspaceId){
                await this.deleteWorkspaceById(workspaceId)
                await this.refreshData()
            }
        },
        computed: {
            ...mapState("projectStore", ["allProjects"]),
            ...mapState("workspaceStore", ["allWorkspaces"]),
            subMenuStyle(){
                return `left: ${window.scrollX + this.subMenuX}px; top: ${window.scrollY + this.subMenuY }px;`
            },
            getLastConsultedProject(){
                const projects = this.allProjects
                projects?.sort((a,b) => {
                    return new Date(b.lastConsultationDate) - new Date(a.lastConsultationDate)
                })
                return projects.slice(0,4)
            },
            getFavoriteProject(){
                return this.allProjects?.filter(project => project.isFavorite)
            },
            displayFavoriteRow(){
                return this.getFavoriteProject.length > 0
            },
            displayLastConsultedRow(){
                return this.getLastConsultedProject.length > 0
            },
            getPersonalsProjects(){
                return this.allProjects.filter(project => project.workspaceId === undefined)
            },
            getWorkspacesProjects(){
                let workspacesProjects = []
                this.allWorkspaces.forEach(workspace => {
                    let element = {...workspace}
                    workspacesProjects = [
                        ...workspacesProjects,
                        {
                            ...element, 
                            projects: this.allProjects.filter(project => project.workspaceId === workspace.workspaceId)
                        }
                    ]
                });
                return workspacesProjects
            }
        },
    }
</script>

<style  scoped>
    .allProjects{
        margin: 0 auto !important;
        max-width: 1240px;
        padding-top: 60px;
        height: 100%;
    }
        .allProjects .mainContainer {
            padding-top: 40px;
        }
        .allProjects .v-col {
            padding: 0;
        }
        .allProjects .v-row {
            margin: 0;
        }
        .allProjects .subMenu{
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
    
            .allProjects .subMenu p:hover{
                cursor: pointer;
                background-color: #D9EBF1;
            }
            .allProjects .subMenu p {
                padding-left: 7px;
                padding-right: 7px;
                padding-top: 2px;
                text-align: left;
            }

            .allProjects .subMenu .subMenuTitle {
                border-bottom: 1px solid #aaa;
                margin-left: 7px;
                margin-right: 7px;
                text-align: center;
                margin-bottom: 5px;
                padding-bottom: 5px;
            }

            .allProjects .subMenu .subMenuTitle i{
                float: right;
            }

        .allProjects .header {
            font-size: 16px;
            font-weight: 700;
            margin-bottom: 15px;
            margin-left: 5px;
            color: #444;
        }

        .allProjects .header .v-icon {
            color: #444;
            margin-right: 7px;
        }

        .allProjects .addProjectButton{
            width: 190px;
            height: 90px;
            border-radius: 5px;
            font-size: 14px;
            margin-left: 5px;
            background-color: #eeefff;
            margin-top: 5px;
        }

        .allProjects .projectPanel {
            margin-left: 5px;
            margin-right: 5px;
            margin-bottom: 5px;
            margin-top: 5px;
            cursor: pointer;
        }

        .allProjects .lastConsultedProjectRow{
            margin-bottom: 40px;
        }

        .allProjects .favoriteProjectRow{
            margin-bottom: 40px;
        }

        .allProjects .allProjectRow {
            margin-bottom: 40px;
        }

        .allProjects .workspaceProjectsRow{
            margin-top: 10px;
        }

            .allProjects .workspaceProjectsRow .title{
                font-size: 18px;
                font-weight: 700;
                text-transform: uppercase;
                color: #409DBB;
                margin-bottom: 20px;
            }

            .allProjects .workspaceProjectsRow .workspaceProjectElementRow{
                margin-top: 30px;
                margin-bottom: 30px;
            }
                    .allProjects .workspaceProjectsRow .header button{
                        font-size: 14px;
                        font-weight: 500;
                        background-color: #eeefff;
                        padding: 5px 10px;
                        border-radius: 5px;
                        margin-left: 30px;
                    }   
            .allProjects .workspaceProjectsRow .addWorkspaceButton{
                padding: 10px;
                border-radius: 5px;
                background-color: #D9EBF1;
                font-weight: 700;
                font-size: 14px !important;
                color: #444;
            }

</style>