import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SignUp from './Components/GetStarted/SignUp';
import Login from './Components/GetStarted/Login';



function App() {
    return (
        <Router>
            <Routes>
                <Route path="/signup" element={<SignUp />} />
                <Route path="/login" element={<Login/>}/>
            </Routes>
        </Router>
    );
}

export default App;