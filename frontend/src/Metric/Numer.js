import React from "react";

function Number(data, calData) {
    console.log(calData);
    
    const getTotalCount = () => {
        return calData.metricUserCounts.reduce((total, userCount) => {
            return total + userCount.count;
        }, 0);
    }

    const totalCount = getTotalCount();


    return (
        <div className=" bg-green-700 col-span-1 m-1 text-white rounded-md row-span-1 flexCenterC justify-start p-4 text-5xl font-extrabold">
            <h1 className="text-3xl mb-8">{data.name}</h1>
            <h1 className="text-8xl">{totalCount}</h1>
        </div>

    );
}
export default Number;