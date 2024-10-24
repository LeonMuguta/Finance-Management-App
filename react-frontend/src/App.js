import React from 'react';
import './App.css';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Welcome from './Components/Welcome';
import SignUp from './Components/SignUp';
import Login from './Components/Login';
import Success from './Components/Success';
import Home from './Components/Home';
import Revenues from './Components/Revenues';
import Expenses from './Components/Expenses';
import Profile from './Components/Profile';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={ <Welcome /> } />
        <Route path="/signup" element={ <SignUp /> } />
        <Route path="/login" element={ <Login /> } />
        <Route path="/success" element={ <Success /> } />
        <Route path="/home" element={ <Home /> } />
        <Route path="/revenues" element={ <Revenues /> } />
        <Route path="/expenses" element={ <Expenses /> } />
        <Route path="/profile" element={ <Profile /> } />
      </Routes>
    </Router>
  );
}

export default App;
