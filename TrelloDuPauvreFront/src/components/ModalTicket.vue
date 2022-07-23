<template>
    <v-dialog fullscreen>
        <v-container class="modalContainer">
            <v-row>
                <v-col class="leftCol" cols="8">
                    <v-row class="nameSection">
                        <input ref="ticketName" type="text" id="ticketName" v-model="name"
                            placeholder="Ajouter un label..." class="textArea" />
                    </v-row>
                    <v-row class="descriptionSection">
                        <v-col>
                            <v-row class="header">
                                <v-icon>mdi-text</v-icon>
                                Description
                            </v-row>
                            <v-row>
                                <textarea placeholder="Ajouter une description..." v-model="description"
                                    :autoResize="true" rows="5" class="textArea">
                                </textarea>
                            </v-row>
                        </v-col>
                    </v-row>
                    <v-row class="prioritySection">
                        <v-col>
                            <v-row class="header">
                                <v-icon>mdi-thermometer-low</v-icon>
                                Priorité
                            </v-row>
                            <v-row>
                                <v-slider class="slider" :color="'#409DBB'" v-model="priority" :label="priority"
                                    thumb-label="always" :thumb-size="24" :thumb-color="'#FFDC84'" min="0" max="10"
                                    step="1"></v-slider>
                            </v-row>
                        </v-col>
                    </v-row>
                    <v-row class="checkListSection">
                        <ModalTicketCheckListSection :checkListItems="getCheckListItems"
                            @handleDeleteAction="handleDeleteCheckListItem"
                            @handleUpdateCheckAction="handleUpdateCheckCheckListItem"
                            @handleUpdateLabelAction="handleUpdateLabelCheckListItem"
                            @handleValidationAction="handleCreateCheckListItem" />
                    </v-row>
                </v-col>
                <v-col class="rightCol">
                    <div class="propertySection">
                        <v-row class="statusSection">
                            <span class="header">Status</span>
                            <ActionButtonDropList justify="left" :width="150" :elementsList="getElementsListStatus"
                                :selectedElementIndex="getStatusIndex" @handleSelectionAction="handleUpdateStatus" />
                        </v-row>
                        <v-row class="natureSection">
                            <span class="header">Nature</span>
                            <ActionButtonDropList :width="150" :elementsList="getElementsListNature"
                                :selectedElementIndex="getNatureIndex" @handleSelectionAction="handleUpdateNature" />
                        </v-row>

                    </div>
                    <div class="dateSection">
                        <v-row class="deadLineSection">
                            <v-col>
                                <v-row class="header">Date d'échéance</v-row>
                                <v-row>
                                    <Datepicker style="cursor: pointer !important" class="datePicker" v-model="deadLine"
                                        inputFormat="yyyy-MM-dd">
                                    </Datepicker>
                                    <v-icon class="datePickerIcon">mdi-text-box-check-outline</v-icon>
                                </v-row>
                            </v-col>
                        </v-row>
                        <v-row class="creationDateSection">
                            <v-col>
                                <v-row class="header">Date de création</v-row>
                                <v-row>{{ getCreationDate }}</v-row>
                            </v-col>
                        </v-row>
                        <v-row class="modificationDateSection">
                            <v-col>
                                <v-row class="header">Date de modification</v-row>
                                <v-row>{{ getModificationDate }}</v-row>
                            </v-col>
                        </v-row>
                    </div>
                </v-col>
                <v-icon class="closeButton" @click="handleClose()">mdi-close</v-icon>
            </v-row>
            <v-row class="footerSection" justify="center">
                <button :disabled="isSaveDisabled" @click="handleSave()">Sauvegarder</button>
            </v-row>
        </v-container>
    </v-dialog>
</template>

<script>
import ActionButtonDropList from './common/ActionButtonDropList.vue'
import ModalTicketCheckListSection from './ModalTicketCheckListSection.vue';
import Datepicker from 'vue3-datepicker'
import { mapActions, mapMutations, mapState } from 'vuex';
import utils from '@/utils/utils'
export default {
    components: { Datepicker, ActionButtonDropList, ModalTicketCheckListSection },
    props:{
        modalType: {type: String, default: "create"}
    },
    methods: {
        ...mapMutations("ticketStore", ["setSelectedTicketNature"]),
        ...mapActions("ticketStore", ["updateFieldSelectedTicket", "createUpdateSelectedTicket", "getTicketsFromProjectIdMappedByStory"]),
        handleUpdateNature(index) {
            this.updateFieldSelectedTicket({fieldName: "natureId", fieldValue: this.natureList[index]?.natureId})
        },
        handleUpdateStatus(index) {
            this.updateFieldSelectedTicket({fieldName: "statusId", fieldValue: this.statusList[index]?.statusId})
        },
        handleCreateCheckListItem(label) {
            this.updateFieldSelectedTicket({
                fieldName: "checkListItems", 
                fieldValue: [
                    ...this.selectedTicket?.checkListItems,
                    {
                        id: null,
                        label: label,
                        checked: false,
                        ticketId: this.selectedTicket?.ticketId,
                        updatedStatus: "created"
                    }
                ]
            })
            this.editFieldOpen = false
        },
        handleDeleteCheckListItem(index) {
            this.updateFieldSelectedTicket({
                fieldName: "checkListItems", 
                fieldValue: this.selectedTicket?.checkListItems.map((checkListItem, idx) => {
                    return idx == index 
                        ? checkListItem.updatedStatus != "created"
                            ?  { ...checkListItem, updatedStatus: "deleted" } 
                            :  null
                        : checkListItem
                }).filter(checkListItem => checkListItem != null)
            })
        },
        handleUpdateCheckCheckListItem(value) {
            this.updateFieldSelectedTicket({
                fieldName: "checkListItems", 
                fieldValue: this.selectedTicket?.checkListItems.map((checkListItem, idx) => {
                        return idx == value.index 
                            ? { 
                                ...checkListItem, 
                                checked: value.checked, 
                                updatedStatus: checkListItem.updatedStatus == "created" 
                                    ? "created" 
                                    : "updated" 
                            } 
                            : checkListItem
                    })
            })
        },
        handleUpdateLabelCheckListItem(value) {
            this.updateFieldSelectedTicket({
                fieldName: "checkListItems", 
                fieldValue: this.selectedTicket?.checkListItems.map((checkListItem, idx) => {
                    return idx == value.index 
                            ? { 
                                ...checkListItem, 
                                label: value.label, 
                                updatedStatus: checkListItem.updatedStatus == "created" 
                                    ? "created" 
                                    : "updated" 
                            } 
                            : checkListItem
                })
            })
        },
        handleClose(){
            this.$emit("handleCloseAction")
        },
        async handleSave(){
            await this.createUpdateSelectedTicket(this.modalType)
            this.handleClose()
        }
    },
    computed: {
        ...mapState("ticketStore", ["selectedTicket"]),
        ...mapState("natureStore", ["natureList"]),
        ...mapState("statusStore", ["statusList"]),
        name: {
            get () {
                return this.selectedTicket?.name
            },
            set (value) {
                this.updateFieldSelectedTicket({fieldName: "name", fieldValue: value})
            }
        },
        description: {
            get () {
                return this.selectedTicket?.description
            },
            set (value) {
                this.updateFieldSelectedTicket({fieldName: "description", fieldValue: value})
            }
        },
        priority: {
            get () {
                return this.selectedTicket?.priority
            },
            set (value) {
                this.updateFieldSelectedTicket({fieldName: "priority", fieldValue: value})
            }
        },
        deadLine: {
            get () {
                return this.selectedTicket.deadLine != null ? new Date(this.selectedTicket.deadLine) : null
            },
            set (value) {
                this.updateFieldSelectedTicket({fieldName: "deadLine", fieldValue: utils.formatDate(value)})
            }
        },
        getCreationDate(){
            return this.selectedTicket?.creationDate
        },
        getModificationDate(){
            return this.selectedTicket?.modificationDate
        },
        getNatureIndex() {
            const index = this.natureList.findIndex(nature => nature.natureId == this.selectedTicket?.natureId)
            return index >= 0 ? index : 0
        },
        getStatusIndex() {
            const index = this.statusList.findIndex(status => status.statusId == this.selectedTicket?.statusId)
            return index >= 0 ? index : 0
        },
        getCheckListItems() {
            return this.selectedTicket?.checkListItems
        },
        getElementsListNature() {
            return this.natureList.map(nature => nature.label)
        },
        getElementsListStatus() {
            return this.statusList.map(status => status.label)
        },
        isSaveDisabled() {
            return this.name == ""
        }
    },
}
</script>

<style scoped>

.modalContainer {
    margin-top: 3%;
    border-radius: 10px;
    background-color: #fff;
    width: 60%;
    max-width: 1000px;
    min-height: fit-content;
    height: fit-content;
    position: relative;
    padding-top: 30px;
    font-size: 16px;
    color: #000;
    transform: translateZ(0);
}

.modalContainer .v-col {
    padding: 0
}

.modalContainer .v-row {
    margin: 0
}

.modalContainer .leftCol {
    margin-right: 20px;
    padding-left: 20px;
}

.modalContainer .leftCol .header {
    margin-bottom: 10px;
}

.modalContainer  .leftCol .header .v-icon {
    margin-right: 7px;
}

.modalContainer .leftCol .textArea {
    border-radius: 10px;
    padding-left: 10px;
    margin-left: 20px;
    width: 100%;
}

.modalContainer .leftCol .textArea:focus-visible {
    outline: 2px #409DBB solid !important;
}

.modalContainer .leftCol .nameSection {
    margin-bottom: 20px;
    font-size: 20px;
}

.modalContainer .leftCol .nameSection .textArea {
    height: 33px !important;
}

.modalContainer .leftCol .descriptionSection {
    margin-bottom: 20px;
}

.modalContainer .leftCol .descriptionSection .textArea {
    background-color: #F7F7F7;
    padding-top: 10px;
}

.modalContainer .leftCol .prioritySection {
    margin-bottom: 20px;
}

.modalContainer .leftCol .prioritySection .slider {
    margin-right: 0px;
    margin-left: 30px;
    height: 35px;
}

.modalContainer .leftCol .prioritySection .slider .v-input__details {
    display: none;
}

.modalContainer .rightCol {
    margin-left: 20px;
}

.modalContainer .rightCol .header {
    margin-top: 5px;
    font-size: 14px;
    color: #444;
}

.modalContainer .rightCol .actionButtonDropList {
    margin-left: 10px;
}


.modalContainer .rightCol .statusSection {
    margin-bottom: 20px;
}

.modalContainer .rightCol .natureSection {
    margin-bottom: 50px;
}

.modalContainer .rightCol .creationDateSection {
    margin-bottom: 5px;
}
.modalContainer .rightCol .modificationDateSection {
    margin-bottom: 50px;
}

.modalContainer .rightCol .deadLineSection .datePickerIcon {
    margin-left:-30px; 
    pointer-events: none;
    margin-bottom: 30px;
}

.modalContainer .footerSection {
    width: calc(100% - 32px);
    position: relative;
    margin-top: 30px;
}

.modalContainer .footerSection button {
    background-color: #FFDC84;
    color: #fff;
    padding: 5px 10px;
    height: 40px;
    border-radius: 5px;
}

.modalContainer .footerSection button:disabled {
    background-color: #aaaaaa;
    opacity: 0.5;
}

.modalContainer .closeButton {
    margin-top: -20px;
    font-size: 30px;
    position: absolute;
}


@media screen and (max-width: 1300px){
    .modalContainer {
        width: 80%;
    }
}

@media screen and (max-width: 1000px){
    .modalContainer {
        width: 95%;
    }
}

@media screen and (max-width: 750px){
    .modalContainer {
        padding: 25px;
    }
        .modalContainer .leftCol {
            max-width: 100%;
            flex: 0 0 100%;
            margin-right: 0 !important;
            margin-right: 0px;
            padding-left: 0px;
        }
        .modalContainer .nameSection {
            margin-top: 20px;
        }
            .modalContainer .leftCol .textArea { 
                margin-left: 0;
            }
                .modalContainer .leftCol .prioritySection .slider {
                    margin-left: 0;
                }
        .modalContainer .rightCol {
            margin-top: 20px;
            margin-left: 0;
            display: flex;
        }
            .modalContainer .rightCol .propertySection{
                width: 50%;
            }
            .modalContainer .rightCol .dateSection .modificationDateSection{
                margin-bottom: 10px;
            }
}
@media screen and (max-width: 500px){ 
    .modalContainer .rightCol {
        display: block;
    }
        .modalContainer .rightCol .propertySection {
            width: 100%;
        }
        .modalContainer .rightCol .dateSection {
            margin-top: -20px;
        }
}


</style>