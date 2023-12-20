import React from "react";
import Head from "../Components/Header";
import LoadDashboard from "../Components/LoadDashboard";

export default function Page() {
  
  return (
    <div className="bg-green-50 flex flex-col h-screen">
        <Head/>
        <LoadDashboard/>
        
        
    </div>
  );
}
  