import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link, useLocation } from 'react-router-dom';
import api from '../api/axiosConfig';
import type { Space } from '../types/workspace';

type Review = {
    author: string;
    rating: number;
    comment: string;
};

type SpaceDetails = {
    description: string;
    imageUrl: string;
    photos: string[];
    amenities: string[];
    utilities: string[];
    checkInWindow: string;
    cancellation: string;
    reviews: Review[];
};

type AvailabilitySlot = {
    label: string;
    available: boolean;
};

type AvailabilityDay = {
    dayLabel: string;
    dateLabel: string;
    slots: AvailabilitySlot[];
};

const SAMPLE_WORKSPACES: Space[] = [
    {
        id: 'sample-pod-1',
        name: 'Focus Pod A1',
        location: 'Cebu IT Park',
        type: 'Pod',
        capacity: 1,
        hourlyRate: 120,
        rating: 4.7,
        available: true,
    },
    {
        id: 'sample-meeting-1',
        name: 'Sprint Room B',
        location: 'Ayala Center Cebu',
        type: 'Meeting Room',
        capacity: 8,
        hourlyRate: 450,
        rating: 4.8,
        available: true,
    },
    {
        id: 'sample-studio-1',
        name: 'Creator Studio C3',
        location: 'Mandaue City',
        type: 'Studio',
        capacity: 4,
        hourlyRate: 380,
        rating: 4.6,
        available: false,
    },
    {
        id: 'sample-boardroom-1',
        name: 'Boardroom Skyline',
        location: 'Cebu Business Park',
        type: 'Boardroom',
        capacity: 14,
        hourlyRate: 900,
        rating: 4.9,
        available: true,
    },
];

const SAMPLE_SPACE_DETAILS: Record<string, SpaceDetails> = {
    'sample-pod-1': {
        description: 'A quiet solo pod built for deep work, quick calls, and focused planning sessions. Best for individual tasks that require minimal distractions.',
        imageUrl: 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1400&q=80',
        photos: [
            'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1400&q=80',
            'https://images.unsplash.com/photo-1527192491265-7e15c55b1ed2?auto=format&fit=crop&w=1400&q=80',
            'https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=1400&q=80',
        ],
        amenities: ['Ergonomic chair', 'Noise-controlled booth', 'Task lighting', 'Privacy panel'],
        utilities: ['High-speed Wi-Fi', 'Power outlet', 'USB-C charging', 'Air conditioning'],
        checkInWindow: 'Anytime between 8:00 AM - 9:00 PM',
        cancellation: 'Free cancellation up to 2 hours before check-in.',
        reviews: [
            { author: 'Janine M.', rating: 5, comment: 'Super quiet and very clean. Perfect for focused work.' },
            { author: 'Carlo R.', rating: 4, comment: 'Reliable Wi-Fi and comfy seat. Great value.' },
        ],
    },
    'sample-meeting-1': {
        description: 'Collaborative meeting room for standups, sprint planning, and client discussions. Includes a large display and whiteboard wall for team sessions.',
        imageUrl: 'https://images.unsplash.com/photo-1524758631624-e2822e304c36?auto=format&fit=crop&w=1400&q=80',
        photos: [
            'https://images.unsplash.com/photo-1524758631624-e2822e304c36?auto=format&fit=crop&w=1400&q=80',
            'https://images.unsplash.com/photo-1497215728101-856f4ea42174?auto=format&fit=crop&w=1400&q=80',
            'https://images.unsplash.com/photo-1577415124269-fc1140a69e91?auto=format&fit=crop&w=1400&q=80',
        ],
        amenities: ['8-seater table', 'Whiteboard wall', '55-inch display', 'Video call setup'],
        utilities: ['Fiber Wi-Fi', 'HDMI connectivity', 'Power strips', 'Climate control'],
        checkInWindow: 'Anytime between 8:00 AM - 10:00 PM',
        cancellation: 'Free cancellation up to 4 hours before check-in.',
        reviews: [
            { author: 'Mika T.', rating: 5, comment: 'Great room for sprint retros. Screen quality is excellent.' },
            { author: 'Paolo V.', rating: 4, comment: 'Well-maintained and easy to find in the building.' },
        ],
    },
    'sample-studio-1': {
        description: 'A flexible creator studio suitable for recording, editing, and product shoots with controlled lighting and movable setup zones.',
        imageUrl: 'https://images.unsplash.com/photo-1497215842964-222b430dc094?auto=format&fit=crop&w=1400&q=80',
        photos: [
            'https://images.unsplash.com/photo-1497215842964-222b430dc094?auto=format&fit=crop&w=1400&q=80',
            'https://images.unsplash.com/photo-1521737604893-d14cc237f11d?auto=format&fit=crop&w=1400&q=80',
            'https://images.unsplash.com/photo-1517048676732-d65bc937f952?auto=format&fit=crop&w=1400&q=80',
        ],
        amenities: ['Acoustic treatment', 'Adjustable lights', 'Backdrop mount', 'Movable desk'],
        utilities: ['High-speed internet', 'Multiple outlets', 'Air conditioning', 'Storage cabinet'],
        checkInWindow: 'Anytime between 9:00 AM - 8:00 PM',
        cancellation: 'Free cancellation up to 24 hours before check-in.',
        reviews: [
            { author: 'Ria C.', rating: 5, comment: 'Lighting setup was ideal for content shoots.' },
            { author: 'Alden P.', rating: 4, comment: 'Nice room, but tends to get booked quickly.' },
        ],
    },
    'sample-boardroom-1': {
        description: 'Premium boardroom with skyline views, ideal for executive meetings, interviews, and strategy sessions.',
        imageUrl: 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=1400&q=80',
        photos: [
            'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=1400&q=80',
            'https://images.unsplash.com/photo-1552664730-d307ca884978?auto=format&fit=crop&w=1400&q=80',
            'https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=1400&q=80',
        ],
        amenities: ['14-seat board table', 'Dual presentation screens', 'Conference camera', 'Coffee counter'],
        utilities: ['Dedicated Wi-Fi lane', 'Power outlets at each seat', 'Air conditioning', 'Reception support'],
        checkInWindow: 'Anytime between 8:00 AM - 9:00 PM',
        cancellation: 'Free cancellation up to 6 hours before check-in.',
        reviews: [
            { author: 'Lea S.', rating: 5, comment: 'Professional setup and very presentable for clients.' },
            { author: 'Nico D.', rating: 5, comment: 'Excellent location and smooth check-in process.' },
        ],
    },
};

const buildDetails = (space: Space): SpaceDetails => {
    const sampleDetails = SAMPLE_SPACE_DETAILS[space.id];
    if (sampleDetails) {
        return sampleDetails;
    }

    const defaultUtilities = ['Fast Wi-Fi', 'Power outlets', 'Air conditioning', 'Reception support'];

    return {
        description: space.description || `A comfortable ${space.type.toLowerCase()} in ${space.location}, suitable for focused work and team collaboration.`,
        imageUrl: space.imageUrl || 'https://images.unsplash.com/photo-1497366811353-6870744d04b2?auto=format&fit=crop&w=1400&q=80',
        photos: [
            space.imageUrl || 'https://images.unsplash.com/photo-1497366811353-6870744d04b2?auto=format&fit=crop&w=1400&q=80',
            'https://images.unsplash.com/photo-1497215728101-856f4ea42174?auto=format&fit=crop&w=1400&q=80',
            'https://images.unsplash.com/photo-1497366754035-f200968a6e72?auto=format&fit=crop&w=1400&q=80',
        ],
        amenities: ['Comfortable seating', 'Clean workspace', 'Well-lit interior', 'Easy access'],
        utilities: defaultUtilities,
        checkInWindow: 'Anytime between 8:00 AM - 9:00 PM',
        cancellation: 'Free cancellation up to 4 hours before check-in.',
        reviews: [
            { author: 'Verified Guest', rating: Math.max(4, Math.round(space.rating)), comment: 'Great overall workspace experience.' },
            { author: 'Community Member', rating: Math.max(4, Math.round(space.rating - 0.2)), comment: 'Smooth booking and reliable facilities.' },
        ],
    };
};

const buildAvailabilityPreview = (spaceId: string): AvailabilityDay[] => {
    const dayNames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    const baseSlots = ['8:00-11:00', '12:00-3:00', '4:00-7:00'];
    const hashSeed = spaceId.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);

    return Array.from({ length: 7 }).map((_, index) => {
        const date = new Date();
        date.setDate(date.getDate() + index);

        const slots = baseSlots.map((slot, slotIndex) => {
            const available = (hashSeed + index + slotIndex) % 4 !== 0;
            return { label: slot, available };
        });

        return {
            dayLabel: dayNames[date.getDay()],
            dateLabel: date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }),
            slots,
        };
    });
};

const SpaceDetail: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const location = useLocation();
    const routeState = location.state as { space?: Space } | null;
    const preloadedSpace = routeState?.space ?? null;

    const [space, setSpace] = useState<Space | null>(preloadedSpace);
    const [loading, setLoading] = useState<boolean>(!preloadedSpace);
    const [startTime, setStartTime] = useState('');
    const [endTime, setEndTime] = useState('');
    const [errorMsg, setErrorMsg] = useState('');
    const [activePhotoIndex, setActivePhotoIndex] = useState(0);

    useEffect(() => {
        const normalizedId = decodeURIComponent((id ?? '').trim());

        if (!normalizedId) {
            setErrorMsg('Invalid space selection. Please go back and choose a workspace.');
            setLoading(false);
            return;
        }

        if (preloadedSpace && preloadedSpace.id === normalizedId) {
            setSpace(preloadedSpace);
            setLoading(false);
            return;
        }

        const sampleSpace = SAMPLE_WORKSPACES.find((ws) => ws.id === normalizedId);
        if (sampleSpace) {
            setSpace(sampleSpace);
            setErrorMsg('');
            setLoading(false);
            return;
        }

        const fetchSpace = async () => {
            setLoading(true);
            try {
                const response = await api.get(`/spaces/${normalizedId}`, { timeout: 5000 });
                const fetchedSpace = response.data?.data ?? response.data;

                if (fetchedSpace) {
                    setSpace(fetchedSpace);
                    setErrorMsg('');
                } else {
                    setSpace(null);
                    setErrorMsg('Space details are unavailable right now.');
                }
            } catch (error) {
                setSpace(null);
                setErrorMsg('Failed to load space details. Please try again from the dashboard.');
            } finally {
                setLoading(false);
            }
        };

        fetchSpace();
    }, [id, preloadedSpace]);

    useEffect(() => {
        setActivePhotoIndex(0);
    }, [space?.id]);

    const handleProceed = () => {
        setErrorMsg('');
        const start = new Date(startTime);
        const end = new Date(endTime);
        const now = new Date();

        if (!startTime || !endTime) {
            setErrorMsg('Please select both start and end times.');
            return;
        }
        if (start <= now) {
            setErrorMsg('Start time must be in the future.');
            return;
        }
        if (end <= start) {
            setErrorMsg('End time must be after the start time.');
            return;
        }

        navigate('/checkout', {
            state: {
                spaceId: id,
                startTime: start.toISOString(),
                endTime: end.toISOString(),
            },
        });
    };

    if (loading) {
        return <main className="page"><div className="results">Loading...</div></main>;
    }

    if (!space) {
        return (
            <main className="page">
                <div className="results">{errorMsg || 'Space not found.'}</div>
                <div className="ws-actions" style={{ justifyContent: 'center', marginTop: '12px' }}>
                    <button className="primary-btn" onClick={() => navigate('/dashboard')}>
                        Back to Dashboard
                    </button>
                </div>
            </main>
        );
    }

    const details = buildDetails(space);
    const availability = buildAvailabilityPreview(space.id);
    const photos = details.photos.length > 0 ? details.photos : [details.imageUrl];
    const similarSpaces = SAMPLE_WORKSPACES
        .filter((candidate) => candidate.id !== space.id)
        .sort((a, b) => {
            const aScore = (a.location === space.location ? 2 : 0) + (a.type === space.type ? 1 : 0) + a.rating;
            const bScore = (b.location === space.location ? 2 : 0) + (b.type === space.type ? 1 : 0) + b.rating;
            return bScore - aScore;
        })
        .slice(0, 3);

    const showPreviousPhoto = () => {
        setActivePhotoIndex((prev) => (prev - 1 + photos.length) % photos.length);
    };

    const showNextPhoto = () => {
        setActivePhotoIndex((prev) => (prev + 1) % photos.length);
    };

    return (
        <>
            <header className="topbar">
                <div className="brand">WorkSpace</div>
                <nav className="topbar-links">
                    <Link to="/dashboard">Back to Dashboard</Link>
                </nav>
            </header>

            <main className="page detail-page">
                <section className="detail-hero">
                    <img className="detail-image" src={photos[activePhotoIndex]} alt={space.name} />
                    <button className="gallery-arrow left" onClick={showPreviousPhoto} aria-label="Previous photo">&lt;</button>
                    <button className="gallery-arrow right" onClick={showNextPhoto} aria-label="Next photo">&gt;</button>
                    <div className="detail-overlay">
                        <div className="ws-type">{space.type}</div>
                        <h1 className="detail-title">{space.name}</h1>
                        <p className="detail-subtitle">{space.location} | {space.capacity} people | PHP {space.hourlyRate}/hr</p>
                    </div>
                </section>

                <div className="gallery-thumbs">
                    {photos.map((photo, index) => (
                        <button
                            key={`${photo}-${index}`}
                            className={`thumb ${index === activePhotoIndex ? 'active' : ''}`}
                            onClick={() => setActivePhotoIndex(index)}
                            aria-label={`Show photo ${index + 1}`}
                        >
                            <img src={photo} alt={`${space.name} preview ${index + 1}`} />
                        </button>
                    ))}
                </div>

                <section className="detail-grid">
                    <article className="detail-panel">
                        <h2>About This Workspace</h2>
                        <p>{details.description}</p>

                        <div className="detail-columns">
                            <div>
                                <h3>Capacity</h3>
                                <p>Up to {space.capacity} {space.capacity === 1 ? 'person' : 'people'}</p>
                            </div>
                            <div>
                                <h3>Check-in Window</h3>
                                <p>{details.checkInWindow}</p>
                            </div>
                            <div>
                                <h3>Cancellation</h3>
                                <p>{details.cancellation}</p>
                            </div>
                            <div>
                                <h3>Rating</h3>
                                <p>{space.rating.toFixed(1)} / 5.0</p>
                            </div>
                        </div>
                    </article>

                    <article className="detail-panel">
                        <h2>Amenities</h2>
                        <ul className="detail-list">
                            {details.amenities.map((item) => (
                                <li key={item}>{item}</li>
                            ))}
                        </ul>

                        <h2>Utilities</h2>
                        <ul className="detail-list">
                            {details.utilities.map((item) => (
                                <li key={item}>{item}</li>
                            ))}
                        </ul>
                    </article>

                    <article className="detail-panel">
                        <h2>Availability Preview (Next 7 Days)</h2>
                        <div className="availability-grid">
                            {availability.map((day) => (
                                <div className="availability-day" key={`${day.dayLabel}-${day.dateLabel}`}>
                                    <div className="availability-head">
                                        <strong>{day.dayLabel}</strong>
                                        <span>{day.dateLabel}</span>
                                    </div>
                                    <div className="availability-slots">
                                        {day.slots.map((slot) => (
                                            <span className={`slot ${slot.available ? 'open' : 'closed'}`} key={slot.label}>
                                                {slot.label}
                                            </span>
                                        ))}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </article>

                    <article className="detail-panel">
                        <h2>Customer Reviews</h2>
                        <div className="review-list">
                            {details.reviews.map((review) => (
                                <div className="review-item" key={`${review.author}-${review.comment}`}>
                                    <div className="review-head">
                                        <strong>{review.author}</strong>
                                        <span>{'?'.repeat(review.rating)}{'?'.repeat(5 - review.rating)}</span>
                                    </div>
                                    <p>{review.comment}</p>
                                </div>
                            ))}
                        </div>
                    </article>

                    <article className="detail-panel booking-panel">
                        <h2>Book This Workspace</h2>
                        {errorMsg && <div className="alert">{errorMsg}</div>}

                        <div className="form" style={{ marginTop: '12px' }}>
                            <div className="row">
                                <div className="field" style={{ flex: 1 }}>
                                    <label htmlFor="start-time">Check-in Time</label>
                                    <input
                                        id="start-time"
                                        type="datetime-local"
                                        value={startTime}
                                        onChange={(e) => setStartTime(e.target.value)}
                                    />
                                </div>
                                <div className="field" style={{ flex: 1 }}>
                                    <label htmlFor="end-time">Check-out Time</label>
                                    <input
                                        id="end-time"
                                        type="datetime-local"
                                        value={endTime}
                                        onChange={(e) => setEndTime(e.target.value)}
                                    />
                                </div>
                            </div>

                            <button className="primary-btn" onClick={handleProceed} style={{ marginTop: '16px' }}>
                                Proceed to Checkout
                            </button>
                        </div>
                    </article>

                    <article className="detail-panel detail-wide">
                        <h2>Similar Workspaces Nearby</h2>
                        <div className="similar-grid">
                            {similarSpaces.map((candidate) => (
                                <button
                                    className="similar-card"
                                    key={candidate.id}
                                    onClick={() => navigate(`/space/${candidate.id}`, { state: { space: candidate } })}
                                >
                                    <div className="ws-type">{candidate.type}</div>
                                    <h3>{candidate.name}</h3>
                                    <p>{candidate.location}</p>
                                    <div className="similar-meta">
                                        <span>Capacity: {candidate.capacity}</span>
                                        <span>? {candidate.rating.toFixed(1)}</span>
                                    </div>
                                </button>
                            ))}
                        </div>
                    </article>
                </section>
            </main>
        </>
    );
};

export default SpaceDetail;
