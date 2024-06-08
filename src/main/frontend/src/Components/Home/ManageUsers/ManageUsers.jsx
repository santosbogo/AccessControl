import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./ManageUsers.css"
import authentication from "../../Hoc/Hoc"; // Importar el HOC de autenticación
import { Link } from 'react-router-dom'; // Importar Link si es necesario

const ManageUsers = () => {
    const [users, setUsers] = useState([]);
    const navigate = useNavigate();

    const [activeError, setActiveError] = useState('');
    const [inactiveError, setInactiveError] = useState('');

    const [activeErrors, setActiveErrors] = useState({});
    const [inactiveErrors, setInactiveErrors] = useState({});



    useEffect(() => {
        fetchAllUsers();
    }, []);

    const fetchAllUsers = async () => {
        try {
            const response = await axios.get('http://localhost:3333/users/findAll');
            setUsers(response.data);
            console.log('Fetched users:', response.data);
            setActiveError('');
            setInactiveError('')
        } catch (error) {
            const errorMsg = error.response?.data || 'An unexpected error occurred.';
            if (error.Msg.includes("User is already activated.")){
                setActiveError("User is already activated.");
            } else if (error.Msg.includes("User is already deactivated.")){
                setInactiveError("User is already deactivated.");
            }
        }
    };

    const handleCreateUser = () => {
        console.log("Redirect to create new user page");
        navigate("/Home/manage-users/create");
    };

    const handleBackHome = () => {
        console.log("Redirect to home page");
        navigate("/Home");
    }

    const updateUserState = (userId, newState) => {
        const updatedUsers = users.map(user =>
            user.uid === userId ? { ...user, state: newState } : user
        );
        console.log('Updated users:', updatedUsers); // Mostrar la lista actualizada
        setUsers(updatedUsers);
    };


    const handleDeactivateUser = async (userId) => {
        try {
            await axios.post(`http://localhost:3333/user/deactivate/${userId}`);
            const newInactiveErrors = {...inactiveErrors, [userId]: ''}; // Limpiar errores anteriores
            setInactiveErrors(newInactiveErrors);
            updateUserState(userId, false);
        } catch (error) {
            const errorMsg = error.response?.data.message || 'Error deactivating user.';
            const newInactiveErrors = {...inactiveErrors, [userId]: errorMsg};
            setInactiveErrors(newInactiveErrors);
        }
    };

    const handleActivateUser = async (userId) => {
        try {
            await axios.post(`http://localhost:3333/user/activate/${userId}`);
            const newActiveErrors = {...activeErrors, [userId]: ''}; // Limpiar errores anteriores
            setActiveErrors(newActiveErrors);
            updateUserState(userId, true);
        } catch (error) {
            const errorMsg = error.response?.data.message || 'Error activating user.';
            const newActiveErrors = {...activeErrors, [userId]: errorMsg};
            setActiveErrors(newActiveErrors);
        }
    }

    return (
        <div className="home-page">
            <div className="main-title">Manage Users</div>
            <button onClick={handleCreateUser}>Create New User</button>
            <div className="user-list">
                {users.length > 0 ? (
                    <ul>
                        {users.map(user => (
                            <li key={user.uid}>
                                {user.firstName} {user.lastName}
                                <button className='home-components-modification-button'
                                        onClick={() => handleDeactivateUser(user.uid)}>Deactivate User
                                </button>
                                {inactiveErrors[user.uid] &&
                                    <p className="error-message">{inactiveErrors[user.uid]}</p>}

                                <button className='home-components-modification-button'
                                        onClick={() => handleActivateUser(user.uid)}>Activate User
                                </button>
                                {activeErrors[user.uid] && <p className="error-message">{activeErrors[user.uid]}</p>}
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>No users found.</p>
                )}
            </div>
            <button onClick={handleBackHome}>Home</button>

        </div>
    );
};


export default authentication(ManageUsers);