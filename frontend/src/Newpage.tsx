import React from "react";
import Leaderboard from "./Metric/LeaderBoard"



export default function Home() {
    const leaderboard = Leaderboard();
    return (
        <main className="bg-green-700 h-screen">
            <h1 className="bg-green-200">Hej</h1>
            {leaderboard}
        </main>
        
    );
  }
  