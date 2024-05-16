import React, { useState } from "react";
import { Link } from 'react-router-dom'; // Importar Link si es necesario


const CreateUser = () => {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [username, setUsername] = useState("");

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        if (name === "firstName") {
            setFirstName(value);
        } else if (name === "lastName") {
            setLastName(value);
        } else if (name === "username") {
            setUsername(value);
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log("Creating user:", { firstName, lastName, username });
        // Aquí enviarías la información al servidor o a la lógica de manejo de datos
    };

    return (
        <div className="signup-container">
            <div className="signup-header">
                <div className="signup-title">
                    <div className="title"> Create New User</div>
                </div>
                <div className="logo">
                    {/* Agrega tu logo aquí si es necesario */}
                </div>
            </div>
            <div className="signup-inputs">
                <div className="signup-input">
                    <input
                        type="text"
                        name="firstName"
                        value={firstName}
                        onChange={handleInputChange}
                        placeholder="First name"
                    />
                </div>
                <div className="signup-input">
                    <input
                        type="text"
                        name="lastName"
                        value={lastName}
                        onChange={handleInputChange}
                        placeholder="Last name"
                    />
                </div>
                <div className="signup-input">
                    <input
                        type="text"
                        name="username"
                        value={username}
                        onChange={handleInputChange}
                        placeholder="Username"
                    />
                </div>
            </div>

            <div className="general-error-message"></div>

            <button className="signup-button" onClick={handleSubmit}>Create User</button>
        </div>
    );
};

export default CreateUser;
