"use client";
import React, { useState } from 'react';
import { useSnackbar } from './SnackbarProvider';

function GetIntegration() {
  const [data, setData] = useState([
    {
      id: '',
      name: '',
      type: '',
      myField1: '',
    },
  ]);

  const [buttonClicked, setButtonClicked] = useState(false);
  const { showSnackbar } = useSnackbar(); 

  const getData = async () => {
    try {
      const response = await fetch('http://localhost/api/v1/integrations');
      const data = await response.json();
      setData(data);
      console.log('Success:', data);
      showSnackbar('success', 'Data fetched successfully!');
    } catch (error) {
      console.error('Error:', error);
      showSnackbar('error', 'An error occurred while fetching data.');
    } finally {
      setButtonClicked(true);
    }
  };

  const listitems = data.map((integration) => (
    <li className="py-0" key={integration.id}>
      Id: {integration.id}, Name: {integration.name}, Type: {integration.type}
    </li>
  ));

  const handleButtonClick = async () => {
    // Reset the data state before making the request
    setData([]);
    setButtonClicked(false);
    await getData();
  };

  return (
    <>
      <div className="text-black rounded w-full h-20 pb-2 mb-4 bg-slate-200">
        <ul className="m-1 pl-2 max-h-full border-white overflow-y-scroll">
          {listitems}
        </ul>
      </div>
      <button
        className="bg-sky-50 bg-opacity-50 hover:bg-blue-900 text-white font-bold py-2 px-4 rounded-full mb-5"
        onClick={handleButtonClick}
      >
        Get button
      </button>
    </>
  );
}

export default GetIntegration;