import React, { useState, useEffect, useContext } from 'react';
import { dashboardService } from '../services/dashboard.service';
import { Link } from 'react-router-dom';
import NewsCard from '../components/news/Newscard';
import { useAuth } from '../context/AuthContext';

const LikedArticles = () => {
  const { user } = useAuth();
  const [likedArticles, setLikedArticles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const requireAuth = () => !!user;

  useEffect(() => {
    const fetchLikes = async () => {
      try {
        const res = await dashboardService.getLikedArticles();
        if (res.success) {
          // The API returns an array of liked article objects[cite: 1]
          setLikedArticles(res.articles);
        }
      } catch (err) {
        setError("Failed to load liked articles.");
      } finally {
        setLoading(false);
      }
    };

    fetchLikes();
  }, []);

  if (loading) return <div className="loader">Loading liked articles...</div>;
  if (error) return <div className="error-state">{error}</div>;

  return (
    <div className="liked-articles-container">
      <h2>Articles You Liked</h2>
      
      {likedArticles.length === 0 ? (
        <div className="empty-state">
          <p>You haven't liked any articles yet.</p>
          <Link to="/">Explore News</Link>
        </div>
      ) : (
        <div className="news-grid">
          {likedArticles.map((article) => (
            <NewsCard 
              key={article._id} 
              article={{...article, isLiked: true}} 
              requireAuth={requireAuth} 
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default LikedArticles;