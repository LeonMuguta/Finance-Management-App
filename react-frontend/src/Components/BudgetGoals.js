import React, { useEffect, useState } from "react";
import axios from "axios";
import Sidebar from "./Sidebar";
import TopNav from "./TopNav";
import AddBudgetModal from "./AddBudgetModal";
import "../Styling/BudgetGoals.css";

function Budget() {
    const [budgets, setBudgets] = useState([]);
    const [userId, setUserId] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false); // State to control modal visibility
    const [editingBudget, setEditingBudget] = useState(null); // State to hold expense data for editing
    const [showSuccessMessage, setShowSuccessMessage] = useState(false); // State to control the success message visibility
    const [windowWidth, setWindowWidth] = React.useState(window.innerWidth);

    // Fetch user ID from localStorage when the component mounts
    useEffect(() => {
        const storedUserId = localStorage.getItem('id'); // Assuming userId is stored in localStorage
        if (storedUserId) {
            setUserId(parseInt(storedUserId, 10)); // Convert userId to an integer
        }
    }, []);

    // Fetch budget goals for the logged-in user when userId is available
    useEffect(() => {
        if (userId) {
            const fetchBudgetGoals = async () => {
                try {
                    const response = await axios.get(`http://localhost:8080/budget/user/${userId}`);
                    setBudgets(response.data);
                } catch (error) {
                    console.error("Error fetching budget goals:", error);
                }
            };

            fetchBudgetGoals();
        }
    }, [userId]); // Dependency on userId, fetch data once it's available

    // Sort budgets by date in descending order (most recent first)
    const sortedBudgets = [...budgets].sort((a, b) => new Date(b.id) - new Date(a.id));

    // Function to handle adding a budget goal
    const handleAddBudget = async (newBudget) => {
        try {
            const response = await axios.post('http://localhost:8080/budget', {
                ...newBudget,
                user: { id: userId } // Include user ID in the request
            });
            console.log(response.data); // Log the response (optional)
            setBudgets((prevBudgets) => [...prevBudgets, newBudget]); // Update the state
        } catch (error) {
            if (error.response) {
                throw error.response.data;  // Return server validation error
            } else {
                // eslint-disable-next-line
                throw 'An error occurred while creating the budget.';
            }            
        }
    };

    // Function to handle editing a budget goal
    const handleEditBudget = async (editedBudget) => {
        try {
            const response = await axios.put(`http://localhost:8080/budget/${editingBudget.id}`, {
                ...editedBudget,
                user: { id: userId }
            });
            // Update the state with the updated budget
            setBudgets((prevBudgets) => 
                prevBudgets.map(bud => bud.id === editingBudget.id ? response.data : bud)
            );
            setEditingBudget(null); // Reset editing budget
        } catch (error) {
            if (error.response) {
                throw error.response.data;  // Return server validation error
            } else {
                // eslint-disable-next-line
                throw 'An error occurred while creating the budget.';
            }            
        }
    };

    // Open the modal to add new expense (ensure fields are reset)
    const handleAddClick = () => {
        setEditingBudget(null); // Reset editingExpense to ensure the form is empty
        setIsModalOpen(true); // Open the modal
    };

    // Open the modal to edit the selected expense
    const handleEditClick = (budget) => {
        setEditingBudget(budget); // Set the expense to be edited
        setIsModalOpen(true); // Open the modal
    };

    // Handle delete action
    const handleDelete = async (id) => {
        // Confirm the user wants to delete the budget goal
        if (window.confirm('Are you sure you want to delete? This action cannot be undone.')) {
            try {
                await axios.delete(`http://localhost:8080/budget/${id}`);
                setBudgets(sortedBudgets.filter((budget) => budget.id !== id)); // Update UI after delete
                
                console.log("Successfully deleted the budget goal");

                // Show success message and auto-hide after 3 seconds
                setShowSuccessMessage(true);
                setTimeout(() => {
                    setShowSuccessMessage(false);
                }, 3000); // Hide message after 3 seconds
            } catch (error) {
                console.error("Error deleting budget goal:", error);
                alert("Failed to delete budget goal");
            }
        }
    };

    // Modify the onClose function to unselect the selected expense
    const handleCloseModal = () => {
        setIsModalOpen(false);
        setEditingBudget(null);
    };

    React.useEffect(() => {
        const handleResize = () => {
            setWindowWidth(window.innerWidth);
        };

        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    return (
        <div className="budget-container">
            {/* Conditional rendering of Sidebar or TopNav */}
            {windowWidth > 650 ? <Sidebar /> : <TopNav />}

            {/* Right Content Area */}
            <div className="budget-content">
                <h2>Your Monthly Budget Goals</h2>

                <div className="budget-list">
                    {sortedBudgets.length > 0 ? (
                        sortedBudgets.map((budget, index) => (
                            <div key={index} className="budget-item">
                                <i 
                                    className="fa fa-pencil edit-icon" 
                                    onClick={() => handleEditClick(budget)}
                                ></i>
                                <h3><strong>{budget.month} {budget.year}</strong></h3>
                                <p>Minimum Revenue Goal: <strong>R{budget.minRevenue}</strong></p>
                                <p>Maximum Expense Goal: <strong>R{budget.maxExpense}</strong></p>
                                <p>Net Balance Goal: <strong>R{budget.netBalanceGoal}</strong></p>

                                {/* Delete icon */}
                                <i
                                    className="fa fa-trash delete-icon"
                                    onClick={() => handleDelete(budget.id)}
                                    title="Delete Budget Goal"
                                ></i>
                            </div>
                        ))
                    ) : (
                        <p>No budget goals found. Create one to get started!</p>
                    )}
                </div>

                {/* Success message */}
                {showSuccessMessage && (
                    <div className="successMessage">
                        Successfully deleted
                    </div>
                )}

                {/* Buttons Section */}
                <div className="budgetButtons">
                    <button className="addBudgetButton" onClick={handleAddClick}>
                        <i className="fa fa-plus"></i> Add
                    </button>
                </div>

                {/* Add Budget Modal */}
                <AddBudgetModal
                    isOpen={isModalOpen}
                    onClose={handleCloseModal}
                    onAddBudget={editingBudget ? handleEditBudget : handleAddBudget}
                    editingBudget={editingBudget}
                />
            </div>
        </div>
    );
}

export default Budget;
