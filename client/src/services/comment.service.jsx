import apiClient from '../api/axios';

export const commentService = {
  // GET /api/comments/:articleId
  getComments: async (articleId) => {
    const response = await apiClient.get(`/comments/${articleId}`);
    return response.data;
  },

  // POST /api/comments/:articleId[cite: 1]
  addComment: async (articleId, text) => {
    const response = await apiClient.post(`/comments/${articleId}`, { text });
    return response.data;
  },

  // DELETE /api/comments/:commentId[cite: 1]
  deleteComment: async (commentId) => {
    const response = await apiClient.delete(`/comments/${commentId}`);
    return response.data;
  }
};