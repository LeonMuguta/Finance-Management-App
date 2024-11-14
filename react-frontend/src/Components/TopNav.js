import React, { useState } from 'react';
import { Link, useNavigate } from "react-router-dom";
import axios from 'axios';
import '../Styling/TopNav.css';

function TopNav({ setIsAuthenticated }) {
    const [isOpen, setIsOpen] = useState(false);
    const navigate = useNavigate();

    const deleteSessionCookie = () => {
        // Delete the "SESSION" cookie for path "/"
        document.cookie = "SESSION=;expires=Thu, 01 Jan 1970 00:00:00 UTC;path=/;SameSite=Lax;domain=localhost";
        
        // Optionally, you can add the "Secure" flag if the cookie was set with it
        document.cookie = "SESSION=;expires=Thu, 01 Jan 1970 00:00:00 UTC;path=/;Secure;SameSite=Lax;domain=localhost";
    
        console.log('SESSION cookie deleted!');
    };

    const handleLogout = async () => {
        try {
            const response = await axios.post('http://localhost:8080/api/auth/logout');

            if (response.status === 200) {
                setIsAuthenticated(false);
                console.log('Authentication => False');

                localStorage.clear();
                sessionStorage.clear();
                deleteSessionCookie();

                navigate('/');
            }
        } catch (error) {
            console.error('Logout error:', error);
        }
    };

    const toggleMenu = () => {
        setIsOpen(!isOpen);
    };

    return (
        <div className="topNav">
            <h1 className="appTitle">PerFinancial</h1>
            <button className="menuToggle" onClick={toggleMenu}>
                ☰
            </button>
            {isOpen && (
                <div className="dropdownMenu">
                    <Link to="/home" className="menuItem">Home</Link>
                    <Link to="/revenues" className="menuItem">Revenues</Link>
                    <Link to="/expenses" className="menuItem">Expenses</Link>
                    <Link to="/budget" className="menuItem">Budget Goals</Link>
                    <Link to="/reports" className="menuItem">Reports</Link>
                    <Link to="/profile" className="menuItem">Profile</Link>
                    <span onClick={handleLogout} className="menuItem">Log Out</span>
                </div>
            )}
        </div>
    );
}

export default TopNav;
