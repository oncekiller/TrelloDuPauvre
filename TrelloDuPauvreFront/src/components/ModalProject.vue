<template>
    <v-dialog fullscreen>
        <v-container class="modalProject" >
            <v-row>
                <v-col>
                    <v-row class="labelRow">
                        <input ref="fieldValue" type="text" id="fieldValue" v-model="projectName" placeholder="Ajouter un nom de projet..." class="editInput"/>
                    </v-row>
                    <v-row class="workspaceRow">
                        <span class="header">Workspace</span>
                        <ActionButtonDropList 
                            :width="230" 
                            :elementsList="getElementsListWorkspace"
                            :selectedElementIndex="getWorkspaceIndex" 
                            :preSelectionField="'Choisir un workspace'"
                            @handleSelectionAction="handleUpdateWorkspace" />
                    </v-row>
                    <v-row class="checkBoxRow">
                        <v-col cols="1">
                            <input 
                                class="checkBoxInput" 
                                type="checkbox"
                                v-model="isBackground"
                                @click="handleCheckBoxClick()"
                            />
                        </v-col>
                        <v-col>
                            <span>
                                Ajouter un fond d'écran
                            </span>
                        </v-col>
                    </v-row>
                    <v-row class="backgroundRow" v-if="isBackground"> 
                        <v-col>
                            <v-row class="headerRow">
                                <span>Fond d'écran</span>
                            </v-row>
                            <v-row class="switchRow">
                                <span class="colorSwitchSpan" :class="!bgImage ? 'selected' : ''">Ajouter une couleur</span>
                                <v-switch class="bgSwitch" :class="bgImage ? 'image' : 'color'" v-model="bgImage"></v-switch>
                                <span class="imageSwitchSpan" :class="bgImage ? 'selected' : ''">Ajouter une image</span>
                            </v-row>
                            <v-row v-if="bgImage" class="imageRow">
                                <span>Image de fond d'écran: </span>
                                <v-btn v-if="this.bgImageThumbnail == undefined" @click="handleClickSelectFile()" class="bgImageButton">
                                    Choisir une image
                                </v-btn>
                                <div v-if="this.bgImageThumbnail != undefined">
                                    <v-icon class="iconThumbnailEdit" @click="handleClickSelectFile()">mdi-pencil</v-icon>
                                    <v-icon class="iconThumbnailClose" @click="handleCloseImgThumbnail()">mdi-close</v-icon> 
                                    <v-img 
                                        @click="handleClickSelectFile()"
                                        class="thumbnailImg"
                                        :src="this.bgImageThumbnail"
                                    >
                                    </v-img>
                                </div>
                                <input v-show="false" class="inputFile" ref="uploader" type="file" @change="handleSelectFile"/>
                            </v-row>
                            <v-row v-if="!bgImage" class="colorRow">
                                <span>Couleur de fond d'écran: </span>
                                <div 
                                    v-if="!colorPicker" 
                                    :style="getStyleColorPickerButton" 
                                    @click="handleClickColorPicker()"  
                                >
                                </div>
                                <v-color-picker
                                    v-if="colorPicker"
                                    v-model="bgColor"
                                    v-click-outside="() => this.colorPicker = false"
                                    @click="handleClickColorPicker()"
                                    dot-size="20"
                                    hide-inputs
                                    swatches-max-height="100"
                                >
                                </v-color-picker>
                            </v-row>
                        </v-col>
                    </v-row>
                    <v-row class="footerRow" justify="center">
                        <button :disabled="isSaveDisabled" @click="handleSave()">Sauvegarder</button>
                    </v-row>
                </v-col>
                <v-icon class="closeButton" @click="handleClose()">mdi-close</v-icon> 
            </v-row>
        </v-container>
    </v-dialog>
</template>

<script>
import { mapActions, mapMutations, mapState } from 'vuex'
import ActionButtonDropList from './common/ActionButtonDropList.vue'
import utils from '@/utils/utils'
export default {
    components: {
        ActionButtonDropList
    },
    data() {
        return {
            bgImageThumbnail: undefined,
            colorPicker: false,
            bgImage: false,
            isBackground: false
        }
    },
    props:{
        modalType: {type: String, default: "create"},
    },
    watch: {
        async getBgImage() {
            const image = this.selectedProject?.image
            if(image){
                this.bgImage = true
                this.bgImageThumbnail =  `data:image/png;base64,${image?.data}`
            }else {
                this.bgImage = false
            }
            this.isBackground = this.selectedProject?.bgColor || this.selectedProject?.image
        },
        getBgColor(){
            this.isBackground = this.selectedProject?.bgColor || this.selectedProject?.image
        }
    },
    methods: {
        ...mapActions("imageStore", ["createImage", "getImageById", "deleteImageById"]),
        ...mapActions("projectStore", ["createUpdateSelectedProject", "updateFieldSelectedProject"]),
        ...mapMutations("projectStore", ["setSelectedProject"]),
        handleUpdateWorkspace(index){
            this.updateFieldSelectedProject({fieldName: "workspaceId", fieldValue: this.allWorkspaces?.[index]?.workspaceId})
        },
        handleClickColorPicker(){
            this.colorPicker = !this.colorPicker
        },
        handleClickSelectFile(){
            this.$refs.uploader.click()
        },
        handleSelectFile(event){
            const file = event.target?.files?.[0]
            this.bgImageFile = file
            const reader = new FileReader()
            reader.onload = (e) => {
                this.bgImageThumbnail = e.target.result
            }
            reader.readAsDataURL(file)
        },
        async handleSave() {
            const deletedImageId = this.selectedProject?.bgImageId
            if(this.isBackground){
                if(this.bgImage){
                    this.updateFieldSelectedProject({fieldName: "bgColor", fieldValue: null})
                    if(this.bgImageThumbnail){
                        const file = utils.convertImgUriToFile(this.bgImageThumbnail)
                        const bgImageCreated = await this.createImage(file)
                        if(bgImageCreated?.imageId){
                            this.updateFieldSelectedProject({fieldName: "bgImageId", fieldValue: bgImageCreated?.imageId})
                        }else {
                            this.updateFieldSelectedProject({fieldName: "bgImageId", fieldValue: null})
                        }
                    }else {
                        this.updateFieldSelectedProject({fieldName: "bgImageId", fieldValue: null})
                    }
                }else {
                    if(!this.selectedProject?.bgColor){
                        this.updateFieldSelectedProject({fieldName: "bgColor", fieldValue: "#FFDC84"})
                    }
                    this.updateFieldSelectedProject({fieldName: "bgImageId", fieldValue: null})
                }
            }else {
                this.updateFieldSelectedProject({fieldName: "bgColor", fieldValue: null})
                this.updateFieldSelectedProject({fieldName: "bgImageId", fieldValue: null})
            }
            await this.createUpdateSelectedProject(this.modalType)

            //Delete previous image if exist
            if(deletedImageId){
                await this.deleteImageById(deletedImageId)
            }
            this.handleClose()
        },
        handleClose(){
            this.setSelectedProject({})
            this.resetState()
            this.$emit("handleCloseAction")
        },
        handleCloseImgThumbnail() {
            this.$refs.uploader.value= ""
            this.bgImageThumbnail = undefined
        },
        resetState(){
            this.bgImageThumbnail = undefined
            this.colorPicker= false
            this.bgImage= false
            this.isBackground= false
        }
    },
    computed: {
        ...mapState("projectStore", ["selectedProject"]),
        ...mapState("workspaceStore", ["selectedWorkspace", "allWorkspaces"]),
        projectName: {
            get(){
                return this.selectedProject?.name
            },
            set(value){
                return this.updateFieldSelectedProject({fieldName: "name",fieldValue: value})
            }
        },
        bgColor: {
            get(){
                return  this.selectedProject?.bgColor ? this.selectedProject?.bgColor : "#FFDC84"
            },
            set(value){
                return this.updateFieldSelectedProject({fieldName: "bgColor", fieldValue: value})
            }
        },
        getBgImage(){
            return this.selectedProject?.image
        },
        getBgColor(){
            return this.selectedProject?.bgColor
        },
        getStyleColorPickerButton() {
            return {
                backgroundColor: this.bgColor,
                cursor: "pointer",
                height: "30px",
                width: "30px",
                borderRadius: "5px",
            }
        },
        getElementsListWorkspace(){
            return this.allWorkspaces?.map(workspace => workspace.name)
        },
        getWorkspaceIndex(){
            const index =  this.allWorkspaces?.findIndex(workspace => workspace.workspaceId == this.selectedWorkspace?.workspaceId)
            return index >= 0 ? index : -1
        },
        isSaveDisabled() {
            return this.selectedProject?.name == ""
        }
    },
}
</script>

<style scoped>
    .modalProject {
        width: 500px;
        font-size: 16px;
        margin-top: 3%;
        border-radius: 10px;
        background-color: #fff;
        padding-top: 50px;
        padding-bottom: 15px;
    }
    .modalProject .v-col {
        padding: 0
    }

    .modalProject .v-row {
        margin: 0
    }
    
    .modalProject .closeButton {
        margin-top: -40px;
        margin-left: -5px;
        font-size: 30px;
        position: absolute;
    }
    .modalProject .headerRow span{
        width: 100%;
        text-align: left;
        padding-top: 10px;
        padding-bottom: 10px;
        color: #444;
        font-size: 14px;
        font-weight: 600;
    }


    .modalProject .labelRow .editInput {
        border-radius: 5px;
        padding-left: 10px;
        width: 100%;
        height: 30px;
        outline: 2px #ddd solid !important;
        margin-bottom: 20px;
    }

    .modalProject .labelRow .editInput:focus-visible {
        outline: 2px #409DBB solid !important;
    }

    .modalProject .backgroundRow {
        height: fit-content;
        margin-bottom: 40px;
    }
    .modalProject .checkBoxRow {
        margin-bottom: 20px;
    }
    .modalProject .checkBoxRow .checkBoxInput{
        width: 17px;
        height: 17px;
        border: #fff;
        margin-top: 2px;
        cursor: pointer;
    }

    .modalProject .backgroundRow .switchRow{
        justify-content: center;
    }

    .modalProject .backgroundRow .switchRow span{
        height: fit-content;
        margin-top: 10px;
        border-radius: 5px;
        padding: 5px 10px;
    }
    .modalProject .backgroundRow .switchRow .colorSwitchSpan.selected{
        background-color: #FFDC84;
    }
    .modalProject .backgroundRow .switchRow .imageSwitchSpan.selected{
        background-color: #D9EBF1;
    }
    .modalProject .backgroundRow .switchRow .bgSwitch{
        height: fit-content;
        max-width: 40px;
        margin-left: 20px;
        margin-right: 20px;
    }
    .modalProject .backgroundRow .switchRow .bgSwitch.image{
        color: #D9EBF1;
    }

    .modalProject .backgroundRow .switchRow .bgSwitch.color{
        color: #FFDC84;
    }
    .modalProject .backgroundRow .imageRow {
        justify-content: center;
    }

    .modalProject .backgroundRow .imageRow span {
        margin-top: 43px;
        margin-right: 20px;
    }

    .modalProject .backgroundRow .imageRow .bgImageButton {
        width: 200px;
        height: 112px;
        background-color: #D9EBF1;
    }
    .modalProject .backgroundRow .imageRow .iconThumbnailEdit {
        margin-left: 180px;
    }
    .modalProject .backgroundRow .imageRow i {
        font-size: 20px;
        width: fit-content;
    }
    .modalProject .backgroundRow .imageRow .thumbnailImg {
        min-width: 200px;
        min-height: 112px;
        max-width: 200px;
        max-height: 112px;
        cursor: pointer;
    }

    .modalProject .backgroundRow .colorRow {
        justify-content: center;
    }

    .modalProject .backgroundRow .colorRow span {
        margin-top: 2px;
        margin-right: 20px;
    }
    .modalProject .backgroundRow .colorRow .v-color-picker {
        max-width: 200px !important;
    }

    .modalProject .footerRow button {
        background-color: #FFDC84;
        color: #fff;
        padding: 5px 10px;
        height: 40px;
        border-radius: 5px;
    }
    .modalProject .footerRow button:disabled {
        background-color: #aaaaaa;
        opacity: 0.5;
    }

</style>