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

