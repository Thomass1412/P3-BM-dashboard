import React from "react";
import Head from "../Components/Header";
import MetricForm from "../Components/MetricForm";

export default function Page() {

  return (
    <main className="bg-green-50 h-screen flex flex-col">
        <Head />
        <MetricForm />
    </main> 
  );
}