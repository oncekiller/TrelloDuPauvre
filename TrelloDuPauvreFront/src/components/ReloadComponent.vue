<template>
    <v-container>
        <v-row>
            <v-col>
                <p>Erreur de chargement de données, veuillez recharger la page</p>
            </v-col>
        </v-row>
        <v-row>
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

<style lang="scss" scoped>

</style>