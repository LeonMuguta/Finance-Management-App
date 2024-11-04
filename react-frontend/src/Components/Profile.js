import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import Sidebar from './Sidebar';
import TopNav from './TopNav';
import '../Styling/Profile.css';

function Profile() {
    const [user, setUser] = useState(null);
    // States for a user changing their password
    const [editMode, setEditMode] = useState(false);
    const [formData, setFormData] = useState({
        firstName: '',
        surname: '',
        email: '',
        gender: '',
        dateOfBirth: '',
        twoFactorAuth: false
    });
    const [errorMessage, setErrorMessage] = useState('');
    const [showSuccessMessage, setShowSuccessMessage] = useState(false);
    // States for changing a user's password
    const [showChangePasswordForm, setShowChangePasswordForm] = useState(false);
    const [passwordForm, setPasswordForm] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    });
    const [passwordErrorMessage, setPasswordErrorMessage] = useState('');
    const [passwordSuccessMessage, setPasswordSuccessMessage] = useState('');
    // State for window repsonsiveness
    const [windowWidth, setWindowWidth] = useState(window.innerWidth);
    const navigate = useNavigate();

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
                    twoFactorAuth: response.data.twoFactorAuth
                });
            } catch (error) {
                console.error('Error fetching user data: ', error);
            }
        };

        fetchUserData();
    }, []);

    // Track window resizing
    React.useEffect(() => {
        const handleResize = () => {
            setWindowWidth(window.innerWidth);
        };

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

    const handlePasswordChange = (e) => {
        const { name, value } = e.target;
        setPasswordForm((prevData) => ({
            ...prevData,
            [name]: value
        }));
    };

    const handleChangePasswordSubmit = async () => {
        const userId = localStorage.getItem('id');
        setPasswordErrorMessage('');

        if (passwordForm.newPassword !== passwordForm.confirmPassword) {
            setPasswordErrorMessage("New passwords do not match.");
            return;
        }

        try {
            await axios.post(`http://localhost:8080/users/${userId}/change-password`, {
                currentPassword: passwordForm.currentPassword,
                newPassword: passwordForm.newPassword
            });
            setShowChangePasswordForm(false); // Close the form after successful change

            setPasswordSuccessMessage('Password updated successfully.');
            setTimeout(() => {
                setPasswordSuccessMessage('');                
            }, 3000);
        } catch (error) {
            setPasswordErrorMessage(error.response?.data || "An error occurred while changing the password.");
        }
    };

    // Method to handle account deletion
    const handleDeleteAccount = async () => {
        const userId = localStorage.getItem('id');

        // Confirm the user wants to delete their account
        if (window.confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
            try {
                await axios.delete(`http://localhost:8080/users/${userId}`);
                
                // Clear user data and localStorage
                localStorage.removeItem('id');
                localStorage.removeItem('token'); // If you're storing a token in localStorage
                
                // Redirect to login or welcome page
                navigate('/');
            } catch (error) {
                console.error('Error deleting user account: ', error);
                alert("An error occurred while deleting your account.");
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
                            {!editMode && !showChangePasswordForm ? (
                                <div className="profileDetails">
                                    <p><strong>First Name:</strong> {user.firstName}</p>
                                    <p><strong>Surname:</strong> {user.surname}</p>
                                    <p><strong>Email:</strong> {user.email}</p>
                                    <p><strong>Gender:</strong> {user.gender}</p>
                                    <p><strong>Date of Birth:</strong> {new Date(user.dateOfBirth).toLocaleDateString()}</p>
                                    <p><strong>Two-Factor Authentication:</strong> {user.twoFactorAuth ? 'Enabled' : 'Disabled'}</p>
                                    <button className="edit" onClick={() => setEditMode(true)}>Edit</button>
                                    <button className="changePassword" onClick={() => setShowChangePasswordForm(true)}>Change Password</button>
                                    <button className="deleteAccount" onClick={handleDeleteAccount}>Delete Account</button>
                                </div>
                            ) : editMode ? (
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
                                        Enable Two-Factor Authentication?
                                        <select
                                            name="twoFactorAuth" 
                                            value={formData.twoFactorAuth} 
                                            onChange={handleInputChange}
                                            required
                                            >
                                            <option value="true">Yes</option>
                                            <option value="false">No</option>
                                        </select>
                                    </label>
                                    {/* <label>
                                        Password:
                                        <input
                                            type="password"
                                            name="password"
                                            value={formData.password}
                                            onChange={handleInputChange}
                                        />
                                    </label> */}

                                    {/* Display error messages */}
                                    {errorMessage && <p className="error-message">{errorMessage}</p>}

                                    <button className="save" onClick={handleSave}>Save</button> {/* Save button */}
                                    <button className="cancel" onClick={() => setEditMode(false)}>Cancel</button> {/* Cancel button */}
                                </div>
                            ) : (
                                <div className="changePasswordForm">
                                    {/* Change password form */}
                                    <label>
                                        Current Password:
                                        <input
                                            type="password"
                                            name="currentPassword"
                                            value={passwordForm.currentPassword}
                                            onChange={handlePasswordChange}
                                        />
                                    </label>
                                    <label>
                                        New Password:
                                        <input
                                            type="password"
                                            name="newPassword"
                                            value={passwordForm.newPassword}
                                            onChange={handlePasswordChange}
                                        />
                                    </label>
                                    <label>
                                        Confirm New Password:
                                        <input
                                            type="password"
                                            name="confirmPassword"
                                            value={passwordForm.confirmPassword}
                                            onChange={handlePasswordChange}
                                        />
                                    </label>

                                    {/* Display password error messages */}
                                    {passwordErrorMessage && <p className="error-message">{passwordErrorMessage}</p>}

                                    <button className="confirm" onClick={handleChangePasswordSubmit}>Confirm</button>
                                    <button className="cancel" onClick={() => setShowChangePasswordForm(false)}>Cancel</button>
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

                {/* Password success message */}
                {passwordSuccessMessage && (
                    <div className="successMessage">
                        Password successfully updated
                    </div>
                )}
            </div>
        </div>
    );
}

export default Profile;
