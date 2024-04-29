import React from 'react';
import './SignUp.css'; // Importa el archivo CSS

const SignUp = () => {
    return (
        <div className="signup-container">
            {/* Contenedor izquierdo para la imagen */}
            <div className="form-container">
                {/*<img src="/accessControl.jpeg" alt="Access Control" />*/}
            </div>

            {/* Contenedor derecho para el formulario */}
            <div className="form-container">
                <div className="signup-title">
                    <div className="text"> Sign Up </div>
                </div>
                <form>
                    <input type="text" placeholder="Name" />
                    <input type="text" placeholder="Lastname" />
                    <input type="text" placeholder="Username" />
                    <input type="text" placeholder="Password" />
                    <button type="submit">Sign up</button>
                </form>
            </div>
        </div>
    );
}

export default SignUp;

