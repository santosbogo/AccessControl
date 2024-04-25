import React from 'react';
import './User.css';

const User = () => {
    return (
        <div className="login-container">
            <div className='login-inputs'>
                <div className='login-input'>
                    <input type='text' placeholder='First name'/>
                </div>
                <div className='login-input'>
                    <input type='text' placeholder='Last name'/>
                </div>
            </div>
            <button className='create-button'>Create user</button>
        </div>
    );
}

export default User;
