export interface Space {
    id: string; // Or number, depending on your Spring Boot UUID setup
    name: string;
    location: string;
    type: string;
    capacity: number;
    hourlyRate: number;
    rating: number;
    available: boolean;
    description?: string;
    imageUrl?: string;
}

export interface ReservationPayload {
    spaceId: string;
    startTime: string;
    endTime: string;
    paymentMethod: {
        cardNumber: string;
        expiryDate: string;
        cvv: string;
    };
}

export interface ReservationHistoryItem {
    reservationId: number;
    spaceId: string;
    spaceName: string;
    spaceLocation: string;
    status: string;
    paymentStatus: string;
    startTime: string;
    endTime: string;
    totalAmount: string;
    createdAt?: string;
}