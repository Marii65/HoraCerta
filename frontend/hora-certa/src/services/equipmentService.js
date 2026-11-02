import { apiRequest } from './api';

async function handleResponse(response, defaultMessage) {
    if (response.ok) {
        if (response.status === 204) {
            return null;
        }

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
    } catch {
    }

    throw new Error(message);
}

export async function getEquipments() {
    const response = await apiRequest(
        '/api/equipments',
        {
            method: 'GET'
        }
    );

    return handleResponse(
        response,
        'Não foi possível carregar os equipamentos.'
    );
}

export async function getEquipmentById(id) {
  const response = await apiRequest(
    `/api/equipments/${id}`,
    {
      method: 'GET'
    }
  );

  return handleResponse(
    response,
    'Não foi possível carregar o equipamento.'
  );
}

export async function createEquipment(data) {
  const response = await apiRequest(
    '/api/equipments',
    {
      method: 'POST',
      body: JSON.stringify(data)
    }
  );

  return handleResponse(
    response,
    'Não foi possível cadastrar o equipamento.'
  );
}

export async function updateEquipment(id, data) {
  const response = await apiRequest(
    `/api/equipments/${id}`,
    {
      method: 'PUT',
      body: JSON.stringify(data)
    }
  );

  return handleResponse(
    response,
    'Não foi possível atualizar o equipamento.'
  );
}

export async function deleteEquipment(id) {
  const response = await apiRequest(
    `/api/equipments/${id}`,
    {
      method: 'DELETE'
    }
  );

  return handleResponse(
    response,
    'Não foi possível excluir o equipamento.'
  );
}