import React from 'react';
import { Link, useNavigate } from 'react-router-dom';

const AdminDashboard: React.FC = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('userRole');
        navigate('/login');
    };

    return (
        <>
            <header className="topbar">
                <div className="brand">WorkSpace</div>
                <nav className="topbar-links">
                    <Link to="/admin" className="active">Admin</Link>
                    <Link to="/admin/workspaces">Manage Spaces</Link>
                    <Link to="/admin/reservations">Reservations</Link>
                    <Link to="/admin/availability">Availability</Link>
                    <Link to="/dashboard">Dashboard</Link>
                </nav>
                <button className="secondary-btn" onClick={handleLogout}>Log out</button>
            </header>

            <main className="page">
                <section className="page-head">
                    <div>
                        <h1>Admin Dashboard</h1>
                        <p>Manage workspaces, reservations, and scheduling.</p>
                    </div>
                </section>

                <div className="grid">
                    <Link to="/admin/workspaces" className="ws-card" style={{ textDecoration: 'none', color: 'inherit' }}>
                        <span className="ws-type">Spaces</span>
                        <h3 className="ws-name">Manage Workspaces</h3>
                        <p className="ws-location">Create, edit, and delete workspace listings.</p>
                    </Link>
                    <Link to="/admin/reservations" className="ws-card" style={{ textDecoration: 'none', color: 'inherit' }}>
                        <span className="ws-type">Bookings</span>
                        <h3 className="ws-name">All Reservations</h3>
                        <p className="ws-location">View and cancel user bookings across all spaces.</p>
                    </Link>
                    <Link to="/admin/availability" className="ws-card" style={{ textDecoration: 'none', color: 'inherit' }}>
                        <span className="ws-type">Schedule</span>
                        <h3 className="ws-name">Manage Availability</h3>
                        <p className="ws-location">Define operating hours and block time slots.</p>
                    </Link>
                </div>
            </main>
        </>
    );
};

export default AdminDashboard;
