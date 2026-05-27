import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { fetchCurrentUser } from '../shared/axiosConfig';

const OAuth2RedirectHandler: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        // Grab the token from the URL
        const urlParams = new URLSearchParams(location.search);
        const token = urlParams.get('token');

        if (token) {
            localStorage.setItem('accessToken', token);
            fetchCurrentUser()
                .then((user) => {
                    localStorage.setItem('userRole', user.role);
                    navigate('/dashboard'); // Success! Send them to the app.
                })
                .catch((err) => {
                    console.error('Failed to fetch user info during OAuth redirect:', err);
                    // Fallback to dashboard even if fetch fails
                    navigate('/dashboard');
                });
        } else {
            navigate('/login'); // Failed
        }
    }, [navigate, location]);

    return <div>Authenticating...</div>;
};

export default OAuth2RedirectHandler;