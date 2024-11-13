import React, { useEffect, useState, useRef, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import '../Styling/SessionTimeout.css';

const SessionTimeout = ({ setIsAuthenticated }) => {
    const [timeLeft, setTimeLeft] = useState(null); // 3 minutes countdown (180 seconds)
    const sessionTimeout = 4 * 60 * 1000; // 4 minutes in milliseconds
    const warningTime = 1 * 60 * 1000; // 1 minutes in milliseconds (3 minutes left)
    const navigate = useNavigate();

    const warningTimerRef = useRef(null);
    const sessionTimerRef = useRef(null);
    const countdownTimerRef = useRef(null);

    const clearTimers = () => {
        clearTimeout(warningTimerRef.current);
        clearTimeout(sessionTimerRef.current);
        clearInterval(countdownTimerRef.current);
    };

    const startCountdown = useCallback(() => {
        let countdown = 180; // 3 minutes countdown
        setTimeLeft(countdown);

        countdownTimerRef.current = setInterval(() => {
            countdown -= 1;
            setTimeLeft(countdown);
            console.log(countdown + " seconds left.")

            // End countdown and redirect when time is up
            if (countdown <= 0) {
                clearInterval(countdownTimerRef.current);
                setIsAuthenticated(false);
                navigate('/'); // Redirect to welcome after timeout
            }
        }, 1000);
    }, [navigate, setIsAuthenticated]);

    const resetTimers = useCallback(() => {
        clearTimers();
        setTimeLeft(null);
        console.log("--------------------------TESTING resetTimers function-------------------------------");

        // Set a warning timer to start countdown 3 minutes before timeout
        warningTimerRef.current = setTimeout(() => {
            startCountdown();
        }, warningTime);

        // Redirect after session timeout
        sessionTimerRef.current = setTimeout(() => {
            alert("Session expired. Redirecting to welcome page.");
            setIsAuthenticated(false);
            navigate('/');
        }, sessionTimeout);
    }, [navigate, sessionTimeout, warningTime, startCountdown, setIsAuthenticated]);

    useEffect(() => {
        resetTimers();
        console.log("--------------------------TESTING useEffect function-------------------------------")

        // Event listeners for user activity to reset timers
        const resetOnActivity = () => resetTimers();
        window.addEventListener('click', resetOnActivity);
        window.addEventListener('pointermove', resetOnActivity);
        window.addEventListener('keydown', resetOnActivity);

        // Clean up timers and listeners on unmount
        return () => {
            clearTimers();
            window.removeEventListener('click', resetOnActivity);
            window.removeEventListener('pointermove', resetOnActivity);
            window.removeEventListener('keydown', resetOnActivity);
        };
    }, [resetTimers]);

    return (
        <div>
            {timeLeft <= 180 && timeLeft > 0 && (
                <div className="session-timeout-warning">
                    <p>Your session will expire in {Math.floor(timeLeft / 60)}:{timeLeft % 60 < 10 ? `0${timeLeft % 60}` : timeLeft % 60}</p>
                </div>
            )}
        </div>
    );
};

export default SessionTimeout;
