<template>
  <v-app>
    <v-main>
      <nav-bar/>
      <alert-component/>
      <div class="content">
         <router-view v-if="!reloadPage"/>
         <reload-component v-if="reloadPage"/>
      </div>
    </v-main>
  </v-app>
</template>

<script>
import NavBar from './components/common/NavBar.vue';
import AlertComponent from './components/common/AlertComponent.vue';
import ReloadComponent from './components/ReloadComponent.vue';
import { mapState } from 'vuex';
export default {
  name: 'App',
  components: {
    NavBar,
    AlertComponent,
    ReloadComponent
  },
  computed: {
    ...mapState("commonStore", ["reloadPage"])
  },
  directives: {
    "click-outside": {
      bind: function (el, binding, vnode) {
        el.clickOutsideEvent = function (event) {
          // here I check that click was outside the el and his children
          if (!(el == event.target || el.contains(event.target))) {
            // and if it did, call method provided in attribute value
            vnode.context[binding.expression](event);
          }
        };
        document.body.addEventListener('click', el.clickOutsideEvent)
      },
      unbind: function (el) {
        document.body.removeEventListener('click', el.clickOutsideEvent)
      },
    }
  }
}
</script>


