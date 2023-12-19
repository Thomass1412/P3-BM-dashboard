import React, { useState,useEffect } from 'react';

const DataTable = ({ data }) => {
  const [usernames, setUsernames] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      if (!Array.isArray(data) || data.length === 0) {
        // Handle the case where data is not valid
        setLoading(false);
        return;
      }

      const usernamesArray = [];
      for (const row of data) {
        const username = await getusernames(row.userId);
        usernamesArray.push(username);
      }
      setUsernames(usernamesArray);
      setLoading(false);
    };

    fetchData();
  }, [data]);
  

  if (!data) {
    return <div>No data available</div>;
  } else if (!Array.isArray(data) || data.length === 0) {
    return <div>No data available or data is not in the expected format.</div>;
  }

  
  
    // Assuming each item in `data` has a `data` object
    // Merge all keys from the data objects into a single array of unique keys
    const dataKeys = Array.from(new Set(data.flatMap(item => Object.keys(item.data || {}))));
    console.log(dataKeys)

    const getusernames = async (userID) => {
      try {
        const token = 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOIiwiZXhwIjoxNzAyOTI0ODE0LCJpYXQiOjE3MDI4Mzg0MTR9.PgwOZyE-2cvUaoYpvvLvZAPRX1eKQA_5M7SYO1a0v8BLEvZj-VY9b0FPnAzAwB8K6_5s0YIcjS-SUezjKcKvXg';
        const response = await fetch('http://localhost/api/v1/user/'+"657e1ff042cdee02f39569b4", {
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
        const username = responseData.login;
        console.log('Success: user', username);
        return username
        
        // After all requests are done, set the usernames
      } catch (error) {
        // Handle errors here
        console.error('Error:', error);
      }
      return "not found"
    };

    const formatDate = (timestamp) => {
      const date = new Date(timestamp);
      const day = String(date.getDate()).padStart(2, '0');
      const month = String(date.getMonth() + 1).padStart(2, '0'); // Month is 0-indexed
      const year = date.getFullYear();
      return `${day}/${month}/${year}`;
    };

  
    return (
      <div className="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4 overflow-x-scroll">
        <h3 className="text-lg font-semibold mb-4">Integration Data</h3>
        <table className="min-w-full leading-normal">
          <thead>
            <tr>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">ID</th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">IntegrationID</th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Agent</th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider border-r">Timestamp</th>
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
                <td className="px-5 py-3 border-b border-gray-200 bg-white text-sm">{loading ? 'Loading...' : usernames[index]}</td>
                <td className="px-5 py-3 border-b border-gray-200 bg-white text-sm border-r">{formatDate(row.timestamp)}</td>
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
  