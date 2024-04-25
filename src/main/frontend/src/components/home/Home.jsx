import React from 'react';
import './Home.css';

const Home = () => {
    return (
        <div className='admin-home-container'>
            <h1 className='title'>Access Control</h1>
            <div className='admin-actions'>
                <button className='admin-button'>Add user</button>
                <button className='admin-button'>Manage access</button>
                <button className='admin-button'>View access</button>
            </div>
        </div>
    );
}

export default Home;
