import React from 'react';
import { NavLink, useNavigate } from "react-router-dom";
import '../Styling/Sidebar.css';

function Sidebar({ setIsAuthenticated }) {
    const navigate = useNavigate();

    const handleLogout = () => {
        setIsAuthenticated(false);
        console.log('Authentication => False');
        navigate('/');
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