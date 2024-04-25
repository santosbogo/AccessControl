import React from 'react';
import './Login.css';
import person_icon from '../assets/person.png';
import password_icon from '../assets/password.png';

const Login = () => {

    return (
        <div className="login-container">
            <h1 style={{ color: '#0f0', textAlign: 'center', marginBottom: '30px' }}>SIGN IN</h1>
            <div className="login-inputs">
                <div className="login-input">
                    <img src={person_icon} alt=""/>
                    <input type="text" placeholder='Username' />
                </div>
                <div className="login-input">
                    <img src={password_icon} alt=""/>
                    <input type="password" placeholder='Password' />
                </div>
            </div>
            <button className="login-button">Login</button>
        </div>
    );
}

export default Login;
