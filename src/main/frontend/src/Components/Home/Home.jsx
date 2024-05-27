import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import './Home.css'; // Importa los estilos CSS
import axios from "axios";


const HomePage = () => {
    const navigate = useNavigate();
    const [showModal, setShowModal] = useState(false);

    const handleManageUsers = () => {
        console.log('Redirect to manage users page');
        navigate('/home/manage-users');
    };

    const handleViewHistory = () => {
        console.log('Redirect to view history page');
        navigate('/home/view-history');
    };

    const handleLockDoors = () => {
        setShowModal(true);
    };

    const handleConfirmLockDoors = async () => {
        console.log('Locking doors');
        const response = await axios.post('http://localhost:3333/admin/lock-doors');
        setShowModal(false);
    };

    const handleCancelLockDoors = () => {
        setShowModal(false);
    };

    return (
        <div className="home-page">
            <div className="main-title">Welcome to the Access Control System</div>
            <div className="button-container">
                <button onClick={handleManageUsers}>Manage Users</button>
            </div>
            <div className="button-container">
                <button onClick={handleViewHistory}>View History</button>
            </div>
            <div>
                <button className="lock-doors" onClick={handleLockDoors}>Lock Doors</button>
            </div>

            {showModal && (
                <div className="modal">
                    <div className="modal-content">
                        <h2>Are you sure you want to lock all doors?</h2>
                        <div className="modal-buttons">
                            <button onClick={handleConfirmLockDoors} className="confirm-button">Yes</button>
                            <button onClick={handleCancelLockDoors} className="cancel-button">No</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default HomePage;
