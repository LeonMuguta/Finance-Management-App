import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Sidebar from './Sidebar';
import TopNav from './TopNav';
import '../Styling/Profile.css';

function Profile() {
    const [user, setUser] = useState(null);
    const [editMode, setEditMode] = useState(false);
    const [formData, setFormData] = useState({
        firstName: '',
        surname: '',
        email: '',
        gender: '',
        dateOfBirth: '',
        password: ''
    });
    const [errorMessage, setErrorMessage] = useState('');
    const [showSuccessMessage, setShowSuccessMessage] = useState(false);
    const [windowWidth, setWindowWidth] = useState(window.innerWidth);

    useEffect(() => {
        const userId = localStorage.getItem('id');

        // Fetch user data from API
        const fetchUserData = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/users/${userId}`);
                setUser(response.data);
                setFormData({
                    firstName: response.data.firstName,
                    surname: response.data.surname,
                    email: response.data.email,
                    gender: response.data.gender,
                    dateOfBirth: response.data.dateOfBirth,
                    password: ''
                });
            } catch (error) {
                console.error('Error fetching user data: ', error);
            }
        };

        fetchUserData();
    }, []);

    // Track window resizing
    useEffect(() => {
        const handleResize = () => setWindowWidth(window.innerWidth);
        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData((prevData) => ({
            ...prevData,
            [name]: value
        }));
    };

    const handleSave = async () => {
        const userId = localStorage.getItem('id');
        setErrorMessage('');

        try {
            await axios.put(`http://localhost:8080/users/${userId}`, formData);
            setEditMode(false);
            setUser(formData); 

            // Show success message and auto-hide after 3 seconds
            setShowSuccessMessage(true);
            setTimeout(() => {
                setShowSuccessMessage(false);
            }, 3000);
        } catch (error) {
            // Handle error response
            if (error) {
                setErrorMessage(error.response.data);
            } else {
                setErrorMessage("An error occurred while adding the revenue.");
            }
        }
    };

    return (
        <div className="profileContainer">
            {/* Conditional rendering of Sidebar or TopNav based on window width */}
            {windowWidth > 650 ? <Sidebar /> : <TopNav />}

            <div className="profileContent">
                <h2>User Profile</h2>

                <div className="profileDetailsContainer">
                    {user ? (
                        <>
                            {!editMode ? (
                                <div className="profileDetails">
                                    <p><strong>First Name:</strong> {user.firstName}</p>
                                    <p><strong>Surname:</strong> {user.surname}</p>
                                    <p><strong>Email:</strong> {user.email}</p>
                                    <p><strong>Gender:</strong> {user.gender}</p>
                                    <p><strong>Date of Birth:</strong> {new Date(user.dateOfBirth).toLocaleDateString()}</p>
                                    <button className="edit" onClick={() => setEditMode(true)}>Edit</button>
                                </div>
                            ) : (
                                <div className="profileEditForm">
                                    <label>
                                        First Name:
                                        <input
                                            type="text"
                                            name="firstName"
                                            value={formData.firstName}
                                            onChange={handleInputChange}
                                        />
                                    </label>
                                    <label>
                                        Surname:
                                        <input
                                            type="text"
                                            name="surname"
                                            value={formData.surname}
                                            onChange={handleInputChange}
                                        />
                                    </label>
                                    <label>
                                        Email:
                                        <input
                                            type="email"
                                            name="email"
                                            value={formData.email}
                                            readOnly
                                        />
                                    </label>
                                    <label>
                                        Gender:
                                        <select
                                            name="gender" 
                                            value={formData.gender} 
                                            onChange={handleInputChange}
                                            required
                                            >
                                            <option value="MALE">Male</option>
                                            <option value="FEMALE">Female</option>
                                            <option value="OTHER">Other</option>
                                        </select>
                                    </label>
                                    <label>
                                        Date of Birth:
                                        <input
                                            type="date"
                                            name="dateOfBirth"
                                            value={formData.dateOfBirth}
                                            readOnly
                                        />
                                    </label>
                                    <label>
                                        Password:
                                        <input
                                            type="password"
                                            name="password"
                                            value={formData.password}
                                            onChange={handleInputChange}
                                        />
                                    </label>

                                    {/* Display error messages */}
                                    {errorMessage && <p className="error-message">{errorMessage}</p>}

                                    <button className="save" onClick={handleSave}>Save</button> {/* Save button */}
                                    <button className="cancel" onClick={() => setEditMode(false)}>Cancel</button> {/* Cancel button */}
                                </div>
                            )}
                        </>
                    ) : (
                        <p>Loading user data...</p>
                    )}
                </div>

                {/* Success message */}
                {showSuccessMessage && (
                    <div className="successMessage">
                        Successfully updated
                    </div>
                )}
            </div>
        </div>
    );
}

export default Profile;
