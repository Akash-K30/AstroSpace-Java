import { useState, useEffect } from "react";
import NewsCard from "../components/news/Newscard";
import { getBookmarks } from "../services/bookmark.service";

function BookmarkDashboard() {
    const [bookmarks, setBookmarks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        loadBookmarks();
    }, []);

    const loadBookmarks = async () => {
        try {
            setLoading(true);
            const data = await getBookmarks();
            setBookmarks(data || []); // Safety fallback to empty array if data is undefined
        } catch (err) {
            console.error("Error loading bookmarks:", err);
            setError("Failed to load bookmarks.");
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div>Loading your saved articles...</div>;
    if (error) return <div>{error}</div>;

    return (
        <div>
            <h1>Your Bookmarked Articles</h1>
            
            {bookmarks.length === 0 ? (
                <p>You haven't saved any articles yet.</p>
            ) : (
                <div>
                    {bookmarks.map((article) => (
                        <NewsCard
                            key={article._id}
                            article={article}
                        />
                    ))}
                </div>
            )}
        </div>
    );
}

export default BookmarkDashboard;