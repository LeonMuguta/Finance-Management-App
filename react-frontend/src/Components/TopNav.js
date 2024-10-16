import React, { useState } from 'react';
import { Link } from "react-router-dom";
import '../Styling/TopNav.css';

function TopNav() {
    const [isOpen, setIsOpen] = useState(false);

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
                    <Link to="/" className="menuItem">Log Out</Link>
                </div>
            )}
        </div>
    );
}

export default TopNav;
