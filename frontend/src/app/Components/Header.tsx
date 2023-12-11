"use client";

import Logo from "../Components/Logo";
import { useEffect, useRef, useState } from "react";

function Header() {

  const logo = Logo("rgb(134 239 172)", "white")
  
  const [open, setOpen] = useState<boolean>(false);
  const [opend, setdOpen] = useState<string>("Create new &#11206;");

  const dropDownRef = useRef<HTMLDivElement>(null);
  const dropDownFocus = (state: boolean) => {
    setOpen(!state);
  }

  const handelClickOutside = (e: any) => {
    if(open && !dropDownRef.current?.contains(e.target as Node)){
      const not = false
      setOpen(not);
      
    }
  }
  useEffect(() => {
    if (typeof window !== 'undefined') {
      window.addEventListener('click', handelClickOutside);

      return () => {
        window.removeEventListener('click', handelClickOutside);
      };
    }
  }, [open]);

  return (
    <nav className="bg-green-700 border-gray-200">
      <div className="max-w-screen flex flex-wrap items-center justify-between mx-16 p-4">
        <a href="/" className="flex items-center space-x-3">
            <span className="whitespace-nowrap h-8 w-96">{logo}</span>
        </a>
        <div className="w-full md:w-auto" id="navbar-default">
          <ul className="font-medium flex flex-col p-4 md:p-0 mt-4 border rounded-lg md:flex-row md:space-x-8 rtl:space-x-reverse md:mt-0 md:border-0 ">
            <li className="border-solid border-b-2 hover:border-green-200  border-green-700 text-green-50">
              <a href="/supervisor" className="block py-2 px-3 rounded md:bg-transparent md:p-0" aria-current="page">Home</a>
            </li>
            <li className="border-solid border-b-2 hover:border-green-200  border-green-700 text-green-50">
              <a href="/dashboard" className="block py-2 px-3 rounded md:p-0">Dashboard</a>
            </li>
            <div ref={dropDownRef}>
              <li className="border-solid border-b-2 hover:border-green-200  border-green-700 text-green-50 ">
              <button id="createButton"className="block py-2 px-3 rounded md:p-0" onClick={(e)=>dropDownFocus(open)}>Create new &#11206;</button>
              </li>
              {open && (
                <ul className="absolute bg-green-700 w-max rounded-lg border-green-900 border-solid border-2 p-2 mt-2 rounded-tl-none text-green-50">
                  <li className="border-solid border-b-2 hover:border-green-200  border-green-700" >
                    <a href="/newmetric" className="block rounded md:bg-transparent md:p-0">New metric</a>
                  </li>
                  <li className="border-solid border-b-2 hover:border-green-200  border-green-700" >
                    <a href="/supervisor" className="block rounded md:bg-transparent md:p-0">View metrics</a>
                  </li>
                  <li className="border-solid border-b-2 hover:border-green-200  border-green-700" >
                    <a href="/newintergration" className="block rounded md:bg-transparent md:p-0">New integration</a>
                  </li>
                  <li className="border-solid border-b-2 hover:border-green-200 border-green-700" >
                    <a href="/supervisor" className="block rounded md:bg-transparent md:p-0">view integrations</a>
                  </li>
                </ul>
              )}
            </div>
            
            <li className="border-solid border-b-2 hover:border-green-200  border-green-700 text-green-50">
              <a href="#" className="block py-2 px-3 rounded md:p-0">Profile</a>
            </li>
            <li className="border-solid border-b-2 hover:border-green-200  border-green-700 text-green-50">
              <a href="/login" className="block py-2 px-3 rounded md:p-0">logout</a>
            </li>
            
          </ul>
        </div>
      </div>
    </nav>

  );
};

export default Header;
