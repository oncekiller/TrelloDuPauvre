<template>
    <v-container 
        @click="click()" 
        class="actionButtonDropList" 
        :class="containerClass" 
        :style="containerStyle" 
        v-click-outside="() =>  {this.open = false}"
    >
        <v-row class="actionButtonDropListHeader">
            <v-col class="element" cols="9">{{selectedElement}}</v-col>
            <v-col v-if="isCloseDisplay"><v-icon @click="handleClose">mdi-close</v-icon></v-col>
            <v-col v-if="isChevronDisplay"><v-icon>{{this.open ? 'mdi-chevron-up' : 'mdi-chevron-down'}}</v-icon></v-col>
        </v-row>
        <v-row v-if="open" class="actionButtonDropListMain">
            <v-col>
                <v-row v-for="(element, index) in getElementList" :key="index" @click="handleSelection(index)" :class="elementClass(index)">
                    {{element}}
                </v-row>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    export default {
        props: {
            preSelectionField: {type: String, default: ""},
            elementsList: {type: Array, required: true, default: new Array()},
            selectedElementIndex: {type:  Number, default: 0},
            width: {type: Number, default: 100},
            type: {type: String, default: "variantBlue"}
        },
        data() {
            return {
                selectedElement: this.preSelectionField != "" 
                    ? this.selectedElementIndex == -1
                        ? this.preSelectionField
                        : this.elementsList[this.selectedElementIndex]
                    : this.elementsList[this.selectedElementIndex],
                selectedElementIndexData: this.selectedElementIndex,
                open: false
            }
        },
        mounted() {
            this.$watch(() => [
                this.selectedElementIndex, 
                this.elementsList, 
            ], () => {
                this.selectedElementIndexData = this.selectedElementIndex
                this.selectedElement= this.preSelectionField != "" 
                    ? this.selectedElementIndex == -1
                        ? this.preSelectionField
                        : this.elementsList[this.selectedElementIndex]
                    : this.elementsList[this.selectedElementIndex]
            })
        },
        methods: {
            click(){
                this.open = !this.open
            },
            handleSelection(index){
                this.selectedElementIndexData = this.preSelectionField ? index - 1 : index
                this.selectedElement = this.getElementList[index]
                this.$emit("handleSelectionAction", this.preSelectionField ? index - 1 : index)
            },
            handleClose(event){
                this.selectedElementIndexData = this.preSelectionField ? - 1 : 0
                this.selectedElement = this.getElementList[0]
                this.open = false
                this.$emit("handleCloseAction")
                event.stopPropagation()
            },
        },
        computed: {
            containerStyle() {
                return `width : ${this.width}px`
            },
            containerClass() {
                return this.open ? this.type + ' opened' : this.type + ''
            },
            elementClass() {
                return (index) => {
                    return index === this.selectedElementIndexData + (this.preSelectionField != "" ? 1 : 0 ) 
                    ?  this.preSelectionField != "" && index == 0
                        ? ''
                        : 'selected' 
                    : ''
                }
            },
            getElementList() {
                return this.preSelectionField != "" ?  [this.preSelectionField, ...this.elementsList]: this.elementsList
            },
            isCloseDisplay(){
                return this.preSelectionField != "" 
                    ? this.selectedElementIndexData >= 0
                    : false
            },
            isChevronDisplay(){
                return this.preSelectionField != "" 
                        ? this.selectedElementIndexData < 0
                        : true
            }
        },
    }
</script>

<style scoped>
    .actionButtonDropList{
        font-size: 16px;
        padding: 0;
        cursor: pointer;
        word-break: break-all;
        height: fit-content;
    }
    .actionButtonDropList .v-col {
        padding: 0;
    }
    .actionButtonDropList .v-row {
        margin: 0;
    }
    .actionButtonDropList.opened .actionButtonDropListHeader {
        border-bottom-left-radius: 0px;
        border-bottom-right-radius: 0px;
        border-bottom: #000 solid 2px;
    }
    .actionButtonDropListHeader {
        border-top-left-radius: 5px;
        border-top-right-radius: 5px;
        border-bottom-left-radius: 5px;
        border-bottom-right-radius: 5px;
        font-weight: 600;
        display: flex;
        height: 30px;     
        padding-top: 3px;
        padding-left: 10px;
        padding-right: 7px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }
    .actionButtonDropListHeader .element{
        white-space: nowrap;
        overflow: hidden !important;
        text-overflow: ellipsis;
    }
    .actionButtonDropListMain {
        border-bottom-left-radius: 5px;
        border-bottom-right-radius: 5px;
        width: inherit;
        font-weight: 400;
        padding-bottom: 5px;
        position: fixed;
        z-index: 1;
    }

    .actionButtonDropListMain .v-row{
        padding-left: 10px;
        padding-right: 10px;
    } 
    .actionButtonDropListMain .v-row:hover{
        filter: opacity(0.7);
    } 

    .actionButtonDropListMain .selected{
        font-weight: 600;
    }

    .actionButtonDropListHeader .v-icon{
        float: right;
    }

     /* Variant Blue */
    .actionButtonDropList.variantBlue .actionButtonDropListHeader{
        background-color: #D9EBF1;
        color: #000;
    }
    .actionButtonDropList.variantBlue .actionButtonDropListMain{
        background-color: #409DBB;
        color: #fff;
    }

    /* Variant Yellow */
    .actionButtonDropList.variantYellow .actionButtonDropListHeader{
        background-color: #FFDC84;
        color: #000;
    }
    .actionButtonDropList.variantYellow .actionButtonDropListMain{
        background-color: #D9EBF1;
        color: #000;
    }
</style>