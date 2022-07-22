<template>
    <v-container class="filtersDropDown">
        <v-row class="filterButtonRow">
            <v-col @click="() => {this.dropDownOpen = !this.dropDownOpen}" cols="12" class="actionButton">
                <v-row>
                    <v-col cols="7">
                        <v-icon>mdi-filter-variant</v-icon>
                        <span>Filtres</span>
                    </v-col>
                    <v-col v-if="displayFiltersNumber" class="colFiltersNumber">
                        <div class="filtersNumber">{{getFiltersNumber}}</div>
                         <v-icon @click="handleClose">mdi-close</v-icon>
                    </v-col>
                </v-row>
            </v-col>
        </v-row>
        <v-row v-if="dropDownOpen" class="filterSectionRow" v-click-outside="() =>  {this.dropDownOpen = false}">
            <v-col>
                <v-row class="filterSectionHeader">
                    <span>Filtres</span>
                    <v-icon @click="() => {this.dropDownOpen = false}">mdi-close</v-icon>
                </v-row>
                <v-row class="filterSectionMain">
                    <v-col>
                        <v-row class="keyWordsFilterRow filterRow">
                            <v-col>
                                <v-row class="filterFieldHeader">
                                    <span>Mot-clé</span>
                                </v-row>
                                <v-row>
                                    <input 
                                        placeholder="Ajouter un mot-clé..."
                                        type="search"
                                        v-model="keyWord"
                                    >
                                </v-row>
                            </v-col>
                        </v-row>
                        <v-row class="attributesFilterRow filterRow">
                            <v-col>
                                <v-row class="filterFieldHeader">
                                    <span>Attributs</span>
                                </v-row>
                                <v-row class="natureRow">
                                    <v-col cols="3">
                                        <span>Nature</span>
                                    </v-col>
                                    <v-col>
                                        <ActionButtonDropList 
                                            :preSelectionField="'Choisir un nature...'"
                                            :width="150" 
                                            :elementsList="getElementsListNature"
                                            :selectedElementIndex="0" 
                                            @handleSelectionAction="handleUpdateNature" 
                                            @handleCloseAction="handleCloseNature" 
                                        />
                                    </v-col>
                                </v-row>
                                <v-row class="statusRow">
                                    <v-col cols="3">
                                        <span>Status</span>
                                    </v-col>
                                    <v-col>
                                        <ActionButtonDropList 
                                            :preSelectionField="'Choisir un status...'"
                                            :width="150" 
                                            :elementsList="getElementsListStatus"
                                            :selectedElementIndex="0" 
                                            @handleSelectionAction="handleUpdateStatus"
                                            @handleCloseAction="handleCloseStatus" 
                                        />
                                    </v-col>
                                </v-row>
                            </v-col>
                        </v-row>
                        <v-row class="deadlineFilterRow filterRow">
                            <v-col>
                                <v-row class="filterFieldHeader">
                                    <span>Date limite</span>
                                </v-row>
                                <v-row>
                                    <v-col>
                                        <v-row class="delayRow">
                                            <v-col cols="1">
                                                <input 
                                                    class="checkBoxInput" 
                                                    type="checkbox"
                                                    v-model="delay"
                                                />
                                            </v-col>
                                            <v-col>
                                                <span>
                                                    En retard
                                                </span>
                                            </v-col>
                                        </v-row>
                                        <v-row class="deadlineRow" v-if="!this.filterDelay">
                                            <v-col cols="12">
                                                <v-row>
                                                    <v-col cols="10">
                                                        <Datepicker 
                                                                class="picker"
                                                                v-model="deadline" 
                                                                inputFormat="yyyy-MM-dd"
                                                            >
                                                        </Datepicker>
                                                    </v-col>
                                                    <v-col>
                                                        <v-icon class="datePickerIcon">mdi-text-box-check-outline</v-icon>
                                                        <v-icon class="datePickerIconClose" @click="handleCloseDatePicker">mdi-close</v-icon>
                                                    </v-col>
                                                </v-row>
                                            </v-col> 
                                        </v-row>
                                    </v-col>
                                </v-row>
                            </v-col>
                        </v-row>
                        <v-row class="priorityRow filterRow">
                            <v-col>
                                 <v-row class="filterFieldHeader">
                                    <span>Priorité</span>
                                </v-row>
                                <v-row class="filterPriorityCheckbox">
                                    <v-col cols="1">
                                        <input 
                                            class="checkBoxInput" 
                                            type="checkbox"
                                            v-model="priorityRangeActif"
                                        />
                                    </v-col>
                                    <v-col>
                                        <span>
                                            Filtrer les priorités de ticket
                                        </span>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-range-slider 
                                        :disabled="sliderDisabled" 
                                        class="slider" 
                                        :color="'#409DBB'" 
                                        v-model="priorityRange" 
                                        :label="priorityRange"
                                        thumb-label="always" 
                                        :thumb-size="24" 
                                        :thumb-color="'#FFDC84'" 
                                        min="0" 
                                        max="10"
                                        step="1">
                                    </v-range-slider>
                                </v-row>
                            </v-col>
                        </v-row>
                    </v-col>
                </v-row>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import ActionButtonDropList from './common/ActionButtonDropList.vue'
import Datepicker from 'vue3-datepicker'
import { mapState, mapMutations, mapActions } from 'vuex'
import utils from '@/utils/utils'
export default {
    components: {
        ActionButtonDropList,
        Datepicker
    },
    data() {
        return {
            dropDownOpen: false,
        }
    },
    mounted() {
        this.$watch(() => [
            this.filterKeyWord, 
            this.filterNature, 
            this.filterStatus, 
            this.filterDelay, 
            this.filterDeadline,
            this.filterPriorityRangeActif,
            this.filterPriorityRange
        ], () => {
            this.filterProjectTickets()
        })
    },
    methods: {
        ...mapMutations("filterStore", [
            "setFilterKeyWord", 
            "setFilterNature", 
            "setFilterStatus", 
            "setFilterDelay", 
            "setFilterDeadline",
            "setFilterPriorityRangeActif",
            "setFilterPriorityRange"
        ]),
        ...mapActions("filterStore", ["filterProjectTickets"]),
        handleUpdateNature(index) {
            if(index == 0) {
                this.setFilterNature(undefined)
                return
            }
            this.setFilterNature(this.natureList[index -1].natureId)
        },
        handleUpdateStatus(index) {
            if(index == 0) {
                this.setFilterStatus(undefined)
                return
            }
            this.setFilterStatus(this.statusList[index -1].statusId)
        },
        handleCloseNature(){
            this.setFilterNature(undefined)
        },
        handleCloseStatus(){
            this.setFilterStatus(undefined)
        },
        resetState(){
            this.setFilterKeyWord("")
            this.setFilterNature(undefined)
            this.setFilterStatus(undefined)
            this.setFilterDelay(false)
            this.setFilterDeadline(undefined)
            this.setFilterPriorityRangeActif(false)
            this.setFilterPriorityRange([2,8])
        },
        handleClose(event){
            this.resetState()
            event.stopPropagation();
        },
        handleCloseDatePicker(){
            this.setFilterDeadline(undefined)
        }
    },
    computed: {
        ...mapState("natureStore", ["natureList"]),
        ...mapState("statusStore", ["statusList"]),
        ...mapState("filterStore", [
            "filterKeyWord", 
            "filterNature", 
            "filterStatus", 
            "filterDelay", 
            "filterDeadline",
            "filterPriorityRangeActif",
            "filterPriorityRange"
        ]),
        keyWord: {
            get() {
                return this.filterKeyWord 
            },
            set(value) {
                this.setFilterKeyWord(value)
            }
        },
        delay: {
            get() {
                return this.filterDelay 
            },
            set(value) {
                this.setFilterDelay(value)
            }
        },
        deadline: {
            get() {
                return new Date(this.filterDeadline )
            },
            set(value) {
                this.setFilterDeadline(utils.formatDate(value))
            }
        },
        priorityRangeActif: {
            get() {
                return this.filterPriorityRangeActif
            },
            set(value) {
                this.setFilterPriorityRangeActif(value)
            }
        },
        priorityRange: {
            get() {
                return this.filterPriorityRange 
            },
            set(value) {
                this.setFilterPriorityRange(value)
            }
        },
        getElementsListNature() {
            return this.natureList.map(nature => nature.label)
        },
        getElementsListStatus() {
            return this.statusList.map(status => status.label)
        },
        sliderDisabled(){
            return !this.priorityRangeActif
        },
        getFiltersNumber(){
            let i = 0
            if(this.filterKeyWord != "") i+=1
            if(this.filterNature != undefined) i+=1
            if(this.filterStatus != undefined) i+=1
            if(this.filterDelay) i+=1
            if(!this.filterDelay && this.filterDeadline != undefined) i+=1
            if(this.filterPriorityRangeActif) i+=1
            return i
        },
        displayFiltersNumber() {
            return this.getFiltersNumber > 0
        }
    }
}
</script>

<style scoped>
    .filtersDropDown {
        max-width: 133px;
        height: fit-content;
        z-index: 2;
        padding: 0;
    }
    .filtersDropDown .v-col {
        padding: 0;
    }

    .filtersDropDown .v-row {
        margin: 0;
    }
    .filtersDropDown .checkBoxInput{
        width: 17px;
        height: 17px;
        border: #fff;
        margin-top: 2px;
        cursor: pointer;
    }
    .filtersDropDown .filterButtonRow {
        width: 133px
    }
    .filtersDropDown .filterButtonRow .actionButton{
        padding-left: 6px;
        height: 30px;
        border-radius: 5px;
        background-color: #D9EBF1; 
        cursor: pointer;
    }
    .filtersDropDown .filterButtonRow .actionButton i{
        margin-bottom: 3px;
    }

    .filtersDropDown .filterButtonRow .actionButton span{
        line-height: 30px;
    }

     .filtersDropDown .filterButtonRow .actionButton .colFiltersNumber {
        padding-top: 3px;
        display: inherit;
     }

    .filtersDropDown .filterButtonRow .actionButton .colFiltersNumber .filtersNumber {
        background-color: #FFDC84;
        width: 20px;
        height: 20px;
        margin-top: 2px;
        padding-left: 6px;
        padding-top: 1px;
        line-height: 20px;
        border-radius: 100px;
    }

    .filtersDropDown .filterSectionRow {
        position: absolute;
        padding: 15px;
        border-radius: 5px;
        width: 300px;
        box-shadow: 0 8px 16px -4px #091e4240, 0 0 0 1px #091e4214;
        background-color: #fff;
    }

    .filtersDropDown .filterSectionRow .filterSectionHeader{
        border-bottom: 1px solid #aaa;
        text-align: center;
        margin-bottom: 5px;
        padding-bottom: 5px;
    }
    .filtersDropDown .filterSectionRow .filterSectionHeader span{
        font-size: 18px;
        width: 100%;
    }
    .filtersDropDown .filterSectionRow .filterSectionHeader i{
        position: absolute;
    }

    .filtersDropDown .filterSectionRow .filterSectionMain .filterRow{
        margin-bottom: 10px;
    }

    .filtersDropDown .filterSectionRow .filterSectionMain .filterFieldHeader span{
        width: 100%;
        text-align: left;
        padding-top: 10px;
        padding-bottom: 10px;
        color: #444;
        font-size: 14px;
        font-weight: 600;
    }

    .filtersDropDown .filterSectionRow .filterSectionMain .keyWordsFilterRow input {
        background-color: #fafbfc;
        outline: 2px #D9EBF1 solid !important;
        width: 100%;
        border-radius: 5px;
        height: 30px;
        padding: 10px;
    }
    .filtersDropDown .filterSectionRow .filterSectionMain .keyWordsFilterRow input:focus-visible {
        outline: 2px #409DBB solid !important;
    }
    .filtersDropDown .filterSectionRow .filterSectionMain .attributesFilterRow .natureRow{
        margin-bottom: 10px;
    }
    .filtersDropDown .filterSectionRow .filterSectionMain .deadlineFilterRow .delayRow {
        margin-bottom: 10px;
    }

    .filtersDropDown .filterSectionRow .filterSectionMain .deadlineFilterRow .deadlineRow .datePickerIcon {
        margin-left:-30px; 
        pointer-events: none;
    }

    .filtersDropDown .filterSectionRow .filterSectionMain .deadlineFilterRow .deadlineRow .datePickerIconClose {
        margin-left: 12px;
    }
    .filtersDropDown .filterSectionRow .filterSectionMain .priorityRow .filterPriorityCheckbox {
        margin-bottom: 10px;
    }

    .filtersDropDown .filterSectionRow .filterSectionMain .priorityRow .slider {
        height: 35px;
    }

    .filtersDropDown .filterSectionRow .filterSectionMain .priorityRow .slider .v-input__details {
        display: none;
    }

</style>