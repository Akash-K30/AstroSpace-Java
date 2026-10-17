import { createContext, useContext, useEffect, useState } from "react";
import api from "../api/axios";
import { logout as logoutRequest } from "../services/auth.service";
import { useNavigate } from 'react-router-dom';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {

    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {

        // The auth token now lives in an httpOnly cookie, which JS can't read,
        // so the only way to know if we're logged in is to ask the server.
        api.get("/auth/me")
            .then(res => setUser(res.data))
            .catch(() => setUser(null))
            .finally(() => setLoading(false));

    }, []);

    const login = (user) => {
        setUser(user);
    };

    const logout = async () => {
        try {
            await logoutRequest();
        } catch (err) {
            console.log("Logout request failed:", err.message);
        }
        setUser(null);
    };



     const onRequireAuth = () => {
    console.log("Authentication required. Redirecting...");
    navigate('/login');
  };

    return (

        <AuthContext.Provider
            value={{
                user,
                loading,
                login,
                logout,
                onRequireAuth,
                isAuthenticated: !!user
            }}
        >

            {children}

        </AuthContext.Provider>

    );

};

export const useAuth = () => {
    const ctx = useContext(AuthContext);
    if (!ctx) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return ctx;
};