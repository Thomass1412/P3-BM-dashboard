import React from "react";


function Logo() {
    const type = "Sales Value "
    const period = " Current month"
    return (
        <div className="col-span-2 row-span-2 bg-green-200 rounded-md px-2 grid grid-cols-8 grid-rows-6 gap-2">
            <div className="col-span-8 row-span-1 flexCenterR border-b-2 border-col">
                <p className="text-black text-2xl">
                    {type}
                    <span className="inline-flex items-baseline text-black text-sm">
                        {period}
                    </span>
                </p>    
            </div>
            <div className="flexCenterC row-span-4 bg-green-200 rounded-md grid grid-cols-10 grid-rows-6 gap-2">
            </div>
        </div>
    );
}
export default Logo;