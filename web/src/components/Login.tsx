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
                    <h1 className="card-title">Welcome back</h1>
                    <p className="card-subtitle">Sign in to manage your workspace bookings.</p>

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

                    <div className="card-footer">
                        Don't have an account? <Link to="/register">Create one</Link>
                    </div>
                </section>
            </main>
        </>
    );
};

export default Login;
