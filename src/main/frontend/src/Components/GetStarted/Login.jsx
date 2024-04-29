import React from "react";
import './Login.css';
// import image from './ruta/a/la/imagen'; // Importa la imagen

const Login = () =>{
    return (
        <div className="login-container">
            {/* Contenedor izquierdo para la imagen */}
            <div className="form-container">
                {/*<img src={image} alt="Imagen de ejemplo"/> /!* Usa la variable de imagen *!/*/}
            </div>

            {/* Contenedor derecho para el formulario */}
            <div className="form-container">
                <div className="login-title">
                    <div className="text"> Login </div>
                </div>
                <form>
                    <input type="text" placeholder="Username"/>
                    <input type="password" placeholder="Password"/>
                    <button type="submit">Iniciar sesión</button>
                </form>
            </div>
        </div>
    );
}

export default Login;
