import React, { useState } from 'react';
import './SignUp.css';
import { Link, useNavigate } from 'react-router-dom';
import GetStarted from "./GetStarted";

const SignUp = () => {
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleSignUp = () => {
        // Lógica para manejar el registro
    };

    return (
        <div className="signup-container">
            <div className="signup-header">
                <div className="signup-title">
                    <div className="title"> Sign Up</div>
                </div>
                <div className="logo">
                </div>
            </div>
            <div className="signup-inputs">
                <div className="signup-input">
                    <input type="text" name="firstName" value={firstName} onChange={(e) => setFirstName(e.target.value)}
                           placeholder="First name"/>
                </div>
                <div className="signup-input">
                    <input type="text" name="lastName" value={lastName} onChange={(e) => setLastName(e.target.value)}
                           placeholder="Last name"/>
                </div>
                <div className="signup-input">
                    <input type="text" name="username" value={username} onChange={(e) => setUsername(e.target.value)}
                           placeholder="Username"/>
                </div>
                <div className="signup-input">
                    <input
                        type="password"
                        name="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Password"
                    />
                </div>
            </div>

            <div className="general-error-message"></div>

            <button className="signup-button" onClick={handleSignUp}>Sign Up</button>

            <Link to="/" className="Go-back-button">Go back</Link>
        </div>
    );
};

export default SignUp;
