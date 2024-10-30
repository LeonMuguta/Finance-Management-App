import React, { useState, useEffect } from 'react';
import '../Styling/AddExpenseModal.css';

function AddExpenseModal({ isOpen, onClose, onAddExpense, editingExpense  }) {
    const [amount, setAmount] = useState('');
    const [category, setCategory] = useState('');
    const [description, setDescription] = useState('');
    const [date, setDate] = useState('');
    const [isRecurring, setIsRecurring] = useState(false);
    const [userId, setUserId] = useState(null);
    const [errorMessage, setErrorMessage] = useState('');

    // Fetch user ID from localStorage when the component mounts
    useEffect(() => {
        const storedUserId = localStorage.getItem('id'); // Assuming userId is stored in localStorage
        if (storedUserId) {
            setUserId(storedUserId);
        }

        // If editing an expense, populate the form fields with its data
        if (editingExpense) {
            setAmount(editingExpense.amount);
            setCategory(editingExpense.category);
            setDescription(editingExpense.description);
            setIsRecurring(editingExpense.isRecurring);
            setDate(editingExpense.date);
        } else {
            // Reset form fields when adding a new expense
            setAmount('');
            setCategory('');
            setDescription('');
            setIsRecurring(false);
            setDate('');
        }
    }, [editingExpense]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage(''); // Clear previous error messages before submitting

        if (!userId) {
            console.error("User ID not available.");
            return;
        }

        // Prepare the expense data object, including userId
        const expenseData = {
            amount: Number(amount),  // Ensure amount is a number
            category,
            description,
            date,
            isRecurring,
            user: {
                id: userId,  // Pass user ID as part of the expense object
            },
        };

        try {
            await onAddExpense(expenseData); // Pass the expense data to the parent handler

            // Clear the form fields after successful submission
            setAmount('');
            setCategory('');
            setDescription('');
            setDate('');
            setIsRecurring(false);

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
                setErrorMessage("An error occurred while adding the expense.");
            }            
        }
    };

    const handleCancel = () => {
        setErrorMessage('');
        onClose();
    };

    if (!isOpen) return null; // Don't render if modal is not open

    return ( 
        <div className="modal-overlay">
            <div className="modal-content">
                <h2>{editingExpense ? 'Edit Expense' : 'New Expense Entry'}</h2>
                <form onSubmit={handleSubmit}>
                    <label>
                        Amount:
                        <input 
                            type="number" 
                            value={amount} 
                            onChange={(e) => setAmount(e.target.value)} 
                            required 
                        />
                    </label>
                    <label>
                        Category:
                        <input 
                            type="text" 
                            value={category} 
                            onChange={(e) => setCategory(e.target.value)} 
                            required 
                        />
                    </label>
                    <label>
                        Description:
                        <textarea 
                            value={description} 
                            onChange={(e) => setDescription(e.target.value)} 
                            required 
                        />
                    </label>
                    <label>
                        Date:
                        <input 
                            type="date" 
                            value={date} 
                            onChange={(e) => setDate(e.target.value)} 
                            required 
                        />
                    </label>
                    <label className="recurring-label">
                        <input 
                            type="checkbox" 
                            checked={isRecurring} 
                            onChange={(e) => setIsRecurring(e.target.checked)} 
                        />
                        <span>Recurring</span>
                    </label>

                    {/* Display error messages */}
                    {errorMessage && <p className="error-message">{errorMessage}</p>}

                    <button type="submit" className="submit"> {editingExpense ? 'Confirm Edit' : 'Add Expense'} </button>
                    <button type="button" className="cancel" onClick={handleCancel}>Cancel</button>
                </form>
            </div>
        </div>
    );
}

export default AddExpenseModal;
