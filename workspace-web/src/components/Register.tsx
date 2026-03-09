import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axiosConfig';

const Register: React.FC = () => {
    const [firstname, setFirstname] = useState('');
    const [lastname, setLastname] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errorMsg, setErrorMsg] = useState('');
    const [successMsg, setSuccessMsg] = useState('');
    const [loading, setLoading] = useState(false);
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

    return (
        <>
            <header className="topbar">
                <div className="brand">WorkSpace</div>
                <nav className="topbar-links">
                    <Link to="/login">Login</Link>
                    <Link to="/register" className="active">Register</Link>
                </nav>
            </header>

            <main className="auth-wrap">
                <section className="card">
                    <h1 className="card-title">Create account</h1>
                    <p className="card-subtitle">Get started with your workspace booking profile.</p>

                    {errorMsg && <div className="alert">{errorMsg}</div>}
                    {successMsg && <div className="alert-success">{successMsg}</div>}

                    <form onSubmit={handleRegister} className="form">
                        <div className="row">
                            <div className="field" style={{ flex: 1 }}>
                                <label htmlFor="reg-fn">First name</label>
                                <input
                                    id="reg-fn"
                                    type="text"
                                    value={firstname}
                                    onChange={(e) => setFirstname(e.target.value)}
                                    placeholder="John"
                                    required
                                    autoFocus
                                />
                            </div>

                            <div className="field" style={{ flex: 1 }}>
                                <label htmlFor="reg-ln">Last name</label>
                                <input
                                    id="reg-ln"
                                    type="text"
                                    value={lastname}
                                    onChange={(e) => setLastname(e.target.value)}
                                    placeholder="Doe"
                                    required
                                />
                            </div>
                        </div>

                        <div className="field">
                            <label htmlFor="reg-email">Email address</label>
                            <input
                                id="reg-email"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="you@example.com"
                                required
                            />
                        </div>

                        <div className="row">
                            <div className="field" style={{ flex: 1 }}>
                                <label htmlFor="reg-pw">Password</label>
                                <input
                                    id="reg-pw"
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    placeholder="Min. 8 characters"
                                    required
                                />
                            </div>

                            <div className="field" style={{ flex: 1 }}>
                                <label htmlFor="reg-cpw">Confirm password</label>
                                <input
                                    id="reg-cpw"
                                    type="password"
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    placeholder="Re-enter password"
                                    required
                                />
                            </div>
                        </div>

                        <button className="primary-btn" type="submit" disabled={loading}>
                            {loading ? 'Creating account…' : 'Create account'}
                        </button>
                    </form>

                    <div className="card-footer">
                        Already have an account? <Link to="/login">Sign in</Link>
                    </div>
                </section>
            </main>
        </>
    );
};

export default Register;
