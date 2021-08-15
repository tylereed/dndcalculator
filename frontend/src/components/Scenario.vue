<template>
  <v-card class="pa-4 ma-5" id="scenario" width="250" :loading="isLoading">
    <v-icon class="float-right" @click="remove">mdi-close-circle</v-icon>
    <v-text-field class="ma-2" label="Attack" v-model="scenario.attack" />
    <v-text-field class="ma-2" label="Damage" v-model="scenario.damage" />
  </v-card>
</template>

<script>
import debounce from "lodash/debounce";

export default {
  name: "scenario",
  props: {
    scenario: Object,
    index: Number,
    isLoading: Boolean,
  },
  methods: {
    remove() {
      this.$emit("close", this.index);
    },
  },
  watch: {
    scenario: {
      handler: debounce(function () {
        this.$emit("change", this.index);
      }, 500),
      deep: true,
    },
  },
};
</script>

<style>
</style>