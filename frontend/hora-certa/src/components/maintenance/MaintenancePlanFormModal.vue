<script setup>
import { computed, ref } from 'vue';

import {
  Plus,
  Trash2
} from 'lucide-vue-next';


const props = defineProps({
  plan: {
    type: Object,
    default: null
  }
});


const emit = defineEmits([
  'close',
  'save'
]);

const isEditing = computed(() => {
  return props.plan !== null;
});

function createEmptyItem() {
  return {
    id: null,
    serviceDescription: '',
    intervalHours: '',
    alertMarginHours: 20
  };
}


const planForm = ref({
  name: props.plan?.name ?? '',

  description: props.plan?.description ?? '',

  items: props.plan?.items?.length
    ? props.plan.items.map((item) => ({
        ...item
      }))
    : [
        createEmptyItem()
      ]
});

function addItem() {
  planForm.value.items.push(
    createEmptyItem()
  );
}

function removeItem(index) {

  if (planForm.value.items.length === 1) {
    return;
  }

  planForm.value.items.splice(index, 1);
}

function closeModal() {
  emit('close');
}

function handleSubmit() {

  const formData = {
    name: planForm.value.name.trim(),

    description:
      planForm.value.description.trim(),

    items: planForm.value.items.map((item) => ({
      id: item.id,

      serviceDescription:
        item.serviceDescription.trim(),

      intervalHours:
        Number(item.intervalHours),

      alertMarginHours:
        Number(item.alertMarginHours)
    }))
  };


  emit('save', formData);
}
</script>


<template>
  <div
    class="modal-overlay"
    @click.self="closeModal"
  >

    <section class="maintenance-plan-modal">

      <header class="modal-header">

        <div>
          <h2>
            {{
              isEditing
                ? 'Editar Plano de Manutenção'
                : 'Novo Plano de Manutenção'
            }}
          </h2>

          <p>
            {{
              isEditing
                ? 'Atualize o plano e seus serviços.'
                : 'Defina o plano e os serviços de manutenção.'
            }}
          </p>
        </div>


        <button
          type="button"
          class="modal-close"
          aria-label="Fechar"
          @click="closeModal"
        >
          ×
        </button>

      </header>

      <form
        class="maintenance-plan-form"
        @submit.prevent="handleSubmit"
      >

        <div class="plan-form-main">

          <div class="form-group">

            <label for="plan-name">
              Nome *
            </label>

            <input
              id="plan-name"
              v-model="planForm.name"
              type="text"
              maxlength="100"
              placeholder="Ex: Revisão Preventiva 250h"
              required
            />

          </div>


          <div class="form-group">

            <label for="plan-description">
              Descrição
            </label>

            <textarea
              id="plan-description"
              v-model="planForm.description"
              rows="3"
              placeholder="Descreva o objetivo deste plano..."
            ></textarea>

          </div>

        </div>

        <div class="plan-items-editor">

          <div class="plan-items-editor-header">

            <div>
              <h3>
                Serviços de manutenção
              </h3>

              <p>
                Defina os serviços, intervalos e alertas deste plano.
              </p>
            </div>


            <button
              type="button"
              class="add-plan-item-button"
              @click="addItem"
            >
              <Plus :size="16" />

              Adicionar serviço
            </button>

          </div>

          <div
            v-for="(item, index) in planForm.items"
            :key="item.id ?? index"
            class="plan-form-item"
          >

            <div class="plan-form-item-header">

              <strong>
                Serviço {{ index + 1 }}
              </strong>


              <button
                type="button"
                class="remove-plan-item-button"
                :disabled="planForm.items.length === 1"
                aria-label="Remover serviço"
                @click="removeItem(index)"
              >
                <Trash2 :size="16" />
              </button>

            </div>

            <div class="plan-item-form-grid">

              <div class="form-group service-field">

                <label>
                  Serviço *
                </label>

                <input
                  v-model="item.serviceDescription"
                  type="text"
                  maxlength="200"
                  placeholder="Ex: Troca de óleo do motor"
                  required
                />

              </div>

              <div class="form-group">

                <label>
                  Intervalo *
                </label>

                <div class="hours-input">

                  <input
                    v-model.number="item.intervalHours"
                    type="number"
                    min="0.01"
                    step="0.01"
                    placeholder="250"
                    required
                  />

                  <span>
                    h
                  </span>

                </div>

              </div>

              <div class="form-group">

                <label>
                  Margem de alerta *
                </label>

                <div class="hours-input">

                  <input
                    v-model.number="item.alertMarginHours"
                    type="number"
                    min="0"
                    step="0.01"
                    required
                  />

                  <span>
                    h
                  </span>

                </div>

              </div>

            </div>

          </div>

        </div>

        <footer class="modal-footer">

          <button
            type="button"
            class="cancel-button"
            @click="closeModal"
          >
            Cancelar
          </button>


          <button
            type="submit"
            class="save-button"
          >
            {{
              isEditing
                ? 'Salvar Alterações'
                : 'Cadastrar Plano'
            }}
          </button>

        </footer>

      </form>

    </section>

  </div>
</template>