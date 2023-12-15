import React, { useState } from 'react';
import { useSnackbar } from './SnackbarProvider'; // Import the Snackbar hook

function GetIntegration() {
  const [data, setData] = useState([]);
  const [buttonClicked, setButtonClicked] = useState(false);
  const { showSnackbar } = useSnackbar(); // Use the Snackbar hook

  const getData = async () => {
    try {
      const response = await fetch('http://localhost/api/v1/integrations/pageable?page=0&size=10');
      if (!response.ok) {
        // Handle non-OK responses
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      const responseData = await response.json();
      setData(responseData.content);
      console.log('Success:', responseData);
      showSnackbar('success', 'Data fetched successfully.');
    } catch (error) {
      const errorMessage = data.messages.join(', ');
      showSnackbar('error', errorMessage);
    } finally {
      setButtonClicked(true); // Set buttonClicked to true after the button is pressed
    }
  };

  const handleButtonClick = async () => {
    // Reset the data state before making the request
    setData([]);
    setButtonClicked(false);
    await getData();
  };

  return (
    <div className="flex flex-col relative text-black items-center h-full w-full">
      <div className="flex flex-col text-black w-full">
        <div className="overflow-x-auto sm:-mx-6 lg:-mx-8">
          <div className="inline-block min-w-full sm:px-6 lg:px-8">
            <div className="overflow-hidden">
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
                    <tr className="py-2 even:bg-green-200 odd:bg-green-50 " key={integration.id}>
                      <td className="whitespace-nowrap px-6 py-4">{integration.id}</td>
                      <td className="whitespace-nowrap px-6 py-4">{integration.name}</td>
                      <td className="whitespace-nowrap px-6 py-4">{integration.type}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
      <div className='flex flex-row w-full absolute bottom-0 h-{5%}'>
        <button
          className="bg-green-700 hover:bg-green-900 text-white font-bold rounded-tr-sm py-2 bottom-0 left-0 w-1/4 h-full"
          onClick={handleButtonClick}>
          Get button
        </button>
        <div className='flex flex-row w-full h-full rtl:space-x-reverse'>
          <button
            className="bg-green-700 hover:bg-green-900 text-white font-bold py-2 rounded-tr-sm w-1/4 h-full"
            onClick={handleButtonClick}>
            Get button
          </button>
          <h1>hej </h1>
          <button
            className="bg-green-700 hover:bg-green-900 text-white font-bold py-2 rounded-tr-sm w-1/4 h-full"
            onClick={handleButtonClick}>
            Get button
          </button>
        </div>
      </div>
    </div>
  );

}

export default GetIntegration;