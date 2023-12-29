// Logout.js
import React, { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';

const LogOut = () => {
  const nav=useNavigate();
  useEffect(()=>{
    console.log("Entered logout");
    sessionStorage.setItem("role","login");
    nav("/")
    window.location.reload();
  });
  return (
    <div>
     
    </div>
  );
};

export default LogOut;
