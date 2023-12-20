import React from 'react';
import { Navigate } from 'react-router-dom';
import Cookies from 'js-cookie';

const withAuth = (Component) => {
  return (props) => {
    const isAuth = Cookies.get('login'); // Replace 'login' with your cookie name
    
    if (!isAuth) {
      // If the cookie is not present, redirect to login
      return <Navigate to="/login" />;
    }

    // If the cookie is present, render the component
    return <Component {...props} />;
  };
};

export default withAuth;