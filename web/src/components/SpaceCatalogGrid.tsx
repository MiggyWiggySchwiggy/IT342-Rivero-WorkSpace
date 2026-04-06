import React, { useEffect, useState } from 'react';
import type { Space } from '../types/workspace';
import { apiClient } from '../api/client';

interface Props {
  onSelectSpace: (spaceId: string) => void;
}

export const SpaceCatalogGrid: React.FC<Props> = ({ onSelectSpace }) => {
  const [spaces, setSpaces] = useState<Space[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchSpaces = async () => {
      try {
        const response = await apiClient.get<Space[]>('/spaces');
        setSpaces(response.data);
      } catch (error) {
        console.error('Failed to fetch spaces', error);
      } finally {
        setLoading(false);
      }
    };

    fetchSpaces();
  }, []);

  if (loading) return <div className="p-8 text-center text-gray-500">Loading catalog...</div>;

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 p-6">
      {spaces.map((space) => (
        <div key={space.id} className="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-lg transition-shadow">
          <img src={space.imageUrl || '/placeholder.jpg'} alt={space.name} className="w-full h-48 object-cover" />
          <div className="p-5">
            <h3 className="text-xl font-bold text-gray-900">{space.name}</h3>
            <p className="text-gray-500 mt-1">Capacity: up to {space.capacity} people</p>
            <p className="text-blue-600 font-semibold mt-2">${space.hourlyRate.toFixed(2)} / hr</p>
            <button
              onClick={() => onSelectSpace(space.id)}
              className="mt-4 w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition-colors"
            >
              View & Book
            </button>
          </div>
        </div>
      ))}
    </div>
  );
};
