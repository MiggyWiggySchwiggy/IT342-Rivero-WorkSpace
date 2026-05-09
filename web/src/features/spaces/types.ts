export interface Space {
    id: string;
    name: string;
    location: string;
    type: string;
    capacity: number;
    hourlyRate: number;
    rating: number;
    available: boolean;
    description?: string;
    imageUrl?: string;
    amenities?: string;      // Comma-separated list
    utilities?: string;      // Comma-separated list
    checkInWindow?: string;
    cancellationPolicy?: string;
}

