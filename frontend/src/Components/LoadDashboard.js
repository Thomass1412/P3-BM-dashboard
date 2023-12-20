import React from "react";
import { useEffect, useRef, useState } from "react";
import LeaderBoard from "../Metric/LeaderBoard";
import Number from "../Metric/Numer";

function LoadDashBoard() {
    const [data, setData] = useState([]);
    const [dashboard,setDashBoard] = useState([]);
    const [fulldashboard,setFullDashBoard] = useState([]);
    const [data2,setData2] = useState()

    const getData = async () => {
        try {
          const response = await fetch('http://localhost/api/v1/dashboard/pageable?page=0&size=100', {
            method: 'GET',
              headers: {
                'Accept': 'application/json',
                'Authorization': 'Bearer '+ document.cookie.split("=")[1],
              },
            });
          if (!response.ok) {
            // Handle non-OK responses
            throw new Error(`HTTP error! Status: ${response.status}`);
          }
          const responseData = await response.json();
          setData(responseData.content);
          setDashBoard(responseData.content[0])
          console.log(responseData.content[0])
          console.log('Successss:', responseData);
        } catch (error) {
          const errorMessage = data.messages.join(', ');
        }
    };

    const getData2 = async () => {
        try {
          const response = await fetch('http://localhost/api/v1/integrations/pageable?page=0&size=9999');
          if (!response.ok) {
            // Handle non-OK responses
            throw new Error(`HTTP error! Status: ${response.status}`);
          }
          const responseData = await response.json();
          setData2(responseData.content.length);
          console.log('Success:', responseData.content.length);
        } catch (error) {
          const errorMessage = data.messages.join(', ');
        }
      };
    
    useEffect(() => {
        getData();
      }, []);

    useEffect(() => {
        getWidgets()
    }, [dashboard]);

    const getWidgets=()=>{
        const renderedDashboard =[];
        console.log("her")
        console.log(dashboard)
        if (dashboard && Array.isArray(dashboard.widgets)) {
            dashboard.widgets.forEach(async element => {
                switch (element.type) {
                    case "LEADERBOARD":
                        renderedDashboard.push(LeaderBoard(element));
                        break;
                    case "NUMBER":
                        console.log(data2)
                        await renderedDashboard.push(Number(element, data2))
                        break;
                    case "TABLE":
                        
                        break;
                
                    default:
                        break;
                }
            });
            setFullDashBoard(renderedDashboard)
        } else {
            console.log('dashboard.widgets is not defined or not an array');
        }
    }

    const handleFieldChange = async (event) => {
        console.log(data[event.target.value])
        setDashBoard(data[event.target.value]);
    }

    useEffect(() => {
    const interval = setInterval(() => {
        getWidgets();
        getData2();
    }, 1000);

    return () => clearInterval(interval);
    }, [dashboard]);

  return (
    <div className="flex-1">
        <select
        name="type"
        onChange={(event) => handleFieldChange(event)}
        className="px-3 w-1/4 mx-8 py-2 mt-6 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
        >
        {data.map((dashboard, index) => (
          <option value={index}>{dashboard.name}</option>
        ))}
        </select>
        <div className="flex-1 h-[90%] grid grid-cols-4 grid-rows-3 p-1 mx-8 mb-4 bg-green-50 rounded-lg my-1">
        {fulldashboard}
        
        
        

        </div> 
    </div>
    
  );
}

export default LoadDashBoard;
