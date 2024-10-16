import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../Styling/SignUp.css';

function SignUp() {
    const [formData, setFormData] = useState({
        firstName: '',
        surname: '',
        email: '',
        dateOfBirth: '',
        gender: 'MALE',
        password: ''
    });

    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setSuccessMessage('');

    if (formData.password !== formData.confirmPassword) {
        setErrorMessage("Passwords do not match.");
        return;
    }

    try {
        const response = await axios.post('http://localhost:8080/users', formData);
        setSuccessMessage(response.data);

        // Redirect to the success page after 5 seconds
        setTimeout(() => {
            navigate('/success');
        }, 5000);
    } catch (error) {
        if (error.response) {
            setErrorMessage(error.response.data);
        } else {
            setErrorMessage("An error occurred while registering.");
        }
    }
  };

  return (
    <div className="signupPage">
        <Link to="/" className="backButton">Back</Link>
        <form className="signupForm" onSubmit={handleSubmit}>
            <h2>Sign Up</h2>

            <input 
            type="text" 
            name="firstName"
            placeholder="First Name"
            value={formData.firstName} 
            onChange={handleChange} 
            required 
            />

            <input 
            type="text" 
            name="surname"
            placeholder="Surname" 
            value={formData.surname} 
            onChange={handleChange} 
            required 
            />

            <input 
            type="email" 
            name="email" 
            placeholder="Email"
            value={formData.email} 
            onChange={handleChange} 
            required 
            />

            <input 
            type="date" 
            name="dateOfBirth" 
            value={formData.dateOfBirth} 
            onChange={handleChange} 
            required 
            />

            <select 
            name="gender" 
            value={formData.gender} 
            onChange={handleChange}
            required
            >
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
            <option value="OTHER">Other</option>
            </select>

            <input 
            type="password" 
            name="password"
            placeholder="Password"
            value={formData.password} 
            onChange={handleChange} 
            required 
            />

            <input 
            type="password" 
            name="confirmPassword"
            placeholder="Confirm Password"
            value={formData.confirmPassword} 
            onChange={handleChange} 
            required 
            />

            <button type="submit" className="signupBtn">Sign Up</button>

            {errorMessage && <div className="error">{errorMessage}</div>}
            {successMessage && <div className="success">{successMessage}</div>}
        </form>
    </div>
  );
}

export default SignUp;
