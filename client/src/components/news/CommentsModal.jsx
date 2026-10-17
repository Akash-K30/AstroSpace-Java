import { useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext";
import { commentService } from "../../services/comment.service";

const CommentModal = ({ article, onClose, onRequireAuth }) => {
    const { isAuthenticated, user } = useAuth();

    const [comments, setComments] = useState([]);
    const [text, setText] = useState("");
    const [loading, setLoading] = useState(true);
    const [showComments, setShowComments] = useState(false); // Toggle state for comments

    useEffect(() => {
        if (article?._id) {
            fetchComments();
        }
    }, [article?._id]);

    const fetchComments = async () => {
        try {
            const res = await commentService.getComments(article._id);
            setComments( res.comments || []);
        } catch (error) {
            console.error("Error fetching comments:", error);
        } finally {
            setLoading(false);
        }
    };

    const addComment = async () => {
        if (!isAuthenticated) {
            onRequireAuth?.();
            return;
        }

        if (!text.trim()) return;

        try {
            const res = await commentService.addComment(article._id, text);
            setComments([res.comment, ...comments]);
            setText("");
            setShowComments(true); // Automatically show the comment list if they add one
        } catch (error) {
            console.error("Error adding comment:", error);
        }
    };

    const deleteComment = async (id) => {
        try {
            await commentService.deleteComment(id);
            setComments(comments.filter((c) => c._id !== id));
        } catch (error) {
            console.error("Error deleting comment:", error);
        }
    };

    return (
        <div className="astro-comment-section">
            {/* --- SCROLLABLE COMMENTS LIST --- */}
            <div className="comments-scroll-area">
                {loading ? (
                    <p className="comment-muted">Loading comments...</p>
                ) : comments.length === 0 ? (
                    <p className="comment-muted">No comments yet. Be the first!</p>
                ) : (
                    comments.map((comment) => {
                        if (!comment) return null;

                        return (
                            <div key={comment._id} className="astro-comment-item">
                                <div className="comment-content">
                                    {/* Name is small and inline with the comment text */}
                                    <span className="comment-username">
                                        {comment.user?.name || "Unknown"}
                                    </span>
                                    <span className="comment-text">{comment.text}</span>
                                </div>

                                {/* Delete button pushed to the far right */}
                                {user?._id === comment.user?._id && (
                                    <button 
                                        onClick={() => deleteComment(comment._id)}
                                        className="comment-delete-btn"
                                        title="Delete Comment"
                                    >
                                        ❌
                                    </button>
                                )}
                            </div>
                        );
                    })
                )}
            </div>

            {/* --- ADD COMMENT SECTION --- */}
            <div className="insta-add-comment">
                {isAuthenticated ? (
                    <div className="add-comment-wrapper">
                        <input
                            type="text"
                            value={text}
                            onChange={(e) => setText(e.target.value)}
                            placeholder="Add a comment..."
                            className="comment-input-pill"
                            onKeyDown={(e) => e.key === 'Enter' && text.trim() && addComment()}
                        />
                        {/* Small, circular post button on the right */}
                        <button 
                            onClick={addComment} 
                            disabled={!text.trim()}
                            className="comment-post-circular"
                        >
                           📡
                        </button>
                    </div>
                ) : (
                    <p className="comment-muted">
                        Please <button onClick={onRequireAuth} className="auth-link">log in</button> to comment.
                    </p>
                )}
            </div>
        </div>
    );
};

export default CommentModal;

