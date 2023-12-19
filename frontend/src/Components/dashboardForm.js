import React, { useState, useEffect } from 'react';

export default function DashboardForm() {
  const [dashboardName, setDashboardName] = useState('');
  const [widgets, setWidgets] = useState([{ name: '', type: '', options: {} }]);
  const [metrics, setMetrics] = useState([]);

  useEffect(() => {
    fetch('http://localhost/api/v1/metric/pageable')
      .then(response => response.json())
      .then(data => setMetrics(data.content)) // Assuming the metrics are in the 'content' key
      .catch(error => console.error('Error fetching metrics:', error));
  }, []);

  const handleDashboardNameChange = (event) => {
    setDashboardName(event.target.value);
  };

  const handleWidgetChange = (index, key, value) => {
    const updatedWidgets = [...widgets];
    updatedWidgets[index][key] = value;
    setWidgets(updatedWidgets);
  };

  const addWidget = () => {
    setWidgets([...widgets, { name: '', type: '', options: {} }]);
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
    // ... Submit to backend ...
  };

  const handleOptionsChange = (index, key, value) => {
    const updatedWidgets = [...widgets];
    updatedWidgets[index].options[key] = value;
    setWidgets(updatedWidgets);
  };

  // Widget Types Enum
  const widgetTypes = ['LEADERBOARD', 'NUMBER', 'TABLE'];

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
          className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
          placeholder="Enter dashboard name"
        />
      </div>

      {widgets.map((widget, index) => (
        <div key={index} className="border p-4 rounded-lg mb-4">
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
              {widgetTypes.map((type) => (
                <option key={type} value={type}>{type}</option>
              ))}
            </select>
          </div>
          <div className="mt-4">
            {/* Example: Dropdown for Metric ID */}
            <label htmlFor={`widget-metric-${index}`} className="block text-sm font-medium text-gray-700">
              Metric ID
            </label>
            <select
              id={`widget-metric-${index}`}
              name={`widget-metric-${index}`}
              value={widget.options.metricId || ''}
              onChange={(e) => handleOptionsChange(index, 'metricId', e.target.value)}
              className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            >
              {metrics.map(metric => (
                <option key={metric.id} value={metric.id}>{metric.name}</option>
              ))}
            </select>
          </div>
          {/* Additional fields for widget options can be added here */}
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