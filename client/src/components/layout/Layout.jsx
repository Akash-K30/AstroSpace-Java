import React from 'react';
import Navbar from './Navbar';
import { FaGithub, FaLinkedin } from "react-icons/fa";

const Layout = ({ children }) => {
  return (
    <div className="app-container">
      <Navbar />
      
      {/* Main content area where nested routes will render */}
      <main className="main-content">
        {children}
      </main>
      
      <footer className="footer">
  <p>
    &copy; {new Date().getFullYear()} AstroSpace. Created by Akash.
  </p>

  <div className="footer-links">
    <a
      href="https://github.com/Akash-K30"
      target="_blank"
      rel="noopener noreferrer"
      aria-label="GitHub"
    >
      <FaGithub />
      <span>GitHub</span>
    </a>

    <a
      href="https://www.linkedin.com/in/akash-kaliraman00"
      target="_blank"
      rel="noopener noreferrer"
      aria-label="LinkedIn"
    >
      <FaLinkedin />
      <span>LinkedIn</span>
    </a>
  </div>
</footer>
    </div>
  );
};

export default Layout;