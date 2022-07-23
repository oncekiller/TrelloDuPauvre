<template>
    <v-container class="createHeaderDropDown" v-if="dropDownOpenData" v-click-outside="() =>  {this.setCreateHeaderDropDownOpen(false)}">
        <v-row>
            <v-col>
                <v-row class="headerRow">
                    <span>Créer</span>
                    <v-icon @click="this.setCreateHeaderDropDownOpen(false)">mdi-close</v-icon>
                </v-row>
                <v-row class="actionsRow">
                    <v-col>
                        <v-row class="actionRow" @click="handleClickElement(element)" v-for="(element, index) in elements" :key="index">
                            {{ element.name }}
                        </v-row>
                    </v-col>
                </v-row>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import { mapMutations, mapState } from 'vuex'

export default {
    props:{
        elements: {type: Array, default: () => []},
    },
    data() {
        return {
            dropDownOpenData: this.createHeaderDropDownOpen,
        }
    },
     mounted() {
        this.$watch(() => [
            this.createHeaderDropDownOpen, 
        ], () => {
            this.dropDownOpenData = this.createHeaderDropDownOpen
        })
    },
    computed: {
        ...mapState("commonStore", ["createHeaderDropDownOpen"]),
    },
    methods: {
        ...mapMutations("commonStore", ["setCreateHeaderDropDownOpen"]),
        handleClickElement(element){
            this.$emit(element.emit)
            this.setCreateHeaderDropDownOpen(false)
        },
    }
}
</script>

<style scoped>
    .createHeaderDropDown {
        width: 200px;
        height: fit-content;
        z-index: 3;
        padding: 0;
        padding: 10px 0;
        border-radius: 5px;
        box-shadow: 0 8px 16px -4px #091e4240, 0 0 0 1px #091e4214;
        background-color: #fff;
        left: 180px;
        position: fixed;
    }
        .createHeaderDropDown .v-col {
            padding: 0;
        }

        .createHeaderDropDown .v-row {
            margin: 0;
        }
        .createHeaderDropDown .headerRow{
            border-bottom: 1px solid #aaa;
            text-align: center;
            margin-bottom: 5px;
            padding-bottom: 5px;
            margin: 0 10px;
            margin-bottom: 5px;
        }
            .createHeaderDropDown  .headerRow span{
                font-size: 16px;
                width: 100%;
            }
            .createHeaderDropDown .headerRow i{
                position: absolute;
                font-size: 22px;
            }
            .createHeaderDropDown .actionsRow .actionRow {
                cursor: pointer;
                padding: 0px 10px;
                font-size: 16px;
            }
            .createHeaderDropDown .actionsRow .actionRow:hover {
                background-color: #D9EBF1;
            }
    @media (max-width:500px) {
        .createHeaderDropDown {
            transform: translateX(50vw);
            margin-left: -100px;
            left: 0;
        }
    }
</style>