import React from "react";
import Getint from "../Components/GetIntegration";
import Postint from "../Components/IntegrationForm";

export default function Page() {
  
  return (
    <main className="bg grid h-screen grid-cols-8 grid-rows-6 gap-0">
        <Getint/>
        <Postint/>
    </main>
  );
}
  