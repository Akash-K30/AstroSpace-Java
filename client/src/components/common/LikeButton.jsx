import { useState, useEffect } from "react";
import { toggleLike } from "../services/like.service";

function LikeButton({ article }) {
    
    const [liked, setLiked] = useState(article.isLiked);
    const [count, setCount] = useState(article.likeCount);

    useEffect(() => {
        setLiked(article.isLiked);
        setCount(article.likeCount);
    }, [article.isLiked, article.likeCount]);

    const handleLike = async () => {
        
        const fallbackLiked = liked;
        const fallbackCount = count;

        setLiked(!liked);
        setCount(prev => liked ? prev - 1 : prev + 1);

        try {
            const data = await toggleLike(article._id);
            
           
            if (data && typeof data.liked !== 'undefined') {
                setLiked(data.liked);
               
            } else {
                throw new Error("Invalid server response");
            }
        } catch (error) {
            console.error("Failed to toggle like:", error);
           
            setLiked(fallbackLiked);
            setCount(fallbackCount);
        }
    };

    return (
        <button onClick={handleLike} aria-label="Like article">
            {liked ? "❤️" : "🤍"} {count}
        </button>
    );
}

export default LikeButton;