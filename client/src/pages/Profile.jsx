import React, { useState, useContext, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { dashboardService} from '../services/dashboard.service';

const Profile = () => {
  const { user, login } = useAuth();
  const [formData, setFormData] = useState({ name: '', avatar: '' });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

  // Pre-fill the form with the current user's data when the component mounts[cite: 1]
  useEffect(() => {
    if (user) {
      setFormData({
        name: user.name || '',
        avatar: user.avatar || ''
      });
    }
  }, [user]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage(null);
    setError(null);

    try {
      // PATCH request to update profile[cite: 1]
      const updatedUser = await dashboardService.updateProfile(formData);
      
      // Update the AuthContext with the new user data so the Navbar and other components reflect the change
      login(updatedUser); 
      
      setMessage("Profile updated successfully!");
    } catch (err) {
      setError(err.response?.data?.message || "Failed to update profile.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="profile-container">
      <h2>Edit Profile</h2>
      
      {/* Toast notifications or inline messages[cite: 1] */}
      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit} className="profile-form">
        <div className="form-group">
          <label htmlFor="name">Full Name</label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="avatar">Avatar</label>
          <input
            type="text"
            id="avatar"
            name="avatar"
            placeholder="👩‍🚀"
            value={formData.avatar}
            onChange={handleChange}
          />
        </div>
        

        <button type="submit" disabled={loading}>
          {loading ? 'Saving...' : 'Save Changes'}
        </button>
      </form>
    </div>
  );
};

export default Profile;