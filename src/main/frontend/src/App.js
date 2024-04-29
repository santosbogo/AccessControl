import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SignUp from './Components/GetStarted/SignUp';
import Home from './Components/Home/Home';
import ManageUsers from './Components/Home/ManageUsers';
import History from './Components/Home/History';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/signup" element={<SignUp/>}/>
                <Route path="/Home" element={<Home/>}/>
                <Route path="/Home/manage-users" element={<ManageUsers/>}/>
                <Route path="/Home/view-history" element={<History/>}/>
            </Routes>
        </Router>
    );
}

export default App;



