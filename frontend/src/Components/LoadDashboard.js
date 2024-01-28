import React from "react";
import { useEffect, useRef, useState } from "react";
import LeaderBoard from "../Metric/LeaderBoard";
import Number from "../Metric/Numer";
import { useParams } from 'react-router-dom';
import { useNavigate } from "react-router-dom";

function LoadDashBoard() {
    const [data, setData] = useState([]);
    const [dashboard,setDashBoard] = useState([]);
    const [fulldashboard,setFullDashBoard] = useState([]);
    const [calDashboard,setCalDashboard] = useState([])
    const { id } = useParams();

    const navigate = useNavigate();

    const getData = async () => {
        try {
          const response = await fetch('https://dashboard.ollioddi.dk/api/v1/dashboard/pageable?page=0&size=100', {
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
          setDashBoard(responseData.content[id])
          console.log(responseData.content[id])
          console.log('Successss:', responseData);
        } catch (error) {
          console.log(data.messages.join(', '));
        }
    };

  

    const getCalDashboard = async () => {
      console.log(dashboard) 
        if (!dashboard || !dashboard.id) {
          console.log("Dashboard or Dashboard ID is not set.");
          return;
        }
        try {
          console.log(dashboard.id)
          const response = await fetch('https://dashboard.ollioddi.dk/api/v1/dashboard/{dashboardId}?dashboardId='+dashboard.id, {
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
          setCalDashboard(responseData);
          console.log('Success:', responseData);
        } catch (error) {
          console.log(data.messages.join(', '));
        }
      };
    
    useEffect(() => {
        console.log("her1");
        const fetchData = async () => {
          try {
              await getData();
              await getWidgets(); 
          } catch (error) {
              // Handle or log error
              console.error("Error fetching data", error);
          }
        };

        fetchData();
    }, []);

    useEffect(() => {
      // Existing useEffect logic
      // ...

      // New code to execute something after 30 seconds
      const timer = setTimeout(() => {
          // Place your code here that you want to execute after 30 seconds
          navigate(0);
      }, 30000);

      // Cleanup function
      return () => clearTimeout(timer);
  }, []);

    /*useEffect(() => {
      console.log("her1");
        const fetchData = async () => {
          try {
              await getData();
              await getWidgets(); 
          } catch (error) {
              // Handle or log error
              console.error("Error fetching data", error);
          }
        };
        setTimeout(() => {
          fetchData();
        }, 5000);

  }, [fulldashboard]);*/


    useEffect(() => {
        if (dashboard && dashboard.id) {
            getCalDashboard();
        }
        
    }, [dashboard]);

    useEffect(() => {
          getWidgets();
    }, [calDashboard]);
    

    const getWidgets=()=>{
        const renderedDashboard =[];
        console.log("her")
        console.log(dashboard)
        if (dashboard && Array.isArray(dashboard.widgets)) {
            dashboard.widgets.forEach(async (element , index)=> {
                switch (element.type) {
                    case "LEADERBOARD":
                        renderedDashboard.push(LeaderBoard(element, calDashboard.calculatedMetrics[index]));
                        break;
                    case "NUMBER":
                        await renderedDashboard.push(Number(element, calDashboard.calculatedMetrics[index]))
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
        navigate("/dashboard/"+event.target.value)
        setDashBoard(data[event.target.value]);
    }

    

  return (
    <div className="flex-1">
        <select
        name="type"
        onChange={(event) => handleFieldChange(event)}
        className="px-3 w-1/4 mx-8 py-2 mt-6 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
        >
          <option>Select Dashboard</option>
        {data.map((dashboard, index) => (
          <option key={dashboard.id} value={index}>{dashboard.name}</option>
        ))}
        </select>
        <div className="flex-1 h-[90%] grid grid-cols-4 grid-rows-3 p-1 mx-8 mb-4 bg-green-50 rounded-lg my-1">
        {fulldashboard}
        
        
        

        </div> 
    </div>
    
  );
}

export default LoadDashBoard;
