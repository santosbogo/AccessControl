import React from "react";
import {useNavigate} from "react-router-dom";
const HomePage = () => {
    const navigate = useNavigate();
    const handleManageUsers = () => {
        console.log('Redirect to manage users page');
        navigate('/manage-users')
    }

    const handleViewHistory = () => {
        console.log('Redirect to view history page');
        navigate('/view-history')
    }

    return(
        <div className="home-page">
            <h1>Welcome to the Access Control System</h1>
            <button onClick={handleManageUsers}>Manage Users</button>
            <button onClick={handleViewHistory}>View History</button>
        </div>
    );
};
export default HomePage;
