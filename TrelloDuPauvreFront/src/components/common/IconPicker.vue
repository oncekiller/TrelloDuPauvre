<template>
    <v-container class="iconPicker">
        <v-row>
            <input ref="fieldValue" type="text" id="fieldValue" v-model="filter" placeholder="Filtrer les icônes..." class="editInput"/>
        </v-row>
        <v-row class="iconsRow">
            <v-icon @click="handleSelect(icon)" v-for="icon in getIcons" :key="icon">
                {{ icon }}
            </v-icon>
        </v-row>
    </v-container>
</template>

<script>
import mdiIcons from "@/assets/mdiIcons.json"
export default {
    data(){
        return {
            filter: "",
        }
    },
    methods: {
        handleSelect(icon){
            console.log(icon)
            this.$emit("handleSelectAction", icon)
        }
    },
    computed: {
        getIcons(){
            const filtered = mdiIcons
                .map(icon => {
                    if(icon.includes(this.filter)){
                        return icon
                    }
                })
                .filter(icon => {
                    return icon != undefined
                })
            console.log(filtered)
            return filtered
        }
    }
}
</script>

<style scoped>
    .iconPicker {
        padding: 10px;
        border-radius: 5px;
        background-color: #D9EBF1;
    }
        .iconPicker .v-col {
            padding: 0
        }

        .iconPicker .v-row {
            margin: 0
        }
        .iconPicker .editInput {
            border-radius: 5px;
            padding-left: 10px;
            width: 100%;
            height: 30px;
            background-color: #fff;
            margin-bottom: 20px;
        }
        .iconPicker .editInput:focus-visible {
            outline: 2px #409DBB solid !important;
        }
        .iconPicker .iconsRow {
            max-height: 150px;
            overflow-y: scroll;
            min-width: 100%;
        }
            .iconPicker .iconsRow i {
                width: 30px;
                height: 30px;
                cursor: pointer;
            }
</style>