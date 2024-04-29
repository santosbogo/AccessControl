import React from 'react';
import './SignUp.css'; // Importa el archivo CSS

const SignUp = () => {
    return (
        <div className="signup-container">
            {/* Contenedor izquierdo para la imagen */}
            <div className="form-container left">
                <img src="/accessControl.jpeg" alt="Access Control" />
            </div>

            {/* Contenedor derecho para el formulario */}
            <div className="form-container right">
                <div className="signup-header">
                    <div className="logo">
                        {/* Aquí falta cerrar la etiqueta div */}
                    </div>
                    <div className="text"> Sign Up </div>
                </div>
                <div className="signup-inputs">
                    <div className="signup-input">
                        <input type="text" placeholder="Name" />
                    </div>
                    <div className="signup-input">
                        <input type="text" placeholder="Lastname" />
                    </div> {/* Cierra la etiqueta div para el segundo input */}
                    <div className="signup-input">
                        <input type="text" placeholder="Username" />
                    </div> {/* Cierra la etiqueta div para el tercer input */}
                    <div className="signup-input">
                        <input type="password" placeholder="Password" />
                    </div> {/* Cierra la etiqueta div para el cuarto input */}
                </div>
                <button className="signup-button" type="submit">Registrarse</button>
            </div>
        </div>
    );
}

export default SignUp;
