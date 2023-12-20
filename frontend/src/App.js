import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./Pages/loginPage.tsx";
import Supervisor from "./Pages/supervisorPage.tsx";
import Dashboard from "./Pages/dashboardPage.tsx";
import NewIntegration from "./Pages/newIntegrationPage.tsx";
import NewMetric from "./Pages/newMetricPage.tsx";
import GetInt from "./Pages/getntgrationpage.tsx";
import Getmetric from "./Pages/getMetricpage.tsx";
import SnackbarProvider from "./Components/SnackbarProvider.js";
import CreateNewUser from "./Pages/CreateUserPage.tsx";
import IntegrationDetail from "./Pages/IntegrationDetail.tsx"; // Make sure to create this component
import CreateDashboard from "./Pages/CreateDashboardPage.tsx";
import { Navigate, Outlet } from 'react-router-dom';

export default function App() {

  const isAuthenticated = () => {
    // Split the document.cookie string on semicolons
    const cookies = document.cookie.split('; ');
  
    // Find the 'login' cookie
    const loginCookie = cookies.find(cookie => cookie.startsWith('login='));
  
    // Check if the loginCookie is not null and has a value
    return loginCookie != null && loginCookie.split('=')[1] !== '';
  };

  const ProtectedRoute = () => {
    return isAuthenticated() ? <Outlet /> : <Navigate to="/login" />;
  };
  return (
    <BrowserRouter>
      <SnackbarProvider>
      <Routes>
          <Route path="/" element={<Login />}/>
          <Route path="/login" element={<Login />}/>
          <Route element={<ProtectedRoute />}>
            <Route path="/supervisor" element={<Supervisor />}/>
            <Route path="/dashboard/:id" element={<Dashboard />}/>
            <Route path="/newintegration" element={<NewIntegration />}/>
            <Route path="/newMetric" element={<NewMetric />}/>
            <Route path="/integrations" element={<GetInt />}/>
            <Route path="/metrics" element={<Getmetric />}/>
            <Route path="/newuser" element={<CreateNewUser />}/>
            <Route path="/integrations/:integrationId" element={<IntegrationDetail />}/>
            <Route path="/createdashboard" element={<CreateDashboard />}/>
          </Route>
        </Routes>
      </SnackbarProvider>
    </BrowserRouter>
  );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);