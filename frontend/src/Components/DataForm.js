import React, { useState } from 'react';

const DataForm = ({ integrationId, schema }) => {
  const [formData, setFormData] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const payload = {
      timestamp: new Date().toISOString(),
      data: formData, // assuming 'data' is the nested object required by the API
    };

    try {
      const response = await fetch(`/api/v1/integration/${integrationId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const result = await response.json();
      console.log('Data submitted:', result);
    } catch (error) {
      console.error('Error submitting data:', error);
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
        <button className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline" type="submit">
          Submit
        </button>
      </div>
    </form>
  );
};

export default DataForm;
