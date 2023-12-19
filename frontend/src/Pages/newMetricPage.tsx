import React from "react";
import Head from "../Components/Header";
import MetricForm from "../Components/MetricForm";

export default function Page() {

  return (
    <main className="bg-green-50 h-screen flex flex-col">
        <Head />
        <div className="flex flex-1 items-center justify-center p-4">
            <div className="w-full max-w-md">
                <MetricForm />
            </div>
        </div>
        
    </main> 
  );
}