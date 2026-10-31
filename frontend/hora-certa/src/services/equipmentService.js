import { API_URL, getAuthHeaders } from 'http://localhost:8080/api/equipments';

export async function getEquipments() {

    const response = await fetch(
        `${API_URL}/equipments`,
        {
            method: 'GET',
            headers: getAuthHeaders()
        });

    if (!response.ok) {
        if (response.status === 401) {
            throw new Error(
                'Sua sessão não é válida. Faça login novamente.'
            );
        }

        if (response.status === 403) {
            throw new Error(
                'Você não possui permissão para acessar os equipamentos.'
            )
        }

        throw new Error(
            'Não foi possível carregar os equipamentos.'
        );
    }

    return response.json();
}