import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import '../Styling/VerifyCode.css';

function VerifyCode({ setIsAuthenticated }) {
    const [code, setCode] = useState("");
    const [message, setMessage] = useState("");
    const [isVerified, setIsVerified] = useState(false);
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

    return (
        <div className="verifyCodePage">
            <form className="verifyForm" onSubmit={handleSubmit}>
                <h2>2FA Authentication</h2>
                <label htmlFor="code">Enter Verification Code</label>
                <input
                    type="text"
                    id="code"
                    value={code}
                    onChange={handleCodeChange}
                    required
                    disabled={isVerified}
                />
                {!isVerified && <button type="submit" className="verifyBtn">Submit</button>}
                {message && (
                    <p style={{ color: isVerified ? "green" : "red", fontWeight: "bold", fontSize: "14px", textAlign: "center" }}>{message}</p>
                )}
            </form>
        </div>
    );
}

export default VerifyCode;
