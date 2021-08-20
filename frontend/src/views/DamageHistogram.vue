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
  name: "DamageHistogram",
  components: {
    Scenario,
    DamageBarChart,
  },
  data() {
    return {
      scenario: {
        type: "DamageHistogram",
        attack: "d20",
        damage: "d8",
        ac: 14,
      },
      loading: false,
      histogram: {},
    };
  },
  methods: {
    async recalc() {
      try {
        this.loading = true;
        const sim = await Calculator.getDamageHistogram(
          this.scenario.attack,
          this.scenario.damage,
          this.scenario.ac
        );
        this.histogram = this.getBars(sim);
      } finally {
        this.loading = false;
      }
    },
    getBars(calculation) {
      const labels = Object.keys(calculation.results);
      const label = `${calculation.attack} / ${calculation.damage} / ${this.scenario.ac} AC`;
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