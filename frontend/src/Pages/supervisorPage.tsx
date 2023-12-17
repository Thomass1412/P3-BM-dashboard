import React from "react";
import Head from "../Components/Header";
import LeaderBoard from "../Metric/LeaderBoard";


export default function Page() {
  const leaderboard = LeaderBoard();
  
  return (
    <main className="bg-green-50 h-screen flex flex-col">
        <Head />
        <div className="flex-1 mt-16 p-1 grid grid-cols-5 grid-rows-2 gap-1 mx-8 mb-4 bg-green-50 rounded-lg my-1">
            <div className="flex flex-col col-span-4 row-span-2">
                <div className="mx-8">
                    <h1 className="flex items-center justify-center rounded-t-lg w-1/4 text-2xl font-extrabold text-green-100 bg-green-700">DashBoard Preview</h1>
                </div>
                <div className="flex-1 flex flex-row flex-wrap mx-8 mb-4 overflow-hidden bg-green-50 border-green-700 border-4 rounded-lg rounded-tl-none">
                    <div className="border-green-700 h-1/2 w-1/3 border "></div>
                    <div className="border-green-700 h-1/2 w-1/3 border "></div>
                    <div className="border-green-700 h-1/2 w-1/3 border "></div>
                    <div className="border-green-700 h-1/2 w-1/3 border "></div>
                    <div className="border-green-700 h-1/2 w-1/3 border "></div>
                    <div className="border-green-700 h-1/2 w-1/3 border "></div>

                </div>
            </div>
            <div className="flex flex-col col-span-1 row-span-2">
                <div className="mx-8">
                    <h1 className="flex items-center justify-center rounded-t-lg w-1/2 lg:text-2xl font-extrabold text-green-100 bg-green-700 ">Matrices</h1>
                </div>
                <div className="flex-1 mx-8 mb-4 bg-green-50 border-green-700 border-4 rounded-lg overflow-y-scroll rounded-tl-none">
                    <div className="col-span-1 bg-green-200 rounded-md flexCenterC justify-center text-green-700 text-5xl h-1/4 mb-1">+</div>
                    <div className="col-span-1 bg-green-200 rounded-md flexCenterC justify-center text-green-700 text-5xl h-1/4 mb-1">+</div>
                    <div className="col-span-1 bg-green-200 rounded-md flexCenterC justify-center text-green-700 text-5xl h-1/4 mb-1">+</div>
                    <div className="col-span-1 bg-green-200 rounded-md flexCenterC justify-center text-green-700 text-5xl h-1/4 mb-1">+</div>
                    <div className="col-span-1 bg-green-200 rounded-md flexCenterC justify-center text-green-700 text-5xl h-1/4 mb-1">+</div>
                    <div className="col-span-1 bg-green-200 rounded-md flexCenterC justify-center text-green-700 text-5xl h-1/4 mb-1">+</div>     
                </div>
            </div>
        </div> 
    </main> 
  );
}
  