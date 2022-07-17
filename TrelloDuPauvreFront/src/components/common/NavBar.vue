<template>
    <v-toolbar app class="navBar"> 
      <v-toolbar-title>
        <router-link to="/" class="routerLink">
          {{ appTitleData }}
        </router-link>
      </v-toolbar-title>
      <v-toolbar-items>
        <v-btn
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
      ...mapState("commonStore", ["createHeaderDropDownOpen"]),
    },
    methods: {
      ...mapMutations("commonStore", ["setCreateHeaderDropDownOpen"]),
    }
}
</script>

<style scoped>
    .navBar {
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
            .navBar .v-toolbar-title .routerLink {
                color: #fff;
                font-weight: 500;
                text-decoration: none;
            }

        .navBar .createButton {
            color: #fff;
        }
</style>