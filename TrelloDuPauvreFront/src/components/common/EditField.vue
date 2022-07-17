<template>
    <v-container 
        class="editField" 
        :class="containerClass" 
        :style="containerStyle"
        v-on:keyup.enter="handleValidation()"
    >
        <v-row>
            <v-col>
                <button class="validationButton" @click="handleValidation()">
                    {{this.validationButtonLabel}}
                </button>
            </v-col>
            <v-col>
                <v-icon class="closeButton" @click="handleClose()">mdi-close</v-icon>
            </v-col>
        </v-row>
        <v-row>
            <input ref="fieldValue" type="text" id="fieldValue" v-model="newFieldValue" placeholder="Modifier..." class="editInput"/>
        </v-row>
    </v-container>
</template>

<script>
    export default {
        props: {
            type: {type: String, default: "variantYellow"},
            validationButtonLabel: {type: String,  default: "Modifier"},
            fieldValue: {type: String, default: ""},
            width: {type: Number, default: 350},
        },
        data() {
            return {
                newFieldValue: this.fieldValue,
            }
        },
        methods: {
           handleValidation(){
               this.$emit("handleValidationAction", this.newFieldValue)
           },
           handleClose() {
               this.$emit("handleCloseAction")
           }
        },
        computed: {
            containerClass() {
                return this.type
            },
            containerStyle() {
                return `width : ${this.width}px`
            },
        },
    }
</script>

<style scoped>
    .editField {
        border-radius: 10px;
        padding: 7px;
        height: 88px;
        font-size: 16px;
    }

    .editField .validationButton{
        border-radius: 5px;
        width: 90px;
        height: 33px;
    }

    .editField .closeButton{
        padding-top: 10px;
        font-size: 26px;
        float: right;
    }

    .editField .v-row{
        margin: 0;
    }

    .editField .v-col{
        padding: 0;
    }

    .editField .editInput{
        width: 100%;
        background-color: #fff;
        border-radius: 5px;
        height: 30px;
        
        margin-top: 10px;
        padding-left: 10px;
        padding-right: 10px;
    }
    .editField .editInput:focus-visible{
        outline: #D9EBF1 solid 2px !important;
    }

    .editField.variantYellow{
        background-color: #EFEFEF;
        border-radius: 10px;
        color: #000;
    }

    .editField.variantYellow .validationButton{
        background-color: #FFDC84;
        color: #fff
    }

    .editField.variantBlue{
        background-color: #EFEFEF;
        border-radius: 10px;
        color: #fff;
    }

    .editField.variantYellow .validationButton{
        background-color: #409DBB;
        color: #000
    }

</style>