import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./Pages/loginPage.tsx";
import Supervisor from "./Pages/supervisorPage.tsx";
import Dashboard from "./Pages/dashboardPage.tsx";
import NewIntergration from "./Pages/newIntergrationPage.tsx";
import NewMetric from "./Pages/newMetricPage.tsx";
import GetInt from "./Pages/getint.tsx";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />}/>
        <Route path="/login" element={<Login />}/>
        <Route path="/supervisor" element={<Supervisor />}/>
        <Route path="/dashboard" element={<Dashboard />}/>
        <Route path="/newintergration" element={<NewIntergration />}/>
        <Route path="/newMetric" element={<NewMetric />}/>
        <Route path="/getint" element={<GetInt />}/>

      </Routes>
    </BrowserRouter>
  );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);