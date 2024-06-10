import React, {useEffect, useState} from "react";
import { useNavigate } from "react-router-dom";
import './Home.css'; // Importa los estilos CSS
import axios from "axios";




const HomePage = () => {
    const navigate = useNavigate();
    const [showLockDoors, setLockAllDoorsModal] = useState(false);
    const [showUnlockDoors, setUnlockAllDoorsModal] = useState(false);
    const [showReturnToNormal, setReturnToNormal] = useState(false);
    const [loading, setLoading] = useState(false);
    const [lockState, setLockState] = useState('');


    useEffect(() => {
        const fetchLockState = async () => {
            setLoading(true);
            try {
                const response = await axios.get(`http://${process.env.REACT_APP_PUBLIC_IP}/admin/getState`);
                console.log('Lock state:', response.data);
                setLockState(response.data);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching lock state:', error);
                setLoading(false);
            }
        };
        fetchLockState();
    } ,[]);

    const handleManageUsers = () => {
        console.log('Redirect to manage users page');
        navigate('/home/manage-users');
    };

    const handleViewHistory = () => {
        console.log('Redirect to view history page');
        navigate('/home/view-history');
    };

    const handleLockDoors = () => {
        setLockAllDoorsModal(true);
    };

    const handleUnlockDoors = () => {
        setUnlockAllDoorsModal(true);
    };

    const handleReturnNormal = () => {
        setReturnToNormal(true);
    };

    const handleConfirmLockDoors = async () => {
        console.log('Locking doors');
        try{
            const response = await axios.post(`http://${process.env.REACT_APP_PUBLIC_IP}/admin/lock`);
            console.log(response.data);
            setLockAllDoorsModal(false);
            window.location.reload();

        }
        catch(error){
            console.error('Error sending request:', error);
        }

    };

    const handleConfirmUnlockDoors = async () => {
        console.log('Locking doors');
        try{
            const response = await axios.post(`http://${process.env.REACT_APP_PUBLIC_IP}/admin/unlock`);
            console.log(response.data);
            setUnlockAllDoorsModal(false);
            window.location.reload();

        }
        catch(error){
            console.error('Error sending request:', error);
        }

    };

    const handleReturnToNormalState = async () => {
        console.log('Locking doors');
        try{
            const response = await axios.post(`http://${process.env.REACT_APP_PUBLIC_IP}/admin/normal-state`);
            console.log(response.data);
            setReturnToNormal(false);
            window.location.reload();

        }
        catch(error){
            console.error('Error sending request:', error);
        }

    };

    const handleCancelLockDoors = () => {
        setLockAllDoorsModal(false);
    };

    const handleCancelUnlockDoors = () => {
        setUnlockAllDoorsModal(false);
    };

    const handleCancelReturnToNormal = () => {
        setReturnToNormal(false);
    };


    const handleLogout = async () => {
        navigate('/login');
    };

    return (
        <div className="home-page">
            <div className="main-title">Welcome to the Access Control System</div>
            <div className="text-State">
                {loading ? <h2>Loading...</h2> :
                    <h2>Lock State: {lockState}</h2>
                }
            </div>
            <div className="button-container">
                <button onClick={handleManageUsers}>Manage Users</button>
            </div>
            <div className="button-container">
                <button onClick={handleViewHistory}>View History</button>
            </div>
            <div className="button-container">
                <button className="button-spacing" onClick={handleLockDoors}>Lock Doors</button>
            </div>
            <div className="button-container">
                <button className="button-spacing" onClick={handleUnlockDoors}>Unlock Doors</button>
            </div>
            <div className="button-container">
                <button className="button-spacing" onClick={handleReturnNormal}>Return to normal state</button>
            </div>
            <div className='button-container'>
                <button onClick={handleLogout}>Log Out</button>
            </div>
            {showLockDoors && (
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

            {showUnlockDoors && (
                <div className="modal">
                    <div className="modal-content">
                        <h2>Are you sure you want to unlock all doors?</h2>
                        <div className="modal-buttons">
                            <button onClick={handleConfirmUnlockDoors} className="confirm-button">Yes</button>
                            <button onClick={handleCancelUnlockDoors} className="cancel-button">No</button>
                        </div>
                    </div>
                </div>
            )}

            {showReturnToNormal && (
                <div className="modal">
                    <div className="modal-content">
                        <h2>Are you sure you want to return to normal state of security?</h2>
                        <div className="modal-buttons">
                            <button onClick={handleReturnToNormalState} className="confirm-button">Yes</button>
                            <button onClick={handleCancelReturnToNormal} className="cancel-button">No</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default HomePage;
