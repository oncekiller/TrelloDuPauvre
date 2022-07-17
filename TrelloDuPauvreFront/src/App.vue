<template>
  <v-app>
    <v-main>
      <nav-bar/>
      <router-view />
    </v-main>
  </v-app>
</template>

<script>
import NavBar from './components/common/NavBar.vue';
export default {
  name: 'App',

  components: {
    NavBar
},

  data: () => ({
    //
  }),
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


