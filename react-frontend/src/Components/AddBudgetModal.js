import React, { useState, useEffect } from "react";
import "../Styling/AddBudgetModal.css";

function AddBudgetModal({ isOpen, onClose, onAddBudget, editingBudget }) {
    const [month, setMonth] = useState("");
    const [year, setYear] = useState(new Date().getFullYear().toString());
    const [minRevenue, setMinRevenue] = useState("");
    const [maxExpense, setMaxExpense] = useState("");
    const [netBalanceGoal, setNetBalanceGoal] = useState("");
    const [userId, setUserId] = useState(null);
    const [errorMessage, setErrorMessage] = useState('');

    // Fetch user ID from localStorage when the component mounts
    useEffect(() => {
        const storedUserId = localStorage.getItem('id'); // Assuming userId is stored in localStorage
        if (storedUserId) {
            setUserId(storedUserId);
        }

        // If editing a budget, populate the form fields with its data
        if (editingBudget) {
            setMonth(editingBudget.month);
            setYear(editingBudget.year)
            setMinRevenue(editingBudget.minRevenue);
            setMaxExpense(editingBudget.maxExpense);
            setNetBalanceGoal(editingBudget.netBalanceGoal);
        } else {
            // Reset form fields when adding a new budget goal
            setMonth('');
            setMinRevenue('');
            setMaxExpense('');
            setNetBalanceGoal('');
        }
    }, [editingBudget]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');  // Clear previous error messages before submitting

        if (!userId) {
            console.error("User ID not available.");
            return;
        }

        // Prepare the budget data object, including userId
        const newBudgetData = {
            month,
            year: parseInt(year),
            minRevenue: parseFloat(minRevenue),
            maxExpense: parseFloat(maxExpense),
            netBalanceGoal: parseFloat(netBalanceGoal),
            user: { 
                id: userId 
            },
        };
        
        try {
            await onAddBudget(newBudgetData); // Pass the budget data to the parent handler
            
            // Clear the form fields after successful submission
            setMonth('');
            setYear(new Date().getFullYear().toString());
            setMinRevenue('');
            setMaxExpense('');
            setNetBalanceGoal('');
            
            onClose(); // Close the modal after submission

            // Refresh the page after a successful submission
            window.location.reload();
        } catch (error) {
            // Handle error response
            if (error) {
                // Server responded with a status other than 2xx
                setErrorMessage(error);
            } else {
                // Network error or other errors
                setErrorMessage("An error occurred while adding the budget goal.");
            }  
        }
    };

    if (!isOpen) return null; // Don't render if modal is not open

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <h2>{editingBudget ? 'Edit Budget Goal' : 'Add New Budget Goal'}</h2>
                <form onSubmit={handleSubmit}>
                    <label>
                        Month
                        <select value={month} onChange={(e) => setMonth(e.target.value)} required>
                            <option value="JANUARY">January</option>
                            <option value="FEBRUARY">February</option>
                            <option value="MARCH">March</option>
                            <option value="APRIL">April</option>
                            <option value="MAY">May</option>
                            <option value="JUNE">June</option>
                            <option value="JULY">July</option>
                            <option value="AUGUST">August</option>
                            <option value="SEPTEMBER">September</option>
                            <option value="OCTOBER">October</option>
                            <option value="NOVEMBER">November</option>
                            <option value="DECEMBER">December</option>
                        </select>
                    </label>
                    <label>
                        Year
                        <input 
                            type="number"
                            value={year}
                            readOnly
                        />
                    </label>
                    <label>
                        Minimum Revenue Goal
                        <input 
                            type="number" 
                            value={minRevenue} 
                            onChange={(e) => setMinRevenue(e.target.value)} 
                            required 
                        />
                    </label>                    
                    <label>
                        Maximum Expense Goal
                        <input 
                            type="number" 
                            value={maxExpense} 
                            onChange={(e) => setMaxExpense(e.target.value)} 
                            required 
                        />
                    </label>                    
                    <label>
                        Net Balance Goal
                        <input 
                            type="number" 
                            value={netBalanceGoal} 
                            onChange={(e) => setNetBalanceGoal(e.target.value)} 
                            required 
                        />
                    </label>

                    {/* Display error messages */}
                    {errorMessage && <p className="error-message">{errorMessage}</p>}

                    <button type="submit" className="submit">{editingBudget ? 'Confirm Edit' : 'Submit'}</button>
                    <button type="button" className="cancel" onClick={onClose}>Cancel</button>
                    
                </form>
            </div>
        </div>
    );
}

export default AddBudgetModal;
