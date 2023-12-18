import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./Pages/loginPage.tsx";
import Supervisor from "./Pages/supervisorPage.tsx";
import Dashboard from "./Pages/dashboardPage.tsx";
import NewIntergration from "./Pages/newIntergrationPage.tsx";
import NewMetric from "./Pages/newMetricPage.tsx";
import GetInt from "./Pages/getntgrationpage.tsx";
import SnackbarProvider from "./Components/SnackbarProvider.js";
import CreateNewUser from "./Pages/CreateUserPage.tsx";
import IntegrationDetail from "./Pages/IntegrationDetail.tsx"; // Make sure to create this component
import IntegrationsList from './Pages/IntegrationsList.tsx';

export default function App() {
  return (
    <BrowserRouter>
      <SnackbarProvider>
      <Routes>
          <Route path="/" element={<Login />}/>
          <Route path="/login" element={<Login />}/>
          <Route path="/supervisor" element={<Supervisor />}/>
          <Route path="/dashboard" element={<Dashboard />}/>
          <Route path="/newintergration" element={<NewIntergration />}/>
          <Route path="/newMetric" element={<NewMetric />}/>
          <Route path="/viewintergrations" element={<GetInt />}/>
          <Route path="/newuser" element={<CreateNewUser />}/>
          <Route path="/integrations" element={<IntegrationsList />}/>
          <Route path="/integrations/:integrationId" element={<IntegrationDetail />}/>
        </Routes>
      </SnackbarProvider>
    </BrowserRouter>
  );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);