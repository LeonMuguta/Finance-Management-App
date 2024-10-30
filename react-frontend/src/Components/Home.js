import React, { useEffect, useState } from "react";
import axios from "axios";
import Sidebar from "./Sidebar";
import TopNav from "./TopNav";
import { Pie } from "react-chartjs-2";
import { Chart, ArcElement, Tooltip, Legend } from "chart.js";
import '../Styling/Home.css';

// Register the necessary Chart.js components
Chart.register(ArcElement, Tooltip, Legend);

function Home() {
    const [firstName, setFirstName] = useState('');
    const [surname, setSurname] = useState('');
    const [totalRevenue, setTotalRevenue] = useState(0);
    const [totalExpense, setTotalExpense] = useState(0);
    const [transactions, setTransactions] = useState([]);
    const [goalData, setGoalData] = useState(null);
    const [userId, setUserId] = useState(null);
    const [windowWidth, setWindowWidth] = React.useState(window.innerWidth);

    useEffect(() => {
        const storedFirstName = localStorage.getItem('firstName');
        const storedSurname = localStorage.getItem('surname');
        const storedUserId = localStorage.getItem('id');

        if (storedFirstName) {
            setFirstName(storedFirstName);
        }
        if (storedSurname) {
            setSurname(storedSurname)
        }
        if (storedUserId) {
            setUserId(parseInt(storedUserId, 10));
        }
    }, []);

    // Fetch and calculate total revenue for the current month
    useEffect(() => {
        const fetchMonthlyTotalRevenue = async () => {
            if (userId) {
                try {
                    const response = await axios.get(`http://localhost:8080/revenues/user/${userId}`);
                    const revenues = response.data;

                    const currentMonth = new Date().getMonth();
                    const totalForMonth = revenues
                        .filter((revenue) => new Date(revenue.date).getMonth() === currentMonth)
                        .reduce((total, revenue) => total + revenue.amount, 0);

                    setTotalRevenue(totalForMonth);
                } catch (error) {
                    console.error("Error fetching revenues: ", error);
                }
            }
        };

        fetchMonthlyTotalRevenue();
    }, [userId]);

    // Fetch and calculate total expenses for the current month
    useEffect(() => {
        const fetchMonthlyTotalExpenses = async () => {
            if (userId) {
                try {
                    const response = await axios.get(`http://localhost:8080/expenses/user/${userId}`);
                    const expenses = response.data;

                    const currentMonth = new Date().getMonth();
                    const totalForMonth = expenses
                        .filter((expense) => new Date(expense.date).getMonth() === currentMonth)
                        .reduce((total, expense) => total + expense.amount, 0);

                    setTotalExpense(totalForMonth);
                } catch (error) {
                    console.error("Error fetching expenses: ", error);
                }
            }
        };

        fetchMonthlyTotalExpenses();
    }, [userId]);

    // Calculate the net balance
    const netBalance = totalRevenue - totalExpense;

    // Fetch all transactions to display summary (Of the current month) on home page 
    useEffect(() => {
        const currentMonth = new Date().getMonth() + 1; // Get current month (January = 0)

        // Fetch total revenue and expense for the user
        const fetchData = async () => {
            if (userId) {
                try {
                    // Fetch revenues for the current month
                    const revenueResponse = await axios.get(`http://localhost:8080/revenues/user/${userId}`);
                    const currentMonthRevenues = revenueResponse.data.filter(revenue => new Date(revenue.date).getMonth() + 1 === currentMonth);
                    const totalRevenues = currentMonthRevenues.reduce((acc, revenue) => acc + revenue.amount, 0);
                    setTotalRevenue(totalRevenues);

                    // Fetch expenses for the current month
                    const expenseResponse = await axios.get(`http://localhost:8080/expenses/user/${userId}`);
                    const currentMonthExpenses = expenseResponse.data.filter(expense => new Date(expense.date).getMonth() + 1 === currentMonth);
                    const totalExpenses = currentMonthExpenses.reduce((acc, expense) => acc + expense.amount, 0);
                    setTotalExpense(totalExpenses);

                    // Combine revenues and expenses into one list
                    const mergedTransactions = [
                        ...currentMonthRevenues.map(revenue => ({ ...revenue, type: 'Revenue' })),
                        ...currentMonthExpenses.map(expense => ({ ...expense, type: 'Expense' }))
                    ];

                    // Sort by date in descending order (most recent first)
                    const sortedTransactions = mergedTransactions.sort((a, b) => new Date(b.date) - new Date(a.date));

                    // Store transactions in state
                    setTransactions(sortedTransactions);

                } catch (error) {
                    console.error('Error fetching data:', error);
                }
            }
        };

        fetchData();
    }, [userId]);

    // Fetch goal data for the dashboard
    useEffect(() => {
        const fetchGoalData = async () => {
            if (userId) {
                try {
                    const response = await axios.get(`http://localhost:8080/budget/user/${userId}`);
                    
                    // Get the current month
                    const currentMonth = new Date().toLocaleString('default', { month: 'long' }).toUpperCase();

                    // Filter the goal data for the current month
                    const filteredGoals = response.data.filter(goal => goal.month === currentMonth);

                    setGoalData(filteredGoals[0]);
                } catch (error) {
                    console.error("Error fetching goal data: ", error);
                }
            }
        };

        fetchGoalData();
    }, [userId]);

    React.useEffect(() => {
        const handleResize = () => {
            setWindowWidth(window.innerWidth);
        };

        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    return (
        <div className="homeContainer">
            {/* Conditional rendering of Sidebar or TopNav */}
            {windowWidth > 650 ? <Sidebar /> : <TopNav />}

            {/* Right Content Area */}
            <div className="content">
                <h2>Welcome, {firstName} {surname}!</h2>
                
                {/* Placeholder for additional content */}
                <div className="summarySection">
                    <h3>Dashboard Summary</h3>
                    <div className="dashboard">
                        <div className="summary">
                            <h4>
                                <i className="fa fa-arrow-up" aria-hidden="true"></i>
                                Total Revenues (Current Month)
                            </h4>
                            <p className="totalRevenue">R{totalRevenue.toLocaleString()}</p>
                        </div>
                        <div className="summary">
                            <h4>
                                <i className="fa fa-arrow-down" aria-hidden="true"></i>
                                Total Expenses (Current Month)
                            </h4>
                            <p className="totalExpense">R{totalExpense.toLocaleString()}</p>
                        </div>
                        <div className="summary">
                            <h4>
                                <i className="fa fa-check-circle" aria-hidden="true"></i>
                                Net Balance
                            </h4>
                            <p className={netBalance > 0 ? 'positiveBalance' : netBalance < 0 ? 'negativeBalance' : 'zeroBalance'}>
                                R{netBalance.toLocaleString()}
                            </p>
                        </div>
                    </div>
                </div>

                {/* Goal Section */}
                <div className="goalsSection">
                    <h3>Monthly Goals</h3>
                    {goalData ? (
                        <div className="goalsSummary">
                            <p className={goalData.minRevenue < totalRevenue ? 'positiveAmount' : 'negativeAmount'}><i className="fa fa-bullseye" aria-hidden="true"></i> Revenue Goal: <strong>R{goalData.minRevenue}</strong></p>
                            <p className={goalData.maxExpense > totalExpense ? 'positiveAmount' : 'negativeAmount'}><i className="fa fa-bullseye" aria-hidden="true"></i> Expense Limit: <strong>R{goalData.maxExpense}</strong></p>
                            <p className={goalData.netBalanceGoal < netBalance ? 'positiveAmount' : 'negativeAmount'}><i className="fa fa-bullseye" aria-hidden="true"></i> Net Balance Goal: <strong>R{goalData.netBalanceGoal}</strong></p>
                        </div>
                    ) : (
                        <p>No budget goals set for the current month...</p>
                    )}
                </div>

                {/* New Section for Transactions and Monthly Statistics */}
                <div className="transactionsStatsSection">
                    <div className="transactions">
                        <h4>Transactions</h4>
                        <br/>
                        {transactions.length > 0 ? (
                            <table className="transactionTable">
                                <thead>
                                    <tr>
                                        <th>Date</th>
                                        <th>Description</th>
                                        <th>Amount</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {transactions.map((transaction) => (
                                        <tr key={transaction.id}>
                                            <td>{new Date(transaction.date).toLocaleDateString()}</td>
                                            <td>{transaction.description}</td>
                                            <td className={transaction.type === 'Revenue' ? 'positiveAmount' : 'negativeAmount'}>
                                                R{transaction.amount.toLocaleString()}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        ) : (
                            <p>No transactions for the current month</p>
                        )}
                    </div>
                    <div className="monthlyStatistics">
                        <h4>Monthly Statistics</h4>
                        <br/>
                        <Pie
                            data={{
                                labels: ['Total Revenue', 'Total Expenses'],
                                datasets: [{
                                    data: [totalRevenue, totalExpense],
                                    backgroundColor: ['#36A2EB', '#FF6384'],
                                }],
                            }}
                            options={{
                                responsive: true,
                                plugins: {
                                    legend: {
                                        position: 'top',
                                    },
                                    title: {
                                        display: true,
                                        text: 'Revenue vs Expenses',
                                    },
                                },
                            }}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Home;