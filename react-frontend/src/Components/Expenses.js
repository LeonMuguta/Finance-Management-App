import React, { useEffect, useState } from "react";
import axios from "axios";
import Sidebar from "./Sidebar";
import TopNav from "./TopNav";
import AddExpenseModal from "./AddExpenseModal";
import { Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import '../Styling/Expenses.css';

// Register necessary chart components
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

function Expenses({ setIsAuthenticated }) {
    const [expenses, setExpenses] = useState([]); // State to hold fetched expense data
    const [userId, setUserId] = useState(null); // State to hold logged-in user ID
    const [isModalOpen, setIsModalOpen] = useState(false); // State to control modal visibility
    const [selectedExpenses, setSelectedExpenses] = useState([]); // State to hold selected expense IDs
    const [editingExpense, setEditingExpense] = useState(null); // State to hold expense data for editing
    const [showSuccessMessage, setShowSuccessMessage] = useState(false); // State to control the success message visibility
    const [windowWidth, setWindowWidth] = React.useState(window.innerWidth);
    const [expandedMonths, setExpandedMonths] = useState({}); // Track expanded/collapsed months

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

    // Group exoenses by month and year
    const groupedExpenses = sortedExpenses.reduce((groups, expense) => {
        const date = new Date(expense.date);
        const monthYear = `${date.toLocaleString('default', { month: 'long' })} ${date.getFullYear()}`;
        if (!groups[monthYear]) {
            groups[monthYear] = [];
        }
        groups[monthYear].push(expense);
        return groups;
    }, {});

    // Calculate total expense for each month
    const monthlyTotals = Object.keys(groupedExpenses).map((monthYear) => {
        const total = groupedExpenses[monthYear].reduce((sum, expense) => sum + expense.amount, 0);
        return { monthYear, total };
    });

    // Sort monthlyTotals by date in ascending order
    monthlyTotals.sort((a, b) => new Date(a.monthYear) - new Date(b.monthYear));

    const chartData = {
        labels: monthlyTotals.map(item => item.monthYear),
        datasets: [
            {
                label: 'Total Expense',
                data: monthlyTotals.map(item => item.total),
                backgroundColor: '#fff',
                borderColor: 'black',
                borderWidth: 1
            }
        ]
    };
    
    const chartOptions = {
        responsive: true,
        plugins: {
            legend: { position: 'top' },
            title: { display: true, text: 'Total Expenses per Month' }
        }
    };

    // Handle expanding/collapsing month sections
    const toggleMonth = (monthYear) => {
        setExpandedMonths((prevState) => ({
            ...prevState,
            [monthYear]: !prevState[monthYear],
        }));
    };

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
            if (error.response) {
                throw error.response.data;  // Return server validation error
            } else {
                // eslint-disable-next-line
                throw 'An error occurred while creating expense.';
            }
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

    // Open the modal to add new expense (ensure fields are reset)
    const handleAddClick = () => {
        setEditingExpense(null); // Reset editingExpense to ensure the form is empty
        setIsModalOpen(true); // Open the modal
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

    // Modify the onClose function to unselect the selected expense
    const handleCloseModal = () => {
        setIsModalOpen(false);
        setEditingExpense(null);
        setSelectedExpenses([]); // Unselect any selected expenses
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
            {windowWidth > 650 ? <Sidebar setIsAuthenticated={setIsAuthenticated} /> : <TopNav setIsAuthenticated={setIsAuthenticated} />}

            {/* Right Content Area */}
            <div className="expensesContent">
                <h2>Expenses</h2>
                
                {/* Loop through grouped expenses by month */}
                {Object.keys(groupedExpenses).map((monthYear) => (
                    <div key={monthYear} className="monthSection">
                        {/* Collapsible header */}
                        <div className="monthHeader" onClick={() => toggleMonth(monthYear)}>
                            <h3>{monthYear}</h3>
                            <button>
                                {expandedMonths[monthYear] ? 'Collapse' : 'Expand'}
                            </button>
                        </div>

                        {/* Collapsible content */}
                        {expandedMonths[monthYear] && (
                            <div className={`tableContainer ${windowWidth <= 650 ? 'scrollableTable' : ''}`}>
                                <table className="expensesTable">
                                    <thead>
                                        <tr>
                                            <th>Select</th>
                                            <th>Amount</th>
                                            <th>Category</th>
                                            <th>Description</th>
                                            <th>Recurring</th>
                                            <th>Date</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {groupedExpenses[monthYear].map((expense) => (
                                            <tr key={expense.id} className={selectedExpenses.includes(expense.id) ? 'selectedRow' : ''}>
                                                <td style={{ width: '5%' }}>
                                                    <input 
                                                        type="checkbox" 
                                                        checked={selectedExpenses.includes(expense.id)} 
                                                        onChange={() => handleCheckboxChange(expense.id)} 
                                                    />
                                                </td>
                                                <td style={{ width: '10%' }}>R{expense.amount.toFixed(2)}</td>
                                                <td style={{ width: '20%' }}>{expense.category}</td>
                                                <td style={{ width: '50%' }}>{expense.description}</td>
                                                <td style={{ width: '5%' }}>{expense.isRecurring ? 'Yes' : 'No'}</td>
                                                <td style={{ width: '10%' }}>{new Date(expense.date).toLocaleDateString()}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </div>
                ))}

                {/* Success message */}
                {showSuccessMessage && (
                    <div className="successMessage">
                        Successfully deleted
                    </div>
                )}

                {/* Buttons Section */}
                <div className="expenseButtons">
                    <button className="addExpenseButton" onClick={handleAddClick}>
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
                    onClose={handleCloseModal} 
                    onAddExpense={editingExpense ? handleEditExpense : handleAddExpense}
                    editingExpense={editingExpense} // Pass selected expense for editing
                />

                {/* Bar Chart Section */}
                <div className="expenseChart">
                    <Bar data={chartData} options={chartOptions} />
                </div>
            </div>
        </div>
    );
}

export default Expenses;