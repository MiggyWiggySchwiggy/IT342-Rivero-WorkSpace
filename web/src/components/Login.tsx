import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axiosConfig';

const Login: React.FC = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMsg, setErrorMsg] = useState('');
    const [successMsg, setSuccessMsg] = useState('');
    const [loading, setLoading] = useState(false);
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
        // Redirect directly to Spring Boot's OAuth2 login endpoint
        const backendBaseUrl = import.meta.env.VITE_BACKEND_BASE_URL ?? 'http://localhost:8080';
        window.location.href = `${backendBaseUrl}/oauth2/authorization/google`;
    };

    return (
        <>
            <header className="topbar">
                <div className="brand">WorkSpace</div>
                <nav className="topbar-links">
                    <Link to="/login" className="active">Login</Link>
                    <Link to="/register">Register</Link>
                </nav>
            </header>

            <main className="auth-wrap">
                <section className="card">
                    <h1 className="card-title">Sign in</h1>
                    <p className="card-subtitle">Welcome back! Please enter your details.</p>

                    {errorMsg && <div className="alert">{errorMsg}</div>}
                    {successMsg && <div className="alert-success">{successMsg}</div>}

                    <form onSubmit={handleLogin} className="form">
                        <div className="field">
                            <label htmlFor="login-email">Email address</label>
                            <input
                                id="login-email"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="you@example.com"
                                required
                                autoFocus
                            />
                        </div>

                        <div className="field">
                            <label htmlFor="login-password">Password</label>
                            <input
                                id="login-password"
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Enter your password"
                                required
                            />
                        </div>

                        <div className="inline-end">
                            <a href="#">Forgot password?</a>
                        </div>

                        <button className="primary-btn" type="submit" disabled={loading}>
                            {loading ? 'Signing in…' : 'Sign in'}
                        </button>
                    </form>

                    {/* --- NEW GOOGLE LOGIN SECTION --- */}
                    <div style={{ display: 'flex', alignItems: 'center', margin: '24px 0 16px' }}>
                        <div style={{ flex: 1, height: '1px', backgroundColor: '#e5e7eb' }}></div>
                        <span style={{ margin: '0 10px', color: '#6b7280', fontSize: '14px', fontWeight: '500' }}>OR</span>
                        <div style={{ flex: 1, height: '1px', backgroundColor: '#e5e7eb' }}></div>
                    </div>

                    <button
                        type="button"
                        onClick={handleGoogleLogin}
                        style={{
                            width: '100%', padding: '10px', backgroundColor: '#ffffff', border: '1px solid #d1d5db',
                            borderRadius: '6px', color: '#374151', fontWeight: '600', cursor: 'pointer',
                            display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '10px',
                            boxShadow: '0 1px 2px 0 rgba(0, 0, 0, 0.05)'
                        }}
                    >
                        <img src="https://www.svgrepo.com/show/475656/google-color.svg" alt="Google Logo" style={{ width: '20px', height: '20px' }}/>
                        Sign in with Google
                    </button>
                    {/* -------------------------------- */}

                    <div className="card-footer">
                        Don't have an account? <Link to="/register">Create one</Link>
                    </div>
                </section>
            </main>
        </>
    );
};

export default Login;