<template>
  <v-container>
    <v-container>
      <scenario
        :index="0"
        :scenario="scenario"
        :isLoading="loading"
        width="100px"
        @change="recalc"
      />
    </v-container>
    <v-card>
      <v-card :loading="loading">
        <damage-bar-chart :chart-data="histogram" /> </v-card
    ></v-card>
  </v-container>
</template>

<script>
import Scenario from "@/components/Scenario.vue";
import Calculator from "@/gateways/Calculator.js";
import DamageBarChart from "@/components/DamageBarChart.vue";

export default {
  name: "RoundHistogram",
  components: {
    Scenario,
    DamageBarChart,
  },
  data() {
    return {
      scenario: {
        type: "RoundHistogram",
        attack: "d20+7",
        damage: "2d10",
        ac: 19,
        health: 300,
      },
      loading: false,
      histogram: {},
    };
  },
  methods: {
    async recalc() {
      try {
        this.loading = true;
        const sim = await Calculator.getRoundHistogram(
          this.scenario.attack,
          this.scenario.damage,
          this.scenario.ac,
          this.scenario.health
        );
        this.histogram = this.getBars(sim);
      } finally {
        this.loading = false;
      }
    },
    getBars(calculation) {
      const labels = Object.keys(calculation.results);
      const label = `${calculation.attack} / ${calculation.damage} / ${this.scenario.ac} AC / ${this.scenario.health}`;
      return {
        labels: labels,
        datasets: [
          {
            data: labels.map((l) => calculation.results[l]),
            label: label,
          },
        ],
      };
    },
  },
  async beforeMount() {
    await this.recalc();
  },
};
</script>

<style>
</style>