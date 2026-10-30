export const workOrderMocks = [

  {
    id: 1,
    orderNumber: 'OS-001',

    equipmentId: 1,
    equipmentCode: 'ESC-01',
    equipmentName: 'Escavadeira Hidráulica 320',
    equipmentCategory: 'Escavadeira',

    maintenancePlanId: 1,
    maintenancePlanName: 'Revisão Preventiva 500h',

    scheduledDate: '2026-11-05',
    scheduledHorometer: 1500,

    status: 'SCHEDULED',

    observation:
      'Realizar revisão preventiva ao atingir aproximadamente 1.500 horas.',

    startedAt: null,
    completedAt: null,
    completionHorometer: null
  },


  {
    id: 2,
    orderNumber: 'OS-002',

    equipmentId: 2,
    equipmentCode: 'CAM-04',
    equipmentName: 'Caminhão Articulado A40G',
    equipmentCategory: 'Caminhão Articulado',

    maintenancePlanId: 2,
    maintenancePlanName: 'Revisão do Sistema Hidráulico',

    scheduledDate: '2026-10-30',
    scheduledHorometer: 2100,

    status: 'IN_PROGRESS',

    observation:
      'Verificar mangueiras, conexões e nível do fluido hidráulico.',

    startedAt: '2026-10-30T08:15:00',
    completedAt: null,
    completionHorometer: null
  },


  {
    id: 3,
    orderNumber: 'OS-003',

    equipmentId: 3,
    equipmentCode: 'GER-01',
    equipmentName: 'Gerador Estacionário 500kVA',
    equipmentCategory: 'Gerador',

    maintenancePlanId: 3,
    maintenancePlanName: 'Manutenção Preventiva do Gerador',

    scheduledDate: '2026-10-24',
    scheduledHorometer: 850,

    status: 'COMPLETED',

    observation:
      'Manutenção preventiva concluída sem irregularidades.',

    startedAt: '2026-10-24T09:00:00',
    completedAt: '2026-10-24T11:35:00',
    completionHorometer: 852
  },


  {
    id: 4,
    orderNumber: 'OS-004',

    equipmentId: 1,
    equipmentCode: 'ESC-01',
    equipmentName: 'Escavadeira Hidráulica 320',
    equipmentCategory: 'Escavadeira',

    maintenancePlanId: 4,
    maintenancePlanName: 'Inspeção do Sistema Hidráulico',

    scheduledDate: '2026-11-12',
    scheduledHorometer: 1520,

    status: 'SCHEDULED',

    observation:
      'Inspecionar possíveis vazamentos e condições das mangueiras.',

    startedAt: null,
    completedAt: null,
    completionHorometer: null
  },


  {
    id: 5,
    orderNumber: 'OS-005',

    equipmentId: 4,
    equipmentCode: 'PAG-02',
    equipmentName: 'Pá Carregadeira WA380',
    equipmentCategory: 'Pá Carregadeira',

    maintenancePlanId: 1,
    maintenancePlanName: 'Revisão Preventiva 500h',

    scheduledDate: '2026-10-18',
    scheduledHorometer: 3200,

    status: 'CANCELED',

    observation:
      'Ordem cancelada após equipamento ser colocado como inativo.',

    startedAt: null,
    completedAt: null,
    completionHorometer: null
  }

];