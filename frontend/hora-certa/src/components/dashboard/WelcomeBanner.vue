<script setup>
import { Plus } from 'lucide-vue-next';
import dashboardMaintenance from '../../assets/images/dashboard-maintenance.png';
import { useRouter } from 'vue-router';

const router = useRouter();

function goToWorkOrders() {
  router.push('/app/ordens-servico');
}
</script>

<template>
  <section class="welcome-banner">

    <!-- Conteúdo principal -->
    <div class="welcome-content">

      <span class="welcome-label">
        VISÃO GERAL
      </span>

      <h1>
        Sua frota no caminho certo.
      </h1>

      <p>
        Acompanhe equipamentos, leituras e manutenções
        preventivas em um só lugar.
      </p>

      <button class="welcome-action" type="button" @click="goToWorkOrders">
        <Plus :size="18" />
        Nova Ordem de Serviço
      </button>

    </div>

    <!-- Elemento visual do banner -->
    <div class="welcome-visual">
      <img :src="dashboardMaintenance" alt="Escavadeira representando os equipamentos monitorados" />
    </div>

  </section>
</template>

<style scoped>
.welcome-banner {
  position: relative;

  display: flex;
  align-items: stretch;

  min-height: 210px;

  overflow: hidden;

  border: 1px solid var(--color-border);
  border-radius: var(--radius-xl);

  background-color: var(--color-surface);

  box-shadow: var(--shadow-sm);
}

/*CONTEÚDO*/

.welcome-content {
  position: relative;
  z-index: 2;

  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;

  max-width: 52%;

  padding: 28px 0 28px 34px;
}

.welcome-label {
  margin-bottom: 10px;

  color: var(--color-success);

  font-size: 15px;
  font-weight: 700;
  letter-spacing: 1px;
}

.welcome-content h1 {
  margin-bottom: 8px;

  color: var(--color-text-primary);

  font-size: clamp(30px, 3vw, 38px);
  line-height: 1.15;
}

.welcome-content p {
  max-width: 470px;

  margin-bottom: 22px;

  color: var(--color-text-secondary);

  font-size: 20px;
  line-height: 1.6;
}


/*BOTÃO*/

.welcome-action {
  display: inline-flex;
  align-items: center;
  gap: 8px;

  min-height: 42px;

  padding: 0 18px;

  border-radius: var(--radius-md);

  background-color: var(--color-primary-dark);
  color: white;

  font-size: 13px;
  font-weight: 600;

  box-shadow: var(--shadow-sm);

  transition:
    background-color 0.2s ease,
    transform 0.2s ease;
}

.welcome-action:hover {
  background-color: var(--color-primary);
  transform: translateY(-1px);
}


/*IMAGEM*/

.welcome-visual {
  position: relative;

  width: 52%;
  min-height: 210px;

  overflow: hidden;
}

.welcome-visual img {
  width: 100%;
  height: 100%;

  object-fit: cover;
  object-position: center;

  display: block;
}

.welcome-visual::before {
  position: absolute;
  z-index: 1;

  top: 0;
  bottom: 0;
  left: 0;

  width: 120px;

  background: linear-gradient(90deg,
      var(--color-surface) 0%,
      rgba(255, 255, 255, 0) 100%);

  content: "";
  pointer-events: none;
}

/*RESPONSIVIDADE*/

@media (max-width: 850px) {
  .welcome-content {
    max-width: 60%;
    padding: 28px;
  }

  .welcome-visual {
    width: 40%;
  }
}

@media (max-width: 650px) {
  .welcome-content {
    max-width: 100%;
  }

  .welcome-visual {
    display: none;
  }
}
</style>