import React, { useState } from 'react';
import './App.css';
import { BrowserRouter as Router, Route, Routes, Navigate  } from 'react-router-dom';
import Welcome from './Components/Welcome';
import SignUp from './Components/SignUp';
import Login from './Components/Login';
import VerifyCode from './Components/VerifyCode';
// import Success from './Components/Success';
import Home from './Components/Home';
import Revenues from './Components/Revenues';
import Expenses from './Components/Expenses';
import Profile from './Components/Profile';
import Budget from './Components/BudgetGoals';
import Report from './Components/Report';
import SessionTimeout from './Components/SessionTimeout';

// ProtectedRoute Component
const ProtectedRoute = ({ children, setIsAuthenticated }) => {
  const isAuthenticated = localStorage.getItem('isAuthenticated') === 'true'; // Or use state/context

  return isAuthenticated ? children : <Navigate to="/login" />;
};

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(localStorage.getItem('isAuthenticated') === 'true');

  return (
    <Router>
      {isAuthenticated && <SessionTimeout setIsAuthenticated={setIsAuthenticated} />}
      <Routes>
        <Route path="/" element={<Welcome />} />
        <Route path="/signup" element={<SignUp />} />
        <Route path="/login" element={<Login setIsAuthenticated={setIsAuthenticated} />} />
        <Route path="/verify" element={<VerifyCode setIsAuthenticated={setIsAuthenticated} />} />
        {/* <Route path="/success" element={<Success />} /> */}

        {/* Protected Routes */}
        <Route
          path="/home"
          element={
            <ProtectedRoute setIsAuthenticated={setIsAuthenticated}>
              <Home setIsAuthenticated={setIsAuthenticated} />
            </ProtectedRoute>
          }
        />
        <Route
          path="/revenues"
          element={
            <ProtectedRoute setIsAuthenticated={setIsAuthenticated}>
              <Revenues setIsAuthenticated={setIsAuthenticated} />
            </ProtectedRoute>
          }
        />
        <Route
          path="/expenses"
          element={
            <ProtectedRoute setIsAuthenticated={setIsAuthenticated}>
              <Expenses setIsAuthenticated={setIsAuthenticated} />
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <ProtectedRoute setIsAuthenticated={setIsAuthenticated}>
              <Profile setIsAuthenticated={setIsAuthenticated} />
            </ProtectedRoute>
          }
        />
        <Route
          path="/budget"
          element={
            <ProtectedRoute setIsAuthenticated={setIsAuthenticated}>
              <Budget setIsAuthenticated={setIsAuthenticated} />
            </ProtectedRoute>
          }
        />
        <Route
          path="/reports"
          element={
            <ProtectedRoute setIsAuthenticated={setIsAuthenticated}>
              <Report setIsAuthenticated={setIsAuthenticated} />
            </ProtectedRoute>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;
