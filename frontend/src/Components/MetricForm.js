import React, { useEffect, useState } from 'react';

const metricOperations = ["ADD", "SUBTRACT", "MULTIPLY", "DIVIDE", "COUNT"];

export default function IntegrationForm() {
  const [data, setData] = useState([]);
  const [integration, setIntegration] = useState([]);
  const [integrationSchema, setIntegrationSchema] = useState({});
  const [formFields, setFormFields] = useState([{ name: '', type: 'text' }]);

  const getData = async () => {
    try {
      const response = await fetch('http://localhost/api/v1/integrations/pageable?page=0&size=999', {
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
      setData(responseData.content);
      console.log('Success:', responseData);
    } catch (error) {
      const errorMessage = data.messages.join(', ');
    }
  };
  
  const handleFieldChange = async (event) => {
    try{
      const integrationResponse = await fetch(`http://localhost/api/v1/integration/${event.target.value}/data/pageable`);
        if (!integrationResponse.ok) {
            throw new Error(`HTTP error! status: ${integrationResponse.status}`);
        }
        const integrationResponseNonJSON = await integrationResponse.json();
        
        console.log(integrationResponseNonJSON)
        setIntegration(integrationResponseNonJSON);
        const integrationSchemaResponse = await fetch(`http://localhost/api/v1/integration/${event.target.value}/schema`);
        if (!integrationSchemaResponse.ok) {
            throw new Error(`HTTP error! status: ${integrationSchemaResponse.status}`);
        }
        const integrationResponseSchemaNonJSON = await integrationSchemaResponse.json();
        
        console.log(integrationResponseSchemaNonJSON.properties.data.properties)
        setIntegrationSchema(integrationResponseSchemaNonJSON.properties.data.properties);
    } catch (error) {
      console.error('Error fetching:', error);
    }
  }
  

  useEffect(() => {
    getData();
  }, []);

  const addField = () => {
    setFormFields([...formFields, { name: '', type: 'text' }]);
  };


  const handleSubmit =()=>{
    console.log("submit")
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label htmlFor="integrationName" className="block text-sm font-medium text-gray-700">
          Metric Name
        </label>
        <input
          type="text"
          id="integrationName"
          name="integrationName"
          className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
          placeholder="Enter integration name"
        />
      </div>

      <label htmlFor={`field-`} className="block text-sm font-medium text-gray-700">Data Type</label>
      <select
        name="type"
        onChange={(event) => handleFieldChange(event)}
        className="px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
      >
        {data.map((integration, index) => (
          <option value={integration.id}>{integration.name}</option>
        )
          
        )}
      </select>

      

      

      
      


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
