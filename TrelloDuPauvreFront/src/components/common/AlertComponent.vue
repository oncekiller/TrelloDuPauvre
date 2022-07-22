<template>
    <v-alert icon="mdi-alert" class="alertComponent" type="error" v-if="display">
        Impossible de se connecter à TrelloDuPauvre
    </v-alert>
</template>

<script>
import { mapState, mapMutations } from 'vuex'

export default {
    data(){
        return {
            display: false
        }
    },
    mounted() {
        this.$watch(() => [
            this.alertDisplay, 
        ], () => {
            this.display = this.alertDisplay
            if(this.display){
                setTimeout(()=>{
                    this.setAlertDisplay(false)
                },3000)
            }
        })
    },
    computed: {
        ...mapState("commonStore", ["alertDisplay"])
    },
    methods: {
        ...mapMutations("commonStore", ["setAlertDisplay"])
    }
}
</script>

<style scoped>
    .alertComponent {
        min-height: 55px;
        position: fixed;
        width: 100%;
        z-index: 3;
    }
</style>