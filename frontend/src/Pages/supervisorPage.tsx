import React from "react";
import Head from "../Components/Header";
import DashboardForm from "../Components/dashboardForm";



export default function Page() {

  
  return (
    <main className="bg-green-50 h-screen flex flex-col">
        <Head />
        <div className="flexCenterC w-full mt-16">
            <div className="w-10/12 flexCenterC">
                <DashboardForm />
            </div>
            
        </div>
        
    </main> 
  );
}
  