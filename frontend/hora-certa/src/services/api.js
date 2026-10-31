export const API_URL = 'http://localhost:8080/api';

export function getToken(){
    return localStorage.getItem(
        'horacerta_token'
    );
}

export function getAuthHeaders(){
    const token = getToken();

    return{
        'Content-Type': 'application/json',
        ...(token &&{
            Authorization: `Bearer ${token}`
        })
    };
}