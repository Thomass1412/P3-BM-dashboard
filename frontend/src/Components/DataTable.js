const DataTable = ({ data }) => {
    if (!data) {
      return <div>No data available</div>;
    }
  
    if (!Array.isArray(data) || data.length === 0) {
      return <div>No data available or data is not in the expected format.</div>;
    }
  
    // Assuming each item in `data` has a `data` object
    // Merge all keys from the data objects into a single array of unique keys
    const dataKeys = Array.from(new Set(data.flatMap(item => Object.keys(item.data || {}))));
  
    return (
      <div className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 overflow-x-auto">
        <h3 className="text-lg font-semibold mb-4">Integration Data</h3>
        <table className="min-w-full leading-normal">
          <thead>
            <tr>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">ID</th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">IntegrationID</th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Timestamp</th>
              {dataKeys.map(key => (
                <th key={key} className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                  {key}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {data.map((row, index) => (
              <tr key={index}>
                <td className="px-5 py-3 border-b border-gray-200 bg-white text-sm">{row._id}</td>
                <td className="px-5 py-3 border-b border-gray-200 bg-white text-sm">{row.integrationId}</td>
                <td className="px-5 py-3 border-b border-gray-200 bg-white text-sm">{row.timestamp}</td>
                {dataKeys.map(key => (
                  <td key={key} className="px-5 py-3 border-b border-gray-200 bg-white text-sm">
                    {row.data ? row.data[key] : 'N/A'}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  };
  
  export default DataTable;
  