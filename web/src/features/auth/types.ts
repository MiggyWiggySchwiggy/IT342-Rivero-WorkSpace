export interface User {
    id: number;
    email: string;
    firstname: string;
    lastname: string;
    role: 'ROLE_USER' | 'ROLE_ADMIN';
}
