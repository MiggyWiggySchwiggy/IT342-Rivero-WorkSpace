import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

const WORKSPACES = [
    { id: 1, name: 'Quiet Workspace Box', location: 'Building A · Floor 2', type: 'Pod', capacity: 1, rate: '₱150/hr', rating: '4.8', available: true },
    { id: 2, name: 'Focus Pod Alpha', location: 'Building A · Floor 3', type: 'Pod', capacity: 2, rate: '₱200/hr', rating: '4.6', available: true },
    { id: 3, name: 'Creative Studio', location: 'Building B · Floor 1', type: 'Studio', capacity: 6, rate: '₱500/hr', rating: '4.9', available: false },
    { id: 4, name: 'Standard Meeting Room', location: 'Building A · Floor 1', type: 'Meeting Room', capacity: 10, rate: '₱350/hr', rating: '4.5', available: true },
    { id: 5, name: 'Executive Boardroom', location: 'Building C · Floor 5', type: 'Boardroom', capacity: 16, rate: '₱800/hr', rating: '4.7', available: true },
    { id: 6, name: 'Open Collaborative Area', location: 'Building B · Floor 2', type: 'Open Space', capacity: 20, rate: '₱100/hr', rating: '4.4', available: true },
];

const Dashboard: React.FC = () => {
    const navigate = useNavigate();
    const [search, setSearch] = useState('');
    const [typeFilter, setTypeFilter] = useState('');
    const [capacityFilter, setCapacityFilter] = useState('');

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('userRole');
        navigate('/login');
    };

    const filtered = WORKSPACES.filter((ws) => {
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
                    <Link to="/login">Login</Link>
                </nav>
                <button className="secondary-btn" onClick={handleLogout}>Log out</button>
            </header>

            <main className="page">
                <section className="page-head">
                    <div>
                        <h1>Workspaces</h1>
                        <p>Browse and book available spaces.</p>
                    </div>
                    <button className="primary-btn">+ New Booking</button>
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

                <div className="results">{filtered.length} result{filtered.length !== 1 ? 's' : ''}</div>

                <section className="grid">
                    {filtered.map((ws) => (
                        <article className="ws-card" key={ws.id}>
                            <div className="ws-type">{ws.type}</div>
                            <h3 className="ws-name">{ws.name}</h3>
                            <div className="ws-location">{ws.location}</div>
                            <div className="ws-meta">
                                <span>{ws.capacity} {ws.capacity === 1 ? 'person' : 'people'} · {ws.rate}</span>
                                <span>★ {ws.rating} · {ws.available ? 'Available' : 'Occupied'}</span>
                            </div>
                            <div className="ws-actions">
                                <button className="secondary-btn">Details</button>
                                <button className="primary-btn" disabled={!ws.available}>
                                    {ws.available ? 'Book' : 'Unavailable'}
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
