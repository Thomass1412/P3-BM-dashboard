import React from "react";
import Head from "../Components/Header";
import LeaderBoard from "../Metric/LeaderBoard";

export default function Page() {
  const leaderboard = LeaderBoard();
  
  return (
    <div className="bg-green-50 flex flex-col h-screen">
        <Head/>
        <div className="flex-1 mt-16 p-1 grid grid-cols-4 grid-rows-3 gap-1 mx-8 mb-4 bg-green-50 rounded-lg my-1">
            {leaderboard}
            <div className="col-span-1 bg-green-200 rounded-md"></div>
            <div className="col-span-1 bg-green-200 rounded-md"></div>
            <div className="col-span-1 bg-green-200 rounded-md"></div>
            <div className="col-span-1 bg-green-200 rounded-md"></div>
            <div className="col-span-1 bg-green-200 rounded-md"></div>
            <div className="col-span-1 bg-green-200 rounded-md"></div>
            <div className="col-span-1 bg-green-200 rounded-md"></div>
            <div className="col-span-1 bg-green-200 rounded-md"></div>
        </div> 
    </div>
  );
}
  