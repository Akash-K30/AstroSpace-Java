import React from 'react';
import { Link } from 'react-router-dom';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    // Update state so the next render will show the fallback UI.
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    // You can also log the error to an error reporting service here
    console.error("ErrorBoundary caught an error", error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="error-boundary-container" style={{ padding: '2rem', textAlign: 'center' }}>
          <h2>Oops! Something went wrong.</h2>
          <p>We encountered an unexpected error.</p>
          <button 
            onClick={() => window.location.reload()} 
            style={{ padding: '10px 20px', margin: '10px' }}
          >
            Refresh Page
          </button>
          <br />
          <Link to="/">Return to Home</Link>
        </div>
      );
    }

    return this.props.children; 
  }
}

export default ErrorBoundary;