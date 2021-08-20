<template>
  <v-app>
    <v-container>
      <v-tabs v-model="tab">
        <v-tab> Armor Class vs Damage </v-tab>
        <v-tab> Damage Histogram </v-tab>
      </v-tabs>
      <v-tabs-items v-model="tab">
        <v-tab-item>
          <damage-vs-ac />
        </v-tab-item>
        <v-tab-item>
          <damage-histogram />
        </v-tab-item>
      </v-tabs-items>
    </v-container>
  </v-app>
</template>

<script>
import DamageVsAc from "@/views/DamageVsAc.vue";
import DamageHistogram from "@/views/DamageHistogram.vue";

export default {
  components: {
    DamageVsAc,
    DamageHistogram,
  },
  name: "app",
  data() {
    return {
      tab: null,
    };
  },
};
</script>

<style>
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 30px;
}
</style>
