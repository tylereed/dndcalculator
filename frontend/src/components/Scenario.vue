<template>
  <v-card class="pa-4 ma-5" id="scenario" width="250" :loading="isLoading">
    <v-icon v-if="showClose" class="float-right" @click="remove"
      >mdi-close-circle</v-icon
    >
    <v-text-field class="ma-2" label="Attack" v-model="scenario.attack" />
    <v-text-field class="ma-2" label="Damage" v-model="scenario.damage" />
    <v-text-field v-if="showAc" class="ma-2" label="AC" v-model="scenario.ac" />
    <v-text-field v-if="showHealth" class="ma-2" label="Enemy Health" v-model="scenario.health" />
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
  computed: {
    showAc() {
      return (
        this.scenario.type === "DamageHistogram" ||
        this.scenario.type === "RoundHistogram"
      );
    },
    showHealth() {
      return this.scenario.type === "RoundHistogram";
    },
    showClose() {
      return this.scenario.type === "AverageDamage";
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