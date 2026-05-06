import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './components/Dashboard';
import SpaceDetailView from './components/SpaceDetailView';
import Checkout from './components/Checkout';
import Reservations from './components/Reservations';
import OAuth2RedirectHandler from './components/OAuth2RedirectHandler';

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