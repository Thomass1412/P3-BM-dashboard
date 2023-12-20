import React from "react";

function LeaderBoard(data, calData) {
    return (
        <div className=" bg-green-700 col-span-2 m-1 text-white rounded-md row-span-2 flexCenterC justify-start p-4 text-5xl font-extrabold">
            <h1>{calData.metricName}</h1>
            <h1 className=" mb-16">LeaderBoard</h1>
            <table className="w-full">
                <thead>
                    <tr>
                    <th className="px-5 py-3 border-b-2 border-gray-200 text-left text-xl font-semibold text-gray-200 uppercase tracking-wider">#</th>
                    <th className="px-5 py-3 border-b-2 border-gray-200 text-left text-xl font-semibold text-gray-200 uppercase tracking-wider">Agent</th>
                    <th className="px-5 py-3 border-b-2 border-gray-200 text-left text-xl font-semibold text-gray-200 uppercase tracking-wider">Score</th>
                    
                    </tr>
                </thead>
                {calData.metricUserCounts.map((agent, index) => (
                    <tbody>
                    <td className="px-5 py-3 border-b border-gray-200  text-sm">{index +1}</td>
                    <td className="px-5 py-3 border-b border-gray-200  text-sm">{agent.displayName}</td>
                    <td className="px-5 py-3 border-b border-gray-200  text-sm">{agent.count}</td>
                    </tbody>
                ))}
            </table>
        </div>
        
    );
}
export default LeaderBoard;