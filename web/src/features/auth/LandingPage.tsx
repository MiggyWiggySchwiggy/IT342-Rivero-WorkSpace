import React from 'react';
import { useNavigate } from 'react-router-dom';

const LandingPage: React.FC = () => {
    const navigate = useNavigate();

    const features = [
        {
            icon: '🗓️',
            title: 'Instant Booking',
            description: 'Reserve your perfect workspace in seconds. Browse, pick a time slot, and confirm — all in one seamless flow.',
        },
        {
            icon: '🏢',
            title: 'Flexible Spaces',
            description: 'From private studios to open co-working areas and executive boardrooms. We have a space for every need.',
        },
        {
            icon: '🔒',
            title: 'Secure Payments',
            description: 'Industry-standard payment security powered by Stripe. Your transactions are encrypted end-to-end.',
        },
        {
            icon: '📍',
            title: 'Prime Locations',
            description: 'Strategically located workspaces across the city, so you\'re never far from a professional environment.',
        },
        {
            icon: '⚡',
            title: 'Real-Time Availability',
            description: 'See live availability for any workspace. No double bookings, no surprises — just reliable scheduling.',
        },
        {
            icon: '📱',
            title: 'Mobile Ready',
            description: 'Book on the go with our Android app. Full functionality in the palm of your hand, wherever you are.',
        },
    ];

    const steps = [
        { step: '01', title: 'Browse Spaces', description: 'Explore our curated collection of professional workspaces filtered by type, location, and availability.' },
        { step: '02', title: 'Reserve Your Slot', description: 'Pick your date and time, review pricing, and confirm your booking with secure Stripe payment.' },
        { step: '03', title: 'Show Up & Work', description: 'Arrive at your workspace and get straight to work. Your booking confirmation is your key.' },
    ];

    return (
        <div style={{ fontFamily: "'Inter', system-ui, sans-serif", color: '#111827', background: '#fff', overflowX: 'hidden' }}>

            {/* ── Nav ── */}
            <nav style={{
                position: 'fixed', top: 0, left: 0, right: 0, zIndex: 100,
                display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                padding: '0 2rem', height: '60px',
                background: 'rgba(255,255,255,0.85)',
                backdropFilter: 'blur(12px)',
                borderBottom: '1px solid rgba(229,231,235,0.8)',
            }}>
                <div style={{ fontSize: '1.1rem', fontWeight: 800, color: '#4f46e5', letterSpacing: '-0.04em' }}>
                    Work<span style={{ color: '#111827' }}>Space</span>
                </div>
                <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
                    <button
                        onClick={() => navigate('/login')}
                        style={{
                            background: 'transparent', border: '1px solid #e5e7eb', color: '#374151',
                            padding: '0.45rem 1.1rem', borderRadius: '8px', fontWeight: 600, fontSize: '0.875rem', cursor: 'pointer',
                            transition: 'all 150ms ease',
                        }}
                        onMouseOver={e => { (e.target as HTMLButtonElement).style.borderColor = '#4f46e5'; (e.target as HTMLButtonElement).style.color = '#4f46e5'; }}
                        onMouseOut={e => { (e.target as HTMLButtonElement).style.borderColor = '#e5e7eb'; (e.target as HTMLButtonElement).style.color = '#374151'; }}
                    >
                        Sign In
                    </button>
                    <button
                        onClick={() => navigate('/register')}
                        style={{
                            background: '#4f46e5', border: 'none', color: '#fff',
                            padding: '0.45rem 1.1rem', borderRadius: '8px', fontWeight: 600, fontSize: '0.875rem', cursor: 'pointer',
                            boxShadow: '0 1px 3px rgba(79,70,229,0.4)', transition: 'all 150ms ease',
                        }}
                        onMouseOver={e => (e.target as HTMLButtonElement).style.background = '#4338ca'}
                        onMouseOut={e => (e.target as HTMLButtonElement).style.background = '#4f46e5'}
                    >
                        Get Started
                    </button>
                </div>
            </nav>

            {/* ── Hero ── */}
            <section style={{
                minHeight: '100vh',
                background: 'linear-gradient(135deg, #0f172a 0%, #1e1b4b 40%, #312e81 70%, #4f46e5 100%)',
                display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
                padding: '8rem 2rem 6rem', textAlign: 'center', position: 'relative', overflow: 'hidden',
            }}>
                {/* Background glow orbs */}
                <div style={{
                    position: 'absolute', width: '600px', height: '600px', borderRadius: '50%',
                    background: 'radial-gradient(circle, rgba(99,102,241,0.25) 0%, transparent 70%)',
                    top: '-100px', left: '-100px', pointerEvents: 'none',
                }} />
                <div style={{
                    position: 'absolute', width: '400px', height: '400px', borderRadius: '50%',
                    background: 'radial-gradient(circle, rgba(167,139,250,0.2) 0%, transparent 70%)',
                    bottom: '0px', right: '-50px', pointerEvents: 'none',
                }} />

                <div style={{
                    display: 'inline-flex', alignItems: 'center', gap: '0.5rem',
                    background: 'rgba(255,255,255,0.1)', border: '1px solid rgba(255,255,255,0.2)',
                    borderRadius: '999px', padding: '0.35rem 1rem', marginBottom: '1.75rem',
                }}>
                    <span style={{ width: '8px', height: '8px', borderRadius: '50%', background: '#34d399', display: 'inline-block' }} />
                    <span style={{ color: 'rgba(255,255,255,0.9)', fontSize: '0.8rem', fontWeight: 500 }}>Now live — Book your workspace today</span>
                </div>

                <h1 style={{
                    fontSize: 'clamp(2.5rem, 7vw, 5rem)', fontWeight: 900,
                    color: '#fff', lineHeight: 1.1, letterSpacing: '-0.04em',
                    marginBottom: '1.25rem', maxWidth: '800px',
                }}>
                    The smarter way to{' '}
                    <span style={{
                        background: 'linear-gradient(90deg, #a5b4fc, #c4b5fd)',
                        WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent',
                    }}>
                        book workspaces
                    </span>
                </h1>

                <p style={{
                    fontSize: 'clamp(1rem, 2.5vw, 1.2rem)', color: 'rgba(255,255,255,0.7)',
                    maxWidth: '560px', lineHeight: 1.7, marginBottom: '2.5rem',
                }}>
                    Find and reserve professional workspaces in minutes. Studios, boardrooms, co-working desks — all in one platform.
                </p>

                <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', justifyContent: 'center' }}>
                    <button
                        onClick={() => navigate('/register')}
                        style={{
                            background: '#fff', color: '#4f46e5', border: 'none',
                            padding: '0.85rem 2rem', borderRadius: '10px', fontWeight: 700,
                            fontSize: '1rem', cursor: 'pointer',
                            boxShadow: '0 4px 24px rgba(0,0,0,0.3)', transition: 'all 150ms ease',
                        }}
                        onMouseOver={e => { (e.target as HTMLButtonElement).style.transform = 'translateY(-2px)'; (e.target as HTMLButtonElement).style.boxShadow = '0 8px 32px rgba(0,0,0,0.35)'; }}
                        onMouseOut={e => { (e.target as HTMLButtonElement).style.transform = 'translateY(0)'; (e.target as HTMLButtonElement).style.boxShadow = '0 4px 24px rgba(0,0,0,0.3)'; }}
                    >
                        Get started free →
                    </button>
                    <button
                        onClick={() => navigate('/login')}
                        style={{
                            background: 'rgba(255,255,255,0.1)', color: '#fff',
                            border: '1px solid rgba(255,255,255,0.25)',
                            padding: '0.85rem 2rem', borderRadius: '10px', fontWeight: 600,
                            fontSize: '1rem', cursor: 'pointer', transition: 'all 150ms ease',
                            backdropFilter: 'blur(8px)',
                        }}
                        onMouseOver={e => (e.target as HTMLButtonElement).style.background = 'rgba(255,255,255,0.18)'}
                        onMouseOut={e => (e.target as HTMLButtonElement).style.background = 'rgba(255,255,255,0.1)'}
                    >
                        Sign in
                    </button>
                </div>

                {/* Stats row */}
                <div style={{
                    display: 'flex', gap: '3rem', marginTop: '4rem', flexWrap: 'wrap', justifyContent: 'center',
                }}>
                    {[
                        { value: '50+', label: 'Workspaces' },
                        { value: '100%', label: 'Verified Listings' },
                        { value: '24/7', label: 'Support Ready' },
                    ].map(s => (
                        <div key={s.label} style={{ textAlign: 'center' }}>
                            <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#fff', letterSpacing: '-0.04em' }}>{s.value}</div>
                            <div style={{ fontSize: '0.82rem', color: 'rgba(255,255,255,0.55)', marginTop: '0.15rem' }}>{s.label}</div>
                        </div>
                    ))}
                </div>
            </section>

            {/* ── Features ── */}
            <section style={{ padding: '6rem 2rem', background: '#f8fafc' }}>
                <div style={{ maxWidth: '1100px', margin: '0 auto' }}>
                    <div style={{ textAlign: 'center', marginBottom: '3.5rem' }}>
                        <span style={{
                            display: 'inline-block', fontSize: '0.75rem', fontWeight: 700, color: '#4f46e5',
                            textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: '0.75rem',
                            background: '#eef2ff', padding: '0.3rem 0.85rem', borderRadius: '999px',
                        }}>
                            Features
                        </span>
                        <h2 style={{ fontSize: 'clamp(1.75rem, 4vw, 2.75rem)', fontWeight: 800, letterSpacing: '-0.03em', color: '#111827' }}>
                            Everything you need,<br />nothing you don't
                        </h2>
                        <p style={{ fontSize: '1.05rem', color: '#6b7280', marginTop: '0.75rem', maxWidth: '480px', margin: '0.75rem auto 0' }}>
                            WorkSpace is built for professionals who demand reliable, fast, and flexible workspace solutions.
                        </p>
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '1.25rem' }}>
                        {features.map((f, i) => (
                            <div key={i} style={{
                                background: '#fff', border: '1px solid #e5e7eb',
                                borderRadius: '16px', padding: '1.75rem',
                                transition: 'all 200ms ease',
                                boxShadow: '0 1px 3px rgba(0,0,0,0.04)',
                            }}
                                onMouseOver={e => { (e.currentTarget as HTMLDivElement).style.boxShadow = '0 8px 24px rgba(79,70,229,0.1)'; (e.currentTarget as HTMLDivElement).style.borderColor = '#c7d2fe'; (e.currentTarget as HTMLDivElement).style.transform = 'translateY(-3px)'; }}
                                onMouseOut={e => { (e.currentTarget as HTMLDivElement).style.boxShadow = '0 1px 3px rgba(0,0,0,0.04)'; (e.currentTarget as HTMLDivElement).style.borderColor = '#e5e7eb'; (e.currentTarget as HTMLDivElement).style.transform = 'translateY(0)'; }}
                            >
                                <div style={{ fontSize: '2rem', marginBottom: '1rem' }}>{f.icon}</div>
                                <h3 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '0.5rem', color: '#111827' }}>{f.title}</h3>
                                <p style={{ fontSize: '0.875rem', color: '#6b7280', lineHeight: 1.65 }}>{f.description}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* ── How It Works ── */}
            <section style={{ padding: '6rem 2rem', background: '#fff' }}>
                <div style={{ maxWidth: '900px', margin: '0 auto', textAlign: 'center' }}>
                    <span style={{
                        display: 'inline-block', fontSize: '0.75rem', fontWeight: 700, color: '#4f46e5',
                        textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: '0.75rem',
                        background: '#eef2ff', padding: '0.3rem 0.85rem', borderRadius: '999px',
                    }}>
                        How It Works
                    </span>
                    <h2 style={{ fontSize: 'clamp(1.75rem, 4vw, 2.75rem)', fontWeight: 800, letterSpacing: '-0.03em', color: '#111827', marginBottom: '3rem' }}>
                        Book a workspace in 3 steps
                    </h2>

                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '2rem', textAlign: 'left' }}>
                        {steps.map((s, i) => (
                            <div key={i} style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                                <div style={{
                                    fontSize: '0.75rem', fontWeight: 800, color: '#4f46e5',
                                    background: '#eef2ff', width: '40px', height: '40px',
                                    borderRadius: '10px', display: 'flex', alignItems: 'center', justifyContent: 'center',
                                    letterSpacing: '0.05em',
                                }}>
                                    {s.step}
                                </div>
                                <h3 style={{ fontSize: '1.05rem', fontWeight: 700, color: '#111827' }}>{s.title}</h3>
                                <p style={{ fontSize: '0.875rem', color: '#6b7280', lineHeight: 1.65 }}>{s.description}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* ── CTA Banner ── */}
            <section style={{
                padding: '5rem 2rem',
                background: 'linear-gradient(135deg, #1e1b4b 0%, #4f46e5 100%)',
                textAlign: 'center',
            }}>
                <h2 style={{ fontSize: 'clamp(1.75rem, 4vw, 2.5rem)', fontWeight: 800, color: '#fff', marginBottom: '1rem', letterSpacing: '-0.03em' }}>
                    Ready to find your workspace?
                </h2>
                <p style={{ color: 'rgba(255,255,255,0.7)', fontSize: '1.05rem', marginBottom: '2rem' }}>
                    Join professionals who trust WorkSpace for reliable, flexible, and premium work environments.
                </p>
                <button
                    onClick={() => navigate('/register')}
                    style={{
                        background: '#fff', color: '#4f46e5', border: 'none',
                        padding: '0.9rem 2.5rem', borderRadius: '10px', fontWeight: 700,
                        fontSize: '1rem', cursor: 'pointer',
                        boxShadow: '0 4px 24px rgba(0,0,0,0.25)', transition: 'all 150ms ease',
                    }}
                    onMouseOver={e => (e.target as HTMLButtonElement).style.transform = 'translateY(-2px)'}
                    onMouseOut={e => (e.target as HTMLButtonElement).style.transform = 'translateY(0)'}
                >
                    Create your free account →
                </button>
            </section>

            {/* ── Footer ── */}
            <footer style={{
                padding: '2rem',
                background: '#0f172a',
                display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                flexWrap: 'wrap', gap: '1rem',
            }}>
                <div style={{ fontSize: '1rem', fontWeight: 800, color: '#4f46e5' }}>
                    Work<span style={{ color: '#fff' }}>Space</span>
                </div>
                <p style={{ color: 'rgba(255,255,255,0.4)', fontSize: '0.8rem' }}>
                    © 2026 WorkSpace. All rights reserved. Built for IT342 — Systems Integration and Architecture.
                </p>
                <div style={{ display: 'flex', gap: '1.25rem' }}>
                    {['Sign In', 'Register'].map(l => (
                        <button
                            key={l}
                            onClick={() => navigate(l === 'Sign In' ? '/login' : '/register')}
                            style={{
                                background: 'none', border: 'none', color: 'rgba(255,255,255,0.5)',
                                fontSize: '0.82rem', cursor: 'pointer', padding: 0,
                                transition: 'color 150ms ease',
                            }}
                            onMouseOver={e => (e.target as HTMLButtonElement).style.color = '#fff'}
                            onMouseOut={e => (e.target as HTMLButtonElement).style.color = 'rgba(255,255,255,0.5)'}
                        >
                            {l}
                        </button>
                    ))}
                </div>
            </footer>
        </div>
    );
};

export default LandingPage;
