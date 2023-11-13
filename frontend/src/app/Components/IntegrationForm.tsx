"use client";

import React, { useState } from "react";

function IntegrationForm() {
  // State to store the form data
  const [formData, setFormData] = useState({
    name: "",
    type: "",
  });

  // Function to handle the form input changes
  const handleChange = (e: any) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  // Function to handle the form submission
  const handleSubmit = async (e: any) => {
    e.preventDefault();

    /*
      Access to fetch at 'http://localhost/api/v1/integration' from origin 'http://localhost:3000' has been blocked by CORS policy: Response to preflight request doesn't pass access control check: No 'Access-Control-Allow-Origin' header is present on the requested resource. If an opaque response serves your needs, set the request's mode to 'no-cors' to fetch the resource with CORS disabled.
    */
    try {
      const response = await fetch("http://localhost/api/v1/integrations", {
        method: "POST",
        mode: "cors", // See comment above
        credentials: "omit",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name: formData.name,
          title: formData.type,
        }),
      });
      const data = await response.json();
      // Handle success response
      console.log("Success:", data);
    } catch (error) {
      // Handle errors here
      console.error("Error:", error);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <label className="mx-2" htmlFor="name">
        Name:
        <input
          className="w-full rounded-3xl mb-3 text-black py-1 px-2"
          type="text"
          name="name"
          value={formData.name}
          onChange={handleChange}
        />
      </label>
      <label htmlFor="type">
        Type:
        <input
          className="w-full rounded-3xl mb-3 text-black py-1 px-2"
          type="text"
          name="type"
          value={formData.type}
          onChange={handleChange}
        />
      </label>
      <button
        className="bg-sky-50 bg-opacity-50 hover:bg-blue-900 text-white font-bold py-2 px-4 rounded-full mb-5"
        type="submit"
      >
        Submit
      </button>
    </form>
  );
}

export default IntegrationForm;
