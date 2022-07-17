<template>
    <v-dialog fullscreen>
        <v-container class="modalWorkspace" >
            <v-row class="labelRow">
                <v-icon class="closeButton" @click="handleClose()">mdi-close</v-icon> 
                <v-col>
                    <input ref="fieldValue" type="text" id="fieldValue" v-model="workspaceName" placeholder="Ajouter un nom d'espace..." class="editInput"/>
                </v-col>
            </v-row>
            <v-row class="iconRow">
                <div v-if="!displayIconsPicker">
                    Icône de l'espace : 
                    <v-icon @click="openIconsPicker()">{{icon}}</v-icon>
                </div>
                <div class="iconPicker" v-if="displayIconsPicker">
                    <IconPicker 
                        ref="iconPicker"
                        @handleSelectAction="handleSelectIcon"
                    />
                </div>
            </v-row>
            <v-row class="footerRow" justify="center">
                <button :disabled="isSaveDisabled" @click="handleSave()">Sauvegarder</button>
            </v-row>
        </v-container>
    </v-dialog>
</template>

<script>
import { mapActions } from 'vuex';
import IconPicker from './common/IconPicker.vue';
export default {
    components: {
      IconPicker
    },
    data(){
        return {
            workspaceName: "",
            icon: "mdi-folder",
            displayIconsPicker: false
        }
    },
    computed: {
        isSaveDisabled() {
            return this.workspaceName == ""
        }
    },
    methods:{
         ...mapActions("workspaceStore", ["getAllWorkspaces", "createWorkspace"]),
        handleClose(){
            this.resetState()
            this.$emit("handleCloseAction")
        },
        resetState(){
            this.workspaceName = "",
            this.icon = "mdi-folder"
            this.displayIconsPicker = false
        },
        async handleSave() {
            await this.createWorkspace({
                name: this.workspaceName,
                iconName: this.icon
            })
            this.handleClose()
        },
        openIconsPicker(){
            this.displayIconsPicker = true
            //Focus input text of Edit field element on next render
            this.$nextTick(() => {
                this.$refs.iconPicker.$refs.fieldValue.focus()
            })
        },
        handleSelectIcon(icon){
            this.displayIconsPicker = false
            this.icon = icon
        }
    }
}
</script>

<style scoped>
    .modalWorkspace {
        width: 500px;
        font-size: 16px;
        margin-top: 3%;
        border-radius: 10px;
        background-color: #fff;
        padding-top: 50px;
        padding-bottom: 15px;
    }
        .modalWorkspace .v-col {
            padding: 0
        }

        .modalWorkspace .v-row {
            margin: 0
        }
            .modalWorkspace .labelRow .editInput {
                border-radius: 5px;
                padding-left: 10px;
                width: 100%;
                height: 30px;
                outline: 2px #ddd solid !important;
                margin-bottom: 20px;
            }
            .modalWorkspace .labelRow .editInput:focus-visible {
                outline: 2px #409DBB solid !important;
            }
        .modalWorkspace .closeButton {
            margin-top: -40px;
            margin-left: -5px;
            font-size: 30px;
            position: absolute;
        }
            .modalWorkspace .footerRow button {
                background-color: #FFDC84;
                color: #fff;
                padding: 5px 10px;
                height: 40px;
                border-radius: 5px;
            }
            .modalWorkspace .footerRow button:disabled {
                background-color: #aaaaaa;
                opacity: 0.5;
            }
        .modalWorkspace .iconRow {
            margin-bottom: 20px;
        }
            .modalWorkspace .iconRow  .iconPicker{
                width: 100%;
            }
</style>