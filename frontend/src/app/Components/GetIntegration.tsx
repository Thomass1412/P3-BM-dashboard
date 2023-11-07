"use client";

import React, { useEffect, useState } from "react";

function GetIntegration() {

    const [data, setData] = useState([{
        id: "",
        title: "",
      }]);

    const getData = async ()=>{
        try{
            const response = await fetch("http://localhost/api/v1/integration")
            const data = await response.json();
            setData(data);
            console.log("Success:", data);
        }catch(error){
            console.error("Error:", error);
        } 
    }
    const listitems = data.map((integration) => (
        <li className="py-0" key={integration.id}>{integration.id} {integration.title}</li>
    ))


    

    return (
        <><div className="text-black rounded w-full max-w-28 h-28 max-h-28 pb-2 mb-4 bg-slate-200">
            <ul className="m-1 pl-2 max-h-full border-white overflow-y-scroll">{listitems}</ul>
        </div>
        <button className="bg-sky-50 bg-opacity-50 hover:bg-blue-900 text-white font-bold py-2 px-4 rounded-full mb-5" onClick={getData}>
            Get button
        </button></>
    );

    
}

export default GetIntegration;
