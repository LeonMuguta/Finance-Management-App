import React, { useEffect, useState } from "react";
import axios from "axios";
import Sidebar from "./Sidebar";
import TopNav from "./TopNav";
import AddRevenueModal from "./AddRevenueModal";
import { Bar } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import '../Styling/Revenues.css';

// Register necessary chart components
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

function Revenues() {
    const [revenues, setRevenues] = useState([]); // State to hold fetched revenue data
    const [userId, setUserId] = useState(null); // State to hold logged-in user ID
    const [isModalOpen, setIsModalOpen] = useState(false); // State to control modal visibility
    const [selectedRevenues, setSelectedRevenues] = useState([]); // State to hold selected revenue IDs
    const [editingRevenue, setEditingRevenue] = useState(null); // State to hold revenue data for editing
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

    // Group revenues by month and year
    const groupedRevenues = sortedRevenues.reduce((groups, revenue) => {
        const date = new Date(revenue.date);
        const monthYear = `${date.toLocaleString('default', { month: 'long' })} ${date.getFullYear()}`;
        if (!groups[monthYear]) {
            groups[monthYear] = [];
        }
        groups[monthYear].push(revenue);
        return groups;
    }, {});

    // Calculate total revenue for each month
    const monthlyTotals = Object.keys(groupedRevenues).map((monthYear) => {
        const total = groupedRevenues[monthYear].reduce((sum, revenue) => sum + revenue.amount, 0);
        return { monthYear, total };
    });

    // Sort monthlyTotals by date in ascending order
    monthlyTotals.sort((a, b) => new Date(a.monthYear) - new Date(b.monthYear));

    const chartData = {
        labels: monthlyTotals.map(item => item.monthYear),
        datasets: [
            {
                label: 'Total Revenue',
                data: monthlyTotals.map(item => item.total),
                backgroundColor: '#fff', // Light teal background
                borderColor: 'black',       // Dark teal border
                borderWidth: 1
            }
        ]
    };
    
    const chartOptions = {
        responsive: true,
        plugins: {
            legend: { position: 'top' },
            title: { display: true, text: 'Total Revenues per Month' }
        }
    };

    // Handle expanding/collapsing month sections
    const toggleMonth = (monthYear) => {
        setExpandedMonths((prevState) => ({
            ...prevState,
            [monthYear]: !prevState[monthYear],
        }));
    };

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
            if (error.response) {
                throw error.response.data;  // Return server validation error
            } else {
                // eslint-disable-next-line
                throw 'An error occurred while creating revenue.';
            }
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
                
                {/* Loop through grouped revenues by month */}
                {Object.keys(groupedRevenues).map((monthYear) => (
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
                                        {groupedRevenues[monthYear].map((revenue) => (
                                            <tr key={revenue.id} className={selectedRevenues.includes(revenue.id) ? 'selectedRow' : ''}>
                                                <td style={{ width: '5%' }}>
                                                    <input 
                                                        type="checkbox" 
                                                        checked={selectedRevenues.includes(revenue.id)} 
                                                        onChange={() => handleCheckboxChange(revenue.id)} 
                                                    />
                                                </td>
                                                <td style={{ width: '10%' }}>R{revenue.amount.toFixed(2)}</td>
                                                <td style={{ width: '20%' }}>{revenue.category}</td>
                                                <td style={{ width: '50%' }}>{revenue.description}</td>
                                                <td style={{ width: '5%' }}>{revenue.isRecurring ? 'Yes' : 'No'}</td>
                                                <td style={{ width: '10%' }}>{new Date(revenue.date).toLocaleDateString()}</td>
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

                {/* Bar Chart Section */}
                <div className="revenueChart">
                    <Bar data={chartData} options={chartOptions} />
                </div>
            </div>
        </div>
    );
}

export default Revenues;