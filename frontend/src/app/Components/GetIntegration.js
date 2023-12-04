"use client";
import React, { useState } from 'react';
import './ErrorPopup.css';

function ErrorPopup(props) {
  return props.trigger ? (
    <div className="ErrorPopup">
      <div className="ErrorPopup-inner">
        {props.children}
      </div>
    </div>
  ) : null; // Use null instead of an empty string
}

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

  const getData = async () => {
    try {
      const response = await fetch('http://localhost/api/v1/integrations');
      const data = await response.json();
      setData(data);
      console.log('Success:', data);
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setButtonClicked(true); // Set buttonClicked to true after the button is pressed
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
      <ErrorPopup trigger={buttonClicked}>
        <h3>Hello error</h3>
      </ErrorPopup>
    </>
  );
}

export default GetIntegration;