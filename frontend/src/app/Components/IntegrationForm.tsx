"use client";

import React, { useState } from "react";

function IntegrationForm() {
  // State to store the form data
  const [formData, setFormData] = useState({
    id: "",
    title: "",
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
    try {
      const response = await fetch("http://localhost/api/v1/integration", {
        method: "POST",
        mode: "cors",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          id: formData.id,
          title: formData.title,
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
<<<<<<< HEAD
      <div>
        <label htmlFor="id">ID:</label>
        <input
          type="text"
          id="id"
          name="id"
          value={formData.id}
          onChange={handleChange}
        />
      </div>
      <div>
        <label htmlFor="title">Title:</label>
        <input
          type="text"
          id="title"
          name="title"
          value={formData.title}
          onChange={handleChange}
        />
      </div>
      <button type="submit">Submit</button>
=======
        <label className="mx-2" htmlFor="id">ID:
          <input className="w-full rounded-3xl mb-3 text-black py-1 px-2"
            type="text"
            id="id"
            name="id"
            value={formData.id}
            onChange={handleChange}
          />
        </label>
        <label htmlFor="title">Title:
          <input className="w-full rounded-3xl mb-3 text-black py-1 px-2"
            type="text"
            id="title"
            name="title"
            value={formData.title}
            onChange={handleChange}
          />
        </label>
      <button className="bg-sky-50 bg-opacity-50 hover:bg-blue-900 text-white font-bold py-2 px-4 rounded-full mb-5" 
        type="submit">Submit
      </button>
>>>>>>> parent of be5ab45 (Merge branch 'frontend-getandpost' of https://github.com/Thomass1412/P3-BM-dashboard into frontend-getandpost)
    </form>
  );
}

export default IntegrationForm;
