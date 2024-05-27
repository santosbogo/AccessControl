import React from "react";
import { useNavigate } from "react-router-dom";
import './Home.css'; // Importa los estilos CSS

const HomePage = () => {
    const navigate = useNavigate();

    const handleManageUsers = () => {
        console.log('Redirect to manage users page');
        navigate('/home/manage-users')
    }

    const handleViewHistory = () => {
        console.log('Redirect to view history page');
        navigate('/home/view-history')
    }

    const handleLockDoors = () => {

        // Implementar la lógica para bloquear las puertas
    }

    return(
        <div className="home-page">
            <div className="main-title"> Welcome to the Access Control System</div>
            <div className="button-container">
                <button onClick={handleManageUsers}>Manage Users</button>
            </div>
            <div className="button-container">
                <button onClick={handleViewHistory}>View History</button>
            </div>
            <div className={"lock-doors"}>
                <button onClick={handleLockDoors}>Lock Doors</button>
            </div>
        </div>
    );
};
export default HomePage;
