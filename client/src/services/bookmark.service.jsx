import apiClient from '../api/axios';

export const bookmarkService = {
  // POST /api/bookmarks/:articleId - Adds a bookmark
  addBookmark: async (articleId) => {
    const response = await apiClient.post(`/bookmarks/${articleId}`);
    return response.data;
  },

  // DELETE /api/bookmarks/:articleId - Removes a bookmark[cite: 1]
  removeBookmark: async (articleId) => {
    const response = await apiClient.delete(`/bookmarks/${articleId}`);
    return response.data;
  }
};

export const getBookmarks = async () => {

    const { data } = await api.get(

        "/bookmarks"

    );

    return data.bookmarks;

};

