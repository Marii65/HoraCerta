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
  } catch {}
  throw new Error(message);
}

export async function getWorkOrders() {
  const response = await apiRequest(
    '/api/work-orders',
    {
      method: 'GET'
    }
  );
  return handleResponse(
    response,
    'Não foi possível carregar as ordens de serviço.'
  );
}

export async function createWorkOrder(data) {
  const response = await apiRequest(
    '/api/work-orders',
    {
      method: 'POST',
      body: JSON.stringify(data)
    }
  );
  return handleResponse(
    response,
    'Não foi possível criar a ordem de serviço.'
  );
}

export async function startWorkOrder(id) {
  const response = await apiRequest(
    `/api/work-orders/${id}/start`,
    {
      method: 'PATCH'
    }
  );
  return handleResponse(
    response,
    'Não foi possível iniciar a ordem de serviço.'
  );
}

export async function completeWorkOrder(
  id,
  executedHorometer
) {
  const response = await apiRequest(
    `/api/work-orders/${id}/complete`,
    {
      method: 'PATCH',

      body: JSON.stringify({
        executedHorometer:
          Number(executedHorometer)
      })
    }
  );
  return handleResponse(
    response,
    'Não foi possível concluir a ordem de serviço.'
  );
}

export async function cancelWorkOrder(id) {
  const response = await apiRequest(
    `/api/work-orders/${id}/cancel`,
    {
      method: 'PATCH'
    }
  );
  return handleResponse(
    response,
    'Não foi possível cancelar a ordem de serviço.'
  );
}