import React from 'react';
import { Routes, Route } from 'react-router-dom';

// Import Pages
import Home from '../pages/Home';
import Login from '../pages/Login';
import Register from '../pages/Register';
import Dashboard from '../pages/Dashboard';
import Bookmarks from '../pages/Bookmarks';
import LikedArticles from '../pages/LikedArticles';
import MyComments from '../pages/MyComments';
import Profile from '../pages/Profile';

// Import Protected Route Wrapper
import ProtectedRoute from '../routes/ProtectedRoute';

const AppRoutes = () => {
  return (
    <Routes>
      
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      
      <Route element={<ProtectedRoute />}>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/dashboard/bookmarks" element={<Bookmarks />} />
        <Route path="/dashboard/likes" element={<LikedArticles />} />
        <Route path="/dashboard/comments" element={<MyComments />} />
        <Route path="/profile" element={<Profile />} />
      </Route>
    </Routes>
  );
};

export default AppRoutes;