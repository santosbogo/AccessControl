import React, { useState } from "react";
import { Link } from 'react-router-dom';

const CreateUser = () => {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [username, setUsername] = useState("");
    const [uid, setUid] = useState("");
    const [showUidField, setShowUidField] = useState(false);
    const [showCreateButton, setShowCreateButton] = useState(false);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        if (name === "firstName") {
            setFirstName(value);
        } else if (name === "lastName") {
            setLastName(value);
        } else if (name === "username") {
            setUsername(value);
        } else if (name === "uid") {
            setUid(value);
            setShowCreateButton(true); // Muestra el botón "Create User" cuando el campo UID está lleno
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log("Creating user:", { firstName, lastName, username, uid });
        // Aquí enviarías la información al servidor o a la lógica de manejo de datos
    };

    const handleRequestUid = () => {
        setShowUidField(true);
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
                <div className="search-input">
                    <input
                        type="text"
                        name="username"
                        value={username}
                        onChange={handleInputChange}
                        placeholder="Username"
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
