import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./ManageUsers.css"; // Importar los estilos CSS
import { Link } from 'react-router-dom'; // Importar Link si es necesario


const ManageUsers = () => {
    const [searchTerm, setSearchTerm] = useState("");
    const navigate = useNavigate();
    const [users, setUsers] = useState([
        { id: 1, name: "John Doe", email: "john@example.com" },
        { id: 2, name: "Jane Smith", email: "jane@example.com" },
        { id: 3, name: "Alice Johnson", email: "alice@example.com" },
        { id: 4, name: "Bob Brown", email: "bob@example.com" },
        { id: 5, name: "Emma Davis", email: "emma@example.com" }
    ]);
    const handleCreateUser = () => {
        console.log("Redirect to create new user page");
        navigate("/Home/manage-users/create");
    };

    const handleEditUser = (userId) => {
        console.log(`Redirect to edit user page for user ${userId}`);
        navigate(`/home/manage-users/edit/${userId}`);
    };

    const handleDeleteUser = (userId) => {
        console.log(`Delete user with ID ${userId}`);
        // Aquí añadir lógica para confirmar y eliminar usuario
    };

    const handleSearchChange = (event) => {
        setSearchTerm(event.target.value);
    };

    const handleSearchSubmit = (event) => {
        event.preventDefault();
        console.log(`Searching for users with term: ${searchTerm}`);

        // Aquí implementar lógica para enviar la búsqueda a la base de datos
        // Por ejemplo, una llamada a API para obtener los usuarios filtrados
    };

    return (
        <div className="manage-users">
            <div className="main-title">Manage Users</div>
            <button onClick={handleCreateUser}>Create New User</button>
            <form onSubmit={handleSearchSubmit}>
                <input
                    type="text"
                    placeholder="Search users by name..."
                    value={searchTerm}
                    onChange={handleSearchChange}
                />
                <button type="submit">Search</button>
                <Link to="/Home/" className="home-button">Home</Link>
            </form>
            <div>
                {/* Aquí se debería renderizar la lista de usuarios buscados */}
                {/* Por ejemplo, después de la búsqueda, los usuarios podrían estar en un estado y mapeados aquí */}
            </div>
        </div>
    );
};

export default ManageUsers;
