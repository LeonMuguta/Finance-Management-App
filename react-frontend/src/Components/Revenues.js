import React, { useEffect, useState } from "react";
import axios from "axios";
import Sidebar from "./Sidebar";
import TopNav from "./TopNav";
import AddRevenueModal from "./AddRevenueModal";
import '../Styling/Revenues.css';

function Revenues() {
    const [revenues, setRevenues] = useState([]); // State to hold fetched revenue data
    const [userId, setUserId] = useState(null); // State to hold logged-in user ID
    const [isModalOpen, setIsModalOpen] = useState(false); // State to control modal visibility
    const [selectedRevenues, setSelectedRevenues] = useState([]); // State to hold selected revenue IDs
    const [editingRevenue, setEditingRevenue] = useState(null); // State to hold revenue data for editing
    const [showSuccessMessage, setShowSuccessMessage] = useState(false); // State to control the success message visibility
    const [windowWidth, setWindowWidth] = React.useState(window.innerWidth);

    // Fetch user ID from localStorage when the component mounts
    useEffect(() => {
        const storedUserId = localStorage.getItem('id'); // Assuming userId is stored in localStorage
        if (storedUserId) {
            setUserId(parseInt(storedUserId, 10)); // Convert userId to an integer
        }
    }, []);

    // Fetch revenue data for the logged-in user when userId is available
    useEffect(() => {
        if (userId) {
            const fetchRevenues = async () => {
                try {
                    const response = await axios.get(`http://localhost:8080/revenues/user/${userId}`);
                    setRevenues(response.data); // Store user-specific revenue data in state
                } catch (error) {
                    console.error('Error fetching revenues: ', error);
                }
            };

            fetchRevenues();
        }
    }, [userId]); // Dependency on userId, fetch data once it's available

    // Sort revenues by date in descending order (most recent first)
    const sortedRevenues = [...revenues].sort((a, b) => new Date(b.date) - new Date(a.date));

    // Function to handle adding revenue
    const handleAddRevenue = async (newRevenue) => {
        try {
            const response = await axios.post('http://localhost:8080/revenues', {
                ...newRevenue,
                user: { id: userId } // Include user ID in the request
            });
            console.log(response.data);
            setRevenues((prevRevenues) => [...prevRevenues, newRevenue]); // Update the state
        } catch (error) {
            if (error.response) {
                throw error.response.data;  // Return server validation error
            } else {
                // eslint-disable-next-line
                throw 'An error occurred while creating revenue.';
            }            
        }
    };

    // Handle edit revenue
    const handleEditRevenue = async (editedRevenue) => {
        try {
            const response = await axios.put(`http://localhost:8080/revenues/${editingRevenue.id}`, {
                ...editedRevenue,
                user: { id: userId }
            });
            // Update the state with the updated revenue
            setRevenues((prevRevenues) => 
                prevRevenues.map(rev => rev.id === editingRevenue.id ? response.data : rev)
            );
            setEditingRevenue(null); // Reset editing revenue
        } catch (error) {
            console.error('Error updating revenue: ', error);
        }
    };

    // Handle checkbox toggle
    const handleCheckboxChange = (revenueId) => {
        setSelectedRevenues((prevSelected) => {
            if (prevSelected.includes(revenueId)) {
                // Remove the revenueId if already selected
                return prevSelected.filter(id => id !== revenueId);
            } else {
                // Add the revenueId to the selected array
                return [...prevSelected, revenueId];
            }
        });
    };

    // Open the modal to add new revenue (ensure fields are reset)
    const handleAddClick = () => {
        setEditingRevenue(null); // Reset editingRevenue to ensure the form is empty
        setIsModalOpen(true); // Open the modal
    };

    // Open the modal to edit the selected revenue
    const handleEditClick = () => {
        const revenueToEdit = revenues.find(revenue => revenue.id === selectedRevenues[0]);
        setEditingRevenue(revenueToEdit); // Set the revenue to be edited
        setIsModalOpen(true); // Open the modal
    };

    // Function to handle removing selected revenues
    const handleRemoveSelected = async () => {
        try {
            // Loop through selected revenues and send a DELETE request for each
            await Promise.all(
                selectedRevenues.map(async (id) => {
                    await axios.delete(`http://localhost:8080/revenues/${id}`);
                })
            );

            console.log("Successfully deleted the selected revenue transactions");

            // After successful deletion, update the state to remove the deleted revenues
            setRevenues((prevRevenues) =>
                prevRevenues.filter((revenue) => !selectedRevenues.includes(revenue.id))
            );
            setSelectedRevenues([]); // Clear the selected revenues after deletion

            // Show success message and auto-hide after 3 seconds
            setShowSuccessMessage(true);
            setTimeout(() => {
                setShowSuccessMessage(false);
            }, 3000); // Hide message after 3 seconds

        } catch (error) {
            console.error('Error deleting revenues:', error);
        }
    };

    // Modify the onClose function to unselect the selected revenue
    const handleCloseModal = () => {
        setIsModalOpen(false);
        setEditingRevenue(null);
        setSelectedRevenues([]); // Unselect any selected revenues
    };

    React.useEffect(() => {
        const handleResize = () => {
            setWindowWidth(window.innerWidth);
        };

        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    return (
        <div className="revenuesContainer">
            {/* Conditional rendering of Sidebar or TopNav */}
            {windowWidth > 650 ? <Sidebar /> : <TopNav />}

            {/* Right Content Area */}
            <div className="revenuesContent">
                <h2>Revenues</h2>
                
                {/* Revenue Table Section */}
                <div className="revenuesTableContainer">
                    <table className="revenuesTable">
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
                            {sortedRevenues.length > 0 ? (
                                sortedRevenues.map((revenue) => (
                                    <tr key={revenue.id} className={selectedRevenues.includes(revenue.id) ? 'selectedRow' : ''}>
                                        <td>
                                            <input 
                                                type="checkbox" 
                                                checked={selectedRevenues.includes(revenue.id)} 
                                                onChange={() => handleCheckboxChange(revenue.id)} 
                                            />
                                        </td>
                                        <td>R{revenue.amount}</td>
                                        <td>{revenue.category}</td>
                                        <td>{revenue.description}</td>
                                        <td>{revenue.isRecurring ? 'Yes' : 'No'}</td>
                                        <td>{new Date(revenue.date).toLocaleDateString()}</td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="5">No revenues to display</td>
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
                <div className="revenueButtons">
                    <button className="addRevenueButton" onClick={handleAddClick}>
                        <i className="fa fa-plus"></i> Add
                    </button>
                    <button 
                        className={`removeRevenueButton ${selectedRevenues.length === 0 ? 'disabledButton' : ''}`} 
                        disabled={selectedRevenues.length === 0} // Disable if no revenue is selected
                        onClick={handleRemoveSelected} // Call the remove function
                    >
                        <i className="fa fa-trash"></i> Remove
                    </button>
                    <button 
                        className={`editRevenueButton ${selectedRevenues.length !== 1 ? 'disabledButton' : ''}`} 
                        disabled={selectedRevenues.length !== 1} // Disable if no revenue is selected
                        onClick={handleEditClick} // Display the edit form once button is clicked
                    >
                        <i className="fa fa-pencil"></i> Edit
                    </button>
                </div>

                {/* Add Revenue Modal */}
                <AddRevenueModal 
                    isOpen={isModalOpen} 
                    onClose={handleCloseModal} 
                    onAddRevenue={editingRevenue ? handleEditRevenue : handleAddRevenue}
                    editingRevenue={editingRevenue} // Pass selected revenue for editing
                />
            </div>
        </div>
    );
}

export default Revenues;