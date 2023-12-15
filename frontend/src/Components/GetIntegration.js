import React, { useState } from 'react';
import { useSnackbar } from './SnackbarProvider'; // Import the Snackbar hook

function GetIntegration() {
  const [data, setData] = useState([]);
  const [buttonClicked, setButtonClicked] = useState(false);
  const { showSnackbar } = useSnackbar(); // Use the Snackbar hook

  const getData = async () => {
    try {
      const response = await fetch('http://localhost/api/v1/integrations/pageable');
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
    <div className="flex flex-col items-center justify-center h-screen">
      <div className="text-black rounded w-full h-96 pb-2 mb-4 bg-slate-200 overflow-y-scroll">
        <ul className="m-1 pl-2 max-h-full border-white">
          {data.map((integration) => (
            <li className="py-2" key={integration.id}>
              Id: {integration.id}, Name: {integration.name}, Type: {integration.type}
            </li>
          ))}
        </ul>
      </div>
      <button
        className="bg-green-700 hover:bg-opacity-50 text-white font-bold py-2 px-4 rounded-full"
        onClick={handleButtonClick}
      >
        Get button
      </button>
    </div>
  );

}

export default GetIntegration;