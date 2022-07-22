<template>
    <v-container class="modalTicketCheckListSection">
        <v-row class="header">
            <v-icon>mdi-text-box-check-outline</v-icon>
            Checklist
        </v-row>
        <v-row v-if="!noItem">
            <div class="progressBar">
                <span>{{checkListProgress}}%</span>
                <v-progress-linear
                    :color="'#409DBB'"
                    height="10"
                    v-model="checkListProgress"
                />
            </div>
            
        </v-row>
        <v-row class="itemListRow">
            <v-col>
                <v-row v-for="(checkListItem, key) in getCheckListItems" :key="key">
                    <CheckListItem 
                        v-if="checkListItem.updatedStatus != 'deleted'"
                        v-bind="$attrs"
                        :index="key"  
                        :checkListItemLabel="checkListItem.label"
                        :checked="checkListItem.checked"
                    />
                </v-row>
            </v-col>
        </v-row>
        <v-row>
            <button class="addButton" v-if="!editFieldOpen" @click="handelOpenEditField()">Ajouter un élément</button>
            <EditField 
                class="editField"
                ref="editField"
                v-if="editFieldOpen"
                :validationButtonLabel="'Créer'"
                @handleValidationAction="handleCreateCheckListItem"
                @handleCloseAction="handelCloseEditField"
            />
        </v-row>
    </v-container>
</template>
<script>
import CheckListItem from '@/components/CheckListItem.vue'
import EditField from './common/EditField.vue'
export default {
    components : {
        CheckListItem,
        EditField
    },
    props: {
        checkListItems: {type: Array, required: true, default: new Array()},
    },
    data() {
        return {
            editFieldOpen: false,
        }
    },
    methods: {
        handleCreateCheckListItem(label) {
            this.$emit("handleValidationAction", label)
            this.editFieldOpen=false
        },
        handelCloseEditField() {
            this.editFieldOpen=false
        },
        handelOpenEditField(){
            this.editFieldOpen = true
            //Focus input text of Edit field element on next render
            this.$nextTick(() => {
                this.$refs.editField.$refs.fieldValue.focus()
            })
        },

    },
    computed: {
        noItem(){
            return this.checkListItems?.filter(checkListItem => checkListItem.updatedStatus != 'deleted')?.length == 0
        },
        getCheckListItems(){
            return this.checkListItems
        },
        checkListProgress(){
            const total = this.checkListItems?.filter(checkListItem => checkListItem.updatedStatus != 'deleted')?.length
            return total == 0 
                ? 0 
                : Math.trunc(
                    this.checkListItems.filter(checkListItem => checkListItem.checked).length 
                    / total
                    * 100
                )
        }
    },
}
</script>
<style scoped>
    .modalTicketCheckListSection {
        padding: 0
    }
    .modalTicketCheckListSection .v-col{
        padding: 0
    }
    .modalTicketCheckListSection .v-row{
        margin: 0
    }
    .modalTicketCheckListSection .header {
        margin-bottom: 10px;
    }
    .modalTicketCheckListSection .header .v-icon{
        margin-right: 7px;
    }
    .modalTicketCheckListSection .progressBar {
        margin-left: 3px;
        margin-bottom: 10px;
        width: 100%;
        display: flex;
    }
    .modalTicketCheckListSection .progressBar span{
        width: 32px;
        font-size: 12px;
    }
    .modalTicketCheckListSection .progressBar .v-progress-linear {
        margin-top: 4px;
        border-radius: 10px;
    }
    .modalTicketCheckListSection .itemListRow {
        margin-bottom: 20px;
    }
    .modalTicketCheckListSection .addButton {
        background-color: #D9EBF1;
        color: #000;
        padding: 5px 10px;
        height: 40px;
        border-radius: 5px;
    }
    .modalTicketCheckListSection .editField {
        margin-left: 0;
    }
</style>