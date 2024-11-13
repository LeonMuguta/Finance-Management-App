import React, { useState } from 'react';
import './App.css';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Welcome from './Components/Welcome';
import SignUp from './Components/SignUp';
import Login from './Components/Login';
import VerifyCode from './Components/VerifyCode';
import Success from './Components/Success';
import Home from './Components/Home';
import Revenues from './Components/Revenues';
import Expenses from './Components/Expenses';
import Profile from './Components/Profile';
import Budget from './Components/BudgetGoals';
import Report from './Components/Report';
import SessionTimeout from './Components/SessionTimeout';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  return (
    <Router>
      {isAuthenticated && <SessionTimeout setIsAuthenticated={setIsAuthenticated} />}
      <Routes>
        <Route path="/" element={ <Welcome /> } />
        <Route path="/signup" element={ <SignUp /> } />
        <Route path="/login" element={ <Login setIsAuthenticated={setIsAuthenticated} /> } />
        <Route path="/verify" element={ <VerifyCode setIsAuthenticated={setIsAuthenticated} /> } />
        <Route path="/success" element={ <Success /> } />
        <Route path="/home" element={ <Home setIsAuthenticated={setIsAuthenticated} /> } />
        <Route path="/revenues" element={ <Revenues setIsAuthenticated={setIsAuthenticated} /> } />
        <Route path="/expenses" element={ <Expenses setIsAuthenticated={setIsAuthenticated} /> } />
        <Route path="/profile" element={ <Profile setIsAuthenticated={setIsAuthenticated} /> } />
        <Route path="/budget" element={ <Budget setIsAuthenticated={setIsAuthenticated} /> } />
        <Route path="/reports" element={ <Report setIsAuthenticated={setIsAuthenticated} /> } />
      </Routes>
    </Router>
  );
}

export default App;
