import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../Styling/Login.css';

function Login({ setIsAuthenticated }) {
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [isDisabled, setIsDisabled] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
          ...formData,
          [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setIsDisabled(true);

        try {
            const response = await axios.post('http://localhost:8080/api/auth/login', formData, { withCredentials: true });
            if (response.status === 200) {
                // Assuming the response contains the user's first name in the data
                const { firstName, surname, id, twoFactorAuth } = response.data;
                localStorage.setItem('firstName', firstName);
                localStorage.setItem('surname', surname);
                localStorage.setItem('id', id);
                
                if (twoFactorAuth) {
                    setSuccess('Sending verification code to your email');
                } else {
                    setSuccess('Welcome!');
                }

                // Delay navigation to display the success message
                if (twoFactorAuth) {
                    setTimeout(() => {
                        navigate('/verify');
                    }, 2000);
                } else {
                    setIsAuthenticated(true);
                    setTimeout(() => {
                        localStorage.setItem('isAuthenticated', 'true');
                        navigate('/home');
                    }, 2000); 
                }
                
            }
        } catch (err) {
            setIsDisabled(false); // Re-enable the button in case of an error

            // Handle error response
            if (err.response) {
                // Server responded with a status other than 2xx
                setError(err.response.data);
            } else {
                // Network error or other errors
                setError('An error occured. Please try again later.');
                console.log(err);
            }
        }
    };

    return (
        <div className="loginPage">
            <Link to="/" className="backButton">Back</Link>
            <form className="loginForm" onSubmit={handleSubmit}>
                <h2>Log In</h2>
                <label>Email</label>
                <input 
                type="email" 
                name="email" 
                value={formData.email} 
                onChange={handleChange} 
                required 
                />

                <label>Password</label>
                <input 
                type="password" 
                name="password" 
                value={formData.password} 
                onChange={handleChange} 
                required 
                />

                <button type="submit" className="loginBtn" disabled={isDisabled}>Log In</button>
                {error && <p className="error">{ error }</p>}
                {success && <p className="success">{ success }</p>}
            </form>
        </div>
    );
}

export default Login;
