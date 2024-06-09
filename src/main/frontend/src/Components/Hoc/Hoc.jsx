import React, {useEffect, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import axios from "axios";

const authentication = WrappedComponent => {

    return (props) => {
        const navigate = useNavigate();

        useEffect(() => {
            const token = localStorage.getItem('token');

            if (!token) {
                navigate('/login');
            }

            const verifyToken = async () => {
                try {
                    const response = await axios.get(`http://${process.env.REACT_APP_PUBLIC_IP}/user/verify`, {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });

                } catch (error) {
                    console.error('Token validation failed:', error);
                    navigate('/login');
                }
            }
            verifyToken();

        }, []);

        return <WrappedComponent {...props} />;
    };
};

export default authentication;