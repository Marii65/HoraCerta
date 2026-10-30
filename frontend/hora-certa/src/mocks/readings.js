// Dados fictícios utilizados enquanto o frontend ainda não está conectado à API.
// A estrutura segue os principais campos de HorometerReading do backend.

export const readingMocks = [
  {
    id: 1,

    equipmentId: 1,
    equipmentCode: 'ESC-01',
    equipmentName: 'Escavadeira Hidráulica 320',
    equipmentCategory: 'Escavadeira',

    readingValue: 1485,
    previousReadingValue: 1420,

    readAt: '2026-10-26T14:32:00',

    responsibleName: 'Maria Luiza',

    observation: 'Leitura realizada no início do turno.',

    evidence: null
  },

  {
    id: 2,

    equipmentId: 2,
    equipmentCode: 'CAM-04',
    equipmentName: 'Caminhão Articulado A40G',
    equipmentCategory: 'Caminhão Articulado',

    readingValue: 2098,
    previousReadingValue: 2040,

    readAt: '2026-10-26T10:15:00',

    responsibleName: 'João Silva',

    observation: 'Leitura registrada após inspeção do equipamento.',

    evidence: null
  },

  {
    id: 3,

    equipmentId: 3,
    equipmentCode: 'GER-01',
    equipmentName: 'Gerador Estacionário 500kVA',
    equipmentCategory: 'Gerador',

    readingValue: 867,
    previousReadingValue: 820,

    readAt: '2026-10-25T16:48:00',

    responsibleName: 'Maria Luiza',

    observation: '',

    evidence: null
  },

  {
    id: 4,

    equipmentId: 1,
    equipmentCode: 'ESC-01',
    equipmentName: 'Escavadeira Hidráulica 320',
    equipmentCategory: 'Escavadeira',

    readingValue: 1420,
    previousReadingValue: 1360,

    readAt: '2026-10-22T08:20:00',

    responsibleName: 'Ana Costa',

    observation: 'Leitura periódica.',

    evidence: null
  },

  {
    id: 5,

    equipmentId: 4,
    equipmentCode: 'PAG-02',
    equipmentName: 'Pá Carregadeira WA380',
    equipmentCategory: 'Pá Carregadeira',

    readingValue: 3215,
    previousReadingValue: 3168,

    readAt: '2026-10-20T13:10:00',

    responsibleName: 'João Silva',

    observation: 'Última leitura antes da inativação do equipamento.',

    evidence: null
  },

  {
    id: 6,

    equipmentId: 2,
    equipmentCode: 'CAM-04',
    equipmentName: 'Caminhão Articulado A40G',
    equipmentCategory: 'Caminhão Articulado',

    readingValue: 2040,
    previousReadingValue: 1985,

    readAt: '2026-10-18T09:05:00',

    responsibleName: 'Maria Luiza',

    observation: '',

    evidence: null
  },

  {
    id: 7,

    equipmentId: 3,
    equipmentCode: 'GER-01',
    equipmentName: 'Gerador Estacionário 500kVA',
    equipmentCategory: 'Gerador',

    readingValue: 820,
    previousReadingValue: 780,

    readAt: '2026-10-15T15:40:00',

    responsibleName: 'Ana Costa',

    observation: 'Funcionamento normal.',

    evidence: null
  }
];