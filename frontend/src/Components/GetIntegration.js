import React, { useState,useEffect } from 'react';
import { useSnackbar } from './SnackbarProvider'; // Import the Snackbar hook

function GetIntegration() {
  const [data, setData] = useState([]);
  const [integrationdata, setIntegrationData] = useState([]);
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
  const handleNextClick = (e) => {
    setPage(page + 1);
  };
  const handleIntegrationClick = (e) => {
    console.log(e);
    //setIntegrationData(getIntegrationdata);
  };
  const handlePrevClick = (e) => {
    setPage(page - 1);
  };

  useEffect(() => {
    handleClick();
  }, [page]);

  const handleClick = async () => {
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
                <tr className="py-4 even:bg-green-200 odd:bg-green-50 hover:bg-blend-darken hover:bg-gray-300" key={integration.id} onClick={handlePrevClick}>
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
      <div className="flex flex-col relative text-black items-center h-full w-1/2 border-l-2 border-green-700">
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
            <tbody>
              {data.map((integration) => (
                <tr className="py-4 even:bg-green-200 odd:bg-green-50 hover:bg-blend-darken hover:bg-gray-300" key={integration.id}>
                  <td className="whitespace-nowrap px-6 py-4">{integration.id}</td>
                  <td className="whitespace-nowrap px-6 py-4">{integration.name}</td>
                  <td className="whitespace-nowrap px-6 py-4">{integration.type}</td>
                </tr>
              ))}
            </tbody>
          </table>
      </div>
    </div>
  );
}


function GetIntegrationdata(id){
  const [data, setData] = useState([]);

  const getData = async () => {
    try {
      const response = await fetch('http://localhost/api/v1/integration/'+id+'/data/pageable?page=0&size=10');
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

  return data;
};


export default GetIntegration;