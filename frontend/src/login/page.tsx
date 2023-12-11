import React from "react";
import {Logo} from "../Components/Logo";
import {Login} from "../Components/Login";

export default function Page() {
  const logo = Logo("rgb(134 239 172)", "white")
  
  return (
    <main className="bg grid h-screen grid-cols-8 grid-rows-6 gap-0">
      <div className="flexCenterR col-span-2 p-2 bg-green-700 bg-opacity-90">
        {logo}
      </div>
      <div className="col-span-6"></div>
      <div className="row-span-5 col-span-2 bg-green-50 bg-opacity-95 p-4">
        <div className="flexCenterC">
          <h1 className="lg:text-4xl mb-8 mt-8 text-4xl font-extrabold text-black">
            Login
          </h1>
        </div>
        <Login/>
      </div>
    </main>
  );
}
  