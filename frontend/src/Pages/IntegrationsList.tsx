import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

interface Integration {
    id: string;
    name: string;
    // include other properties as needed
}

const IntegrationsList = () => {
    const [integrations, setIntegrations] = useState<Integration[]>([]);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchIntegrations = async () => {
            const page = 0; // Start from the first page
            const size = 100; // Number of items per page
            try {
                const response = await fetch(`/api/v1/integrations/pageable?page=${page}&size=${size}`);
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const data = await response.json();
                setIntegrations(data.content || []); // Use 'content' for the list of integrations
            } catch (error) {
                console.error('Error fetching integrations:', error);
                // Handle the error appropriately
            }
        };

        fetchIntegrations();
    }, []);

    return (
        <div className="container mx-auto px-4 sm:px-6 lg:px-8 mt-5">
            <h1 className="text-3xl font-bold leading-tight text-gray-900">Integrations</h1>
            <div className="mt-6 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                {integrations.map((integration) => (
                    <div
                        key={integration.id}
                        className="cursor-pointer bg-white hover:bg-gray-100 transition duration-150 ease-in-out rounded-lg shadow overflow-hidden"
                        onClick={() => navigate(`/integrations/${integration.id}`)}
                    >
                        <div className="p-5">
                            <h2 className="text-xl font-semibold text-gray-800">{integration.name}</h2>
                            {/* Additional details can be placed here */}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default IntegrationsList;
