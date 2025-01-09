import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import '../Styling/VerifyCode.css';

function VerifyCode({ setIsAuthenticated }) {
    const [code, setCode] = useState("");
    const [message, setMessage] = useState("");
    const [isVerified, setIsVerified] = useState(false);
    const [isExpired, setIsExpired] = useState(false);
    const [timeLeft, setTimeLeft] = useState(300); // 300 seconds (5 minutes)
    const navigate = useNavigate();

    const handleCodeChange = (e) => {
        setCode(e.target.value);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const userId = localStorage.getItem("id");
            const response = await axios.post("http://localhost:8080/api/verify/verify-code", {
                userId: userId,
                code: code
            });

            if (response.status === 200) {
                setMessage("Success!");
                setIsVerified(true);
                setIsAuthenticated(true);
                setTimeout(() => {
                    localStorage.setItem('isAuthenticated', 'true');
                    navigate("/home")
                }, 5000);
            }
        } catch (error) {
            setMessage("Invalid code, please try again.");
            console.error("Verification failed: ", error);
        }
    };

    useEffect(() => {
        if (timeLeft > 0) {
            const timer = setTimeout(() => setTimeLeft(timeLeft - 1), 1000);
            return () => clearTimeout(timer); // Cleanup the timer
        } else {
            setMessage("Time expired. Redirecting to login...");
            setIsExpired(true);
            setTimeout(() => {
                navigate("/login");
            }, 2000); // Give user 2 seconds to see the message before redirecting
        }
    }, [timeLeft, navigate]);

    const formatTime = (seconds) => {
        const minutes = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `0${minutes}:${secs < 10 ? `0${secs}` : secs}`;
    };

    return (
        <div className="verifyCodePage">
            <form className="verifyForm" onSubmit={handleSubmit}>
                <h2>2FA Authentication</h2>
                <div className="labelTimer">
                    <label htmlFor="code">Enter Verification Code</label>
                    <p style={{ fontWeight: "bold", margin: "0", color: timeLeft < 60 ? "red" : "black" }}>
                        {formatTime(timeLeft)}
                    </p>
                </div>
                <input
                    type="text"
                    id="code"
                    value={code}
                    onChange={handleCodeChange}
                    required
                    disabled={isVerified || isExpired}
                />
                {!isVerified && <button type="submit" className="verifyBtn" disabled={isExpired}>Submit</button>}
                {message && (
                    <p style={{ color: isVerified ? "green" : "red", fontWeight: "bold", fontSize: "14px", textAlign: "center" }}>{message}</p>
                )}
            </form>
        </div>
    );
}

export default VerifyCode;
