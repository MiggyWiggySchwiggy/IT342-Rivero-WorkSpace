import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

const OAuth2RedirectHandler: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        // Grab the token from the URL
        const urlParams = new URLSearchParams(location.search);
        const token = urlParams.get('token');

        if (token) {
            localStorage.setItem('accessToken', token);
            navigate('/dashboard'); // Success! Send them to the app.
        } else {
            navigate('/login'); // Failed
        }
    }, [navigate, location]);

    return <div>Authenticating...</div>;
};

export default OAuth2RedirectHandler;