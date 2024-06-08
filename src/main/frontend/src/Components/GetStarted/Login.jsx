import React from "react";
import { Link, useNavigate } from 'react-router-dom';
import './Login.css';
import axios from "axios";

const Login = () => {
    const [username, setUsername] = React.useState("");
    const [password, setPassword] = React.useState("");
    const [loginError, setLoginError] = React.useState('');
    const navigate = useNavigate();

    const login = async () => {
        try {
            const response = await axios.post(`http://52.207.227.239/admin/login`, {
                username: username,
                password: password
            });
            console.log(response.data);
            navigate('/home')

        } catch (error) {
            const errorMsg = error.response?.data || 'An unexpected error occurred.';
            console.error('Error while sending request:', errorMsg);
            setLoginError('');
            if (errorMsg.includes("User does not exist")|| errorMsg.includes("User not found")) {
                setLoginError("Invalid username or password");
            }
        }
    };

    return (
        <div className="login-container">
            <div className="login-header">
                <div className="login-title">
                    <div className="title"> Login </div>
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

            {loginError &&
                <div className="error-message" style={{color: 'red', textAlign: 'center'}}>{loginError}</div>}
            <button className="login-button" onClick={login}>Login</button>
        </div>
    );
}

export default Login;
