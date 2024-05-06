import React from 'react';
import './GetStarted.css';
import {Link, useNavigate} from "react-router-dom";

const GetStarted = ()=>{
    let navigate = useNavigate();

    return (
        <div className='get-started-container'>
            <div className="get-started-header">
                <div className="get-started-title">
                    <div className="text"> Get Started </div>
                </div>
            </div>
            <div className='logo-get-started'>
            </div>
            <div className='get-started-actions'>
                <button className='get-started-button' onClick={() => navigate('/login')}>Login
                </button>
                <button className='get-started-button' onClick={() => navigate('/signup')}>Sign Up
                </button>
            </div>
        </div>
    )
}
export default GetStarted