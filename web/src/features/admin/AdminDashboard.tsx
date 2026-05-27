import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../shared/axiosConfig';

interface Reservation {
    id: number;
    user?: { firstName?: string; lastName?: string; email?: string };
    space?: { name?: string };
    startTime: string;
    endTime: string;
    status: string;
    totalAmount?: number;
    spaceName?: string;
    userName?: string;
}

interface Space {
    id: number;
    name: string;
    type: string;
    location: string;
}

interface SpaceStat {
    name: string;
    count: number;
    revenue: number;
}

const AdminDashboard: React.FC = () => {
    const navigate = useNavigate();
    const [reservations, setReservations] = useState<Reservation[]>([]);
    const [spaces, setSpaces] = useState<Space[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [resRes, spacesRes] = await Promise.all([
                    api.get('/reservations/all'),
                    api.get('/spaces'),
                ]);
                const resData = resRes.data?.data ?? resRes.data ?? [];
                const spacesData = spacesRes.data?.data ?? spacesRes.data ?? [];
                setReservations(Array.isArray(resData) ? resData : []);
                setSpaces(Array.isArray(spacesData) ? spacesData : []);
            } catch {
                // fail silently — KPIs will show 0
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('userRole');
        navigate('/login');
    };

    // ── KPI Computations ──
    // Force-parse totalAmount to a real number (backend may return it as a string)
    const toNum = (val: any): number => parseFloat(String(val ?? 0)) || 0;

    const totalBookings = reservations.length;
    const activeBookings = reservations.filter(r => r.status === 'CONFIRMED').length;
    const totalRevenue = reservations.reduce((sum, r) => sum + toNum(r.totalAmount), 0);
    const totalSpaces = spaces.length;

    // Bookings this week
    const now = new Date();
    const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
    const thisWeek = reservations.filter(r => new Date(r.startTime) >= weekAgo).length;

    // Top spaces by bookings
    const spaceMap: Record<string, SpaceStat> = {};
    reservations.forEach(r => {
        const name = r.space?.name ?? r.spaceName ?? 'Unknown';
        if (!spaceMap[name]) spaceMap[name] = { name, count: 0, revenue: 0 };
        spaceMap[name].count += 1;
        spaceMap[name].revenue += toNum(r.totalAmount);
    });
    const topSpaces = Object.values(spaceMap).sort((a, b) => b.count - a.count).slice(0, 5);
    const maxBookings = topSpaces[0]?.count ?? 1;

    // Recent reservations
    const recentReservations = [...reservations]
        .sort((a, b) => new Date(b.startTime).getTime() - new Date(a.startTime).getTime())
        .slice(0, 6);

    const formatDate = (d: string) => new Date(d).toLocaleString('en-PH', {
        month: 'short', day: 'numeric', year: 'numeric', hour: 'numeric', minute: '2-digit',
    });

    const formatPHP = (n: number) => `₱${n.toLocaleString('en-PH', { minimumFractionDigits: 2 })}`;

    const statusBadge = (status: string) => {
        const styles: Record<string, { bg: string; color: string }> = {
            CONFIRMED: { bg: '#dcfce7', color: '#15803d' },
            PENDING: { bg: '#fef9c3', color: '#a16207' },
            CANCELLED: { bg: '#fee2e2', color: '#b91c1c' },
        };
        const s = styles[status] ?? { bg: '#f3f4f6', color: '#374151' };
        return (
            <span style={{
                background: s.bg, color: s.color, fontSize: '0.72rem',
                fontWeight: 600, padding: '0.2rem 0.65rem', borderRadius: '999px',
                textTransform: 'uppercase', letterSpacing: '0.04em',
            }}>{status}</span>
        );
    };

    const kpis = [
        { label: 'Total Revenue', value: formatPHP(totalRevenue), icon: '💰', delta: `${thisWeek} bookings this week`, color: '#4f46e5' },
        { label: 'Total Bookings', value: totalBookings.toString(), icon: '📅', delta: `${thisWeek} in last 7 days`, color: '#0891b2' },
        { label: 'Active Bookings', value: activeBookings.toString(), icon: '✅', delta: `${totalBookings - activeBookings} cancelled/other`, color: '#16a34a' },
        { label: 'Total Spaces', value: totalSpaces.toString(), icon: '🏢', delta: 'Listed workspaces', color: '#d97706' },
    ];

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

            <main className="page" style={{ maxWidth: '1200px' }}>
                {/* ── Page Header ── */}
                <section className="page-head" style={{ marginBottom: '1.75rem' }}>
                    <div>
                        <h1>Admin Dashboard</h1>
                        <p>Real-time analytics for workspaces and reservations.</p>
                    </div>
                    <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                        <span style={{
                            fontSize: '0.75rem', fontWeight: 600, color: '#16a34a',
                            background: '#dcfce7', padding: '0.3rem 0.75rem',
                            borderRadius: '999px', display: 'flex', alignItems: 'center', gap: '0.35rem',
                        }}>
                            <span style={{ width: '7px', height: '7px', borderRadius: '50%', background: '#16a34a', display: 'inline-block' }} />
                            Live Data
                        </span>
                    </div>
                </section>

                {/* ── KPI Cards ── */}
                <div style={{
                    display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)',
                    gap: '0.85rem', marginBottom: '1.75rem',
                }}>
                    {loading
                        ? Array(4).fill(0).map((_, i) => (
                            <div key={i} style={{
                                border: '1px solid #e5e7eb', borderRadius: '14px',
                                padding: '1.25rem', height: '110px',
                                background: 'linear-gradient(90deg, #f3f4f6 25%, #e5e7eb 50%, #f3f4f6 75%)',
                                backgroundSize: '200% 100%', animation: 'shimmer 1.5s infinite',
                            }} />
                        ))
                        : kpis.map((k, i) => (
                            <div key={i} style={{
                                border: '1px solid #e5e7eb', borderRadius: '14px', background: '#fff',
                                padding: '1.25rem 1.4rem',
                                boxShadow: '0 1px 4px rgba(0,0,0,0.04)',
                                transition: 'box-shadow 200ms ease, transform 200ms ease',
                                cursor: 'default',
                            }}
                                onMouseOver={e => { (e.currentTarget as HTMLDivElement).style.boxShadow = `0 4px 16px rgba(0,0,0,0.08)`; (e.currentTarget as HTMLDivElement).style.transform = 'translateY(-2px)'; }}
                                onMouseOut={e => { (e.currentTarget as HTMLDivElement).style.boxShadow = '0 1px 4px rgba(0,0,0,0.04)'; (e.currentTarget as HTMLDivElement).style.transform = 'translateY(0)'; }}
                            >
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.65rem' }}>
                                    <span style={{
                                        fontSize: '0.72rem', fontWeight: 700, color: '#6b7280',
                                        textTransform: 'uppercase', letterSpacing: '0.06em',
                                    }}>{k.label}</span>
                                    <span style={{ fontSize: '1.4rem' }}>{k.icon}</span>
                                </div>
                                <div style={{ fontSize: 'clamp(1.1rem, 2vw, 1.6rem)', fontWeight: 800, color: k.color, letterSpacing: '-0.03em', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                                    {k.value}
                                </div>
                                <div style={{ fontSize: '0.73rem', color: '#9ca3af', marginTop: '0.3rem', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{k.delta}</div>
                            </div>
                        ))
                    }
                </div>

                {/* ── Analytics Row ── */}
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1.5fr', gap: '0.85rem', marginBottom: '1.75rem' }}>

                    {/* Top Spaces */}
                    <div style={{ border: '1px solid #e5e7eb', borderRadius: '14px', background: '#fff', padding: '1.25rem', boxShadow: '0 1px 4px rgba(0,0,0,0.04)' }}>
                        <h2 style={{ fontSize: '0.9rem', fontWeight: 700, marginBottom: '1.1rem', color: '#111827' }}>🏆 Most Booked Spaces</h2>
                        {loading ? (
                            <p style={{ color: '#9ca3af', fontSize: '0.85rem' }}>Loading...</p>
                        ) : topSpaces.length === 0 ? (
                            <p style={{ color: '#9ca3af', fontSize: '0.85rem' }}>No booking data yet.</p>
                        ) : (
                            <div style={{ display: 'grid', gap: '0.85rem' }}>
                                {topSpaces.map((s, i) => (
                                    <div key={i}>
                                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.3rem', fontSize: '0.83rem' }}>
                                            <span style={{ fontWeight: 600, color: '#111827' }}>
                                                <span style={{ color: '#9ca3af', marginRight: '0.4rem' }}>#{i + 1}</span>
                                                {s.name}
                                            </span>
                                            <span style={{ color: '#6b7280', fontWeight: 500 }}>{s.count} bookings</span>
                                        </div>
                                        <div style={{ height: '6px', borderRadius: '999px', background: '#f3f4f6', overflow: 'hidden' }}>
                                            <div style={{
                                                height: '100%', borderRadius: '999px',
                                                width: `${(s.count / maxBookings) * 100}%`,
                                                background: `linear-gradient(90deg, #4f46e5, #818cf8)`,
                                                transition: 'width 600ms ease',
                                            }} />
                                        </div>
                                        <div style={{ fontSize: '0.72rem', color: '#9ca3af', marginTop: '0.2rem' }}>
                                            {formatPHP(s.revenue)} revenue
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>

                    {/* Quick Actions */}
                    <div style={{ border: '1px solid #e5e7eb', borderRadius: '14px', background: '#fff', padding: '1.25rem', boxShadow: '0 1px 4px rgba(0,0,0,0.04)' }}>
                        <h2 style={{ fontSize: '0.9rem', fontWeight: 700, marginBottom: '1.1rem', color: '#111827' }}>⚡ Quick Actions</h2>
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
                            {[
                                { to: '/admin/workspaces', icon: '🏢', label: 'Manage Workspaces', desc: 'Create, edit, and delete listings' },
                                { to: '/admin/reservations', icon: '📋', label: 'All Reservations', desc: 'View and manage all bookings' },
                                { to: '/admin/availability', icon: '🗓️', label: 'Availability', desc: 'Set hours and block time slots' },
                                { to: '/dashboard', icon: '👁️', label: 'User View', desc: 'See the workspace as a user would' },
                            ].map((a, i) => (
                                <Link
                                    key={i}
                                    to={a.to}
                                    style={{
                                        textDecoration: 'none', display: 'block', padding: '1rem',
                                        border: '1px solid #e5e7eb', borderRadius: '12px',
                                        transition: 'all 150ms ease',
                                        background: '#fafafa',
                                    }}
                                    onMouseOver={e => { (e.currentTarget as HTMLAnchorElement).style.borderColor = '#c7d2fe'; (e.currentTarget as HTMLAnchorElement).style.background = '#eef2ff'; }}
                                    onMouseOut={e => { (e.currentTarget as HTMLAnchorElement).style.borderColor = '#e5e7eb'; (e.currentTarget as HTMLAnchorElement).style.background = '#fafafa'; }}
                                >
                                    <div style={{ fontSize: '1.5rem', marginBottom: '0.4rem' }}>{a.icon}</div>
                                    <div style={{ fontSize: '0.84rem', fontWeight: 700, color: '#111827' }}>{a.label}</div>
                                    <div style={{ fontSize: '0.75rem', color: '#9ca3af', marginTop: '0.2rem' }}>{a.desc}</div>
                                </Link>
                            ))}
                        </div>
                    </div>
                </div>

                {/* ── Recent Reservations Table ── */}
                <div style={{ border: '1px solid #e5e7eb', borderRadius: '14px', background: '#fff', padding: '1.25rem', boxShadow: '0 1px 4px rgba(0,0,0,0.04)', marginBottom: '1.5rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                        <h2 style={{ fontSize: '0.9rem', fontWeight: 700, color: '#111827' }}>📋 Recent Reservations</h2>
                        <Link to="/admin/reservations" style={{ fontSize: '0.78rem', color: '#4f46e5', fontWeight: 600 }}>View all →</Link>
                    </div>

                    {loading ? (
                        <p style={{ color: '#9ca3af', fontSize: '0.85rem' }}>Loading...</p>
                    ) : recentReservations.length === 0 ? (
                        <p style={{ color: '#9ca3af', fontSize: '0.85rem' }}>No reservations yet.</p>
                    ) : (
                        <div style={{ overflowX: 'auto' }}>
                            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.83rem' }}>
                                <thead>
                                    <tr style={{ borderBottom: '1px solid #f3f4f6' }}>
                                        {['Space', 'User', 'Check-In', 'Check-Out', 'Amount', 'Status'].map(h => (
                                            <th key={h} style={{
                                                textAlign: 'left', padding: '0.5rem 0.75rem',
                                                fontSize: '0.72rem', fontWeight: 700, color: '#9ca3af',
                                                textTransform: 'uppercase', letterSpacing: '0.05em',
                                            }}>{h}</th>
                                        ))}
                                    </tr>
                                </thead>
                                <tbody>
                                    {recentReservations.map((r, i) => (
                                        <tr key={r.id} style={{
                                            borderBottom: i < recentReservations.length - 1 ? '1px solid #f9fafb' : 'none',
                                            transition: 'background 150ms ease',
                                        }}
                                            onMouseOver={e => (e.currentTarget as HTMLTableRowElement).style.background = '#f9fafb'}
                                            onMouseOut={e => (e.currentTarget as HTMLTableRowElement).style.background = ''}
                                        >
                                            <td style={{ padding: '0.75rem', fontWeight: 600, color: '#111827' }}>
                                                {r.space?.name ?? r.spaceName ?? '—'}
                                            </td>
                                            <td style={{ padding: '0.75rem', color: '#6b7280' }}>
                                                {r.user?.firstName ? `${r.user.firstName} ${r.user.lastName ?? ''}` : r.user?.email ?? r.userName ?? '—'}
                                            </td>
                                            <td style={{ padding: '0.75rem', color: '#6b7280' }}>{formatDate(r.startTime)}</td>
                                            <td style={{ padding: '0.75rem', color: '#6b7280' }}>{formatDate(r.endTime)}</td>
                                            <td style={{ padding: '0.75rem', fontWeight: 600, color: '#111827' }}>
                                                {r.totalAmount != null ? formatPHP(r.totalAmount) : '—'}
                                            </td>
                                            <td style={{ padding: '0.75rem' }}>{statusBadge(r.status)}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </main>
        </>
    );
};

export default AdminDashboard;
