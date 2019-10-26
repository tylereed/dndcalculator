<template>
  <div id="app">
    <scenario :sim="sim" @calc="doCalculation" />
  </div>
</template>

<script>
import Scenario from '@/components/Scenario.vue';
import Roll from '@/gateways/roll.js';


export default {
  name: 'app',
  components: {
    Scenario
  },
  data() {
    return {
      sim: {
        attack: "d20",
        damage: "2d6",
        ac: 13,
        health: 25
      }
    }
  },
  methods: {
    async doCalculation() {
      try {

        const throws = Roll.getThrows(this.sim.attack, 50);
        for await (let t of throws) {
          console.log(t);
        }

      } catch (error) {
        console.log("Error performing calculation");
        console.log(error);
      }
    }
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 30px;
}
</style>
