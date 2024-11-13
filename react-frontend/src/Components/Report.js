import React, { useEffect, useState } from "react";
import axios from "axios";
import Sidebar from "./Sidebar";
import TopNav from "./TopNav";
import '../Styling/Report.css';

function Report({ setIsAuthenticated }) {
    const [year, setYear] = useState(new Date().getFullYear());
    const [month, setMonth] = useState(new Date().getMonth() + 1);
    const [windowWidth, setWindowWidth] = React.useState(window.innerWidth);

    const handleDownload = async () => {
        const userId = localStorage.getItem('id');
        
        try {
            const response = await axios.get(`http://localhost:8080/reports/download`, {
                params: { year, month, userId },
                responseType: 'blob',
                validateStatus: (status) => status < 500
            });
            
            if (response.status === 404) {
                alert("Report cannot be downloaded: No info present for the selected Month and Year.");
                return;
            }

            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", `Monthly_Report_${year}_${month}.xlsx`);
            document.body.appendChild(link);
            link.click();
        } catch (error) {
            console.error("Download failed: ", error);
        }
    };

    useEffect(() => {
        const handleResize = () => {
            setWindowWidth(window.innerWidth);
        };

        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    return (
        <div className="reportContainer">
            {/* Conditional rendering of Sidebar or TopNav */}
            {windowWidth > 650 ? <Sidebar setIsAuthenticated={setIsAuthenticated} /> : <TopNav setIsAuthenticated={setIsAuthenticated} />}

            {/* Right Content Area */}
            <div className="reportContent">
                <h2>Download Monthly Report</h2>

                <div className="reportDetailsContainer">
                    <div className="formGroup">
                        <label><strong>Year:</strong></label>
                        <input type="number" value={year} onChange={(e) => setYear(e.target.value)} min="2020" max="2030"/>
                    </div>
                    <div className="formGroup">
                        <label><strong>Month:</strong></label>
                        <select value={month} onChange={(e) => setMonth(e.target.value)} required>
                            <option value="1">January</option>
                            <option value="2">February</option>
                            <option value="3">March</option>
                            <option value="4">April</option>
                            <option value="5">May</option>
                            <option value="6">June</option>
                            <option value="7">July</option>
                            <option value="8">August</option>
                            <option value="9">September</option>
                            <option value="10">October</option>
                            <option value="11">November</option>
                            <option value="12">December</option>
                        </select>
                    </div>
                    <button className="downloadButton" onClick={handleDownload}>Download Report</button>
                </div>
            </div>
        </div>
    );
}

export default Report;
