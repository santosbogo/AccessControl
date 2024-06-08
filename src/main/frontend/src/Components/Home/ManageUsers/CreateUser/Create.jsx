import React, { useState } from "react";
import { Link } from 'react-router-dom';
import authentication from "../../../Hoc/Hoc"; // Importar el HOC de autenticación
import axios from "axios";
import './Create.css';
import { useNavigate } from "react-router-dom";



const CreateUser = () => {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [uid, setUid] = useState("");
    const [showUidField, setShowUidField] = useState(false);
    const [showCreateButton, setShowCreateButton] = useState(false);
    const navigate = useNavigate();


    const handleInputChange = (e) => {
        const { name, value } = e.target;
        if (name === "firstName") {
            setFirstName(value);
        } else if (name === "lastName") {
            setLastName(value);
        }
    };



    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:3333/user/add', {
                uid : uid,
                firstName: firstName,
                lastName: lastName,
            });
            console.log(response.data);
        } catch (error) {
            console.error('Error sending request:', error);
        }
    };

    const handleGoBack = () => {
        console.log("Redirect to manage users page");
        navigate("/Home/manage-users");
    }

    const handleRequestUid = async () => {
        try {
            const response = await axios.get(`http://localhost:3333/uid/getUid`);
            if (response.data && response.data.uid) {
                setUid(response.data.uid);
                setShowUidField(true);
                setShowCreateButton(true);
                console.log("UID received:", response.data.uid);
            } else {
                console.log("No UID received", response.data);
            }
        } catch (error) {
            console.error('Error fetching UID:', error);
        }
    };

        return (
            <div className="header-container">
                <header className="main-title">
                    <h1>Create New User</h1>
                </header>
                <div className="container">
                    <div className="search-input">
                        <input
                            type="text"
                            name="firstName"
                            value={firstName}
                            onChange={handleInputChange}
                            placeholder="First name"
                        />
                    </div>
                    <div className="search-input">
                        <input
                            type="text"
                            name="lastName"
                            value={lastName}
                            onChange={handleInputChange}
                            placeholder="Last name"
                        />
                    </div>
                    {showUidField && (
                        <h3>UID Detected: {uid}</h3>
                    )}

                    <div className="button-group">
                        {!showUidField && (
                            <button className="home-button" onClick={handleRequestUid}>Request UID</button>
                        )}

                        {showCreateButton && (
                            <button className="home-button" onClick={handleSubmit}>Create User</button>
                        )}
                        <button className="home-button" onClick={handleGoBack}>Go Back</button>
                    </div>
                </div>
            </div>
        );
    };

export default authentication(CreateUser);
