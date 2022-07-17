<template>
        <v-btn 
            class="actionButton"
            :class="type"
            @click="action()">
                {{text}}
        </v-btn>
</template>

<script>
    export default {
        name: "ActionButton",
        props: {
            type: {type: String,  default: "variantYellow"},
            action: {type:Function ,required : true, default: () => {} },
            text: {type: String, required: true, default: ""}
        },
    }
</script>

<style scoped>
    .actionButton.variantYellow{
        color: #ffffff;
        background-color: #FFDC84;
    }
    .actionButton.variantBlue{
        color: #000000;
        background-color: #D9EBF1;
    }
</style>