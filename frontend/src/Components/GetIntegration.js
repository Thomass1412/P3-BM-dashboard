import React, { useState,useEffect } from 'react';
import { useSnackbar } from './SnackbarProvider'; // Import the Snackbar hook
import { useNavigate } from "react-router-dom";

function GetIntegration() {
  const [data, setData] = useState([]);
  const [maxPage, setMaxPage] = useState(1);
  const [page, setPage] = useState(0);
  const { showSnackbar } = useSnackbar(); // Use the Snackbar hook

  const getData = async () => {
    try {
      const response = await fetch('http://localhost/api/v1/integrations/pageable?page='+page+'&size=10');
      if (!response.ok) {
        // Handle non-OK responses
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      const responseData = await response.json();
      setMaxPage(responseData.totalPages)
      setData(responseData.content);
      console.log('Success:', responseData);
      showSnackbar('success', 'Data fetched successfully.');
    } catch (error) {
      const errorMessage = data.messages.join(', ');
      showSnackbar('error', errorMessage);
    }
  };


  const getusernames = async (userID) => {
    try {
      const token = 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOIiwiZXhwIjoxNzAyOTI0ODE0LCJpYXQiOjE3MDI4Mzg0MTR9.PgwOZyE-2cvUaoYpvvLvZAPRX1eKQA_5M7SYO1a0v8BLEvZj-VY9b0FPnAzAwB8K6_5s0YIcjS-SUezjKcKvXg';
      const response = await fetch('http://localhost/api/v1/user/'+userID, {
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

  const handleNextClick = (e) => {
    setPage(page + 1);
  };
  const navigate = useNavigate();
  const handleIntegrationClick = (e) => {
    navigate("/integrations/"+(e.target.parentNode.getAttribute('id')))
  };
  const handlePrevClick = (e) => {
    setPage(page - 1);
  };

  useEffect(() => {
    handleLoad();
  }, [page]);


  useEffect(() => {
    if (integrationdata.length > 0) {
      const fetchUsernames = async () => {
        const resolvedUsernames = await Promise.all(
          integrationdata.map((data) => getusernames(data.userId))
        );
        setUsernames(resolvedUsernames);
      };
      fetchUsernames();
    }
  }, [integrationdata]);

  const handleLoad = async () => {
    await getData();
  };

  const formatDate = (timestamp) => {
    const date = new Date(timestamp);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Month is 0-indexed
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${hours}:${minutes} - ${day}/${month}/${year}`;
  };

  return (
    <div className="flex flex-row relative text-black h-full w-full">
      <div className="flex flex-col relative text-black items-center h-full w-full">
        <div className="flex flex-col text-black w-full">
          <table className="min-w-full text-left text-xl font-light">
            <thead className="border-b text-xl dark:border-neutral-500">
              <tr className='bg-green-50'>
                <th scope="col" className="px-6 py-4">Id</th>
                <th scope="col" className="px-6 py-4">Name</th>
                <th scope="col" className="px-6 py-4">Type</th>
                <th scope="col" className="px-6 py-4">Last updated</th>
              </tr>
            </thead>
            <tbody>
              {data.map((integration) => (
                <tr id={integration.id} className="py-4 even:bg-green-200 odd:bg-green-50 hover:bg-blend-darken hover:bg-gray-300" key={integration.id} onClick={handleIntegrationClick}>
                  <td className="whitespace-nowrap px-6 py-4">{integration.id}</td>
                  <td className="whitespace-nowrap px-6 py-4">{integration.name}</td>
                  <td className="whitespace-nowrap px-6 py-4">{integration.type}</td>
                  <td className="whitespace-nowrap px-6 py-4">{formatDate(integration.lastUpdated)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div className='absolute bottom-0 left-0 w-full items-center justify-between'>
          <div className='flex flex-row w-full h-full items-center justify-center gap-x-4'>
            <button
              className={`bg-green-700 hover:bg-green-900 text-white font-bold py-2 rounded-t-sm w-12 h-full ${page === 0 ? "opacity-50" : ""}`}
              disabled={page === 0}
              onClick={handlePrevClick}>
              &#11104;
            </button>
            <div className='flex items-center justify-center w-4'>
              <h1>{page + 1}</h1>
            </div>
            <button
              className={`bg-green-700 hover:bg-green-900 text-white font-bold py-2 rounded-t-sm w-12 h-full ${page === maxPage - 1 ? "opacity-50" : ""}`}
              disabled={page === maxPage - 1}
              onClick={handleNextClick}>
              &#11106;
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}


export default GetIntegration;