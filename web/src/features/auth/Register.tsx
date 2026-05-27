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
            if (error.response?.data?.error) {
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
        background: '#f9fafb',
        outline: 'none',
        boxSizing: 'border-box',
        transition: 'all 150ms ease',
    };

    const passwordMismatch = confirmPassword.length > 0 && confirmPassword !== password;

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
                width: '100%', maxWidth: '460px', zIndex: 1,
                background: 'rgba(255, 255, 255, 0.97)',
                borderRadius: '20px',
                padding: '2.25rem 2.5rem',
                boxShadow: '0 25px 50px rgba(0,0,0,0.35), 0 0 0 1px rgba(255,255,255,0.1)',
            }}>
                <div style={{ textAlign: 'center', marginBottom: '1.75rem' }}>
                    <h1 style={{ fontSize: '1.6rem', fontWeight: 800, color: '#111827', letterSpacing: '-0.03em', marginBottom: '0.35rem' }}>
                        Create your account
                    </h1>
                    <p style={{ fontSize: '0.875rem', color: '#6b7280' }}>
                        Join WorkSpace and book your perfect workspace today
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

                <form onSubmit={handleRegister} style={{ display: 'grid', gap: '1rem' }}>
                    {/* Name row */}
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
                        <div>
                            <label htmlFor="reg-fn" style={{ display: 'block', fontSize: '0.8rem', fontWeight: 700, color: '#374151', marginBottom: '0.4rem' }}>First name</label>
                            <input
                                id="reg-fn"
                                type="text"
                                value={firstname}
                                onChange={(e) => setFirstname(e.target.value)}
                                placeholder="John"
                                required
                                autoFocus
                                style={inputStyle}
                                onFocus={e => { e.target.style.borderColor = '#4f46e5'; e.target.style.background = '#fff'; e.target.style.boxShadow = '0 0 0 3px rgba(79,70,229,0.12)'; }}
                                onBlur={e => { e.target.style.borderColor = '#e5e7eb'; e.target.style.background = '#f9fafb'; e.target.style.boxShadow = 'none'; }}
                            />
                        </div>
                        <div>
                            <label htmlFor="reg-ln" style={{ display: 'block', fontSize: '0.8rem', fontWeight: 700, color: '#374151', marginBottom: '0.4rem' }}>Last name</label>
                            <input
                                id="reg-ln"
                                type="text"
                                value={lastname}
                                onChange={(e) => setLastname(e.target.value)}
                                placeholder="Doe"
                                required
                                style={inputStyle}
                                onFocus={e => { e.target.style.borderColor = '#4f46e5'; e.target.style.background = '#fff'; e.target.style.boxShadow = '0 0 0 3px rgba(79,70,229,0.12)'; }}
                                onBlur={e => { e.target.style.borderColor = '#e5e7eb'; e.target.style.background = '#f9fafb'; e.target.style.boxShadow = 'none'; }}
                            />
                        </div>
                    </div>

                    <div>
                        <label htmlFor="reg-email" style={{ display: 'block', fontSize: '0.8rem', fontWeight: 700, color: '#374151', marginBottom: '0.4rem' }}>Email address</label>
                        <input
                            id="reg-email"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="you@example.com"
                            required
                            style={inputStyle}
                            onFocus={e => { e.target.style.borderColor = '#4f46e5'; e.target.style.background = '#fff'; e.target.style.boxShadow = '0 0 0 3px rgba(79,70,229,0.12)'; }}
                            onBlur={e => { e.target.style.borderColor = '#e5e7eb'; e.target.style.background = '#f9fafb'; e.target.style.boxShadow = 'none'; }}
                        />
                    </div>

                    <div>
                        <label htmlFor="reg-pw" style={{ display: 'block', fontSize: '0.8rem', fontWeight: 700, color: '#374151', marginBottom: '0.4rem' }}>Password</label>
                        <div style={{ position: 'relative' }}>
                            <input
                                id="reg-pw"
                                type={showPassword ? 'text' : 'password'}
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Min. 8 characters"
                                required
                                style={{ ...inputStyle, paddingRight: '2.75rem' }}
                                onFocus={e => { e.target.style.borderColor = '#4f46e5'; e.target.style.background = '#fff'; e.target.style.boxShadow = '0 0 0 3px rgba(79,70,229,0.12)'; }}
                                onBlur={e => { e.target.style.borderColor = '#e5e7eb'; e.target.style.background = '#f9fafb'; e.target.style.boxShadow = 'none'; }}
                            />
                            <button type="button" onClick={() => setShowPassword(!showPassword)} style={{ position: 'absolute', right: '0.75rem', top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', cursor: 'pointer', padding: 0, color: '#9ca3af', fontSize: '1rem', lineHeight: 1 }}>
                                {showPassword ? '🙈' : '👁️'}
                            </button>
                        </div>
                    </div>

                    <div>
                        <label htmlFor="reg-cpw" style={{ display: 'block', fontSize: '0.8rem', fontWeight: 700, color: '#374151', marginBottom: '0.4rem' }}>Confirm password</label>
                        <input
                            id="reg-cpw"
                            type="password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            placeholder="Re-enter your password"
                            required
                            style={{ ...inputStyle, borderColor: passwordMismatch ? '#fca5a5' : '#e5e7eb' }}
                            onFocus={e => { if (!passwordMismatch) { e.target.style.borderColor = '#4f46e5'; e.target.style.boxShadow = '0 0 0 3px rgba(79,70,229,0.12)'; } e.target.style.background = '#fff'; }}
                            onBlur={e => { e.target.style.borderColor = passwordMismatch ? '#fca5a5' : '#e5e7eb'; e.target.style.background = '#f9fafb'; e.target.style.boxShadow = 'none'; }}
                        />
                        {passwordMismatch && (
                            <p style={{ fontSize: '0.75rem', color: '#ef4444', marginTop: '0.3rem' }}>⚠️ Passwords do not match</p>
                        )}
                    </div>

                    <button
                        type="submit"
                        disabled={loading || passwordMismatch}
                        style={{
                            width: '100%', padding: '0.85rem', borderRadius: '10px',
                            background: 'linear-gradient(135deg, #4f46e5, #6366f1)',
                            color: '#fff', border: 'none', fontWeight: 700,
                            fontSize: '0.95rem', cursor: (loading || passwordMismatch) ? 'not-allowed' : 'pointer',
                            boxShadow: '0 4px 14px rgba(79,70,229,0.4)',
                            transition: 'all 150ms ease', marginTop: '0.25rem',
                            opacity: (loading || passwordMismatch) ? 0.65 : 1,
                        }}
                        onMouseOver={e => { if (!loading && !passwordMismatch) { (e.currentTarget).style.transform = 'translateY(-1px)'; (e.currentTarget).style.boxShadow = '0 6px 20px rgba(79,70,229,0.5)'; } }}
                        onMouseOut={e => { (e.currentTarget).style.transform = 'translateY(0)'; (e.currentTarget).style.boxShadow = '0 4px 14px rgba(79,70,229,0.4)'; }}
                    >
                        {loading ? 'Creating account…' : 'Create account →'}
                    </button>
                </form>

                <p style={{ textAlign: 'center', fontSize: '0.84rem', color: '#6b7280', marginTop: '1.5rem' }}>
                    Already have an account?{' '}
                    <Link to="/login" style={{ color: '#4f46e5', fontWeight: 700, textDecoration: 'none' }}>
                        Sign in
                    </Link>
                </p>

                <p style={{ textAlign: 'center', fontSize: '0.72rem', color: '#9ca3af', marginTop: '0.75rem' }}>
                    By creating an account, you agree to our terms of service and privacy policy.
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

export default Register;
