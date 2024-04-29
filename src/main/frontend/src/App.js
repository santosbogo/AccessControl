import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SignUp from './Components/GetStarted/SignUp';


function App() {
    return (
        <Router>
            <Routes>
                <Route path="/signup" element={<SignUp/>}/>
            </Routes>
        </Router>
    );
}

export default App;



//{/*// return (*/}
//   {/*//   <div className="App">*/}
//   {/*//     <header className="App-header">*/}
//   {/*//       <img src={logo} className="App-logo" alt="logo" />*/}
//   {/*//       <p>*/}
//   {/*//         Edit <code>src/App.js</code> and save to reload.*/}
//   {/*//       </p>*/}
//   {/*//       <a*/}
//   {/*//         className="App-link"*/}
//   {/*//         href="https://reactjs.org"*/}
//   {/*//         target="_blank"*/}
//   {/*//         rel="noopener noreferrer"*/}
//   {/*//       >*/}
//   {/*//         Learn React*/}
//   {/*//       </a>*/}
//   {/*//     </header>*/}
//   {/*//   </div>*/}
//   {/*// );*/}