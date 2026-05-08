import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import Login from './features/auth/Login';
import Register from './features/auth/Register';
import Dashboard from './features/spaces/Dashboard';
import SpaceDetailView from './features/spaces/SpaceDetailView';
import Checkout from './features/reservations/Checkout';
import Reservations from './features/reservations/Reservations';
import OAuth2RedirectHandler from './features/auth/OAuth2RedirectHandler';

// Protect routes: Redirects to login if accessToken is missing
const ProtectedRoute = ({ children }: { children: React.ReactElement }) => {
    const token = localStorage.getItem('accessToken');
    if (!token) return <Navigate to="/login" replace />;
    return children;
};

const App: React.FC = () => {
    return (
        <Router>
            <Routes>
                {/* Public Routes */}
                <Route path="/" element={<Navigate to="/login" replace />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/oauth2/redirect" element={<OAuth2RedirectHandler />} />

                {/* Protected Routes */}
                <Route path="/dashboard" element={
                    <ProtectedRoute><Dashboard /></ProtectedRoute>
                } />
                <Route path="/space/:id" element={
                    <ProtectedRoute><SpaceDetailView /></ProtectedRoute>
                } />
                <Route path="/checkout" element={
                    <ProtectedRoute><Checkout /></ProtectedRoute>
                } />
                <Route path="/reservations" element={
                    <ProtectedRoute><Reservations /></ProtectedRoute>
                } />
            </Routes>
        </Router>
    );
};

export default App;