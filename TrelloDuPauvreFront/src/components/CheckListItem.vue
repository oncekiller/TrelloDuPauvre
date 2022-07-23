<template>
    <v-container class="checkListItem" >
        <v-row v-if="!editFieldOpen" class="checkListItemInput" @click="handleCheckListItemClick()">
            <v-col cols="1">
                <input ref="test" type="checkbox" class="checkBoxInput" :checked="this.checked" @click="(event) => handleChangeCheck(event)"/>
            </v-col>
            <v-col cols="9">
                <p>{{this.checkListItemLabel}}</p>
            </v-col>
            <v-col cols="1" class="checkListItemIcon edit">
                <v-icon>mdi-pencil</v-icon>
            </v-col>
            <v-col cols="1" class="checkListItemIcon delete">
                <v-icon @click="(event) => handleDelete(event)">mdi-delete</v-icon>
            </v-col>
        </v-row>
        <EditField 
            class="editField"
            ref="editField"
            v-if="editFieldOpen"
            :validationButtonLabel="'Modifier'"
            @handleValidationAction="handleValidationEditFieldCheckListItem"
            @handleCloseAction="handleCloseEditFieldCheckListItem"
            :fieldValue="checkListItemLabel"
        />
    </v-container>
</template>

<script>
import EditField from './common/EditField.vue';
    export default {
    props: {
        index: {type: Number, required: true},
        checkListItemLabel: {type: String, required: true, default: ""},
        checked: {type: Boolean, default: false},
    },
    data() {
        return {
            editFieldOpen: false,
        };
    },
    methods: {
        handleCloseEditFieldCheckListItem(){
            this.editFieldOpen = false
        },
        handleCheckListItemClick(){
            this.editFieldOpen = true
            //Focus input text of Edit field element on next render
            this.$nextTick(() => {
                this.$refs.editField.$refs.fieldValue.focus()
            })
        },
        handleValidationEditFieldCheckListItem(value){
            this.editFieldOpen = false
            this.$emit("handleUpdateLabelAction", {index: this.index, label:value})
        },
        handleChangeCheck(event){
            event.stopPropagation()
            this.$emit("handleUpdateCheckAction" , {index: this.index, checked:!this.checked})
        },
        handleDelete(event) {
            event.stopPropagation()
            this.$emit("handleDeleteAction", this.index)
        }
    },
    components: { EditField }
}
</script>

<style scoped>
    .checkListItem {
         width: 350px;
         margin: 0;
         padding: 0;
    }
    .checkListItemInput{
        padding-top: 5px;
        padding-left: 5px;
        cursor: pointer;
    }
    .checkListItemInput:hover {
        background-color: #D9EBF1;

    }
    .checkListItemInput:hover .checkListItemIcon{
        display: block !important; 
    }
    .checkListItem .v-col{
        padding: 0;
    }
    .checkListItem .v-row{
        margin: 0;
    }
    .checkBoxInput{
        width: 17px;
        height: 17px;
        border: #fff;
        margin-top: 2px;
        cursor: pointer;
    }
    .checkListItemIcon {
        display: none;
        color: #FFDC84;
        margin-top: -2px;
    }
    @media screen and (max-width: 750px){
        .checkListItem {
            width: 100%;
        }
    }

</style>