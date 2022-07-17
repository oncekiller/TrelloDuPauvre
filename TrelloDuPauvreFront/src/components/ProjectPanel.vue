<template>
    <v-container class="projectPanel">
        <div class="backGroundHoverContainer"></div>
        <div class="backGroundContainer" :style="getStyle">
            <v-row class="contentRow">
                <v-col class="colName">
                    {{getName}}
                </v-col>
                <v-col class="text-right colActionButtons" >
                    <v-icon ref="subMenuButton" @click="handleSubMenuClick">mdi-dots-vertical</v-icon>
                </v-col>
            </v-row>
            <v-row class="favoriteRow">
                <v-col>
                    <v-icon @click="handleFavoriteClick">{{this.isFavoriteData ? "mdi-star" : "mdi-star-outline"}}</v-icon>
                </v-col>
            </v-row>
        </div>
    </v-container>
</template>

<script>
    export default {
        props: {
            projectId: {type: Number, required: true},
            name: {type: String, default: ""},
            isFavorite: {type: Boolean, default: false},
            bgImgUrl: {type: String, default: ""},
            bgImgColor: {type: String, default: "#D9EBF1"},
        },
        data(){
            return {
                isFavoriteData: this.isFavorite
            }
        },
        watch: {
            isFavorite: {
                handler(isFavorite){
                    this.isFavoriteData = isFavorite
                }
            }
        },
        computed: {
            getName(){
                return this.name
            },
            getStyle(){
               return (this.bgImgUrl != undefined && this.bgImgUrl != "") ? `background: url(${this.bgImgUrl})` : `background-color: ${this.bgImgColor}`
            }
        },
        methods: {
            handleSubMenuClick(event) {
                event.stopPropagation()
                this.$emit("handleSubMenuClickAction",  this.projectId)
            },
            async handleFavoriteClick(event){
                event.stopPropagation()
                this.isFavoriteData = !this.isFavoriteData
                this.$emit("handleFavoriteClickAction", {
                    projectId: this.projectId, 
                    isFavorite: this.isFavoriteData
                })
            }
        }
    }
</script>

<style scoped>
    .projectPanel {
        width: 190px;
        height: 90px;
        border-radius: 5px;
        font-size: 16px;
        font-weight: 700;
        color: #fff;
        padding: 0;
    }   
        .projectPanel .v-col {
            padding : 0
        }

        .projectPanel .v-row{
            margin: 0;
        }
    
        .projectPanel .backGroundHoverContainer{
            position: absolute;
            background-size: cover !important;
            width: 190px;
            height: 90px;
            z-index: 0;
            border-radius: 5px;
        }
        .projectPanel:hover .backGroundHoverContainer {
            background-color: #00000010 !important;
        }

        .projectPanel .backGroundContainer {
            background-size: cover !important;
            padding: 10px;
            width: 190px;
            height: 90px;
            z-index: 0;
            border-radius: 5px;
        }
                .projectPanel .backGroundContainer .contentRow .colName{
                    z-index: 2;
                }
                .projectPanel .backGroundContainer .contentRow .colActionButtons {
                    font-size: 14px;
                }

            .projectPanel:hover .backGroundContainer .favoriteRow{
                display: block;
            }

            .projectPanel .backGroundContainer .favoriteRow {
                display: none;
            }
                .projectPanel .backGroundContainer .favoriteRow i{
                    margin-top: 20px;
                }
</style>