import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';

function Header() {
  const [navCollapse, setNavCollapse] = useState(false);
  var user=sessionStorage.getItem("userId");
  return (
    <nav className='navbar navbar-expand-lg navbar-dark bg-dark'>
      <div className='container'>
        <NavLink to="/" className='navbar-brand'>
          <img src="exam3.png" alt="Logo" style={{ width: 160 }} />
        </NavLink>

        <button
          className='navbar-toggler'
          type='button'
          onClick={() => setNavCollapse(!navCollapse)}
        >
          <span className='navbar-toggler-icon'></span>
        </button>

        <div className={`collapse navbar-collapse ${navCollapse ? '' : 'show'}`}>
          <div className='navbar-nav'>
            <NavLink to="/dashboard" className='nav-link'>
              <i className='bi bi-speedometer2'></i> Dashboard
            </NavLink>

            <NavLink to="/report" className='nav-link'>
              <i className='bi bi-chat-square-text'></i> Report
            </NavLink>

            <NavLink to="/logout" className='nav-link'>
              <i className='bi bi-power'></i> Logout
            </NavLink>
          </div>
        </div>

        <h4 className='ml-auto' style={{ color: 'white', marginRight: 12, width:200 }}>
          {user}
        </h4>
      </div>
    </nav>
  );
}

export default Header;
