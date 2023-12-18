import React, { useState } from 'react';

export default function IntegrationForm() {
  const [integrationName, setIntegrationName] = useState('');
  const [formFields, setFormFields] = useState([{ name: '', type: 'text' }]);

  const handleIntegrationNameChange = (event) => {
    setIntegrationName(event.target.value);
  };

  const handleFieldChange = (index, event) => {
    const values = [...formFields];
    values[index][event.target.name] = event.target.value;
    setFormFields(values);
  };

  const getUser = async() =>{
    
  }

  const handleSubmit = async (event) => {
    event.preventDefault();
    const payload = {
      userId: userid,
      name: integrationName,
      type: 'internal', // Adjust this if it should be dynamic as well
      schema: formFields,
    };

    try {
      const response = await fetch('http://localhost/api/v1/integration', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      console.log(data);
      // Handle response or redirect user
    } catch (error) {
      console.error(error);
      // Handle error
    }
  };

  const addField = () => {
    setFormFields([...formFields, { name: '', type: 'text' }]);
  };

  const removeField = (index) => {
    const values = [...formFields];
    values.splice(index, 1);
    setFormFields(values);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label htmlFor="integrationName" className="block text-sm font-medium text-gray-700">
          Integration Name
        </label>
        <input
          type="text"
          id="integrationName"
          name="integrationName"
          value={integrationName}
          onChange={handleIntegrationNameChange}
          className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
          placeholder="Enter integration name"
        />
      </div>

      {formFields.map((field, index) => (
        <div key={index} className="flex flex-col space-y-2">
          <label htmlFor={`field-${index}`} className="block text-sm font-medium text-gray-700">Data Field</label>
          <input
            type="text"
            name="name"
            value={field.name}
            onChange={(event) => handleFieldChange(index, event)}
            className="px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            placeholder="Field name"
          />
          <label htmlFor={`field-${index}`} className="block text-sm font-medium text-gray-700">Data Type</label>
          <select
            name="type"
            value={field.type}
            onChange={(event) => handleFieldChange(index, event)}
            className="px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
          >
            <option value="text">Text</option>
            <option value="date">Date</option>
            <option value="number">Number</option>
          </select>
          <button
            type="button"
            onClick={() => removeField(index)}
            className="px-4 py-1 text-sm font-medium text-white bg-red-600 rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
          >
            Delete Field
          </button>
        </div>
      ))}

      <div className="flex justify-between">
        <button
          type="button"
          onClick={addField}
          className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
        >
          Add Field
        </button>
        <button
          type="submit"
          className="px-4 py-2 text-sm font-medium text-white bg-green-600 rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2"
        >
          Submit
        </button>
      </div>
    </form>
  );
}
