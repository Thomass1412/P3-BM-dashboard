"use client";

import React, { useEffect, useState } from "react";

function GetIntegration() {

    const [data, setData] = useState({
        id: "",
        title: "",
    });


    const getData = async ()=>{
        try{
            const response = fetch("http://localhost/api/v1/integration")
            const ddata =  (await response).json;
            console.log("Success:", data);
        }catch(error){
            console.error("Error:", error);
        }
        
    }

    return (
        <><div className="text-black rounded-3xl w-full h-20 pb-5 mb-2 bg-slate-200 py-2 px-4">
            hej
        </div>
        <button className="bg-sky-50 bg-opacity-50 hover:bg-blue-900 text-white font-bold py-2 px-4 rounded-full mb-5" onClick={getData}>
            Get button
        </button></>
    );

    
}

export default GetIntegration;
