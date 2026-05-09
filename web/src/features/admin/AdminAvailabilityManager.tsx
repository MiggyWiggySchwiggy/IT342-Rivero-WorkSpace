import React, { useEffect, useState, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api, { fetchAvailability, saveAvailability } from '../shared/axiosConfig';
import type { Space } from '../spaces/types';

interface Slot {
    id?: number;
    dayOfWeek: number;
    startTime: string;
    endTime: string;
    blocked: boolean;
}

const DAY_NAMES = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
const DEFAULT_TIMES = [
    { startTime: '08:00', endTime: '11:00' },
    { startTime: '12:00', endTime: '15:00' },
    { startTime: '16:00', endTime: '19:00' },
];

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

const AdminAvailabilityManager: React.FC = () => {
    const navigate = useNavigate();
    const [spaces, setSpaces] = useState<Space[]>([]);
    const [selectedSpaceId, setSelectedSpaceId] = useState<string>('');
    const [slots, setSlots] = useState<Slot[]>([]);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [toasts, setToasts] = useState<ToastMsg[]>([]);

    const toast = (text: string, type: 'success' | 'error' = 'success') =>
        setToasts(prev => [...prev, { id: Date.now(), text, type }]);
    const removeToast = useCallback((id: number) => setToasts(prev => prev.filter(t => t.id !== id)), []);

    // Fetch all spaces on mount
    useEffect(() => {
        (async () => {
            try {
                const res = await api.get('/spaces');
                const data: Space[] = res.data.data ?? [];
                setSpaces(data);
                if (data.length > 0) setSelectedSpaceId(data[0].id);
            } catch { toast('Failed to load workspaces.', 'error'); }
            finally { setLoading(false); }
        })();
    }, []);

    // Fetch availability whenever space selection changes
    useEffect(() => {
        if (!selectedSpaceId) return;
        (async () => {
            try {
                const data = await fetchAvailability(selectedSpaceId);
                setSlots(data ?? []);
            } catch { setSlots([]); }
        })();
    }, [selectedSpaceId]);

    /* ── Slot CRUD helpers ── */

    const addSlot = () => {
        setSlots(prev => [...prev, { dayOfWeek: 1, startTime: '09:00', endTime: '12:00', blocked: false }]);
    };

    const removeSlot = (idx: number) => setSlots(prev => prev.filter((_, i) => i !== idx));

    const updateSlot = (idx: number, field: keyof Slot, value: string | number | boolean) => {
        setSlots(prev => prev.map((s, i) => i === idx ? { ...s, [field]: value } : s));
    };

    const generateDefaults = () => {
        const newSlots: Slot[] = [];
        for (let day = 1; day <= 5; day++) { // Mon-Fri
            DEFAULT_TIMES.forEach(t => newSlots.push({ dayOfWeek: day, ...t, blocked: false }));
        }
        setSlots(newSlots);
    };

    const handleSave = async () => {
        if (!selectedSpaceId) return;
        setSaving(true);
        try {
            const saved = await saveAvailability(selectedSpaceId, slots as unknown as Record<string, unknown>[]);
            setSlots(saved);
            toast('Availability saved successfully.');
        } catch { toast('Failed to save availability.', 'error'); }
        finally { setSaving(false); }
    };

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('userRole');
        navigate('/login');
    };

    /* Group slots by day for the visual grid */
    const byDay: Record<number, Slot[]> = {};
    slots.forEach((s, idx) => {
        if (!byDay[s.dayOfWeek]) byDay[s.dayOfWeek] = [];
        byDay[s.dayOfWeek].push({ ...s, id: idx }); // use array index as local id
    });

    return (
        <>
            <header className="topbar">
                <div className="brand">WorkSpace</div>
                <nav className="topbar-links">
                    <Link to="/admin">Admin</Link>
                    <Link to="/admin/workspaces">Manage Spaces</Link>
                    <Link to="/admin/reservations">Reservations</Link>
                    <Link to="/admin/availability" className="active">Availability</Link>
                    <Link to="/dashboard">Dashboard</Link>
                </nav>
                <button className="secondary-btn" onClick={handleLogout}>Log out</button>
            </header>

            {/* Toasts */}
            <div style={{ position: 'fixed', top: 70, right: 20, zIndex: 9999, display: 'grid', gap: '0.4rem', width: 320 }}>
                {toasts.map(t => <Toast key={t.id} msg={t} onDone={removeToast} />)}
            </div>

            <main className="page" style={{ maxWidth: 1080 }}>
                <section className="page-head">
                    <div>
                        <h1>Manage Availability</h1>
                        <p>Define operating hours and blocked time slots per workspace.</p>
                    </div>
                </section>

                {loading ? (
                    <p style={{ color: 'var(--text-secondary)' }}>Loading…</p>
                ) : (
                    <>
                        {/* Space selector */}
                        <div style={{ display: 'flex', gap: '0.6rem', alignItems: 'center', marginBottom: '1rem', flexWrap: 'wrap' }}>
                            <div className="field" style={{ flex: 1, minWidth: 220 }}>
                                <label>Workspace</label>
                                <select value={selectedSpaceId} onChange={e => setSelectedSpaceId(e.target.value)}>
                                    {spaces.map(s => <option key={s.id} value={s.id}>{s.name} — {s.location}</option>)}
                                </select>
                            </div>
                            <button className="secondary-btn" style={{ marginTop: '1.2rem' }} onClick={generateDefaults}>
                                Generate Mon–Fri Defaults
                            </button>
                            <button className="primary-btn" style={{ marginTop: '1.2rem' }} onClick={handleSave} disabled={saving}>
                                {saving ? 'Saving…' : 'Save Schedule'}
                            </button>
                        </div>

                        {/* Visual preview grid */}
                        <div className="detail-panel" style={{ marginBottom: '1rem' }}>
                            <h2>Schedule Preview</h2>
                            <div className="availability-grid">
                                {DAY_NAMES.map((name, dayIdx) => {
                                    const daySlots = slots.filter(s => s.dayOfWeek === dayIdx);
                                    return (
                                        <div className="availability-day" key={dayIdx}>
                                            <div className="availability-head">
                                                <strong>{name.slice(0, 3)}</strong>
                                                <span>{daySlots.length} slots</span>
                                            </div>
                                            <div className="availability-slots">
                                                {daySlots.length === 0 ? (
                                                    <span style={{ fontSize: '0.72rem', color: 'var(--text-tertiary)' }}>Closed</span>
                                                ) : daySlots.map((s, i) => (
                                                    <span key={i} className={`slot ${s.blocked ? 'closed' : 'open'}`}>
                                                        {s.startTime}–{s.endTime}
                                                    </span>
                                                ))}
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        </div>

                        {/* Editable slot list */}
                        <div className="detail-panel">
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.7rem' }}>
                                <h2 style={{ margin: 0 }}>Slot Editor</h2>
                                <button className="secondary-btn" style={{ fontSize: '0.78rem' }} onClick={addSlot}>+ Add Slot</button>
                            </div>

                            {slots.length === 0 ? (
                                <p style={{ fontSize: '0.84rem', color: 'var(--text-secondary)' }}>
                                    No slots defined. Click "Add Slot" or "Generate Mon–Fri Defaults".
                                </p>
                            ) : (
                                <div style={{ display: 'grid', gap: '0.4rem' }}>
                                    {/* Header */}
                                    <div style={{
                                        display: 'grid', gridTemplateColumns: '1fr 1fr 1fr 80px 36px', gap: '0.4rem',
                                        fontSize: '0.72rem', fontWeight: 600, color: 'var(--text-secondary)', textTransform: 'uppercase',
                                        letterSpacing: '0.04em', padding: '0 0.1rem',
                                    }}>
                                        <span>Day</span><span>Start</span><span>End</span><span>Blocked</span><span></span>
                                    </div>

                                    {slots.map((slot, idx) => (
                                        <div key={idx} style={{
                                            display: 'grid', gridTemplateColumns: '1fr 1fr 1fr 80px 36px', gap: '0.4rem',
                                            alignItems: 'center', padding: '0.3rem 0', borderBottom: '1px solid var(--border)',
                                        }}>
                                            <select value={slot.dayOfWeek} onChange={e => updateSlot(idx, 'dayOfWeek', Number(e.target.value))}
                                                style={{ fontSize: '0.82rem' }}>
                                                {DAY_NAMES.map((n, i) => <option key={i} value={i}>{n}</option>)}
                                            </select>
                                            <input type="time" value={slot.startTime} onChange={e => updateSlot(idx, 'startTime', e.target.value)}
                                                style={{ fontSize: '0.82rem' }} />
                                            <input type="time" value={slot.endTime} onChange={e => updateSlot(idx, 'endTime', e.target.value)}
                                                style={{ fontSize: '0.82rem' }} />
                                            <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                                                <input type="checkbox" checked={slot.blocked}
                                                    onChange={e => updateSlot(idx, 'blocked', e.target.checked)} />
                                                <span style={{ fontSize: '0.76rem', color: slot.blocked ? '#dc2626' : 'var(--text-secondary)' }}>
                                                    {slot.blocked ? 'Yes' : 'No'}
                                                </span>
                                            </div>
                                            <button onClick={() => removeSlot(idx)} style={{
                                                width: 28, height: 28, borderRadius: '50%', border: '1px solid #fecaca',
                                                background: '#fef2f2', color: '#dc2626', fontSize: '1rem', cursor: 'pointer',
                                                display: 'grid', placeItems: 'center', lineHeight: 1,
                                            }}>×</button>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    </>
                )}
            </main>
        </>
    );
};

export default AdminAvailabilityManager;
