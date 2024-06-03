import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../Home.css"
import authentication from "../../Hoc/Hoc"; // Importar el HOC de autenticación
import { Link } from 'react-router-dom'; // Importar Link si es necesario

const ManageUsers = () => {
    const [users, setUsers] = useState([]);
    const navigate = useNavigate();
    useEffect(() => {
        const fetchActiveUsers = async () => {
            try {
                const response = await axios.get('http://localhost:3333/users/findAll');
                setUsers(response.data);
            } catch (error) {
                console.error('Error fetching active users:', error);
            }
        };

        fetchActiveUsers();
    }, []);


    const handleCreateUser = () => {
        console.log("Redirect to create new user page");
        navigate("/Home/manage-users/create");
    };

    const handleEditUser = (userId) => {
        console.log(`Redirect to edit user page for user ${userId}`);
        navigate(`/home/manage-users/edit/${userId}`);
    };


    const handleDeactivateUser = async (userId) => {
        console.log(userId);
        try {
            await axios.post(`http://localhost:3333/user/deactivate/${userId}`);
            setUsers(users.filter(user => user.uid !== userId));
        } catch (error) {
            console.error('Error deactivating user:', error);
        }
    };


    return (
        <div className="home-page">
            <div className="main-title">Manage Users</div>
            <button onClick={handleCreateUser}>Create New User</button>
            <Link to="/Home/" className="button">Home</Link>
            <div className="user-list">
                {users.length > 0 ? (
                    <ul>
                        {users.map(user => (
                            <li key={user.id}>
                                {user.firstName} {user.lastName}
                                <button onClick={() => handleDeactivateUser(user.uid)}>Deactivate User</button>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>No active users found.</p>
                )}
            </div>
        </div>
    );
};

export default authentication(ManageUsers);
