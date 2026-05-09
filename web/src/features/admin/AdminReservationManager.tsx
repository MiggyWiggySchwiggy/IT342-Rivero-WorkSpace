import React, { useEffect, useState, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { fetchAllReservations, adminCancelReservation } from '../shared/axiosConfig';

interface AdminReservation {
    reservationId: number;
    userEmail: string;
    userFirstName: string;
    userLastName: string;
    spaceId: string;
    spaceName: string;
    spaceLocation: string;
    status: string;
    paymentStatus: string;
    startTime: string;
    endTime: string;
    totalAmount: string;
    createdAt: string | null;
}

/* ─── Toast ─── */
interface ToastMsg { id: number; text: string; type: 'success' | 'error' }
const Toast: React.FC<{ msg: ToastMsg; onDone: (id: number) => void }> = ({ msg, onDone }) => {
    useEffect(() => { const t = setTimeout(() => onDone(msg.id), 3500); return () => clearTimeout(t); }, [msg.id, onDone]);
    return (
        <div style={{
            padding: '0.65rem 1rem', borderRadius: '8px', fontSize: '0.84rem', fontWeight: 500,
            background: msg.type === 'success' ? '#f0fdf4' : '#fef2f2',
            color: msg.type === 'success' ? '#16a34a' : '#dc2626',
            border: `1px solid ${msg.type === 'success' ? '#bbf7d0' : '#fecaca'}`,
            boxShadow: '0 4px 12px rgba(0,0,0,.08)',
        }}>{msg.text}</div>
    );
};

const fmt = (iso: string) => {
    try { return new Date(iso).toLocaleString('en-US', { dateStyle: 'medium', timeStyle: 'short' }); }
    catch { return iso; }
};

const statusBadge = (status: string) => {
    const colors: Record<string, { bg: string; fg: string }> = {
        CONFIRMED: { bg: '#dcfce7', fg: '#166534' },
        CANCELLED: { bg: '#fee2e2', fg: '#991b1b' },
        COMPLETED: { bg: '#dbeafe', fg: '#1e40af' },
    };
    const c = colors[status] ?? { bg: '#f3f4f6', fg: '#374151' };
    return (
        <span style={{
            fontSize: '0.72rem', fontWeight: 600, padding: '0.15rem 0.5rem',
            borderRadius: 999, color: c.fg, background: c.bg,
        }}>{status}</span>
    );
};

const AdminReservationManager: React.FC = () => {
    const navigate = useNavigate();
    const [reservations, setReservations] = useState<AdminReservation[]>([]);
    const [loading, setLoading] = useState(true);
    const [toasts, setToasts] = useState<ToastMsg[]>([]);
    const [filter, setFilter] = useState<'ALL' | 'CONFIRMED' | 'CANCELLED'>('ALL');

    const toast = (text: string, type: 'success' | 'error' = 'success') =>
        setToasts(prev => [...prev, { id: Date.now(), text, type }]);
    const removeToast = useCallback((id: number) => setToasts(prev => prev.filter(t => t.id !== id)), []);

    const load = async () => {
        try { setReservations(await fetchAllReservations()); }
        catch { toast('Failed to load reservations.', 'error'); }
        finally { setLoading(false); }
    };

    useEffect(() => { load(); }, []);

    const handleCancel = async (r: AdminReservation) => {
        if (!window.confirm(`Cancel reservation #${r.reservationId} for ${r.spaceName}?\nUser: ${r.userEmail}`)) return;
        try {
            await adminCancelReservation(r.reservationId);
            toast(`Reservation #${r.reservationId} cancelled.`);
            load();
        } catch { toast('Failed to cancel reservation.', 'error'); }
    };

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('userRole');
        navigate('/login');
    };

    const filtered = filter === 'ALL' ? reservations : reservations.filter(r => r.status === filter);

    return (
        <>
            <header className="topbar">
                <div className="brand">WorkSpace</div>
                <nav className="topbar-links">
                    <Link to="/admin">Admin</Link>
                    <Link to="/admin/workspaces">Manage Spaces</Link>
                    <Link to="/admin/reservations" className="active">Reservations</Link>
                    <Link to="/dashboard">Dashboard</Link>
                </nav>
                <button className="secondary-btn" onClick={handleLogout}>Log out</button>
            </header>

            {/* Toasts */}
            <div style={{ position: 'fixed', top: 70, right: 20, zIndex: 9999, display: 'grid', gap: '0.4rem', width: 320 }}>
                {toasts.map(t => <Toast key={t.id} msg={t} onDone={removeToast} />)}
            </div>

            <main className="page" style={{ maxWidth: 1120 }}>
                <section className="page-head">
                    <div>
                        <h1>All Reservations</h1>
                        <p>View and manage every booking across all workspaces.</p>
                    </div>
                    <div style={{ display: 'flex', gap: '0.35rem' }}>
                        {(['ALL', 'CONFIRMED', 'CANCELLED'] as const).map(f => (
                            <button key={f} onClick={() => setFilter(f)}
                                className={filter === f ? 'primary-btn' : 'secondary-btn'}
                                style={{ fontSize: '0.76rem', padding: '0.3rem 0.7rem' }}>{f}</button>
                        ))}
                    </div>
                </section>

                {loading ? (
                    <p style={{ color: 'var(--text-secondary)', fontSize: '0.88rem' }}>Loading reservations…</p>
                ) : filtered.length === 0 ? (
                    <p style={{ color: 'var(--text-secondary)', fontSize: '0.88rem' }}>No reservations found.</p>
                ) : (
                    <div style={{ overflowX: 'auto' }}>
                        <table style={{
                            width: '100%', borderCollapse: 'collapse', fontSize: '0.82rem',
                            background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: 'var(--radius-lg)',
                        }}>
                            <thead>
                                <tr style={{ borderBottom: '1px solid var(--border)', textAlign: 'left' }}>
                                    {['#', 'User', 'Workspace', 'Schedule', 'Amount', 'Status', 'Payment', 'Actions'].map(h => (
                                        <th key={h} style={{
                                            padding: '0.6rem 0.7rem', fontWeight: 600, fontSize: '0.72rem',
                                            color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '0.04em',
                                        }}>{h}</th>
                                    ))}
                                </tr>
                            </thead>
                            <tbody>
                                {filtered.map(r => (
                                    <tr key={r.reservationId} style={{ borderBottom: '1px solid var(--border)' }}>
                                        <td style={{ padding: '0.55rem 0.7rem', fontWeight: 600 }}>{r.reservationId}</td>
                                        <td style={{ padding: '0.55rem 0.7rem' }}>
                                            <div style={{ fontWeight: 500 }}>{r.userFirstName} {r.userLastName}</div>
                                            <div style={{ fontSize: '0.74rem', color: 'var(--text-secondary)' }}>{r.userEmail}</div>
                                        </td>
                                        <td style={{ padding: '0.55rem 0.7rem' }}>
                                            <div style={{ fontWeight: 500 }}>{r.spaceName}</div>
                                            <div style={{ fontSize: '0.74rem', color: 'var(--text-secondary)' }}>{r.spaceLocation}</div>
                                        </td>
                                        <td style={{ padding: '0.55rem 0.7rem', fontSize: '0.78rem' }}>
                                            <div>{fmt(r.startTime)}</div>
                                            <div style={{ color: 'var(--text-secondary)' }}>→ {fmt(r.endTime)}</div>
                                        </td>
                                        <td style={{ padding: '0.55rem 0.7rem', fontWeight: 600 }}>₱{parseFloat(r.totalAmount).toFixed(2)}</td>
                                        <td style={{ padding: '0.55rem 0.7rem' }}>{statusBadge(r.status)}</td>
                                        <td style={{ padding: '0.55rem 0.7rem' }}>{statusBadge(r.paymentStatus)}</td>
                                        <td style={{ padding: '0.55rem 0.7rem' }}>
                                            {r.status === 'CONFIRMED' && (
                                                <button style={{
                                                    fontSize: '0.74rem', padding: '0.28rem 0.55rem', borderRadius: 'var(--radius)',
                                                    border: '1px solid #fecaca', background: '#fef2f2', color: '#dc2626',
                                                    cursor: 'pointer', fontWeight: 500,
                                                }} onClick={() => handleCancel(r)}>Cancel</button>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </main>
        </>
    );
};

export default AdminReservationManager;
