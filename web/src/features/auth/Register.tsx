import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../shared/axiosConfig';

const Register: React.FC = () => {
    const [firstname, setFirstname] = useState('');
    const [lastname, setLastname] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errorMsg, setErrorMsg] = useState('');
    const [successMsg, setSuccessMsg] = useState('');
    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const navigate = useNavigate();

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorMsg('');
        setSuccessMsg('');

        if (password !== confirmPassword) {
            setErrorMsg('Passwords do not match.');
            return;
        }

        setLoading(true);

        try {
            const payload = { firstname, lastname, email, password };
            const response = await api.post('/auth/register', payload);

            if (response.data.success) {
                const { accessToken, user } = response.data.data;
                localStorage.setItem('accessToken', accessToken);
                localStorage.setItem('userRole', user.role);
                setSuccessMsg('Account created successfully! Redirecting…');
                setTimeout(() => navigate('/dashboard'), 1200);
            }
        } catch (error: any) {
            if (error.response && error.response.data && error.response.data.error) {
                setErrorMsg(error.response.data.error.message);
            } else {
                setErrorMsg('Registration failed. Please try again.');
            }
        } finally {
            setLoading(false);
        }
    };

    const inputStyle: React.CSSProperties = {
        width: '100%',
        padding: '0.75rem 1rem',
        border: '1.5px solid #e5e7eb',
        borderRadius: '10px',
        fontSize: '0.9rem',
        color: '#111827',
        background: '#fff',
        outline: 'none',
        transition: 'border-color 150ms ease, box-shadow 150ms ease',
        boxSizing: 'border-box',
    };

    const labelStyle: React.CSSProperties = {
        display: 'block',
        fontSize: '0.8rem',
        fontWeight: 700,
        color: '#374151',
        marginBottom: '0.4rem',
    };

    const perks = [
        { icon: '⚡', text: 'Instant booking confirmation' },
        { icon: '🏢', text: 'Access 50+ professional spaces' },
        { icon: '🔒', text: 'Secure Stripe-powered payments' },
        { icon: '📱', text: 'Manage bookings from anywhere' },
    ];

    return (
        <div style={{ display: 'flex', minHeight: '100vh', fontFamily: "'Inter', system-ui, sans-serif" }}>

            {/* ── Left Panel — Branding ── */}
            <div style={{
                flex: '0 0 45%',
                background: 'linear-gradient(145deg, #0f172a 0%, #1e1b4b 45%, #4f46e5 100%)',
                display: 'flex', flexDirection: 'column', justifyContent: 'space-between',
                padding: '2.5rem', position: 'relative', overflow: 'hidden',
            }}>
                {/* Background orbs */}
                <div style={{
                    position: 'absolute', width: '500px', height: '500px', borderRadius: '50%',
                    background: 'radial-gradient(circle, rgba(99,102,241,0.3) 0%, transparent 70%)',
                    top: '-100px', right: '-100px', pointerEvents: 'none',
                }} />
                <div style={{
                    position: 'absolute', width: '300px', height: '300px', borderRadius: '50%',
                    background: 'radial-gradient(circle, rgba(167,139,250,0.2) 0%, transparent 70%)',
                    bottom: '60px', left: '-60px', pointerEvents: 'none',
                }} />

                {/* Logo */}
                <Link to="/" style={{ textDecoration: 'none', zIndex: 1 }}>
                    <div style={{ fontSize: '1.3rem', fontWeight: 800, color: '#fff', letterSpacing: '-0.04em' }}>
                        Work<span style={{ color: '#a5b4fc' }}>Space</span>
                    </div>
                </Link>

                {/* Center content */}
                <div style={{ zIndex: 1 }}>
                    <div style={{
                        display: 'inline-flex', alignItems: 'center', gap: '0.5rem',
                        background: 'rgba(255,255,255,0.1)', border: '1px solid rgba(255,255,255,0.2)',
                        borderRadius: '999px', padding: '0.35rem 1rem', marginBottom: '1.5rem',
                    }}>
                        <span style={{ width: '7px', height: '7px', borderRadius: '50%', background: '#34d399', display: 'inline-block' }} />
                        <span style={{ color: 'rgba(255,255,255,0.85)', fontSize: '0.78rem', fontWeight: 500 }}>Free to get started</span>
                    </div>
                    <h1 style={{
                        fontSize: '2.4rem', fontWeight: 900, color: '#fff',
                        lineHeight: 1.15, letterSpacing: '-0.04em', marginBottom: '1rem',
                    }}>
                        Your workspace,<br />your rules.
                    </h1>
                    <p style={{ color: 'rgba(255,255,255,0.6)', fontSize: '0.95rem', lineHeight: 1.7, maxWidth: '320px', marginBottom: '2rem' }}>
                        Join professionals who trust WorkSpace for reliable and flexible work environments across the city.
                    </p>

                    {/* Perks list */}
                    <div style={{ display: 'grid', gap: '0.75rem' }}>
                        {perks.map((p, i) => (
                            <div key={i} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                                <div style={{
                                    width: '32px', height: '32px', borderRadius: '8px',
                                    background: 'rgba(255,255,255,0.1)',
                                    border: '1px solid rgba(255,255,255,0.15)',
                                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                                    fontSize: '0.95rem', flexShrink: 0,
                                }}>{p.icon}</div>
                                <span style={{ color: 'rgba(255,255,255,0.8)', fontSize: '0.875rem' }}>{p.text}</span>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Bottom badge */}
                <div style={{
                    zIndex: 1,
                    border: '1px solid rgba(255,255,255,0.15)',
                    background: 'rgba(255,255,255,0.06)',
                    borderRadius: '14px', padding: '1rem 1.25rem',
                    backdropFilter: 'blur(8px)',
                    display: 'flex', alignItems: 'center', gap: '0.75rem',
                }}>
                    <div style={{ fontSize: '1.5rem' }}>🔐</div>
                    <div>
                        <div style={{ fontSize: '0.82rem', fontWeight: 700, color: '#fff' }}>Enterprise-grade security</div>
                        <div style={{ fontSize: '0.75rem', color: 'rgba(255,255,255,0.5)', marginTop: '0.15rem' }}>
                            Your data is encrypted and never shared.
                        </div>
                    </div>
                </div>
            </div>

            {/* ── Right Panel — Form ── */}
            <div style={{
                flex: 1, display: 'flex', flexDirection: 'column',
                justifyContent: 'center', alignItems: 'center',
                padding: '2.5rem', background: '#f8fafc',
                overflowY: 'auto',
            }}>
                <div style={{ width: '100%', maxWidth: '420px' }}>

                    {/* Back link */}
                    <Link to="/" style={{
                        display: 'inline-flex', alignItems: 'center', gap: '0.35rem',
                        fontSize: '0.8rem', color: '#6b7280', textDecoration: 'none',
                        marginBottom: '2rem', transition: 'color 150ms ease',
                    }}
                        onMouseOver={e => (e.currentTarget as HTMLAnchorElement).style.color = '#4f46e5'}
                        onMouseOut={e => (e.currentTarget as HTMLAnchorElement).style.color = '#6b7280'}
                    >
                        ← Back to home
                    </Link>

                    <h2 style={{ fontSize: '1.75rem', fontWeight: 800, color: '#111827', letterSpacing: '-0.03em', marginBottom: '0.35rem' }}>
                        Create your account
                    </h2>
                    <p style={{ fontSize: '0.875rem', color: '#6b7280', marginBottom: '1.75rem' }}>
                        Already have an account?{' '}
                        <Link to="/login" style={{ color: '#4f46e5', fontWeight: 600, textDecoration: 'none' }}>
                            Sign in →
                        </Link>
                    </p>

                    {errorMsg && (
                        <div style={{
                            background: '#fef2f2', border: '1px solid #fecaca', color: '#dc2626',
                            borderRadius: '10px', padding: '0.7rem 1rem', fontSize: '0.84rem',
                            marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem',
                        }}>
                            ⚠️ {errorMsg}
                        </div>
                    )}
                    {successMsg && (
                        <div style={{
                            background: '#f0fdf4', border: '1px solid #bbf7d0', color: '#16a34a',
                            borderRadius: '10px', padding: '0.7rem 1rem', fontSize: '0.84rem',
                            marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem',
                        }}>
                            ✅ {successMsg}
                        </div>
                    )}

                    <form onSubmit={handleRegister} style={{ display: 'grid', gap: '1rem' }}>
                        {/* Name row */}
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
                            <div>
                                <label htmlFor="reg-fn" style={labelStyle}>First name</label>
                                <input
                                    id="reg-fn"
                                    type="text"
                                    value={firstname}
                                    onChange={(e) => setFirstname(e.target.value)}
                                    placeholder="John"
                                    required
                                    autoFocus
                                    style={inputStyle}
                                    onFocus={e => { e.target.style.borderColor = '#4f46e5'; e.target.style.boxShadow = '0 0 0 3px rgba(79,70,229,0.12)'; }}
                                    onBlur={e => { e.target.style.borderColor = '#e5e7eb'; e.target.style.boxShadow = 'none'; }}
                                />
                            </div>
                            <div>
                                <label htmlFor="reg-ln" style={labelStyle}>Last name</label>
                                <input
                                    id="reg-ln"
                                    type="text"
                                    value={lastname}
                                    onChange={(e) => setLastname(e.target.value)}
                                    placeholder="Doe"
                                    required
                                    style={inputStyle}
                                    onFocus={e => { e.target.style.borderColor = '#4f46e5'; e.target.style.boxShadow = '0 0 0 3px rgba(79,70,229,0.12)'; }}
                                    onBlur={e => { e.target.style.borderColor = '#e5e7eb'; e.target.style.boxShadow = 'none'; }}
                                />
                            </div>
                        </div>

                        <div>
                            <label htmlFor="reg-email" style={labelStyle}>Email address</label>
                            <input
                                id="reg-email"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="you@example.com"
                                required
                                style={inputStyle}
                                onFocus={e => { e.target.style.borderColor = '#4f46e5'; e.target.style.boxShadow = '0 0 0 3px rgba(79,70,229,0.12)'; }}
                                onBlur={e => { e.target.style.borderColor = '#e5e7eb'; e.target.style.boxShadow = 'none'; }}
                            />
                        </div>

                        <div>
                            <label htmlFor="reg-pw" style={labelStyle}>Password</label>
                            <div style={{ position: 'relative' }}>
                                <input
                                    id="reg-pw"
                                    type={showPassword ? 'text' : 'password'}
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    placeholder="Min. 8 characters"
                                    required
                                    style={{ ...inputStyle, paddingRight: '2.75rem' }}
                                    onFocus={e => { e.target.style.borderColor = '#4f46e5'; e.target.style.boxShadow = '0 0 0 3px rgba(79,70,229,0.12)'; }}
                                    onBlur={e => { e.target.style.borderColor = '#e5e7eb'; e.target.style.boxShadow = 'none'; }}
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowPassword(!showPassword)}
                                    style={{
                                        position: 'absolute', right: '0.75rem', top: '50%', transform: 'translateY(-50%)',
                                        background: 'none', border: 'none', cursor: 'pointer', padding: 0,
                                        color: '#9ca3af', fontSize: '1rem', lineHeight: 1,
                                    }}
                                >{showPassword ? '🙈' : '👁️'}</button>
                            </div>
                        </div>

                        <div>
                            <label htmlFor="reg-cpw" style={labelStyle}>Confirm password</label>
                            <input
                                id="reg-cpw"
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                placeholder="Re-enter your password"
                                required
                                style={{
                                    ...inputStyle,
                                    borderColor: confirmPassword && confirmPassword !== password ? '#fca5a5' : '#e5e7eb',
                                }}
                                onFocus={e => { e.target.style.borderColor = '#4f46e5'; e.target.style.boxShadow = '0 0 0 3px rgba(79,70,229,0.12)'; }}
                                onBlur={e => {
                                    e.target.style.borderColor = (confirmPassword && confirmPassword !== password) ? '#fca5a5' : '#e5e7eb';
                                    e.target.style.boxShadow = 'none';
                                }}
                            />
                            {confirmPassword && confirmPassword !== password && (
                                <p style={{ fontSize: '0.75rem', color: '#ef4444', marginTop: '0.3rem' }}>Passwords do not match</p>
                            )}
                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            style={{
                                width: '100%', padding: '0.8rem', borderRadius: '10px',
                                background: loading ? '#818cf8' : '#4f46e5',
                                color: '#fff', border: 'none', fontWeight: 700,
                                fontSize: '0.95rem', cursor: loading ? 'not-allowed' : 'pointer',
                                boxShadow: '0 2px 8px rgba(79,70,229,0.35)',
                                transition: 'all 150ms ease', marginTop: '0.25rem',
                            }}
                            onMouseOver={e => { if (!loading) (e.target as HTMLButtonElement).style.background = '#4338ca'; }}
                            onMouseOut={e => { if (!loading) (e.target as HTMLButtonElement).style.background = '#4f46e5'; }}
                        >
                            {loading ? 'Creating account…' : 'Create account →'}
                        </button>

                        <p style={{ fontSize: '0.75rem', color: '#9ca3af', textAlign: 'center' }}>
                            By creating an account, you agree to our terms of service and privacy policy.
                        </p>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Register;
