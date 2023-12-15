import React from "react";
import Head from "../Components/Header";

export default function Page() {

  return (
    <main className="bg-green-50 h-screen flex flex-col">
        <Head />
        <div className="flex flex-row flex-1 mt-8 mx-8 mb-4 bg-green-50 rounded-lg my-1 gap-8">
            <div className="flex flex-col flex-1" >
                <div className="mx-8">
                    <h1 className="flex items-center justify-center rounded-t-lg w-1/4 text-2xl font-extrabold text-green-100 bg-green-700">Metric builder</h1>
                </div>
                <div className="border-green-700 border-4 flex flex-row flex-1 bg-green-50 rounded-lg"></div>  
            </div> 
            <div className="flex flex-col flex-1" >
                <div className="mx-8">
                    <h1 className="flex items-center justify-center rounded-t-lg w-1/4 text-2xl font-extrabold text-green-100 bg-green-700">Metric preview</h1>
                </div>
                <div className="border-green-700 border-4 flex flex-row flex-1 bg-green-50 rounded-lg">
                    <div className="flex-1 bg-green-200 my-4 mx-8 rounded-md flexCenterC justify-center text-green-700 text-5xl">+</div>
                </div>
            </div> 
        </div> 
    </main> 
  );
}