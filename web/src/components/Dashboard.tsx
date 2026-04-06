import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axiosConfig';
import type { Space } from '../types/workspace';

const SAMPLE_WORKSPACES: Space[] = [
    {
        id: 'sample-pod-1',
        name: 'Focus Pod A1',
        location: 'Cebu IT Park',
        type: 'Pod',
        capacity: 1,
        hourlyRate: 120,
        rating: 4.7,
        available: true,
    },
    {
        id: 'sample-meeting-1',
        name: 'Sprint Room B',
        location: 'Ayala Center Cebu',
        type: 'Meeting Room',
        capacity: 8,
        hourlyRate: 450,
        rating: 4.8,
        available: true,
    },
    {
        id: 'sample-studio-1',
        name: 'Creator Studio C3',
        location: 'Mandaue City',
        type: 'Studio',
        capacity: 4,
        hourlyRate: 380,
        rating: 4.6,
        available: false,
    },
    {
        id: 'sample-boardroom-1',
        name: 'Boardroom Skyline',
        location: 'Cebu Business Park',
        type: 'Boardroom',
        capacity: 14,
        hourlyRate: 900,
        rating: 4.9,
        available: true,
    },
];

const Dashboard: React.FC = () => {
    const navigate = useNavigate();
    const [search, setSearch] = useState('');
    const [typeFilter, setTypeFilter] = useState('');
    const [capacityFilter, setCapacityFilter] = useState('');

    // Replaced hardcoded array with state
    const [workspaces, setWorkspaces] = useState<Space[]>([]);
    const [loading, setLoading] = useState(true);
    const [usingSampleData, setUsingSampleData] = useState(false);

    useEffect(() => {
        const fetchSpaces = async () => {
            try {
                const response = await api.get('/spaces');
                const apiSpaces = Array.isArray(response.data)
                    ? response.data
                    : Array.isArray(response.data?.data)
                        ? response.data.data
                        : [];

                if (apiSpaces.length > 0) {
                    setWorkspaces(apiSpaces);
                    setUsingSampleData(false);
                } else {
                    setWorkspaces(SAMPLE_WORKSPACES);
                    setUsingSampleData(true);
                }
            } catch (error) {
                console.error('Failed to fetch spaces', error);
                setWorkspaces(SAMPLE_WORKSPACES);
                setUsingSampleData(true);
            } finally {
                setLoading(false);
            }
        };
        fetchSpaces();
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('userRole');
        navigate('/login');
    };

    const filtered = workspaces.filter((ws) => {
        const searchMatch =
            ws.name.toLowerCase().includes(search.toLowerCase()) ||
            ws.location.toLowerCase().includes(search.toLowerCase());

        const typeMatch = typeFilter === '' || ws.type === typeFilter;

        const capacityMatch =
            capacityFilter === '' ||
            (capacityFilter === 'small' && ws.capacity <= 2) ||
            (capacityFilter === 'medium' && ws.capacity > 2 && ws.capacity <= 10) ||
            (capacityFilter === 'large' && ws.capacity > 10);

        return searchMatch && typeMatch && capacityMatch;
    });

    return (
        <>
            <header className="topbar">
                <div className="brand">WorkSpace</div>
                <nav className="topbar-links">
                    <Link to="/dashboard" className="active">Dashboard</Link>
                    <Link to="/reservations">Reservations</Link>
                </nav>
                <button className="secondary-btn" onClick={handleLogout}>Log out</button>
            </header>

            <main className="page">
                <section className="page-head">
                    <div>
                        <h1>Workspaces</h1>
                        <p>Browse and book available spaces.</p>
                    </div>
                </section>

                <section className="filters">
                    <input
                        type="text"
                        placeholder="Search by name or location…"
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                    />
                    <select value={typeFilter} onChange={(e) => setTypeFilter(e.target.value)}>
                        <option value="">All Types</option>
                        <option value="Pod">Pod</option>
                        <option value="Studio">Studio</option>
                        <option value="Meeting Room">Meeting Room</option>
                        <option value="Boardroom">Boardroom</option>
                        <option value="Open Space">Open Space</option>
                    </select>
                    <select value={capacityFilter} onChange={(e) => setCapacityFilter(e.target.value)}>
                        <option value="">Any Capacity</option>
                        <option value="small">1–2 people</option>
                        <option value="medium">3–10 people</option>
                        <option value="large">11+ people</option>
                    </select>
                </section>

                {loading ? (
                    <div className="results">Loading catalog...</div>
                ) : (
                    <div className="results">{filtered.length} result{filtered.length !== 1 ? 's' : ''}</div>
                )}

                {!loading && usingSampleData && (
                    <div className="results">Showing sample workspaces while live catalog is unavailable.</div>
                )}

                <section className="grid">
                    {filtered.map((ws) => (
                        <article className="ws-card" key={ws.id}>
                            <div className="ws-type">{ws.type}</div>
                            <h3 className="ws-name">{ws.name}</h3>
                            <div className="ws-location">{ws.location}</div>
                            <div className="ws-meta">
                                <span>{ws.capacity} {ws.capacity === 1 ? 'person' : 'people'} · ₱{ws.hourlyRate}/hr</span>
                                <span>★ {ws.rating} · {ws.available ? 'Available' : 'Occupied'}</span>
                            </div>
                            <div className="ws-actions">
                                <button
                                    className="primary-btn"
                                    disabled={!ws.available}
                                    onClick={() => navigate(`/space/${ws.id}`, { state: { space: ws } })}
                                >
                                    {ws.available ? 'View & Book' : 'Unavailable'}
                                </button>
                            </div>
                        </article>
                    ))}
                </section>
            </main>
        </>
    );
};

export default Dashboard;