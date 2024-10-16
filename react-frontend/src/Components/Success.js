import React from 'react';
import { Link } from 'react-router-dom';
import '../Styling/Success.css';

function Success() {
  return (
    <div className="successPage">
      <Link to="/home" className="backButton">Back</Link>
      <h1>SUCCESS!!!</h1>
    </div>
  );
}

export default Success;
