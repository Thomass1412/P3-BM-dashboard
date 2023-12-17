import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import SchemaComponent from "../Components/SchemaComponent"; // Component to display the schema
import DataForm from "../Components/DataForm"; // Form component for posting new data
import DataTable from "../Components/DataTable"; // Table component for displaying data with pagination
import Header from '../Components/Header';

interface Integration {
    id: string;
    name: string;
    // include other properties as needed
}

const IntegrationDetail = () => {
  const [integration, setIntegration] = useState(null);
  const [schema, setSchema] = useState(null);
  const [data, setData] = useState(null);
  const { integrationId } = useParams();

  useEffect(() => {
    // Fetch the integration details
    const fetchIntegrationDetail = async () => {
      try {
        const detailResponse = await fetch(`/api/v1/integration/${integrationId}`);
        if (!detailResponse.ok) {
          throw new Error(`HTTP error! status: ${detailResponse.status}`);
        }
        const detailData = await detailResponse.json();
        setIntegration(detailData);

        // Fetch the integration schema
        const schemaResponse = await fetch(`/api/v1/integration/${integrationId}/schema`);
        if (!schemaResponse.ok) {
          throw new Error(`HTTP error! status: ${schemaResponse.status}`);
        }
        const schemaData = await schemaResponse.json();
        setSchema(schemaData);

        // Fetch the integration data
        const dataResponse = await fetch(`/api/v1/integration/${integrationId}/data/pageable`);
        if (!dataResponse.ok) {
            throw new Error(`HTTP error! status: ${dataResponse.status}`);
        }
        const dataData = await dataResponse.json();
        
        // If the data is under a property like 'content' or 'data', adjust as necessary
        setData(dataData.content || dataData.data || []);
      } catch (error) {
        console.error('Fetching integration failed:', error);
      }
    };

    fetchIntegrationDetail();
  }, [integrationId]);

  if (!integration) {
    return <div>Loading integration details...</div>;
  }

  return (
    <div className="container mx-auto my-8">
    <Header />
    <h1 className="text-2xl font-bold mb-4">{integration && (integration as Integration).name}</h1>
      
      <section className="mb-8">
        <h2 className="text-xl font-semibold mb-2">Schema</h2>
        <SchemaComponent schema={schema} />
      </section>

      <section className="mb-8">
        <h2 className="text-xl font-semibold mb-2">Submit New Data</h2>
        <DataForm integrationId={integrationId} schema={schema} />
      </section>

      <section className="mb-8">
        <h2 className="text-xl font-semibold mb-2">Integration Data</h2>
        <DataTable data={data} />
      </section>
    </div>
  );
};

export default IntegrationDetail;