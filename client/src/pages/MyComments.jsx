import React, { useState, useEffect } from 'react';
import { dashboardService } from '../services/dashboard.service';
import { Link } from 'react-router-dom';
import Loader from '../components/Loader';

const MyComments = () => {
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchMyComments = async () => {
      try {
        // GET /api/dashboard/comments
        const res = await dashboardService.getMyComments();
        if (res.success) {
          setComments(res.comments);
        }
      } catch (err) {
        setError("Failed to load your comments.");
      } finally {
        setLoading(false);
      }
    };

    fetchMyComments();
  }, []);

  if (loading) return <Loader text="Loading your comments..." />;
  if (error) return <div className="error-state">{error}</div>;

  return (
    <div className="my-comments-container">
      <h2>My Comments</h2>
      
      {comments.length === 0 ? (
        <div className="empty-state">
          <p>You haven't commented on any articles yet.</p>
          <Link to="/">Read News</Link>
        </div>
      ) : (
        <ul className="comments-list">
          {comments.map((comment) => (
            <li key={comment._id} className="comment-list-item">
              <div className="comment-bubble">
                <p>"{comment.text}"</p>
              </div>
              <div className="comment-meta">
                Commented on: <Link to={`/article/${comment.article._id}`}>
                  <strong>{comment.article.title}</strong>
                </Link>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default MyComments;