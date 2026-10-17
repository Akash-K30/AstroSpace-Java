import api from "../api/axios";

export const getNews = async (params = {}) => {
    const response = await api.get("/news", { params });
    return response.data;
};
export const searchNews = async (query) => {

    const response = await api.get(`/news/?q=${query}`);

    return response.data;

};