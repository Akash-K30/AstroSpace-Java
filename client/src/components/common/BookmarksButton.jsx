import { useState, useEffect } from "react";
import { addBookmark, removeBookmark } from "../services/bookmark.service";

function BookmarkButton({ articleId, initialIsBookmarked }) {
    const [saved, setSaved] = useState(initialIsBookmarked);
    const [loading, setLoading] = useState(false);

    // Sync state if the initialIsBookmarked prop updates from outside
    useEffect(() => {
        setSaved(initialIsBookmarked);
    }, [initialIsBookmarked]);

    const toggleBookmark = async () => {
        if (loading) return; // Prevent double-clicks
        setLoading(true);
        
        try {
            if (saved) {
                await removeBookmark(articleId);
            } else {
                await addBookmark(articleId);
            }
            setSaved(!saved);
        } catch (err) {
            console.error("Failed to update bookmark:", err);
            // Optional: Add a toast/notification here for user feedback
        } finally {
            setLoading(false);
        }
    };

    return (
        <button 
            onClick={toggleBookmark} 
            disabled={loading}
            style={{ opacity: loading ? 0.7 : 1, cursor: loading ? 'not-allowed' : 'pointer' }}
        >
            {saved ? "🔖 Bookmarked" : "📑 Save Article"}
        </button>
    );
}

export default BookmarkButton;