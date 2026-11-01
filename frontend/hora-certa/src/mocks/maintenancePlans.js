export const maintenancePlanMocks = [
  {
    id: 1,
    name: 'Revisão Preventiva 250h',
    description: 'Manutenção preventiva realizada a cada 250 horas de operação.',
    color: 'green',

    active: true,

    items: [
      {
        id: 1,
        serviceDescription: 'Troca de óleo do motor',
        intervalHours: 250,
        alertMarginHours: 20
      },
      {
        id: 2,
        serviceDescription: 'Troca do filtro de óleo',
        intervalHours: 250,
        alertMarginHours: 20
      },
      {
        id: 3,
        serviceDescription: 'Inspeção do sistema hidráulico',
        intervalHours: 250,
        alertMarginHours: 30
      }
    ]
  },

  {
    id: 2,
    name: 'Revisão Preventiva 500h',
    description: 'Revisão intermediária dos principais sistemas do equipamento.',
    color: 'purple',
    active: true,

    items: [
      {
        id: 4,
        serviceDescription: 'Troca do filtro de combustível',
        intervalHours: 500,
        alertMarginHours: 40
      },
      {
        id: 5,
        serviceDescription: 'Inspeção do sistema de freios',
        intervalHours: 500,
        alertMarginHours: 50
      },
      {
        id: 6,
        serviceDescription: 'Verificação das correias',
        intervalHours: 500,
        alertMarginHours: 40
      }
    ]
  },

  {
    id: 3,
    name: 'Revisão Geral 1000h',
    description: 'Revisão completa para equipamentos com alto tempo de operação.',
    color: 'blue',
    active: false,

    items: [
      {
        id: 7,
        serviceDescription: 'Substituição do óleo hidráulico',
        intervalHours: 1000,
        alertMarginHours: 100
      },
      {
        id: 8,
        serviceDescription: 'Inspeção completa do motor',
        intervalHours: 1000,
        alertMarginHours: 100
      },
      {
        id: 9,
        serviceDescription: 'Inspeção do sistema elétrico',
        intervalHours: 1000,
        alertMarginHours: 80
      },
      {
        id: 10,
        serviceDescription: 'Verificação estrutural do equipamento',
        intervalHours: 1000,
        alertMarginHours: 100
      }
    ]
  }
];