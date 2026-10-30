// Dados fictícios utilizados enquanto o frontend ainda não está conectado à API.
// A estrutura segue os campos utilizados pelo Equipment no backend.

export const equipmentMocks = [
  {
    id: 1,
    code: 'ESC-01',
    name: 'Escavadeira Hidráulica 320',
    model: 'CAT 320',
    category: 'Escavadeira',
    serialNumber: 'CAT320-001',
    currentHorometer: 1485,
    status: 'ACTIVE'
  },
  {
    id: 2,
    code: 'CAM-04',
    name: 'Caminhão Articulado A40G',
    model: 'Volvo A40G',
    category: 'Caminhão Articulado',
    serialNumber: 'VOL-A40G-004',
    currentHorometer: 2098,
    status: 'ACTIVE'
  },
  {
    id: 3,
    code: 'GER-01',
    name: 'Gerador Estacionário 500kVA',
    model: 'Cummins C500D5',
    category: 'Gerador',
    serialNumber: 'CUM-500-001',
    currentHorometer: 867,
    status: 'ACTIVE'
  },
  {
    id: 4,
    code: 'PAG-02',
    name: 'Pá Carregadeira WA380',
    model: 'Komatsu WA380',
    category: 'Pá Carregadeira',
    serialNumber: 'KOM-WA380-002',
    currentHorometer: 3215,
    status: 'INACTIVE'
  }
];