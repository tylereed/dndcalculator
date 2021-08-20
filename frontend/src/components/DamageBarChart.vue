<template>
  <v-card>
    <bar-chart :chart-data="chartData" :options="options" />
  </v-card>
</template>

<script>
import BarChart from "./BarChart.js";

export default {
  name: "DamageBarChart",
  components: { BarChart },
  props: {
    chartData: Object,
  },
  data() {
    return {
      options: { maintainAspectRatio: false },
    };
  },
};
</script>