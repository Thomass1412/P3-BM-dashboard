import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function Login() {
  const [loginInfo, setLoginInfo] = useState({
    username: "",
    password: "",
  });
  const [loginFailed, setLoginFailed] = useState(false)

  const navigate = useNavigate();

  const handleChange = (e) => {
    e.preventDefault();
    const { name, value } = e.target;
    setLoginInfo({ ...loginInfo, [name]: value });
    setLoginFailed(false);
  };

  const loginSubmit = async (e) => {
    e.preventDefault();
    console.log(loginInfo);
    try {
      const response = await fetch("http://localhost/auth/login", {
        method: "POST",
        mode: "cors", // See comment above
        credentials: "omit",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: loginInfo.username,
          password: loginInfo.password,
        }),
      });
      if (!response.ok) {
        // Check for non-2xx status codes
        setLoginFailed(true);
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      const data = await response.json();
      // Handle success response
      console.log(`HTTP status: ${response.status}`);
      console.log("Success:", data.token);

      navigate("/supervisor")
      
    } catch (error) {
      // Handle errors here
      console.error("Error:", error);
      setLoginFailed(true);
    }

  };

  return (
    <div>
      
      <form className={`relative `} onSubmit={loginSubmit}>
          <div className={`${loginFailed ? "opacity-100" : "opacity-0"} transition-opacity duration-1000 ease-in-out flex flex-row items-center justify-end w-full text-red-600  `}>
            <h1 className="">Wrong email or password</h1>
          </div>
        <input
          id="username"
          className="w-full bg-transparent mb-4 text-black py-1 px-2 border-solid border-b-2 border-green-700 focus:outline-none"
          name="username"
          onChange={handleChange}
          placeholder="Username"
        />
        <label className="mx-2 py-1 text-black left-0 absolute" htmlFor="username">
          Username{" "}
        </label>
        <input
          id="password"
          className="w-full bg-transparent mb-4 text-black py-1 px-2 border-solid border-b-2 border-green-700 focus:outline-none"
          name="password"
          onChange={handleChange}
          placeholder="Password"
          type="password"
        />
        <label className="mx-2 py-1 text-black left-0 absolute" htmlFor="password">
          Password
        </label>
        <button
          className="bg-green-700 bg-opacity-80 hover:bg-green-900 text-white font-bold py-2 px-4 rounded-full "
          type="submit">
          Sign in
        </button>
        
      </form>
      
    </div>
  );
}

export default Login;
