import React, { useState } from "react";

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
        <form onSubmit={handleSubmit} className="create-user-form">
            <h1>Create New User</h1>
            <label>
                First Name:
                <input
                    type="text"
                    name="firstName"
                    value={firstName}
                    onChange={handleInputChange}
                />
            </label>
            <label>
                Last Name:
                <input
                    type="text"
                    name="lastName"
                    value={lastName}
                    onChange={handleInputChange}
                />
            </label>
            <label>
                Username:
                <input
                    type="text"
                    name="username"
                    value={username}
                    onChange={handleInputChange}
                />
            </label>
            <button type="submit">Create User</button>
        </form>
    );
};

export default CreateUser;
