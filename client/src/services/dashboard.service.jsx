import apiClient from '../api/axios';

export const dashboardService = {
  // GET /api/dashboard
  getSummary: async () => {
    const response = await apiClient.get('/dashboard');
    return response.data;
   
  },

  // GET /api/bookmarks
  getBookmarks: async () => {
    const response = await apiClient.get('/bookmarks');
    return response.data;
  },

  // GET /api/dashboard/likes
  getLikedArticles: async () => {
    const response = await apiClient.get('/dashboard/likes');
    return response.data;
  },

  // GET /api/dashboard/comments[cite: 1]
  getMyComments: async () => {
    const response = await apiClient.get('/dashboard/comments');
    return response.data;
  },

  updateProfile : async (profileData) => {
  // PATCH /api/dashboard/profile expects name and avatar in the body[cite: 1]
  const response = await apiClient.patch('/dashboard/profile', profileData);
  return response.data; 

}

};


