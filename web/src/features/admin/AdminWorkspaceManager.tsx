import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api, { createSpace, updateSpace, deleteSpace, uploadSpaceImage } from '../shared/axiosConfig';
import type { Space } from '../spaces/types';

/* ─── Toast component ─── */
interface ToastMsg { id: number; text: string; type: 'success' | 'error' }

const Toast: React.FC<{ msg: ToastMsg; onDone: (id: number) => void }> = ({ msg, onDone }) => {
    useEffect(() => { const t = setTimeout(() => onDone(msg.id), 3500); return () => clearTimeout(t); }, [msg.id, onDone]);
    return (
        <div style={{
            padding: '0.65rem 1rem', borderRadius: '8px', fontSize: '0.84rem', fontWeight: 500,
            background: msg.type === 'success' ? '#f0fdf4' : '#fef2f2',
            color: msg.type === 'success' ? '#16a34a' : '#dc2626',
            border: `1px solid ${msg.type === 'success' ? '#bbf7d0' : '#fecaca'}`,
            boxShadow: '0 4px 12px rgba(0,0,0,.08)', animation: 'fadeIn .25s ease',
        }}>
            {msg.text}
        </div>
    );
};

/* ─── Blank form state ─── */
const blankForm = {
    name: '', location: '', type: 'Pod', capacity: 1, hourlyRate: 0,
    description: '', imageUrl: '', available: true,
    amenities: '', utilities: '', checkInWindow: '', cancellationPolicy: '',
};

const AdminWorkspaceManager: React.FC = () => {
    const navigate = useNavigate();
    const [spaces, setSpaces] = useState<Space[]>([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [editingId, setEditingId] = useState<string | null>(null);
    const [form, setForm] = useState(blankForm);
    const [submitting, setSubmitting] = useState(false);
    const [toasts, setToasts] = useState<ToastMsg[]>([]);

    const toast = (text: string, type: 'success' | 'error' = 'success') =>
        setToasts(prev => [...prev, { id: Date.now(), text, type }]);

    const removeToast = (id: number) => setToasts(prev => prev.filter(t => t.id !== id));

    /* ── Fetch all spaces ── */
    const fetchSpaces = async () => {
        try {
            const res = await api.get('/spaces');
            setSpaces(res.data.data ?? []);
        } catch { toast('Failed to load workspaces.', 'error'); }
        finally { setLoading(false); }
    };

    useEffect(() => { fetchSpaces(); }, []);

    /* ── Form handlers ── */
    const openCreate = () => { setEditingId(null); setForm(blankForm); setShowForm(true); };

    const openEdit = (s: Space) => {
        setEditingId(s.id);
        setForm({
            name: s.name, location: s.location, type: s.type,
            capacity: s.capacity, hourlyRate: s.hourlyRate,
            description: s.description ?? '', imageUrl: s.imageUrl ?? '',
            available: s.available,
            amenities: s.amenities ?? '', utilities: s.utilities ?? '',
            checkInWindow: s.checkInWindow ?? '', cancellationPolicy: s.cancellationPolicy ?? '',
        });
        setShowForm(true);
    };

    const closeForm = () => { setShowForm(false); setEditingId(null); setForm(blankForm); };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        const { name, value, type } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: type === 'number' ? Number(value) : type === 'checkbox' ? (e.target as HTMLInputElement).checked : value,
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setSubmitting(true);
        try {
            if (editingId) {
                await updateSpace(editingId, form);
                toast('Workspace updated successfully.');
            } else {
                await createSpace(form);
                toast('Workspace created successfully.');
            }
            closeForm();
            fetchSpaces();
        } catch (err: any) {
            toast(err?.response?.data?.error?.message ?? 'Operation failed.', 'error');
        } finally { setSubmitting(false); }
    };

    const handleDelete = async (id: string, name: string) => {
        if (!window.confirm(`Delete "${name}"? This action cannot be undone.`)) return;
        try {
            await deleteSpace(id);
            toast('Workspace deleted.');
            fetchSpaces();
        } catch (err: any) {
            toast(err?.response?.data?.error?.message ?? 'Delete failed.', 'error');
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('userRole');
        navigate('/login');
    };


    /* ─── Render ─── */
    return (
        <>
            {/* Topbar */}
            <header className="topbar">
                <div className="brand">WorkSpace</div>
                <nav className="topbar-links">
                    <Link to="/admin">Admin</Link>
                    <Link to="/admin/workspaces" className="active">Manage Spaces</Link>
                    <Link to="/dashboard">Dashboard</Link>
                    <Link to="/reservations">Reservations</Link>
                </nav>
                <button className="secondary-btn" onClick={handleLogout}>Log out</button>
            </header>

            {/* Toast container */}
            <div style={{ position: 'fixed', top: 70, right: 20, zIndex: 9999, display: 'grid', gap: '0.4rem', width: 320 }}>
                {toasts.map(t => <Toast key={t.id} msg={t} onDone={removeToast} />)}
            </div>

            <main className="page" style={{ maxWidth: 1080 }}>
                {/* Page head */}
                <section className="page-head">
                    <div>
                        <h1>Manage Workspaces</h1>
                        <p>Create, edit, and delete workspace listings.</p>
                    </div>
                    <button className="primary-btn" onClick={openCreate}>+ Add Workspace</button>
                </section>

                {/* ── Modal / form overlay ── */}
                {showForm && (
                    <div style={{
                        position: 'fixed', inset: 0, background: 'rgba(0,0,0,.45)', zIndex: 100,
                        display: 'grid', placeItems: 'center', padding: '1rem',
                    }}>
                        <form onSubmit={handleSubmit} style={{
                            background: 'var(--surface)', borderRadius: 'var(--radius-lg)',
                            padding: '1.75rem', width: '100%', maxWidth: 580, maxHeight: '90vh', overflowY: 'auto',
                            border: '1px solid var(--border)', boxShadow: '0 20px 60px rgba(0,0,0,.18)',
                        }}>
                            <h2 style={{ marginBottom: '1.1rem', fontSize: '1.15rem' }}>
                                {editingId ? 'Edit Workspace' : 'New Workspace'}
                            </h2>

                            <div className="form">
                                <div className="field">
                                    <label>Name</label>
                                    <input name="name" value={form.name} onChange={handleChange} required autoFocus />
                                </div>
                                <div className="field">
                                    <label>Location</label>
                                    <input name="location" value={form.location} onChange={handleChange} required />
                                </div>
                                <div className="row">
                                    <div className="field" style={{ flex: 1 }}>
                                        <label>Type</label>
                                        <select name="type" value={form.type} onChange={handleChange}>
                                            <option>Pod</option>
                                            <option>Meeting Room</option>
                                            <option>Studio</option>
                                            <option>Boardroom</option>
                                            <option>Hot Desk</option>
                                        </select>
                                    </div>
                                    <div className="field" style={{ flex: 1 }}>
                                        <label>Capacity</label>
                                        <input name="capacity" type="number" min={1} value={form.capacity} onChange={handleChange} required />
                                    </div>
                                </div>
                                <div className="row">
                                    <div className="field" style={{ flex: 1 }}>
                                        <label>Hourly Rate (₱)</label>
                                        <input name="hourlyRate" type="number" min={0} step="0.01" value={form.hourlyRate} onChange={handleChange} required />
                                    </div>
                                    <div className="field" style={{ flex: 1, display: 'flex', alignItems: 'center', gap: '0.5rem', paddingTop: '1.4rem' }}>
                                        <input name="available" type="checkbox" checked={form.available} onChange={handleChange} id="avail-check" />
                                        <label htmlFor="avail-check" style={{ margin: 0 }}>Available</label>
                                    </div>
                                </div>

                                {/* ── Description ── */}
                                <div className="field">
                                    <label>Description</label>
                                    <textarea name="description" value={form.description} onChange={handleChange} rows={5}
                                        placeholder="Describe the workspace — what makes it special, who it's best for, and any notable features."
                                        style={{
                                            resize: 'vertical', fontFamily: 'inherit', lineHeight: 1.6,
                                            minHeight: 110, fontSize: '0.88rem',
                                        }} />
                                    <span style={{ fontSize: '0.7rem', color: 'var(--text-tertiary)', marginTop: 2 }}>
                                        {form.description.length} characters
                                    </span>
                                </div>


                                {/* ── Photos (Visual Gallery) ── */}
                                <div style={{ borderTop: '1px solid var(--border)', margin: '0.8rem 0 0.6rem', paddingTop: '0.7rem' }}>
                                    <span style={{ fontSize: '0.78rem', fontWeight: 600, color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>Space Photos</span>
                                </div>
                                <div className="field">
                                    <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginBottom: '0.5rem' }}>
                                        {form.imageUrl && form.imageUrl.split(',').map(url => url.trim()).filter(Boolean).map((imgUrl, i) => (
                                            <div key={i} style={{ position: 'relative', width: 80, height: 80, borderRadius: 8, overflow: 'hidden', border: '1px solid var(--border)' }}>
                                                <img src={imgUrl.startsWith('http') ? imgUrl : `http://localhost:8080${imgUrl}`} style={{ width: '100%', height: '100%', objectFit: 'cover' }} alt="space" />
                                                <button type="button" onClick={async () => {
                                                    const newUrls = form.imageUrl.split(',').map(u=>u.trim()).filter(Boolean);
                                                    newUrls.splice(i, 1);
                                                    const newImageUrl = newUrls.join(',');
                                                    setForm({...form, imageUrl: newImageUrl});
                                                    try {
                                                        await api.put(`/spaces/${editingId}`, { ...form, imageUrl: newImageUrl });
                                                        toast('Image removed successfully.');
                                                        fetchSpaces();
                                                    } catch (err) {
                                                        toast('Failed to remove image.', 'error');
                                                    }
                                                }} style={{ position: 'absolute', top: 2, right: 2, background: 'rgba(0,0,0,0.6)', color: 'white', border: 'none', borderRadius: '50%', width: 20, height: 20, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '10px' }}>✕</button>
                                            </div>
                                        ))}
                                    </div>
                                    {editingId ? (
                                        <>
                                            <label className="secondary-btn" style={{ fontSize: '0.8rem', padding: '0.4rem 0.8rem', cursor: 'pointer', display: 'inline-block' }}>
                                                + Add Photos
                                                <input type="file" multiple accept="image/*" style={{ display: 'none' }} onChange={async (e) => {
                                                    const files = e.target.files;
                                                    if (!files || files.length === 0) return;
                                                    try {
                                                        const updatedSpace = await uploadSpaceImage(editingId, files);
                                                        setForm({ ...form, imageUrl: updatedSpace.imageUrl });
                                                        toast('Images added successfully.');
                                                        fetchSpaces();
                                                    } catch (err) {
                                                        toast('Failed to upload images.', 'error');
                                                    }
                                                }} />
                                            </label>
                                            <p style={{ fontSize: '0.7rem', color: 'var(--text-tertiary)', marginTop: '0.3rem' }}>Uploading and removing images saves instantly.</p>
                                        </>
                                    ) : (
                                        <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>You can upload photos after creating the workspace.</p>
                                    )}
                                </div>

                                <div style={{ borderTop: '1px solid var(--border)', margin: '0.8rem 0 0.6rem', paddingTop: '0.7rem' }}>
                                    <span style={{ fontSize: '0.78rem', fontWeight: 600, color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>Space Details</span>
                                </div>

                                {/* ── Amenities ── */}
                                <div className="field">
                                    <label>Amenities</label>
                                    <textarea name="amenities" value={form.amenities} onChange={handleChange} rows={3}
                                        placeholder="Ergonomic chair, Noise-controlled booth, Task lighting, Privacy panel"
                                        style={{
                                            resize: 'vertical', fontFamily: 'inherit', lineHeight: 1.6,
                                            minHeight: 72, fontSize: '0.88rem',
                                        }} />
                                    {form.amenities.trim() && (
                                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.3rem', marginTop: '0.35rem' }}>
                                            {form.amenities.split(',').map(a => a.trim()).filter(Boolean).map((item, i) => (
                                                <span key={i} style={{
                                                    fontSize: '0.72rem', padding: '0.18rem 0.5rem', borderRadius: 999,
                                                    background: '#ede9fe', color: '#6d28d9', fontWeight: 500,
                                                    border: '1px solid #ddd6fe',
                                                }}>{item}</span>
                                            ))}
                                        </div>
                                    )}
                                    <span style={{ fontSize: '0.7rem', color: 'var(--text-tertiary)', marginTop: 2 }}>
                                        Separate items with commas. {form.amenities.split(',').filter(a => a.trim()).length} item(s)
                                    </span>
                                </div>

                                {/* ── Utilities ── */}
                                <div className="field">
                                    <label>Utilities</label>
                                    <textarea name="utilities" value={form.utilities} onChange={handleChange} rows={3}
                                        placeholder="High-speed Wi-Fi, Power outlet, USB-C charging, Air conditioning"
                                        style={{
                                            resize: 'vertical', fontFamily: 'inherit', lineHeight: 1.6,
                                            minHeight: 72, fontSize: '0.88rem',
                                        }} />
                                    {form.utilities.trim() && (
                                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.3rem', marginTop: '0.35rem' }}>
                                            {form.utilities.split(',').map(u => u.trim()).filter(Boolean).map((item, i) => (
                                                <span key={i} style={{
                                                    fontSize: '0.72rem', padding: '0.18rem 0.5rem', borderRadius: 999,
                                                    background: '#ecfdf5', color: '#047857', fontWeight: 500,
                                                    border: '1px solid #a7f3d0',
                                                }}>{item}</span>
                                            ))}
                                        </div>
                                    )}
                                    <span style={{ fontSize: '0.7rem', color: 'var(--text-tertiary)', marginTop: 2 }}>
                                        Separate items with commas. {form.utilities.split(',').filter(u => u.trim()).length} item(s)
                                    </span>
                                </div>

                                <div className="field">
                                    <label>Check-in Window</label>
                                    <input name="checkInWindow" value={form.checkInWindow} onChange={handleChange}
                                        placeholder="Anytime between 8:00 AM - 9:00 PM" />
                                </div>
                                <div className="field">
                                    <label>Cancellation Policy</label>
                                    <select
                                        value={
                                            [
                                                'No cancellation — all bookings are final.',
                                                'Free cancellation up to 2 hours before check-in.',
                                                'Free cancellation up to 4 hours before check-in.',
                                                'Free cancellation up to 6 hours before check-in.',
                                                'Free cancellation up to 24 hours before check-in.',
                                            ].includes(form.cancellationPolicy)
                                                ? form.cancellationPolicy
                                                : form.cancellationPolicy ? '__custom__' : ''
                                        }
                                        onChange={e => {
                                            const val = e.target.value;
                                            if (val === '__custom__') {
                                                setForm(prev => ({ ...prev, cancellationPolicy: '' }));
                                            } else {
                                                setForm(prev => ({ ...prev, cancellationPolicy: val }));
                                            }
                                        }}
                                    >
                                        <option value="">Select a policy…</option>
                                        <option value="No cancellation — all bookings are final.">No cancellation — all bookings are final</option>
                                        <option value="Free cancellation up to 2 hours before check-in.">Free cancellation (2 hours)</option>
                                        <option value="Free cancellation up to 4 hours before check-in.">Free cancellation (4 hours)</option>
                                        <option value="Free cancellation up to 6 hours before check-in.">Free cancellation (6 hours)</option>
                                        <option value="Free cancellation up to 24 hours before check-in.">Free cancellation (24 hours)</option>
                                        <option value="__custom__">Custom policy…</option>
                                    </select>
                                    {/* Show text input when "Custom" is selected or value doesn't match presets */}
                                    {(
                                        form.cancellationPolicy !== '' &&
                                        ![
                                            'No cancellation — all bookings are final.',
                                            'Free cancellation up to 2 hours before check-in.',
                                            'Free cancellation up to 4 hours before check-in.',
                                            'Free cancellation up to 6 hours before check-in.',
                                            'Free cancellation up to 24 hours before check-in.',
                                        ].includes(form.cancellationPolicy)
                                    ) && (
                                        <input name="cancellationPolicy" value={form.cancellationPolicy} onChange={handleChange}
                                            placeholder="Enter your custom cancellation policy…"
                                            style={{ marginTop: '0.4rem' }} autoFocus />
                                    )}
                                </div>
                            </div>

                            <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1.25rem', justifyContent: 'flex-end' }}>
                                <button type="button" className="secondary-btn" onClick={closeForm}>Cancel</button>
                                <button type="submit" className="primary-btn" disabled={submitting}>
                                    {submitting ? 'Saving…' : editingId ? 'Update' : 'Create'}
                                </button>
                            </div>
                        </form>
                    </div>
                )}

                {/* ── Table ── */}
                {loading ? (
                    <p style={{ color: 'var(--text-secondary)', fontSize: '0.88rem' }}>Loading workspaces…</p>
                ) : spaces.length === 0 ? (
                    <p style={{ color: 'var(--text-secondary)', fontSize: '0.88rem' }}>No workspaces found. Create one above!</p>
                ) : (
                    <div style={{ overflowX: 'auto' }}>
                        <table style={{
                            width: '100%', borderCollapse: 'collapse', fontSize: '0.84rem',
                            background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: 'var(--radius-lg)',
                        }}>
                            <thead>
                                <tr style={{ borderBottom: '1px solid var(--border)', textAlign: 'left' }}>
                                    {['Name', 'Location', 'Type', 'Capacity', 'Rate/hr', 'Status', 'Actions'].map(h => (
                                        <th key={h} style={{
                                            padding: '0.65rem 0.75rem', fontWeight: 600, fontSize: '0.75rem',
                                            color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '0.04em',
                                        }}>{h}</th>
                                    ))}
                                </tr>
                            </thead>
                            <tbody>
                                {spaces.map(s => (
                                    <tr key={s.id} style={{ borderBottom: '1px solid var(--border)' }}>
                                        <td style={{ padding: '0.6rem 0.75rem', fontWeight: 500 }}>{s.name}</td>
                                        <td style={{ padding: '0.6rem 0.75rem', color: 'var(--text-secondary)' }}>{s.location}</td>
                                        <td style={{ padding: '0.6rem 0.75rem' }}>
                                            <span className="ws-type">{s.type}</span>
                                        </td>
                                        <td style={{ padding: '0.6rem 0.75rem' }}>{s.capacity}</td>
                                        <td style={{ padding: '0.6rem 0.75rem' }}>₱{s.hourlyRate?.toFixed(2)}</td>
                                        <td style={{ padding: '0.6rem 0.75rem' }}>
                                            <span style={{
                                                fontSize: '0.72rem', fontWeight: 600, padding: '0.15rem 0.45rem',
                                                borderRadius: 999,
                                                color: s.available ? '#166534' : '#991b1b',
                                                background: s.available ? '#dcfce7' : '#fee2e2',
                                            }}>
                                                {s.available ? 'Open' : 'Closed'}
                                            </span>
                                        </td>
                                        <td style={{ padding: '0.6rem 0.75rem' }}>
                                            <div style={{ display: 'flex', gap: '0.35rem' }}>
                                                <button className="secondary-btn" style={{ fontSize: '0.76rem', padding: '0.3rem 0.6rem' }}
                                                    onClick={() => openEdit(s)}>Edit</button>
                                                <button style={{
                                                    fontSize: '0.76rem', padding: '0.3rem 0.6rem', borderRadius: 'var(--radius)',
                                                    border: '1px solid #fecaca', background: '#fef2f2', color: '#dc2626',
                                                    cursor: 'pointer', fontWeight: 500,
                                                }} onClick={() => handleDelete(s.id, s.name)}>Delete</button>
                                            </div>
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

export default AdminWorkspaceManager;
