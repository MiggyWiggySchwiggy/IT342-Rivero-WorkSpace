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
            if (error.response?.data?.error) {
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

    return (
        <div style={{
            minHeight: '100vh',
            background: 'linear-gradient(135deg, #0f172a 0%, #1e1b4b 40%, #312e81 70%, #4f46e5 100%)',
            display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
            padding: '1.5rem', fontFamily: "'Inter', system-ui, sans-serif", position: 'relative', overflow: 'hidden',
        }}>
            {/* Decorative orbs */}
            <div style={{ position: 'absolute', width: '600px', height: '600px', borderRadius: '50%', background: 'radial-gradient(circle, rgba(99,102,241,0.2) 0%, transparent 70%)', top: '-150px', right: '-150px', pointerEvents: 'none' }} />
            <div style={{ position: 'absolute', width: '400px', height: '400px', borderRadius: '50%', background: 'radial-gradient(circle, rgba(167,139,250,0.15) 0%, transparent 70%)', bottom: '-80px', left: '-80px', pointerEvents: 'none' }} />

            {/* Logo */}
            <Link to="/" style={{ textDecoration: 'none', marginBottom: '2rem', zIndex: 1 }}>
                <div style={{ fontSize: '1.5rem', fontWeight: 900, color: '#fff', letterSpacing: '-0.04em', textAlign: 'center' }}>
                    Work<span style={{ color: '#a5b4fc' }}>Space</span>
                </div>
            </Link>

            {/* Glass Card */}
            <div style={{
                width: '100%', maxWidth: '420px', zIndex: 1,
                background: 'rgba(255, 255, 255, 0.97)',
                borderRadius: '20px',
                padding: '2.25rem 2.5rem',
                boxShadow: '0 25px 50px rgba(0,0,0,0.35), 0 0 0 1px rgba(255,255,255,0.1)',
            }}>
                <div style={{ textAlign: 'center', marginBottom: '1.75rem' }}>
                    <h1 style={{ fontSize: '1.6rem', fontWeight: 800, color: '#111827', letterSpacing: '-0.03em', marginBottom: '0.35rem' }}>
                        Welcome back
                    </h1>
                    <p style={{ fontSize: '0.875rem', color: '#6b7280' }}>
                        Sign in to your WorkSpace account
                    </p>
                </div>

                {errorMsg && (
                    <div style={{
                        background: '#fef2f2', border: '1px solid #fecaca', color: '#dc2626',
                        borderRadius: '10px', padding: '0.7rem 1rem', fontSize: '0.84rem',
                        marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem',
                    }}>⚠️ {errorMsg}</div>
                )}
                {successMsg && (
                    <div style={{
                        background: '#f0fdf4', border: '1px solid #bbf7d0', color: '#16a34a',
                        borderRadius: '10px', padding: '0.7rem 1rem', fontSize: '0.84rem',
                        marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem',
                    }}>✅ {successMsg}</div>
                )}

                <form onSubmit={handleLogin} style={{ display: 'grid', gap: '1rem' }}>
                    <div>
                        <label htmlFor="login-email" style={{ display: 'block', fontSize: '0.8rem', fontWeight: 700, color: '#374151', marginBottom: '0.4rem' }}>
                            Email address
                        </label>
                        <input
                            id="login-email"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="you@example.com"
                            required
                            autoFocus
                            style={{ width: '100%', padding: '0.75rem 1rem', border: '1.5px solid #e5e7eb', borderRadius: '10px', fontSize: '0.9rem', color: '#111827', background: '#f9fafb', outline: 'none', boxSizing: 'border-box', transition: 'all 150ms ease' }}
                            onFocus={e => { e.target.style.borderColor = '#4f46e5'; e.target.style.background = '#fff'; e.target.style.boxShadow = '0 0 0 3px rgba(79,70,229,0.12)'; }}
                            onBlur={e => { e.target.style.borderColor = '#e5e7eb'; e.target.style.background = '#f9fafb'; e.target.style.boxShadow = 'none'; }}
                        />
                    </div>

                    <div>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.4rem' }}>
                            <label htmlFor="login-password" style={{ fontSize: '0.8rem', fontWeight: 700, color: '#374151' }}>Password</label>
                            <a href="#" style={{ fontSize: '0.75rem', color: '#4f46e5', textDecoration: 'none', fontWeight: 500 }}>Forgot password?</a>
                        </div>
                        <div style={{ position: 'relative' }}>
                            <input
                                id="login-password"
                                type={showPassword ? 'text' : 'password'}
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Enter your password"
                                required
                                style={{ width: '100%', padding: '0.75rem 2.75rem 0.75rem 1rem', border: '1.5px solid #e5e7eb', borderRadius: '10px', fontSize: '0.9rem', color: '#111827', background: '#f9fafb', outline: 'none', boxSizing: 'border-box', transition: 'all 150ms ease' }}
                                onFocus={e => { e.target.style.borderColor = '#4f46e5'; e.target.style.background = '#fff'; e.target.style.boxShadow = '0 0 0 3px rgba(79,70,229,0.12)'; }}
                                onBlur={e => { e.target.style.borderColor = '#e5e7eb'; e.target.style.background = '#f9fafb'; e.target.style.boxShadow = 'none'; }}
                            />
                            <button type="button" onClick={() => setShowPassword(!showPassword)} style={{ position: 'absolute', right: '0.75rem', top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', cursor: 'pointer', padding: 0, color: '#9ca3af', fontSize: '1rem', lineHeight: 1 }}>
                                {showPassword ? '🙈' : '👁️'}
                            </button>
                        </div>
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        style={{
                            width: '100%', padding: '0.85rem', borderRadius: '10px',
                            background: 'linear-gradient(135deg, #4f46e5, #6366f1)',
                            color: '#fff', border: 'none', fontWeight: 700,
                            fontSize: '0.95rem', cursor: loading ? 'not-allowed' : 'pointer',
                            boxShadow: '0 4px 14px rgba(79,70,229,0.4)',
                            transition: 'all 150ms ease', marginTop: '0.25rem',
                            opacity: loading ? 0.75 : 1,
                        }}
                        onMouseOver={e => { if (!loading) { (e.currentTarget).style.transform = 'translateY(-1px)'; (e.currentTarget).style.boxShadow = '0 6px 20px rgba(79,70,229,0.5)'; } }}
                        onMouseOut={e => { (e.currentTarget).style.transform = 'translateY(0)'; (e.currentTarget).style.boxShadow = '0 4px 14px rgba(79,70,229,0.4)'; }}
                    >
                        {loading ? 'Signing in…' : 'Sign in →'}
                    </button>
                </form>

                {/* Divider */}
                <div style={{ display: 'flex', alignItems: 'center', margin: '1.5rem 0' }}>
                    <div style={{ flex: 1, height: '1px', background: '#e5e7eb' }} />
                    <span style={{ margin: '0 0.85rem', color: '#9ca3af', fontSize: '0.75rem', fontWeight: 600, letterSpacing: '0.05em' }}>OR</span>
                    <div style={{ flex: 1, height: '1px', background: '#e5e7eb' }} />
                </div>

                {/* Google */}
                <button
                    type="button"
                    onClick={handleGoogleLogin}
                    style={{
                        width: '100%', padding: '0.75rem',
                        background: '#fff', border: '1.5px solid #e5e7eb',
                        borderRadius: '10px', color: '#374151', fontWeight: 600,
                        fontSize: '0.9rem', cursor: 'pointer',
                        display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '0.6rem',
                        transition: 'all 150ms ease', boxSizing: 'border-box',
                    }}
                    onMouseOver={e => { (e.currentTarget).style.borderColor = '#4f46e5'; (e.currentTarget).style.background = '#f5f3ff'; }}
                    onMouseOut={e => { (e.currentTarget).style.borderColor = '#e5e7eb'; (e.currentTarget).style.background = '#fff'; }}
                >
                    <img src="https://www.svgrepo.com/show/475656/google-color.svg" alt="Google" style={{ width: '18px', height: '18px' }} />
                    Continue with Google
                </button>

                <p style={{ textAlign: 'center', fontSize: '0.84rem', color: '#6b7280', marginTop: '1.5rem' }}>
                    Don't have an account?{' '}
                    <Link to="/register" style={{ color: '#4f46e5', fontWeight: 700, textDecoration: 'none' }}>
                        Create one free
                    </Link>
                </p>
            </div>

            {/* Bottom back link */}
            <Link to="/" style={{ marginTop: '1.5rem', fontSize: '0.8rem', color: 'rgba(255,255,255,0.55)', textDecoration: 'none', zIndex: 1, transition: 'color 150ms ease' }}
                onMouseOver={e => (e.currentTarget).style.color = 'rgba(255,255,255,0.9)'}
                onMouseOut={e => (e.currentTarget).style.color = 'rgba(255,255,255,0.55)'}
            >
                ← Back to home
            </Link>
        </div>
    );
};

export default Login;