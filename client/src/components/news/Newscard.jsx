import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { likeService } from '../../services/like.service';
import { bookmarkService } from '../../services/bookmark.service';
import CommentModal from "./CommentsModal";

const NewsCard = ({ article}) => {
  const { isAuthenticated, onRequireAuth } = useAuth();

  const [isLiked, setIsLiked] = useState(article.isLiked || false);
  const [likeCount, setLikeCount] = useState(article.likeCount || 0);
  const [isBookmarked, setIsBookmarked] = useState(article.isBookmarked || false);
  const [showComments, setShowComments] = useState(false);

  // Central guard: if not logged in, trigger the caller's login flow
  // (e.g. open a login modal) instead of hitting the API.
  const requireAuth = () => {
    if (!isAuthenticated) {
      onRequireAuth?.();
      return false;
    }
    return true;
  };

  const handleLikeToggle = async () => {
    if (!isAuthenticated) {onRequireAuth();}

    // Optimistic UI Update
    setIsLiked(!isLiked);
    setLikeCount((prev) => (isLiked ? prev - 1 : prev + 1));

    try {
      const res = await likeService.toggleLike(article._id);

      if (res.success && res.liked !== !isLiked) {
        setIsLiked(res.liked);
      }
    } catch (error) {
      // Revert UI on failure
      setIsLiked(isLiked);
      setLikeCount((prev) => (isLiked ? prev + 1 : prev - 1));
      console.error("Failed to toggle like");
    }
  };

  const handleBookmarkToggle = async () => {
    if (!requireAuth()) return;

    const previousState = isBookmarked;
    setIsBookmarked(!isBookmarked);

    try {
      if (previousState) {
        await bookmarkService.removeBookmark(article._id);
      } else {
        await bookmarkService.addBookmark(article._id);
      }
    } catch (error) {
      setIsBookmarked(previousState);
      console.error("Failed to toggle bookmark");
    }
  };

  return (
    <div className="news-card">

      <a href={article.url}
        target="_blank"
        rel="noopener noreferrer"
        className="news-link" >

       
      <img src={article.image} alt= "image is missing" className="news-image" />

</a>

      <div className="news-content">

           <a href={article.url}
        target="_blank"
        rel="noopener noreferrer"
        className="news-link" >

        <span className="news-source">source: {article.source }</span>
        <span className="news-category">{article.category}</span>

        <h3>{article.title}</h3>
        <p>{article.summary}</p>
            </a>

     
        <div className="news-meta">
          <span>{new Date(article.publishedAt).toLocaleDateString()}</span>
        </div>
      </div>

      <div className="news-actions">
        <button
          onClick={handleLikeToggle}
          className={`action-btn star-btn ${isLiked ? 'active' : ''}`}
        >
          <span className="icon">{isLiked ? '🌟' : '⭐'}</span> 
          <span className="count">{likeCount}</span>
        </button>

        <button
          onClick={handleBookmarkToggle}
          className={`action-btn ${isBookmarked ? 'active' : ''}`}
        >
         <span className="icon">{isBookmarked ? '📑' : '🔖'}</span>
        </button>

       <button
          className="action-btn"
          onClick={() => setShowComments(true)}
        >
          💬 {article.commentCount}

        </button>

      </div>

       {showComments && (
          <CommentModal
            article={article}
            onClose={() => setShowComments(false)}
            onRequireAuth={onRequireAuth}
          />
                        )}
    </div>
  );
};

export default NewsCard;