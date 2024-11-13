import React, { useState } from 'react';
import { Link, useNavigate } from "react-router-dom";
import '../Styling/TopNav.css';

function TopNav({ setIsAuthenticated }) {
    const [isOpen, setIsOpen] = useState(false);
    const navigate = useNavigate();

    const handleLogout = () => {
        setIsAuthenticated(false);
        console.log('Authentication => False');
        navigate('/');
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
