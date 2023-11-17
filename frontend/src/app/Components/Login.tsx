"use client";

import React, { useEffect, useState } from "react";

function Login() {

    return (
        <form className="relative ">
          <input id="username" className="w-full bg-transparent mb-4 text-black py-1 px-2 border-solid border-b-2 border-green-700 focus:outline-none" placeholder="Username"/>
          <label className="mx-2 py-1 text-black left-0 absolute" htmlFor="username">Username</label>
          <input id="password" className="w-full bg-transparent mb-4 text-black py-1 px-2 border-solid border-b-2 border-green-700 focus:outline-none" type="password" placeholder="Password" />
          <label className="mx-2 py-1 text-black left-0 absolute" htmlFor="password">Password</label>
          <button className="bg-green-700 bg-opacity-80 hover:bg-green-900 text-white font-bold py-2 px-4 rounded-full mb-5" 
            type="submit">Sign in
          </button>
        </form>
    );

    
}

export default Login;
