<script setup>
const fleet = {
  total: 28,
  ok: 21,
  warning: 4,
  overdue: 3
};

const okPercentage = Math.round(
  (fleet.ok / fleet.total) * 100
);

const warningPercentage = Math.round(
  (fleet.warning / fleet.total) * 100
);

const overduePercentage = Math.round(
  (fleet.overdue / fleet.total) * 100
);

const okEnd = (fleet.ok / fleet.total) * 100;

const warningEnd =
  ((fleet.ok + fleet.warning) / fleet.total) * 100;

const chartStyle = {
  background: `
    conic-gradient(
      var(--color-success) 0% ${okEnd}%,
      var(--color-warning) ${okEnd}% ${warningEnd}%,
      var(--color-danger) ${warningEnd}% 100%
    )
  `
};
</script>

<template>
  <section class="fleet-card">

    <div class="fleet-header">
      <h2>Distribuição da frota</h2>

      <select aria-label="Filtrar distribuição">
        <option>Por status</option>
      </select>
    </div>

    <div class="fleet-content">

      <!-- GRÁFICO -->
      <div
        class="donut-chart"
        :style="chartStyle"
      >
        <div class="donut-center">
          <strong>{{ fleet.total }}</strong>
          <span>Total</span>
        </div>
      </div>

      <!-- LEGENDA -->
      <div class="fleet-legend">

        <div class="legend-item">
          <span class="legend-dot legend-dot--success"></span>

          <strong>{{ fleet.ok }}</strong>

          <span>
            Em dia ({{ okPercentage }}%)
          </span>
        </div>

        <div class="legend-item">
          <span class="legend-dot legend-dot--warning"></span>

          <strong>{{ fleet.warning }}</strong>

          <span>
            Em atenção ({{ warningPercentage }}%)
          </span>
        </div>

        <div class="legend-item">
          <span class="legend-dot legend-dot--danger"></span>

          <strong>{{ fleet.overdue }}</strong>

          <span>
            Vencidos ({{ overduePercentage }}%)
          </span>
        </div>

      </div>

    </div>

  </section>
</template>

<style scoped>
.fleet-card {
  padding: 20px;

  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);

  background-color: var(--color-surface);

  box-shadow: var(--shadow-sm);
}

.fleet-header {
  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 16px;

  margin-bottom: 22px;
}

.fleet-header h2 {
  color: var(--color-text-primary);

  font-size: 16px;
  font-weight: 700;
}

.fleet-header select {
  padding: 8px 10px;

  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);

  outline: none;

  background-color: var(--color-surface);

  color: var(--color-text-secondary);

  font-size: 11px;
}

.fleet-content {
  display: flex;
  align-items: center;
  justify-content: space-around;

  gap: 24px;
}


/* GRÁFICO */

.donut-chart {
  position: relative;

  display: flex;
  align-items: center;
  justify-content: center;

  width: 145px;
  height: 145px;

  flex-shrink: 0;

  border-radius: 50%;
}

.donut-chart::after {
  position: absolute;

  width: 90px;
  height: 90px;

  border-radius: 50%;

  background-color: var(--color-surface);

  content: "";
}

.donut-center {
  position: relative;

  z-index: 1;

  display: flex;
  flex-direction: column;
  align-items: center;
}

.donut-center strong {
  color: var(--color-text-primary);

  font-size: 27px;
}

.donut-center span {
  margin-top: 2px;

  color: var(--color-text-secondary);

  font-size: 11px;
}


/* LEGENDA */

.fleet-legend {
  display: flex;
  flex-direction: column;

  gap: 18px;
}

.legend-item {
  display: grid;

  grid-template-columns: 10px 24px 1fr;

  align-items: center;

  gap: 8px;
}

.legend-dot {
  width: 10px;
  height: 10px;

  border-radius: 50%;
}

.legend-dot--success {
  background-color: var(--color-success);
}

.legend-dot--warning {
  background-color: var(--color-warning);
}

.legend-dot--danger {
  background-color: var(--color-danger);
}

.legend-item strong {
  color: var(--color-text-primary);

  font-size: 15px;
}

.legend-item > span:last-child {
  color: var(--color-text-secondary);

  font-size: 15px;

  white-space: nowrap;
}


@media (max-width: 1250px) {
  .fleet-content {
    flex-direction: column;
  }
}
</style>