<template>
    <v-container class="reloadComponent">
        <v-row class="textRow">
            <v-col>
                <p>Erreur de chargement de données, veuillez recharger la page</p>
            </v-col>
        </v-row>
        <v-row class="buttonRow">
            <v-col>
                <v-btn @click="reload()">Recharger</v-btn>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
export default {
    methods: {
        reload(){
            this.$router.go()
        }
    },
}
</script>

<style scoped>
    .reloadComponent {
        margin-top: 10%;
        width: 80%;
    }
        .reloadComponent .v-row{
            text-align: center;
        }
</style>