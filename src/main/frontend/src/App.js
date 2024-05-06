import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SignUp from './Components/GetStarted/SignUp';
import Login from './Components/GetStarted/Login';
import Home from './Components/Home/Home';
import ManageUsers from "./Components/Home/ManageUsers/ManageUsers";
import Create from "./Components/Home/ManageUsers/CreateUser/Create";
import History from "./Components/Home/History/History";



function App() {
    return (
        <Router>
            <Routes>
                <Route path="/signup" element={<SignUp />} />
                <Route path="/login" element={<Login />} />
                <Route path="/home" element={<Home />} />
                <Route path="/home/manage-users" element={<ManageUsers />} />
                <Route path="/home/manage-users/create" element={<Create />} />
                <Route path="/home/view-history" element={<History />} />
]            </Routes>
        </Router>
    );
}

export default App;