export const dashboardFleetMock = {
  total: 28,
  ok: 21,
  warning: 4,
  overdue: 3
};


export const dashboardStatsMock = [
  {
    id: 'equipment',
    value: 28,
    label: 'Equipamentos',
    description: 'Total da frota',
    variant: 'default'
  },

  {
    id: 'ok',
    value: 21,
    label: 'Em dia',
    description: '75% da frota',
    variant: 'success'
  },

  {
    id: 'warning',
    value: 4,
    label: 'Em atenção',
    description: 'Necessitam planejamento',
    variant: 'warning'
  },

  {
    id: 'overdue',
    value: 3,
    label: 'Vencidos',
    description: 'Ação necessária',
    variant: 'danger'
  }
];


export const upcomingMaintenancesMock = [
  {
    id: 1,
    code: 'ESC-01',
    equipment: 'Escavadeira Hidráulica 320',
    category: 'Escavadeira',
    maintenanceType: 'Troca de óleo do motor e filtros',
    progress: 92,
    status: 'warning',
    expectedDate: '27/10/2026',
    remainingText: 'em 42h'
  },

  {
    id: 2,
    code: 'CAM-04',
    equipment: 'Caminhão Articulado A40G',
    category: 'Caminhão Articulado',
    maintenanceType: 'Revisão do trem de força e freios',
    progress: 104,
    status: 'overdue',
    expectedDate: '28/10/2026',
    remainingText: 'atraso de 50h'
  },

  {
    id: 3,
    code: 'GER-01',
    equipment: 'Gerador Estacionário 500kVA',
    category: 'Gerador',
    maintenanceType: 'Troca de filtros',
    progress: 67,
    status: 'ok',
    expectedDate: '29/10/2026',
    remainingText: 'em 120h'
  },

  {
    id: 4,
    code: 'PAG-02',
    equipment: 'Pá Carregadeira WA380',
    category: 'Pá Carregadeira',
    maintenanceType: 'Lubrificação do sistema hidráulico',
    progress: 40,
    status: 'ok',
    expectedDate: '02/11/2026',
    remainingText: 'em 5 dias'
  }
];