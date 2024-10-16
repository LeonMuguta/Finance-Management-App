import React from 'react';
import '../Styling/Welcome.css';
import { Link } from 'react-router-dom';
import WelcomeImage from '../Images/welcome-page-image.jpg'

function Welcome () {
    return ( 
        <div className="welcomePage">
            <div className="welcomeImage">
                <img src={WelcomeImage} alt="Welcome Finance" />
            </div>
            <div className="welcomeNav">
                <h1>Manage Your Finances</h1>
                <p>Take control of your financial future by staying organized and informed. 
                    Our platform helps you effortlessly track your income and expenses, 
                    giving you clear insights into where your money is going.
                </p>
                <div className="welcomeButtons">
                    <Link to="/signup">
                        <button className="btn btn-signup">Sign Up</button>
                    </Link>
                    <Link to="/login">
                        <button className="btn btn-login">Log In</button>
                    </Link>
                </div>
            </div>
        </div>
    );
}

export default Welcome ;