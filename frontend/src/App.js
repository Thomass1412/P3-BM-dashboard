import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./Pages/loginPage.tsx";
import Supervisor from "./Pages/supervisorPage.tsx";
import Dashboard from "./Pages/dashboardPage.tsx";
import NewIntegration from "./Pages/newIntegrationPage.tsx";
import NewMetric from "./Pages/newMetricPage.tsx";
import GetInt from "./Pages/getntgrationpage.tsx";
import SnackbarProvider from "./Components/SnackbarProvider.js";
import CreateNewUser from "./Pages/CreateUserPage.tsx";
import IntegrationDetail from "./Pages/IntegrationDetail.tsx"; // Make sure to create this component
import CreateDashboard from "./Pages/CreateDashboardPage.tsx";

export default function App() {
  return (
    <BrowserRouter>
      <SnackbarProvider>
      <Routes>
          <Route path="/" element={<Login />}/>
          <Route path="/login" element={<Login />}/>
          <Route path="/supervisor" element={<Supervisor />}/>
          <Route path="/dashboard" element={<Dashboard />}/>
          <Route path="/newintegration" element={<NewIntegration />}/>
          <Route path="/newMetric" element={<NewMetric />}/>
          <Route path="/integrations" element={<GetInt />}/>
          <Route path="/newuser" element={<CreateNewUser />}/>
          <Route path="/integrations/:integrationId" element={<IntegrationDetail />}/>
          <Route path="/c" element={<CreateDashboard />}/>
        </Routes>
      </SnackbarProvider>
    </BrowserRouter>
  );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);