import React from "react";
import Header from "../Components/Header";
import DashboardForm from "../Components/dashboardForm";

export default function Page() {
  return (
    <main className="bg-green-50 h-screen flex flex-col">
        <Header />
        <div className="flex flex-1 items-center justify-center p-4">
            <div className="w-full max-w-md"> {/* Adjust max-w-md to control the form's width */}
                <DashboardForm />
            </div>
        </div>
    </main> 
  );
}
