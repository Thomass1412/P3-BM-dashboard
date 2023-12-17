const SchemaComponent = ({ schema }) => {
    if (!schema) {
      return <div>No schema available</div>;
    }
  
    const formatValue = (value) => {
      return typeof value === 'object' ? JSON.stringify(value, null, 2) : value;
    };
  
    return (
      <div className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
        <h3 className="text-lg font-semibold mb-4">Integration Schema</h3>
        <div className="overflow-x-auto">
          <table className="min-w-full leading-normal">
            <tbody>
              {Object.entries(schema.properties).map(([key, value]) => (
                <tr key={key}>
                  <td className="border px-4 py-2">{key}</td>
                  <td className="border px-4 py-2">{formatValue(value)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    );
  };
  
  export default SchemaComponent;
  