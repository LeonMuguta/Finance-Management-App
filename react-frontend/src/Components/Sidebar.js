import React from 'react';
import { NavLink, useNavigate } from "react-router-dom";
import axios from 'axios';
import '../Styling/Sidebar.css';

function Sidebar({ setIsAuthenticated }) {
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

    return (
        <div className="sidebar">
            <h1 className="appName">PerFinancial</h1>
            <hr className="divider" />

            <ul className="menu">
                <li className="menuItem">
                    <NavLink to="/home" activeClassName="active">
                        <i className="fa fa-home" aria-hidden="true"></i>
                        <span>Home</span>
                    </NavLink>
                </li>
                <li className="menuItem">
                    <NavLink to="/revenues" activeClassName="active">
                        <i className="fa fa-arrow-up" aria-hidden="true"></i>
                        <span>Revenues</span>
                    </NavLink>
                </li>
                <li className="menuItem">
                    <NavLink to="/expenses" activeClassName="active">
                        <i className="fa fa-arrow-down" aria-hidden="true"></i>
                        <span>Expenses</span>
                    </NavLink>
                </li>
                <li className="menuItem">
                    <NavLink to="/budget" activeClassName="active">
                        <i className="fa fa-bullseye" aria-hidden="true"></i>
                        <span>Budget Goals</span>
                    </NavLink>
                </li>
                <li className="menuItem">
                    <NavLink to="/reports" activeClassName="active">
                        <i className="fa fa-table" aria-hidden="true"></i>
                        <span>Reports</span>
                    </NavLink>
                </li>
                <li className="menuItem">
                    <NavLink to="/profile" activeClassName="active">
                        <i className="fa fa-user" aria-hidden="true"></i>
                        <span>Profile</span>
                    </NavLink>
                </li>
            </ul>

            <div className="logoutSection">
                <span className="click" onClick={handleLogout}>
                    <i className="fa fa-sign-out" aria-hidden="true"></i>
                    <span>Log Out</span>
                </span>
            </div>
        </div>
    );
}

export default Sidebar;