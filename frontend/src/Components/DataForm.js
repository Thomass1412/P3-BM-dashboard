import React, { useState } from 'react';
import { jwtDecode } from "jwt-decode";

const DataForm = ({ integrationId, schema }) => {
  const [formData, setFormData] = useState({});
  const [name, setName] = useState();

  const getCookie= (name) => {
    let cookieArray = document.cookie.split(';'); // Split the cookie string into an array
    let cookieName = name + "="; // Create the cookie name with equals sign

    for (let cookie of cookieArray) {
        cookie = cookie.trim(); // Trim whitespace
        if (cookie.indexOf(cookieName) == 0) { // If the cookie's name is found at the beginning of the string
            return cookie.substring(cookieName.length, cookie.length); // Return the value of the cookie
        }
    }
    return ""; // Return empty string if the cookie is not found
  }

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const getNameId = async ()=>{
    try{
      const response = await fetch('http://localhost/api/v1/user/idByLogin/'+jwtDecode(document.cookie.split("=")[1]).sub, {
        method: 'GET',
          headers: {
            'Accept': 'application/json',
            'Authorization': 'Bearer '+ document.cookie.split("=")[1],
          },
        });

      if (!response.ok) {
        // Handle non-OK responses
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      const responseData = await response.json();
      return responseData.id;

    }catch(error) {
      console.error('Error submitting data:', error);
    }
    return "";
  }

  const handleSubmit = async (event) => {
    event.preventDefault();  
    try {
      const loginId = await getNameId();
      console.log("her "+loginId);
      const payload = {
        userId: loginId,
        timestamp: new Date().toISOString(),
        data: formData, // assuming 'data' is the nested object required by the API
      };
      console.log(JSON.stringify(payload))
      try {
        const response = await fetch(`http://localhost/api/v1/integration/${integrationId}`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer '+ document.cookie.split("=")[1],
          },
          body: JSON.stringify(payload),
        });
        console.log(JSON.stringify(payload))
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const result = await response.json();
        console.log('Data submitted:', result);
        console.log('Success: ', result);
        window.location.reload();
      } catch (error) {
        console.error('Error submitting data:', error);
      }
    } catch (error) {
      console.error('Error:', error);
    }
    
  };

  // Dynamically create form inputs based on the schema
  const renderFormFields = () => {
    if (!schema || !schema.properties || !schema.properties.data || !schema.properties.data.properties) {
      return <div>No form schema available</div>;
    }

    return Object.entries(schema.properties.data.properties).map(([fieldName, fieldSchema]) => {
      const inputType = fieldSchema.type === 'string' ? 'text' : fieldSchema.type;
      return (
        <div key={fieldName}>
          <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor={fieldName}>
            {fieldName}
          </label>
          <input
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id={fieldName}
            type={inputType}
            placeholder={fieldName}
            name={fieldName}
            onChange={handleChange}
          />
        </div>
      );
    });
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
      <h3 className="text-lg font-semibold mb-4">Submit New Data</h3>
      {renderFormFields()}
      <div className="flex items-center justify-between mt-4">
        <button className="bg-green-600 hover:bg-green-800 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline" type="submit">
          Submit
        </button>
      </div>
    </form>
  );
};

export default DataForm;
