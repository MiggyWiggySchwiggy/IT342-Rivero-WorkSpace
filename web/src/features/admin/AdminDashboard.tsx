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
                    <Link to="/dashboard">Dashboard</Link>
                    <Link to="/reservations">Reservations</Link>
                </nav>
                <button className="secondary-btn" onClick={handleLogout}>Log out</button>
            </header>

            <main className="page">
                <section className="page-head">
                    <div>
                        <h1>Admin Dashboard</h1>
                        <p>Manage workspaces, users, and reservations.</p>
                    </div>
                </section>
            </main>
        </>
    );
};

export default AdminDashboard;
