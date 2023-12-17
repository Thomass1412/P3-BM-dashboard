import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

const IntegrationsList = () => {
  const [integrations, setIntegrations] = useState([]);

  useEffect(() => {
    const fetchIntegrations = async () => {
      try {
        const response = await fetch('/api/v1/integrations'); // Adjust the URL as needed
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setIntegrations(data);
      } catch (error) {
        console.error('Error fetching integrations:', error);
      }
    };

    fetchIntegrations();
  }, []);

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">Integrations</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {integrations.map((integration) => (
          <Link to={`/integrations/${integration.id}`} key={integration.id} className="block p-6 max-w-sm bg-white rounded-lg border border-gray-200 shadow-md hover:bg-gray-100">
            <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900">{integration.name}</h5>
            {/* Add more details you want to show on the card */}
          </Link>
        ))}
      </div>
    </div>
  );
};

export default IntegrationsList;
