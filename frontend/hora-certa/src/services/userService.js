import { apiRequest } from './api';

const API_URL = 'http://localhost:8080/api/users';

export async function getCurrentUser() {

    const response = await apiRequest(
        '/api/users/me',
        {
            method: 'GET'
        }
    );

    if (!response.ok) {
        throw new Error(
            'Não foi possível carregar os dados do usuário.'
        );
    }

    return response.json();
}