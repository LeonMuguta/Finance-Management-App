import React, { useEffect, useState } from "react";
import axios from "axios";
import Sidebar from "./Sidebar";
import TopNav from "./TopNav";
import AddExpenseModal from "./AddExpenseModal";
import '../Styling/Expenses.css';
// Expense function
function Expenses() {
    const [expenses, setExpenses] = useState([]); // State to hold fetched expense data
    const [userId, setUserId] = useState(null); // State to hold logged-in user ID
    const [isModalOpen, setIsModalOpen] = useState(false); // State to control modal visibility
    const [selectedExpenses, setSelectedExpenses] = useState([]); // State to hold selected expense IDs
    const [editingExpense, setEditingExpense] = useState(null); // State to hold expense data for editing
    const [showSuccessMessage, setShowSuccessMessage] = useState(false); // State to control the success message visibility
    const [windowWidth, setWindowWidth] = React.useState(window.innerWidth);

    // Fetch user ID from localStorage when the component mounts
    useEffect(() => {
        const storedUserId = localStorage.getItem('id'); // Assuming userId is stored in localStorage
        if (storedUserId) {
            setUserId(parseInt(storedUserId, 10)); // Convert userId to an integer
        }
    }, []);

    // Fetch expense data for the logged-in user when userId is available
    useEffect(() => {
        if (userId) {
            const fetchExpenses = async () => {
                try {
                    const response = await axios.get(`http://localhost:8080/expenses/user/${userId}`);
                    setExpenses(response.data); // Store user-specific expense data in state
                } catch (error) {
                    console.error('Error fetching expenses: ', error);
                }
            };

            fetchExpenses();
        }
    }, [userId]); // Dependency on userId, fetch data once it's available

    // Sort expenses by date in descending order (most recent first)
    const sortedExpenses = [...expenses].sort((a, b) => new Date(b.date) - new Date(a.date));

    // Function to handle adding expense
    const handleAddExpense = async (newExpense) => {
        try {
            const response = await axios.post('http://localhost:8080/expenses', {
                ...newExpense,
                user: { id: userId } // Include user ID in the request
            });
            console.log(response.data); // Log the response (optional)
            setExpenses((prevExpenses) => [...prevExpenses, newExpense]); // Update the state
        } catch (error) {
            if (error.response) {
                throw error.response.data;  // Return server validation error
            } else {
                // eslint-disable-next-line
                throw 'An error occurred while creating expense.';
            }            
        }
    };

    // Handle edit expense
    const handleEditExpense = async (editedExpense) => {
        try {
            const response = await axios.put(`http://localhost:8080/expenses/${editingExpense.id}`, {
                ...editedExpense,
                user: { id: userId }
            });
            // Update the state with the updated expense
            setExpenses((prevExpenses) => 
                prevExpenses.map(rev => rev.id === editingExpense.id ? response.data : rev)
            );
            setEditingExpense(null); // Reset editing expense
        } catch (error) {
            console.error('Error updating expense: ', error);
        }
    };

    // Handle checkbox toggle
    const handleCheckboxChange = (expenseId) => {
        setSelectedExpenses((prevSelected) => {
            if (prevSelected.includes(expenseId)) {
                // Remove the expenseId if already selected
                return prevSelected.filter(id => id !== expenseId);
            } else {
                // Add the expenseId to the selected array
                return [...prevSelected, expenseId];
            }
        });
    };

    // Open the modal to edit the selected expense
    const handleEditClick = () => {
        const expenseToEdit = expenses.find(expense => expense.id === selectedExpenses[0]);
        setEditingExpense(expenseToEdit); // Set the expense to be edited
        setIsModalOpen(true); // Open the modal
    };

    // Function to handle removing selected expenses
    const handleRemoveSelected = async () => {
        try {
            // Loop through selected expenses and send a DELETE request for each
            await Promise.all(
                selectedExpenses.map(async (id) => {
                    await axios.delete(`http://localhost:8080/expenses/${id}`);
                })
            );

            console.log("Successfully deleted the selected expense transactions");

            // After successful deletion, update the state to remove the deleted expenses
            setExpenses((prevExpenses) =>
                prevExpenses.filter((expense) => !selectedExpenses.includes(expense.id))
            );
            setSelectedExpenses([]); // Clear the selected expenses after deletion

            // Show success message and auto-hide after 3 seconds
            setShowSuccessMessage(true);
            setTimeout(() => {
                setShowSuccessMessage(false);
            }, 3000); // Hide message after 3 seconds

        } catch (error) {
            console.error('Error deleting expenses:', error);
        }
    };

    React.useEffect(() => {
        const handleResize = () => {
            setWindowWidth(window.innerWidth);
        };

        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    return (
        <div className="expensesContainer">
            {/* Conditional rendering of Sidebar or TopNav */}
            {windowWidth > 650 ? <Sidebar /> : <TopNav />}

            {/* Right Content Area */}
            <div className="expensesContent">
                <h2>Expenses</h2>
                
                {/* Expense Table Section */}
                <div className="expensesTableContainer">
                    <table className="expensesTable">
                        <thead>
                            <tr>
                                <th>Select</th>
                                <th>Amount</th>
                                <th>Category</th>
                                <th>Description</th>
                                <th>Date</th>
                                {/* <th>User</th> */}
                            </tr>
                        </thead>
                        <tbody>
                            {sortedExpenses.length > 0 ? (
                                sortedExpenses.map((expense) => (
                                    <tr key={expense.id} className={selectedExpenses.includes(expense.id) ? 'selectedRow' : ''}>
                                        <td>
                                            <input 
                                                type="checkbox" 
                                                checked={selectedExpenses.includes(expense.id)} 
                                                onChange={() => handleCheckboxChange(expense.id)} 
                                            />
                                        </td>
                                        <td>R{expense.amount}</td>
                                        <td>{expense.category}</td>
                                        <td>{expense.description}</td>
                                        <td>{new Date(expense.date).toLocaleDateString()}</td>
                                        {/* <td>{expense.user.firstName} {expense.user.surname}</td> */}
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="5">No expenses to display</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>

                {/* Success message */}
                {showSuccessMessage && (
                    <div className="successMessage">
                        Successfully deleted
                    </div>
                )}

                {/* Buttons Section */}
                <div className="expenseButtons">
                    <button className="addExpenseButton" onClick={() => setIsModalOpen(true)}>
                        <i className="fa fa-plus"></i> Add
                    </button>
                    <button 
                        className={`removeExpenseButton ${selectedExpenses.length === 0 ? 'disabledButton' : ''}`} 
                        disabled={selectedExpenses.length === 0} // Disable if no expense is selected
                        onClick={handleRemoveSelected} // Call the remove function
                    >
                        <i className="fa fa-trash"></i> Remove
                    </button>
                    <button 
                        className={`editExpenseButton ${selectedExpenses.length !== 1 ? 'disabledButton' : ''}`} 
                        disabled={selectedExpenses.length !== 1} // Disable if no expense is selected
                        onClick={handleEditClick}
                    >
                        <i className="fa fa-pencil"></i> Edit
                    </button>
                </div>

                {/* Add Expense Modal */}
                <AddExpenseModal 
                    isOpen={isModalOpen} 
                    onClose={() => setIsModalOpen(false)} 
                    onAddExpense={editingExpense ? handleEditExpense : handleAddExpense}
                    editingExpense={editingExpense} // Pass selected expense for editing
                />
            </div>
        </div>
    );
}

export default Expenses;