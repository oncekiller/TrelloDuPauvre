<template>
    <v-container class="ticketPanel">
        <v-row class="firstRow">
            <v-col cols="8">
                {{getName}}
            </v-col>
            <v-col class="text-right colActionButtons" >
                <v-icon ref="subMenuButton" @click="handleSubMenuClick">mdi-dots-vertical</v-icon>
            </v-col>
        </v-row>
        <v-row v-if="displayCheckListItemsCounter" class="secondRow">
            <v-icon>mdi-text-box-check-outline</v-icon>
            {{getCheckListItemsCounter}}
        </v-row>
    </v-container>
</template>

<script>
    export default {
        props: {
            ticketId: {type: Number, required: true},
            name: {type: String, default: ""},
            checkListItemsNumber: {type: Number, default: 0},
            checkListItemsValidatedNumber: {type: Number, default: 0}
        },
        data() {
            return {
                subMenuDisplay: false
            }
        },
        methods: {
            handleSubMenuClick(event) {
                event.stopPropagation()
                this.$emit("handleSubMenuClickAction",  this.ticketId)
            },
        },
        computed: {
            displayCheckListItemsCounter(){
                return this.checkListItemsNumber > 0
            },
            getCheckListItemsCounter() {
                return this.checkListItemsValidatedNumber +  '/' + this.checkListItemsNumber
            },
            getName() {
                return this.name
            }
        },
    }
</script>

<style scoped>
    .ticketPanel {
        background-color: #409DBB;
        border-radius: 10px;
        padding: 6px 10px
    }
    .ticketPanel .v-col{
        padding: 0;
    }
    .ticketPanel .v-row{
        margin: 0;
    }

    .ticketPanel .firstRow .colActionButtons {
        font-size: 14px;
        color: #FFDC84;
    }

    .ticketPanel .secondRow {
        font-size: 14px;
    }
</style>