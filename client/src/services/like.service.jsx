import apiClient from '../api/axios';

export const likeService = {
  // POST /api/likes/:articleId - Toggles the like status
  toggleLike: async (articleId) => {
    const response = await apiClient.post(`/likes/${articleId}`);
    return response.data;
  }
};