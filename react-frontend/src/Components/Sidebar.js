import React from 'react';
import { Link, NavLink } from "react-router-dom";
import '../Styling/Sidebar.css';

function Sidebar() {
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
                    <NavLink to="/profile" activeClassName="active">
                        <i className="fa fa-user" aria-hidden="true"></i>
                        <span>Profile</span>
                    </NavLink>
                </li>
            </ul>

            <div className="logoutSection">
                <Link to="/">
                    <i className="fa fa-sign-out" aria-hidden="true"></i>
                    <span>Log Out</span>
                </Link>
            </div>
        </div>
    );
}

export default Sidebar;