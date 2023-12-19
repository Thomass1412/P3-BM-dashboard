import React, { useState, useEffect } from 'react';

const metricOperations = ["ADD", "SUBTRACT", "MULTIPLY", "DIVIDE", "COUNT"];

export default function MetricForm() {
  const [metricName, setMetricName] = useState('');
  const [metricOperationsList, setMetricOperationsList] = useState([]);
  const [integrations, setIntegrations] = useState([]);

  useEffect(() => {
    // Fetch existing integrations when component mounts
    const fetchIntegrations = async () => {
      // Replace with your actual API endpoint
      const response = await fetch('http://localhost/api/v1/integrations');
      const data = await response.json();
      setIntegrations(data);
    };

    fetchIntegrations();
  }, []);

  const handleMetricNameChange = (event) => {
    setMetricName(event.target.value);
  };

  const handleOperationChange = (index, event) => {
    const newOperations = [...metricOperationsList];
    newOperations[index][event.target.name] = event.target.value;
    setMetricOperationsList(newOperations);
  };

  const addMetricOperation = () => {
    setMetricOperationsList([...metricOperationsList, { operation: '', name: '', targetIntegration: '', criteria: {} }]);
  };

  const removeMetricOperation = (index) => {
    const newOperations = [...metricOperationsList];
    newOperations.splice(index, 1);
    setMetricOperationsList(newOperations);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const payload = {
      name: metricName,
      metricOperations: metricOperationsList,
    };

    // Replace with your actual API endpoint
    const response = await fetch('http://localhost/api/v1/metric', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });

    if (response.ok) {
      const data = await response.json();
      console.log(data);
      // Handle success
    } else {
      console.error('Error submitting form');
      // Handle error
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label htmlFor="metricName" className="block text-sm font-medium text-gray-700">
          Metric Name
        </label>
        <input
          type="text"
          id="metricName"
          name="metricName"
          value={metricName}
          onChange={handleMetricNameChange}
          className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
          placeholder="Enter metric name"
        />
      </div>

      {metricOperationsList.map((operation, index) => (
        <div key={index} className="space-y-4">
          <div>
            <label htmlFor={`operation-${index}`} className="block text-sm font-medium text-gray-700">
              Operation
            </label>
            <select
              name="operation"
              value={operation.operation}
              onChange={(event) => handleOperationChange(index, event)}
              className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            >
              {metricOperations.map(op => (
                <option key={op} value={op}>{op}</option>
              ))}
            </select>
          </div>

          {operation.operation === "COUNT" && (
            <div>
              <label htmlFor={`name-${index}`} className="block text-sm font-medium text-gray-700">
                Count Name
              </label>
              <input
                type="text"
                name="name"
                value={operation.name}
                onChange={(event) => handleOperationChange(index, event)}
                className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                placeholder="Enter count name"
              />
            </div>
          )}

          <div>
            <label htmlFor={`integration-${index}`} className="block text-sm font-medium text-gray-700">
              Target Integration
            </label>
            <select
              name="targetIntegration"
              value={operation.targetIntegration}
              onChange={(event) => handleOperationChange(index, event)}
              className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            >
              {integrations.map(integration => (
                <option key={integration.id} value={integration.id}>{integration.name}</option>
              ))}
            </select>
          </div>

          {/* Add criteria inputs based on operation type if needed */}

          <button
            type="button"
            onClick={() => removeMetricOperation(index)}
            className="px-4 py-1 text-sm font-medium text-white bg-red-600 rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
          >
            Delete Operation
          </button>
        </div>
      ))}

      <div className="flex justify-between">
        <button
          type="button"
          onClick={addMetricOperation}
          className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
        >
          Add Metric Operation
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
