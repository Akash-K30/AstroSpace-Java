import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from "../../context/AuthContext";

const Navbar = () => {
const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/">AstroSpace</Link>
      </div>
      
      <ul className="navbar-links">
        
        {user ? (
          <>
            <li><Link to="/dashboard">Trails🛸</Link></li>
            <li><Link to="/profile">{ user.avatar || "🧑‍🚀" } </Link></li>
           
            <li>
              <button onClick={handleLogout} className="logout-btn">
                Logout
              </button>
            </li>
          </>
        ) : (
          <>
            <li><Link to="/login">Login</Link></li>
            <li><Link to="/register" className="register-btn">Register</Link></li>
          </>
        )}
      </ul>
    </nav>
  );
};

export default Navbar;