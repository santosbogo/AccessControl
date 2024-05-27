import React, { useState } from "react";
import { Link } from 'react-router-dom';

import axios from "axios";

const CreateUser = () => {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [uid, setUid] = useState("");
    const [showUidField, setShowUidField] = useState(false);
    const [showCreateButton, setShowCreateButton] = useState(false);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        if (name === "firstName") {
            setFirstName(value);
        } else if (name === "lastName") {
            setLastName(value);
        } else if (name === "uid") {
            setUid(value);
            setShowCreateButton(true); // Muestra el botón "Create User" cuando el campo UID está lleno
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

    const handleRequestUid = async () => {
        setShowUidField(true);
        try {
            const response = await axios.get(`http://localhost:3333/uid/getUid`);
            if (response.data && response.data.uid) {
                setUid(response.data.uid);
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
                        <div className="search-input">
                            <input
                                type="text"
                                name="uid"
                                value={uid}
                                onChange={handleInputChange}
                                placeholder="UID"
                            />
                        </div>
                    )}

                    <div className="button-group">
                        {!showUidField && (
                            <button className="home-button" onClick={handleRequestUid}>Request UID</button>
                        )}

                        {showCreateButton && (
                            <button className="home-button" onClick={handleSubmit}>Create User</button>
                        )}
                        <Link to="/Home/manage-users" className="home-button">Go Back</Link>
                    </div>
                </div>
            </div>
        );
    };

export default CreateUser;
