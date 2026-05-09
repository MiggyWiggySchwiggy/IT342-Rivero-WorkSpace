import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import Login from './features/auth/Login';
import Register from './features/auth/Register';
import Dashboard from './features/spaces/Dashboard';
import SpaceDetailView from './features/spaces/SpaceDetailView';
import Checkout from './features/reservations/Checkout';
import Reservations from './features/reservations/Reservations';
import OAuth2RedirectHandler from './features/auth/OAuth2RedirectHandler';
import AdminDashboard from './features/admin/AdminDashboard';
import AdminWorkspaceManager from './features/admin/AdminWorkspaceManager';
import AdminReservationManager from './features/admin/AdminReservationManager';
import AdminAvailabilityManager from './features/admin/AdminAvailabilityManager';
import { fetchCurrentUser } from './features/shared/axiosConfig';

// Protect routes: Redirects to login if accessToken is missing
const ProtectedRoute = ({ children }: { children: React.ReactElement }) => {
    const token = localStorage.getItem('accessToken');
    if (!token) return <Navigate to="/login" replace />;
    return children;
};

// Admin-only route: Checks role via /me endpoint, redirects non-admins to /dashboard
const AdminProtectedRoute = ({ children }: { children: React.ReactElement }) => {
    const token = localStorage.getItem('accessToken');
    const [status, setStatus] = useState<'loading' | 'authorized' | 'unauthorized'>('loading');

    useEffect(() => {
        if (!token) {
            setStatus('unauthorized');
            return;
        }
        fetchCurrentUser()
            .then((user) => {
                if (user.role === 'ROLE_ADMIN') {
                    setStatus('authorized');
                } else {
                    setStatus('unauthorized');
                }
            })
            .catch(() => {
                setStatus('unauthorized');
            });
    }, [token]);

    if (status === 'loading') return null; // Or a loading spinner
    if (status === 'unauthorized') {
        if (!token) return <Navigate to="/login" replace />;
        return <Navigate to="/dashboard" replace />;
    }
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

                {/* Admin Routes */}
                <Route path="/admin" element={
                    <AdminProtectedRoute><AdminDashboard /></AdminProtectedRoute>
                } />
                <Route path="/admin/workspaces" element={
                    <AdminProtectedRoute><AdminWorkspaceManager /></AdminProtectedRoute>
                } />
                <Route path="/admin/reservations" element={
                    <AdminProtectedRoute><AdminReservationManager /></AdminProtectedRoute>
                } />
                <Route path="/admin/availability" element={
                    <AdminProtectedRoute><AdminAvailabilityManager /></AdminProtectedRoute>
                } />
            </Routes>
        </Router>
    );
};

export default App;