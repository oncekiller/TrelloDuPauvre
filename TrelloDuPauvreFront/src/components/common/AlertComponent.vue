<template>
    <v-scroll-x-transition :hide-on-leave="true">
        <v-alert icon="mdi-alert" class="alertComponent" type="error" v-if="display">
            Impossible de se connecter à TrelloDuPauvre
        </v-alert>
    </v-scroll-x-transition>
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
        margin-top: 10px;
        margin-left: 10px;
        position: fixed;
        width: 300px;
        z-index: 3;
    }
</style>