import React from "react";
import Header from "../Components/Header";
import IntegrationForm from "../Components/IntegrationForm";

export default function Page() {
  return (
    <main className="bg-green-50 h-screen flex flex-col">
        <Header />
        <div className="flex flex-1 items-center justify-center p-4">
            <div className="w-full max-w-md">
                <IntegrationForm />
            </div>
        </div>
    </main> 
  );
}
