// Import the necessary components
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./Pages/loginPage.tsx";
import Supervisor from "./Pages/supervisorPage.tsx";
import Dashboard from "./Pages/dashboardPage.tsx";
import NewIntegration from "./Pages/newIntegrationPage.tsx";
import NewMetric from "./Pages/newMetricPage.tsx";
import GetInt from "./Pages/getint.tsx";
import CreateNewUser from "./Pages/CreateUserPage.tsx";
import IntegrationDetail from "./Pages/IntegrationDetail.tsx"; // Make sure to create this component
import IntegrationsList from './Pages/IntegrationsList.tsx';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />}/>
        <Route path="/login" element={<Login />}/>
        <Route path="/supervisor" element={<Supervisor />}/>
        <Route path="/dashboard" element={<Dashboard />}/>
        <Route path="/integrations" element={<IntegrationsList />}/>
        <Route path="/newintegration" element={<NewIntegration />}/>
        <Route path="/newMetric" element={<NewMetric />}/>
        <Route path="/getint" element={<GetInt />}/>
        <Route path="/newuser" element={<CreateNewUser />}/>
        {/* Add a new dynamic route for integration details */}
        <Route path="/integrations/:integrationId" element={<IntegrationDetail />}/>
      </Routes>
    </BrowserRouter>
  );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);
