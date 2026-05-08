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