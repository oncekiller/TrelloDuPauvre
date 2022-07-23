<template>
    <v-toolbar app class="navBar"> 
      <v-toolbar-title @click="goHome()">
          {{ appTitleData }}
      </v-toolbar-title>
      <v-toolbar-items>
        <v-btn
          v-if="!reloadPage"
          flat
          class="createButton"
          @click="this.setCreateHeaderDropDownOpen(!this.createHeaderDropDownOpen)"
        >
          Créer
        </v-btn>
      </v-toolbar-items>
      <v-spacer></v-spacer>
    </v-toolbar>
</template>

<script>
import { mapMutations, mapState } from 'vuex'

export default {
    data(){
        return {
            appTitleData: "TrelloDuPauvre",
            menuItemsData: this.menuItems
        }
    },
    computed: {
      ...mapState("commonStore", ["createHeaderDropDownOpen","reloadPage"]),
    },
    methods: {
      ...mapMutations("commonStore", ["setCreateHeaderDropDownOpen", "setReloadPage"]),
      async goHome(){
        await this.$router.push("/allProjects")
        this.setReloadPage(false)
      }
    }
}
</script>

<style scoped>
    .navBar {
        top: 0;
        position: fixed;
        width: 100%;
        z-index: 3;
        background-color: #409DBB;
        box-shadow: 0 2px 4px -1px rgb(0 0 0 / 20%), 0 4px 5px 0 rgb(0 0 0 / 14%), 0 1px 10px 0 rgb(0 0 0 / 12%);
    }   
        .navBar .v-toolbar-title {
            max-width: fit-content;
            margin-right: 30px;
        }
            .navBar .v-toolbar-title {
                color: #fff;
                font-weight: 500;
                text-decoration: none;
                cursor: pointer;
            }

        .navBar .createButton {
            color: #fff;
        }
    @media (max-width:500px) {
      .navBar .v-toolbar-title {
        max-width: none;
      }
        .navBar .v-toolbar__content .flex-grow-1{
          display: none;
        }
        .navBar .v-toolbar__content button{
          float: right;
        }
    }
    
</style>