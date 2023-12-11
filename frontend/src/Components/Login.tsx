import React, { useEffect, useState } from "react";
import { Link, Router } from "react-router-dom";

function Login() {

  const [loginInfo, setLoginInfo] = useState({
    username: "",
    password: "",
  });

  const handleChange = (e: any) => {
    e.preventDefault();
    const { name, value } = e.target;
    setLoginInfo({...loginInfo,  [name]: value,});
  };

  const loginSubmit = async (e: any) => {
    e.preventDefault();
    console.log(loginInfo);

  }

    return (
        <form className="relative" onSubmit={loginSubmit}>
          <input id="username" className="w-full bg-transparent mb-4 text-black py-1 px-2 border-solid border-b-2 border-green-700 focus:outline-none" 
            name="username"
            onChange={handleChange}
            placeholder="Username"/>
          <label className="mx-2 py-1 text-black left-0 absolute" htmlFor="username">Username </label>
          <input id="password" className="w-full bg-transparent mb-4 text-black py-1 px-2 border-solid border-b-2 border-green-700 focus:outline-none" 
            name="password"
            onChange={handleChange}
            placeholder="Password" />
          <label className="mx-2 py-1 text-black left-0 absolute" htmlFor="password">Password</label>
          <button className="bg-green-700 bg-opacity-80 hover:bg-green-900 text-white font-bold py-2 px-4 rounded-full mb-5" 
            type="submit">Sign in
          </button>
        </form>
    );

}
export {Login}; 