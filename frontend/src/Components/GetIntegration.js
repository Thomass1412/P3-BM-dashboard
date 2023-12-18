import React, { useState,useEffect } from 'react';
import { useSnackbar } from './SnackbarProvider'; // Import the Snackbar hook

function GetIntegration() {
  const [data, setData] = useState([]);
  const [integrationdata, setIntegrationData] = useState([]);
  const [usernames, setUsernames] = useState([]);
  const [parentID, setparentID] = useState();
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

  const getIntegrationData = async() =>{
    try {
      const response = await fetch('http://localhost/api/v1/integration/'+parentID+'/data/pageable?page=0&size=10');
      if (!response.ok) {
        // Handle non-OK responses
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      const responseData = await response.json();
      setIntegrationData(responseData.content);
      console.log('Success: 11', responseData);
    } catch (error) {
      const errorMessage = integrationdata.messages.join(', ');
    }
  };

  const getusernames = async () => {
    const usernamesMap = {};
  
    try {
      for (const integration of integrationdata) {
        const token = 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOIiwiZXhwIjoxNzAyOTI0ODE0LCJpYXQiOjE3MDI4Mzg0MTR9.PgwOZyE-2cvUaoYpvvLvZAPRX1eKQA_5M7SYO1a0v8BLEvZj-VY9b0FPnAzAwB8K6_5s0YIcjS-SUezjKcKvXg';
  
        const response = await fetch('http://localhost/api/v1/user/' + integration.userId, {
          method: 'GET',
          headers: {
            'Accept': 'application/json',
            'Authorization': token,
          },
        });
  
        if (!response.ok) {
          // Handle non-OK responses
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
  
        const responseData = await response.json();
        usernamesMap[integration.id] = responseData.login;
        console.log('Success: user', usernamesMap);
      }
  
      // After all requests are done, set the usernames
      setUsernames(usernamesMap);
    } catch (error) {
      // Handle errors here
      console.error('Error:', error);
    }
  };

  const handleNextClick = (e) => {
    setPage(page + 1);
  };
  const handleIntegrationClick = (e) => {
    setparentID(e.target.parentNode.getAttribute('id'))
  };
  const handlePrevClick = (e) => {
    setPage(page - 1);
  };

  useEffect(() => {
    handleLoad();
  }, [page]);

  useEffect(() => {
    if(parentID){
      getIntegrationData();
    }
  }, [parentID]);

  useEffect(() => {
    if(integrationdata.length > 0){
      getusernames();
    }
  }, [integrationdata]);

  const handleLoad = async () => {
    await getData();
  };

  return (
    <div className="flex flex-row relative text-black h-full w-full">
      <div className="flex flex-col relative text-black items-center h-full w-1/2 border-r-2 border-green-700">
        <div className="flex flex-col text-black w-full">
          <table className="min-w-full text-left text-xl font-light">
            <thead className="border-b text-xl dark:border-neutral-500">
              <tr className='bg-green-50'>
                <th scope="col" className="px-6 py-4">Id</th>
                <th scope="col" className="px-6 py-4">Name</th>
                <th scope="col" className="px-6 py-4">Type</th>
              </tr>
            </thead>
            <tbody>
              {data.map((integration) => (
                <tr id={integration.id} className="py-4 even:bg-green-200 odd:bg-green-50 hover:bg-blend-darken hover:bg-gray-300" key={integration.id} onClick={handleIntegrationClick}>
                  <td className="whitespace-nowrap px-6 py-4">{integration.id}</td>
                  <td className="whitespace-nowrap px-6 py-4">{integration.name}</td>
                  <td className="whitespace-nowrap px-6 py-4">{integration.type}</td>
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
      <div className="flex flex-col relative text-black items-center h-[83vh] w-1/2 border-l-2 border-green-700 overflow-y-scroll overflow-x-hidden">
        <div className='mx-8 w-full py-4'>
          <h1 className="flex items-center justify-center px-8 font-extrabold text-left text-xl pb-4 border-b dark:border-neutral-500 ">Intgration data</h1>
        </div>
          <table className="min-w-full text-left text-xl font-light">
            <thead className="border-b text-xl dark:border-neutral-500">
              <tr className='bg-green-50'>
                <th scope="col" className="px-6 pb-3">Id</th>
                <th scope="col" className="px-6 pb-3">User</th>
                <th scope="col" className="px-6 pb-3">Time</th>
                <th scope="col" className="px-6 pb-3">Data</th>
              </tr>
            </thead>
            <tbody className=''>
              {integrationdata.map((integrationdata) => (
                <tr className="py-4 even:bg-green-200 odd:bg-green-50" key={integrationdata.id}>
                  <td className="whitespace-nowrap px-6 py-4">{integrationdata.id}</td>
                  <td className="whitespace-nowrap px-6 py-4">{usernames[integrationdata.Id]}</td>
                  <td className="whitespace-nowrap px-6 py-4">{integrationdata.timestamp}</td>
                  <div className='flex flex-col'>
                    {Object.keys(integrationdata.data).map((key) => (
                      <td className="whitespace-nowrap px-6" key={key}>
                        {`${key}: ${integrationdata.data[key]}`}
                      </td>
                    ))}
                  </div>
                  
                </tr>
              ))}
            </tbody>
          </table>
      </div>
    </div>
  );
}


export default GetIntegration;