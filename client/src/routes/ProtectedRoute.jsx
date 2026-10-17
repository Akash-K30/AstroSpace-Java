import React, { useContext } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = () => {
  const { user, loading } = useAuth();

  // Show a loader while the app checks for the /auth/me session on initial load
  if (loading) {
    return <div className="loader">Verifying session...</div>;
  }

  // If no user is authenticated, redirect to login
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // If authenticated, render the child routes (e.g., Dashboard, Profile)[cite: 1]
  return <Outlet />;
};

export default ProtectedRoute;