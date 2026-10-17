import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ErrorBoundary from './components/ErrorBoundary';
import Layout from './components/layout/Layout';
import AppRoutes from './routes/AppRoutes';

function App() {
  return (
    <Router>
    <ErrorBoundary>
      <AuthProvider>
          <Layout>
             <AppRoutes />
          </Layout>
      </AuthProvider>
    </ErrorBoundary>
    </Router>
  );
}

export default App;