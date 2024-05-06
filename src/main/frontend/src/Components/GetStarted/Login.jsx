import React from "react";
import { Link } from 'react-router-dom';
import './Login.css'; // Importa el archivo CSS

const Login = () => {
    const [username, setUsername] = React.useState("");
    const [password, setPassword] = React.useState("");

    const handleLogin = () => {
        // Agrega la lógica de inicio de sesión aquí
    }

    return (
        <div className="login-container">
            <div className="login-header">
                <div className="login-title">
                    <div className="title"> Login </div>
                </div>
                <div className="logo">
                    {/* Agrega tu logo aquí */}
                </div>
            </div>
            <div className="login-inputs">
                <div className="login-input">
                    <input
                        type="text"
                        name="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        placeholder="Username"
                    />
                </div>
                <div className="login-input">
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

            <button className="login-button" onClick={handleLogin}>Login</button>
            <Link to="/" className="Go-back-button">Go back</Link>
        </div>
    );
}

export default Login;
