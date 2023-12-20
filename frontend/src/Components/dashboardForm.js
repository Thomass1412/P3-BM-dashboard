import React, { useState, useEffect } from 'react';
import { useSnackbar } from './SnackbarProvider';

export default function DashboardForm() {
  const [data, setData] = useState([]);
  const [open, setOpen] = useState([]);
  const [dashboardName, setDashboardName] = useState('');
  const [widgets, setWidgets] = useState([{ name: '', type: '', options: {} }]);
  const [metrics, setMetrics] = useState([]);
  const { showSnackbar } = useSnackbar(); // Use the Snackbar hook

  useEffect(() => {
    fetch('http://localhost/api/v1/metric/pageable', {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'Authorization': 'Bearer ' + document.cookie.split("=")[1],
      },
    })
    .then(response => response.json())
    .then(data => setMetrics(data.content || []))
    .catch(error => console.error('Error fetching metrics:', error));
  }, []);

  const handleMetricNameChange = (index, metricName) => {
    const metric = metrics.find(m => m.name === metricName);
    const integrationId = metric?.dependentIntegrationIds?.[0] || '';

    const updatedWidgets = [...widgets];
    updatedWidgets[index].options.metricId = metric?.id || '';
    updatedWidgets[index].options.integrationId = integrationId;
    setWidgets(updatedWidgets);
  };
  const handleSortedByChange = (index, value) => {
    const updatedWidgets = [...widgets];
    updatedWidgets[index].options.sortedBy = { "count": value };
    setWidgets(updatedWidgets);
  };

  const handleDashboardNameChange = (event) => {
    setDashboardName(event.target.value);
  };

  const handleWidgetChange = (index, key, value) => {
    const updatedWidgets = [...widgets];
    if (key === 'limit') {
      updatedWidgets[index].options[key] = Number(value);
    } else if (key === 'startDateType' || key === 'endDateType') {
      updatedWidgets[index].options[key] = value;
    } else {
      updatedWidgets[index][key] = value;
    }
    setWidgets(updatedWidgets);
  };

  const addWidget = () => {
    setWidgets([
      ...widgets,
      { name: '', type: '', options: {} }
    ]);
  };

  const removeWidget = (index) => {
    const updatedWidgets = [...widgets];
    updatedWidgets.splice(index, 1);
    setWidgets(updatedWidgets);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const payload = {
      name: dashboardName,
      widgets: widgets,
    };

    try {
        const response = await fetch('http://localhost/api/v1/dashboard/', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + document.cookie.split("=")[1],
          },
          body: JSON.stringify(payload),
        });
  
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        setData(data.content);
        console.log('Success:', data);
        showSnackbar('success', 'Dashboard created successfully.');
        
      } catch (error) {
        console.error(error);
        const errorMessage = error.message || 'An error occurred while submitting the form.';
        showSnackbar('error', errorMessage);
      }
  };

  // Enums
  const widgetTypes = ['LEADERBOARD', 'NUMBER', 'TABLE'];
  const Dates = ['TODAY', 'YESTERDAY', 'THIS_WEEK', 'LAST_WEEK', 'THIS_MONTH', 'LAST_MONTH', 'THIS_YEAR', 'LAST_YEAR'];

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label htmlFor="dashboardName" className="block text-sm font-medium text-gray-700">
          Dashboard Name
        </label>
        <input
          type="text"
          id="dashboardName"
          name="dashboardName"
          value={dashboardName}
          onChange={handleDashboardNameChange}
          className="mt-1 block w-80 px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
          placeholder="Enter dashboard name"
        />
      </div>
      <div className='flex flex-row flex-wrap'>

      
      {widgets.map((widget, index) => (
        <div key={index} className="border p-4 rounded-lg mb-4 w-80">
          <div>
            <label htmlFor={`widget-name-${index}`} className="block text-sm font-medium text-gray-700">
              Widget Name
            </label>
            <input
              type="text"
              id={`widget-name-${index}`}
              name={`widget-name-${index}`}
              value={widget.name}
              onChange={(e) => handleWidgetChange(index, 'name', e.target.value)}
              className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              placeholder="Enter widget name"
            />
          </div>
          <div className="mt-4">
            <label htmlFor={`widget-type-${index}`} className="block text-sm font-medium text-gray-700">
              Widget Type
            </label>
            <select
              id={`widget-type-${index}`}
              name={`widget-type-${index}`}
              value={widget.type}
              onChange={(e) => handleWidgetChange(index, 'type', e.target.value)}
              className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            >
              <option key={null} value={null}>Select widget</option>
              {widgetTypes.map((type) => (
                <option key={type} value={type}>{type}</option>
              ))}
            </select>
          </div>
          <div className="mt-4">
            <label htmlFor={`widget-metric-${index}`} className="block text-sm font-medium text-gray-700">
              Metric Name
            </label>
            <select
              id={`widget-metric-${index}`}
              name={`widget-metric-${index}`}
              value={metrics.find(m => m.id === widget.options.metricId)?.name || ''}
              onChange={(e) => handleMetricNameChange(index, e.target.value)}
              className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            >
              <option value="">Select a Metric</option>
              {metrics.map(metric => (
                <option key={metric.id} value={metric.name}>{metric.name}</option>
              ))}
            </select>
          </div>
          <div className="mt-4">
            <label htmlFor={`widget-sortedBy-${index}`} className="block text-sm font-medium text-gray-700">
              Sort By count
            </label>
            <select
              id={`widget-sortedBy-${index}`}
              name={`widget-sortedBy-${index}`}
              value={widget.options.sortedBy?.count || ''}
              onChange={(e) => handleSortedByChange(index, e.target.value)}
              className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            >
              <option value="">Select Sorting</option>
              <option value="ASC">Ascending</option>
              <option value="DESC">Descending</option>
            </select>
          </div>
          <div className="mt-4">
            <label htmlFor={`start-date-type${index}`} className="block text-sm font-medium text-gray-700">
              Start Date
            </label>
            <select
              id={`start-date-type${index}`}
              name={`start-date-type${index}`}
              value={widget.options.startDateType || ''}
              onChange={(e) => handleWidgetChange(index, 'startDateType', e.target.value)}
              className="mt-1 block px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            >
              <option key={null} value={null}>Select start date</option>
              {Dates.map((startDateType) => (
              <option key={startDateType} value={startDateType}>{startDateType}</option>
              ))}
            </select>
          </div>
          <div className="mt-4">
            <label htmlFor={`end-date-type${index}`} className="block text-sm font-medium text-gray-700">
              End Date
            </label>
            <select
              id={`end-date-type${index}`}
              name={`end-date-type${index}`}
              value={widget.options.endDateType || ''}
              onChange={(e) => handleWidgetChange(index, 'endDateType', e.target.value)}
              className="mt-1 block px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            >
              <option key={null} value={null}>Select end date</option>
              {Dates.map((endDateType) => (
                <option key={endDateType} value={endDateType}>{endDateType}</option>
              ))}
            </select>
          </div>
          <div className="mt-4">
            <label htmlFor={`widget-limit-${index}`} className="block text-sm font-medium text-gray-700">
              Limit
            </label>
            <input
              type="number"
              id={`widget-limit-${index}`}
              name={`widget-limit-${index}`}
              value={widget.options.limit}
              onChange={(e) => handleWidgetChange(index, 'limit', e.target.value)}
              className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              placeholder="Enter limit"
            />
          </div>
          <div className="mt-4">
            <button
              type="button"
              onClick={() => removeWidget(index)}
              className="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
            >
              Remove Widget
            </button>
          </div>
        </div>
      ))}
    </div>

      <div className="flex justify-between">
        <button
          type="button"
          onClick={addWidget}
          className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
        >
          Add Widget
        </button>
        <button
          type="submit"
          className="px-4 py-2 text-sm font-medium text-white bg-green-600 rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2"
        >
          Submit
        </button>
      </div>
    </form>
  );
}