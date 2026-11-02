import { apiRequest } from './api';

async function handleResponse(response, defaultMessage) {
    if (response.ok) {
        if (response.status === 204) return null;
        return response.json();
    }
    if (response.status === 403) {
        throw new Error(
            'Você não possui permissão para realizar esta ação.'
        );
    }
    let message = defaultMessage;
    try {
        const errorData = await response.json();
        message =
            errorData.message ||
            errorData.error ||
            defaultMessage;
    } catch { }
    throw new Error(message);
}

export async function createHorometerReading(data) {
    const response = await apiRequest(
        '/api/horometer-readings',
        {
            method: 'POST',
            body: JSON.stringify({
                equipmentId: Number(data.equipmentId),
                readingValue: Number(data.readingValue),
                observation: data.observation || null
            })
        }
    );
    return handleResponse(
        response,
        'Não foi possível registrar a leitura.'
    );
}

export async function getHorometerReadingsByEquipment(
    equipmentId
) {
    const response = await apiRequest(
        `/api/horometer-readings/equipment/${equipmentId}`,
        {
            method: 'GET'
        }
    );

    return handleResponse(
        response,
        'Não foi possível carregar as leituras.'
    );
}

export async function getHorometerReadingsByPeriod(
    equipmentId,
    startDate,
    endDate
) {
    const params = new URLSearchParams({
        startDate,
        endDate
    });

    const response = await apiRequest(
        `/api/horometer-readings/equipment/${equipmentId}/period?${params}`,
        {
            method: 'GET'
        }
    );

    return handleResponse(
        response,
        'Não foi possível carregar as leituras do período.'
    );
}

export async function getHorometerReadings() {
  const response = await apiRequest(
    '/api/horometer-readings',
    {
      method: 'GET'
    }
  );

  return handleResponse(
    response,
    'Não foi possível carregar as leituras.'
  );
}