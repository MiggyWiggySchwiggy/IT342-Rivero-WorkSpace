import React, { useEffect, useMemo, useState } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import api from '../api/axiosConfig';
import type { ReservationPayload } from '../types/workspace';

type CheckoutState = {
    spaceId?: string;
    spaceName?: string;
    location?: string;
    type?: string;
    hourlyRate?: number;
    startTime?: string;
    endTime?: string;
};

const formatDateTime = (value?: string): string => {
    if (!value) return 'Not selected';
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return 'Invalid date';
    return date.toLocaleString('en-PH', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
        hour: 'numeric',
        minute: '2-digit',
    });
};

const formatCardNumber = (value: string): string => {
    const digits = value.replace(/\D/g, '').slice(0, 16);
    return digits.replace(/(.{4})/g, '$1 ').trim();
};

const Checkout: React.FC = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const checkoutState = (location.state || {}) as CheckoutState;
    const { spaceId, spaceName, location: spaceLocation, type, hourlyRate, startTime, endTime } = checkoutState;
    const [meta, setMeta] = useState({
        spaceName: spaceName || '',
        spaceLocation: spaceLocation || '',
        type: type || '',
        hourlyRate: typeof hourlyRate === 'number' ? hourlyRate : 0,
    });

    const [cardData, setCardData] = useState({ cardNumber: '4242 4242 4242 4242', expiry: '12/26', cvv: '123' });
    const [loading, setLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState('');
    const [successMsg, setSuccessMsg] = useState('');

    const durationHours = useMemo(() => {
        if (!startTime || !endTime) return 0;
        const start = new Date(startTime).getTime();
        const end = new Date(endTime).getTime();
        if (Number.isNaN(start) || Number.isNaN(end) || end <= start) return 0;
        return Math.max(1, Math.ceil((end - start) / (1000 * 60 * 60)));
    }, [startTime, endTime]);

    useEffect(() => {
        if (!spaceId) return;

        const shouldFetchMeta = !meta.spaceName || !meta.spaceLocation || !meta.type || meta.hourlyRate <= 0;
        if (!shouldFetchMeta) return;

        const fetchSpaceMeta = async () => {
            try {
                const response = await api.get(`/spaces/${spaceId}`);
                const data = response.data?.data ?? response.data;

                if (data) {
                    setMeta({
                        spaceName: data.name || meta.spaceName,
                        spaceLocation: data.location || meta.spaceLocation,
                        type: data.type || meta.type,
                        hourlyRate: typeof data.hourlyRate === 'number' ? data.hourlyRate : meta.hourlyRate,
                    });
                }
            } catch {
                // Keep existing state values if fetch fails.
            }
        };

        fetchSpaceMeta();
    }, [spaceId, meta.spaceName, meta.spaceLocation, meta.type, meta.hourlyRate]);

    const rate = meta.hourlyRate;
    const subtotal = durationHours * rate;
    const serviceFee = subtotal > 0 ? 49 : 0;
    const total = subtotal + serviceFee;

    // If a user navigates here directly without valid booking data, send them back.
    if (!spaceId || !startTime || !endTime) {
        navigate('/dashboard');
        return null;
    }

    const handleCheckout = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setErrorMsg('');

        const payload: ReservationPayload = {
            spaceId,
            startTime,
            endTime,
            paymentMethod: {
                cardNumber: cardData.cardNumber.replace(/\s/g, ''),
                expiryDate: cardData.expiry,
                cvv: cardData.cvv
            }
        };

        try {
            await api.post('/reservations/checkout', payload);
            setSuccessMsg('Reservation confirmed successfully! Redirecting to reservation history...');
            setTimeout(() => navigate('/reservations'), 1200);
        } catch (error: any) {
            if (error.response && error.response.data) {
                const errorCode = error.response.data.error?.code || error.response.data.errorCode;
                const errorMessage = error.response.data.error?.message || error.response.data.message;
                if (errorCode === 'BOOK-001') {
                    setErrorMsg('Schedule Conflict: This space was just booked for this time slot.');
                } else if (errorCode === 'PAY-001') {
                    setErrorMsg('Payment Declined: Please verify your Sandbox card details.');
                } else {
                    setErrorMsg(errorMessage || 'Checkout failed.');
                }
            } else {
                setErrorMsg('Network error. Is your Spring Boot server running?');
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
                    <Link to="/dashboard">Cancel Checkout</Link>
                </nav>
            </header>

            <main className="page checkout-page">
                <section className="checkout-layout">
                    <article className="detail-panel pay-panel">
                        <h1 className="card-title">Secure Checkout</h1>
                        <p className="card-subtitle">Confirm your booking and complete sandbox payment.</p>

                        {errorMsg && <div className="alert">{errorMsg}</div>}
                        {successMsg && <div className="alert-success">{successMsg}</div>}

                        <div className="pay-methods">
                            <span className="pay-chip">Visa Test</span>
                            <span className="pay-chip">Mastercard Test</span>
                            <span className="pay-chip">Encrypted</span>
                        </div>

                        <form onSubmit={handleCheckout} className="form" style={{ marginTop: '12px' }}>
                            <div className="field">
                                <label htmlFor="card-number">Card Number (Test Mode)</label>
                                <input
                                    id="card-number"
                                    type="text"
                                    required
                                    value={cardData.cardNumber}
                                    onChange={(e) => setCardData({ ...cardData, cardNumber: formatCardNumber(e.target.value) })}
                                    placeholder="4242 4242 4242 4242"
                                />
                            </div>

                            <div className="row">
                                <div className="field" style={{ flex: 1 }}>
                                    <label htmlFor="expiry">Expiry (MM/YY)</label>
                                    <input
                                        id="expiry"
                                        type="text"
                                        required
                                        maxLength={5}
                                        value={cardData.expiry}
                                        onChange={(e) => setCardData({ ...cardData, expiry: e.target.value.replace(/[^0-9/]/g, '').slice(0, 5) })}
                                        placeholder="12/26"
                                    />
                                </div>
                                <div className="field" style={{ flex: 1 }}>
                                    <label htmlFor="cvv">CVV</label>
                                    <input
                                        id="cvv"
                                        type="password"
                                        required
                                        maxLength={3}
                                        value={cardData.cvv}
                                        onChange={(e) => setCardData({ ...cardData, cvv: e.target.value.replace(/\D/g, '').slice(0, 3) })}
                                        placeholder="123"
                                    />
                                </div>
                            </div>

                            <button className="primary-btn checkout-submit" type="submit" disabled={loading}>
                                {loading ? 'Processing Transaction...' : `Pay PHP ${total.toFixed(2)} & Reserve`}
                            </button>
                        </form>
                    </article>

                    <aside className="detail-panel summary-panel">
                        <h2>Booking Summary</h2>
                        <div className="summary-grid">
                            <div>
                                <span className="summary-label">Workspace</span>
                                <strong>{meta.spaceName || `Workspace ${spaceId}`}</strong>
                            </div>
                            <div>
                                <span className="summary-label">Type</span>
                                <strong>{meta.type || 'General Workspace'}</strong>
                            </div>
                            <div>
                                <span className="summary-label">Location</span>
                                <strong>{meta.spaceLocation || 'N/A'}</strong>
                            </div>
                            <div>
                                <span className="summary-label">Check-in</span>
                                <strong>{formatDateTime(startTime)}</strong>
                            </div>
                            <div>
                                <span className="summary-label">Check-out</span>
                                <strong>{formatDateTime(endTime)}</strong>
                            </div>
                            <div>
                                <span className="summary-label">Estimated Duration</span>
                                <strong>{durationHours} hour{durationHours === 1 ? '' : 's'}</strong>
                            </div>
                        </div>

                        <div className="price-breakdown">
                            <div><span>Subtotal</span><strong>PHP {subtotal.toFixed(2)}</strong></div>
                            <div><span>Service Fee</span><strong>PHP {serviceFee.toFixed(2)}</strong></div>
                            <div className="total-row"><span>Total</span><strong>PHP {total.toFixed(2)}</strong></div>
                        </div>

                        <p className="summary-note">
                            Sandbox mode is active. Use test card data only.
                        </p>
                    </aside>
                </section>
            </main>
        </>
    );
};

export default Checkout;