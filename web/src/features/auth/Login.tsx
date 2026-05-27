import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../shared/axiosConfig';

const Login: React.FC = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMsg, setErrorMsg] = useState('');
    const [successMsg, setSuccessMsg] = useState('');
    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const navigate = useNavigate();

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorMsg('');
        setSuccessMsg('');
        setLoading(true);

        try {
            const response = await api.post('/auth/login', { email, password });

            if (response.data.success) {
                const { accessToken, user } = response.data.data;
                localStorage.setItem('accessToken', accessToken);
                localStorage.setItem('userRole', user.role);
                setSuccessMsg('Login successful! Redirecting…');
                setTimeout(() => navigate('/dashboard'), 1200);
            }
        } catch (error: any) {
            if (error.response && error.response.data && error.response.data.error) {
                setErrorMsg(error.response.data.error.message);
            } else {
                setErrorMsg('An unexpected error occurred. Please try again.');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleGoogleLogin = () => {
        const backendBaseUrl = import.meta.env.VITE_BACKEND_BASE_URL ?? 'http://localhost:8080';
        window.location.href = `${backendBaseUrl}/oauth2/authorization/google`;
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
                        <span style={{ color: 'rgba(255,255,255,0.85)', fontSize: '0.78rem', fontWeight: 500 }}>Secure Sign In</span>
                    </div>
                    <h1 style={{
                        fontSize: '2.4rem', fontWeight: 900, color: '#fff',
                        lineHeight: 1.15, letterSpacing: '-0.04em', marginBottom: '1rem',
                    }}>
                        Welcome<br />back.
                    </h1>
                    <p style={{ color: 'rgba(255,255,255,0.6)', fontSize: '0.95rem', lineHeight: 1.7, maxWidth: '320px' }}>
                        Sign in to access your bookings, explore available workspaces, and manage your reservations.
                    </p>
                </div>

                {/* Testimonial / trust badge */}
                <div style={{
                    zIndex: 1,
                    border: '1px solid rgba(255,255,255,0.15)',
                    background: 'rgba(255,255,255,0.06)',
                    borderRadius: '14px', padding: '1.1rem 1.25rem',
                    backdropFilter: 'blur(8px)',
                }}>
                    <p style={{ color: 'rgba(255,255,255,0.8)', fontSize: '0.875rem', lineHeight: 1.6, margin: '0 0 0.6rem' }}>
                        "WorkSpace made it incredibly easy to find and book the perfect office space for our team meetings."
                    </p>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
                        <div style={{
                            width: '32px', height: '32px', borderRadius: '50%',
                            background: 'linear-gradient(135deg, #818cf8, #c4b5fd)',
                            display: 'flex', alignItems: 'center', justifyContent: 'center',
                            fontSize: '0.8rem', fontWeight: 700, color: '#fff',
                        }}>A</div>
                        <div>
                            <div style={{ fontSize: '0.8rem', fontWeight: 700, color: '#fff' }}>Alex M.</div>
                            <div style={{ fontSize: '0.72rem', color: 'rgba(255,255,255,0.5)' }}>Verified user</div>
                        </div>
                        <div style={{ marginLeft: 'auto', color: '#fbbf24', fontSize: '0.85rem' }}>★★★★★</div>
                    </div>
                </div>
            </div>

            {/* ── Right Panel — Form ── */}
            <div style={{
                flex: 1, display: 'flex', flexDirection: 'column',
                justifyContent: 'center', alignItems: 'center',
                padding: '2.5rem', background: '#f8fafc',
            }}>
                <div style={{ width: '100%', maxWidth: '400px' }}>

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
                        Sign in to your account
                    </h2>
                    <p style={{ fontSize: '0.875rem', color: '#6b7280', marginBottom: '1.75rem' }}>
                        Don't have an account?{' '}
                        <Link to="/register" style={{ color: '#4f46e5', fontWeight: 600, textDecoration: 'none' }}>
                            Create one free →
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

                    <form onSubmit={handleLogin} style={{ display: 'grid', gap: '1rem' }}>
                        <div>
                            <label htmlFor="login-email" style={labelStyle}>Email address</label>
                            <input
                                id="login-email"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="you@example.com"
                                required
                                autoFocus
                                style={inputStyle}
                                onFocus={e => { e.target.style.borderColor = '#4f46e5'; e.target.style.boxShadow = '0 0 0 3px rgba(79,70,229,0.12)'; }}
                                onBlur={e => { e.target.style.borderColor = '#e5e7eb'; e.target.style.boxShadow = 'none'; }}
                            />
                        </div>

                        <div>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.4rem' }}>
                                <label htmlFor="login-password" style={{ ...labelStyle, marginBottom: 0 }}>Password</label>
                                <a href="#" style={{ fontSize: '0.78rem', color: '#6b7280', textDecoration: 'none' }}>Forgot password?</a>
                            </div>
                            <div style={{ position: 'relative' }}>
                                <input
                                    id="login-password"
                                    type={showPassword ? 'text' : 'password'}
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    placeholder="Enter your password"
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
                            {loading ? 'Signing in…' : 'Sign in →'}
                        </button>
                    </form>

                    {/* Divider */}
                    <div style={{ display: 'flex', alignItems: 'center', margin: '1.5rem 0' }}>
                        <div style={{ flex: 1, height: '1px', background: '#e5e7eb' }} />
                        <span style={{ margin: '0 0.85rem', color: '#9ca3af', fontSize: '0.78rem', fontWeight: 600 }}>OR CONTINUE WITH</span>
                        <div style={{ flex: 1, height: '1px', background: '#e5e7eb' }} />
                    </div>

                    {/* Google Sign In */}
                    <button
                        type="button"
                        onClick={handleGoogleLogin}
                        style={{
                            width: '100%', padding: '0.75rem',
                            background: '#fff', border: '1.5px solid #e5e7eb',
                            borderRadius: '10px', color: '#374151', fontWeight: 600,
                            fontSize: '0.9rem', cursor: 'pointer',
                            display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '0.6rem',
                            boxShadow: '0 1px 2px rgba(0,0,0,0.05)', transition: 'all 150ms ease',
                        }}
                        onMouseOver={e => { (e.currentTarget as HTMLButtonElement).style.borderColor = '#4f46e5'; (e.currentTarget as HTMLButtonElement).style.boxShadow = '0 2px 8px rgba(79,70,229,0.1)'; }}
                        onMouseOut={e => { (e.currentTarget as HTMLButtonElement).style.borderColor = '#e5e7eb'; (e.currentTarget as HTMLButtonElement).style.boxShadow = '0 1px 2px rgba(0,0,0,0.05)'; }}
                    >
                        <img src="https://www.svgrepo.com/show/475656/google-color.svg" alt="Google" style={{ width: '18px', height: '18px' }} />
                        Sign in with Google
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Login;