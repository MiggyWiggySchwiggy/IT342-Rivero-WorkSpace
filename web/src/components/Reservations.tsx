import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';
import type { ReservationHistoryItem } from '../types/workspace';

const formatDateTime = (value: string): string => {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return value;
    return date.toLocaleString('en-PH', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
        hour: 'numeric',
        minute: '2-digit',
    });
};

const Reservations: React.FC = () => {
    const navigate = useNavigate();
    const [reservations, setReservations] = useState<ReservationHistoryItem[]>([]);
    const [loading, setLoading] = useState(true);
    const [errorMsg, setErrorMsg] = useState('');

    useEffect(() => {
        const fetchReservations = async () => {
            try {
                const response = await api.get('/reservations/my');
                const payload = Array.isArray(response.data)
                    ? response.data
                    : Array.isArray(response.data?.data)
                        ? response.data.data
                        : [];
                setReservations(payload);
            } catch (error) {
                setErrorMsg('Failed to load reservations. Please check if backend is running.');
            } finally {
                setLoading(false);
            }
        };

        fetchReservations();
    }, []);

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
                    <Link to="/dashboard">Dashboard</Link>
                    <Link to="/reservations" className="active">Reservations</Link>
                </nav>
                <button className="secondary-btn" onClick={handleLogout}>Log out</button>
            </header>

            <main className="page reservations-page">
                <section className="page-head">
                    <div>
                        <h1>My Reservations</h1>
                        <p>Track your recent bookings and payment status.</p>
                    </div>
                </section>

                {loading && <div className="results">Loading reservation history...</div>}
                {!loading && errorMsg && <div className="alert">{errorMsg}</div>}

                {!loading && !errorMsg && reservations.length === 0 && (
                    <div className="detail-panel">
                        <h2>No Reservations Yet</h2>
                        <p>You have not completed a booking yet. Browse spaces and make your first reservation.</p>
                        <div className="ws-actions" style={{ maxWidth: '260px' }}>
                            <button className="primary-btn" onClick={() => navigate('/dashboard')}>
                                Browse Workspaces
                            </button>
                        </div>
                    </div>
                )}

                {!loading && reservations.length > 0 && (
                    <section className="reservation-list">
                        {reservations.map((item) => (
                            <article className="reservation-card" key={item.reservationId}>
                                <div className="reservation-top">
                                    <div>
                                        <h3>{item.spaceName}</h3>
                                        <p>{item.spaceLocation}</p>
                                    </div>
                                    <div className="reservation-badges">
                                        <span className={`slot ${item.status === 'CONFIRMED' ? 'open' : 'closed'}`}>{item.status}</span>
                                        <span className={`slot ${item.paymentStatus === 'PAID' ? 'open' : 'closed'}`}>{item.paymentStatus}</span>
                                    </div>
                                </div>

                                <div className="reservation-grid">
                                    <div>
                                        <span className="summary-label">Reservation ID</span>
                                        <strong>#{item.reservationId}</strong>
                                    </div>
                                    <div>
                                        <span className="summary-label">Workspace ID</span>
                                        <strong>{item.spaceId}</strong>
                                    </div>
                                    <div>
                                        <span className="summary-label">Check-in</span>
                                        <strong>{formatDateTime(item.startTime)}</strong>
                                    </div>
                                    <div>
                                        <span className="summary-label">Check-out</span>
                                        <strong>{formatDateTime(item.endTime)}</strong>
                                    </div>
                                    <div>
                                        <span className="summary-label">Total Paid</span>
                                        <strong>PHP {item.totalAmount}</strong>
                                    </div>
                                    <div>
                                        <span className="summary-label">Booked At</span>
                                        <strong>{item.createdAt ? formatDateTime(item.createdAt) : 'N/A'}</strong>
                                    </div>
                                </div>
                            </article>
                        ))}
                    </section>
                )}
            </main>
        </>
    );
};

export default Reservations;
