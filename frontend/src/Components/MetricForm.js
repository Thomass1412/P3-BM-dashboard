import React, { useEffect, useState } from 'react';

const metricOperations = ["ADD", "SUBTRACT", "MULTIPLY", "DIVIDE"];

export default function IntegrationForm() {
  const [data, setData] = useState([]);
  const [integration, setIntegration] = useState({ data: {} });
  const [integrationid, setIntegrationid] = useState();
  const [integrationSchema, setIntegrationSchema] = useState([]);
  const [operation, setOperation] = useState()
  const [name, setName] = useState();
  const [checkboxStates, setCheckboxStates] = useState({});
  const [integrationValues, setIntegrationValues] = useState({});
  const [notFilled, setNotFilled] = useState(false)

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
        
        console.log(integrationResponseNonJSON);
        setIntegrationid(event.target.value);
        setIntegration(integrationResponseNonJSON);
    } catch (error) {
      console.error('Error fetching:', error);
    }
  }

  const handleFieldChangeOper = async (event) => {
    setOperation(event.target.value);
    setNotFilled(false);
  }



  const handleCheckSet = async () => {
    try{
      console.log(integration);
        const integrationSchemaResponse = await fetch(`http://localhost/api/v1/integration/${integrationid}/schema`);
        if (!integrationSchemaResponse.ok) {
            throw new Error(`HTTP error! status: ${integrationSchemaResponse.status}`);
        }
        const integrationResponseSchemaNonJSON = await integrationSchemaResponse.json();
        
        console.log(Object.keys(integrationResponseSchemaNonJSON.properties.data.properties))
        setIntegrationSchema(Object.keys(integrationResponseSchemaNonJSON.properties.data.properties));
    } catch (error) {
      console.error('Error fetching:', error);
    }
  }
  useEffect(() => {
    const init = async () => {
      await handleCheckSet();
    };
    init();
    setNotFilled(false);
  }, [integration]);

  
  useEffect(() => {
    getData();
  }, []);

  const handleSubmit =async(event)=>{
    event.preventDefault();

    const criteria = {};
    for (const [integration, isChecked] of Object.entries(checkboxStates)) {
      if (isChecked && integrationValues[integration]) {
        criteria[`data_${integration}`] = integrationValues[integration];
      }
    }

    const metricOperationsPayload = [
      
      {
        operation: operation, // Assuming 'operation' holds the operation type like 'ADD', 'COUNT', etc.
      },
      {
        operation: "COUNT",
        name: name,
        targetIntegration: integrationid, // Assuming 'integrationid' holds the ID of the target integration
        operator: operation, // The operator for the specific metric operation
        criteria: criteria
      }
    ];
    console.log(metricOperationsPayload)

    const payload = {
      metricOperations: metricOperationsPayload,
      name: name
      
    };
    console.log(payload)

    try{
      if(!operation || !name || !integrationid || !criteria){
        setNotFilled(true);
        throw new Error(`something is null`);
      }
      const response = await fetch(`http://localhost/api/v1/metric/`, {
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
    }catch(error){
      setNotFilled(true);
      console.error('Error fetching:', error);
    }
  }


  const nameSetter = (e) =>{
    setNotFilled(false);
    setName(e.target.value)
  }

  useEffect(() => {
    setNotFilled(false);
    const initialCheckboxStates = {};
    integrationSchema.forEach((field) => {
      initialCheckboxStates[field] = false;
    });
    setCheckboxStates(initialCheckboxStates);
  }, [integrationSchema]);

  const handleCheckboxChange = (integration) => {
    setNotFilled(false);
    setCheckboxStates(prev => ({ ...prev, [integration]: !prev[integration] }));
    if (!checkboxStates[integration]) {
      // Remove the integration value if the checkbox is unchecked
      setIntegrationValues(prev => {
        const newValues = { ...prev };
        delete newValues[integration];
        return newValues;
      });
    }
  };

  const handleInputChange = (integration, value) => {
    setIntegrationValues(prev => ({ ...prev, [integration]: value }));
  };

  const handleSubmit2 =(event) =>{
    event.preventDefault();
    
  }

  return (
    <form onSubmit={handleSubmit2} className="space-y-4">
      <div className={`${notFilled ? "opacity-100" : "opacity-0"} transition-opacity duration-1000 ease-in-out flex flex-row items-center justify-end w-full text-red-600  `}>
        <h1 className="">Something is not filled</h1>
      </div>
      <div>
        <label htmlFor="integrationName" className="block text-sm font-medium text-gray-700">
          Metric Name
        </label>
        <input
          type="text"
          id="integrationName"
          name="integrationName"
          onChange={nameSetter}
          className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
          placeholder="Enter integration name"
        />
      </div>
      <label htmlFor={`field-`} className="block text-sm font-medium text-gray-700">Operation</label>
      <select
        name="type"
        onChange={(event) => handleFieldChangeOper(event)}
        className="px-3 w-full py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
      >
        <option value={undefined}>Choose operation</option>
        {metricOperations.map((operation, index) => (
          <option value={operation}>{operation}</option>
        )
          
        )}
      </select>

      <label htmlFor={`field-`} className="block text-sm font-medium text-gray-700">Data Type</label>
      <select
        name="type"
        onChange={(event) => handleFieldChange(event)}
        className="px-3 py-2 w-full bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
      >
        <option value={null}>Choose integration</option>
        {data.map((integration, index) => (
          <option value={integration.id}>{integration.name}</option>
        )
          
        )}
      </select>
      <div>
      {integrationSchema.map((integration, index) => (
        <div key={index}>
          <label className='flex flex-row'>
            <input 
              className="mx-2" 
              type="checkbox" 
              checked={checkboxStates[integration] || false}
              onChange={() => handleCheckboxChange(integration)} 
            />
            {integration}
          </label>
          {checkboxStates[integration] && (
            <input 
              className='mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm'
              value={integrationValues[integration] || ''}
              onChange={(e) => handleInputChange(integration, e.target.value)}
            />
          )}
        </div>
      ))}
    </div>

      <div className="flex justify-between">
        
        <button
          onClick={handleSubmit}
          className="px-4 py-2 text-sm font-medium text-white bg-green-600 rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2"
        >
          Submit
        </button>
      </div>
    </form>
  );
};
