<script setup>
import WelcomeBanner from '../components/dashboard/WelcomeBanner.vue';
import StatsCards from '../components/dashboard/StatsCards.vue';
import UpcomingMaintenance from '../components/dashboard/UpcomingMaintenance.vue';
import FleetDistribution from '../components/dashboard/FleetDistribution.vue';
import QuickActions from '../components/dashboard/QuickActions.vue';

</script>

<template>
  <div class="dashboard-page">

    <WelcomeBanner />

    <StatsCards />

    <div class="dashboard-grid">

      <div class="dashboard-main">
        <UpcomingMaintenance />
      </div>

      <aside class="dashboard-side">

        <FleetDistribution />

        <QuickActions />

      </aside>



    </div>

  </div>
</template>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.dashboard-grid {
  display: grid;

  grid-template-columns:
    minmax(0, 2fr) minmax(300px, 0.95fr);

  gap: 16px;

  align-items: start;
}

.dashboard-main {
  min-width: 0;
}

.dashboard-side {
  display: flex;
  flex-direction: column;

  gap: 16px;

  min-width: 0;
}

@media (max-width: 1000px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
</style>