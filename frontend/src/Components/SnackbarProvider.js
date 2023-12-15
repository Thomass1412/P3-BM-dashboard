import React, { useState, createContext, useContext } from "react";
import CustomSnackbar from "./CustomSnackBar"; 

const SnackbarContext = createContext();

export const useSnackbar = () => {
  return useContext(SnackbarContext);
};

export default function SnackbarProvider({ children }) {
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarSeverity, setSnackbarSeverity] = useState("success");
  const [snackbarMessage, setSnackbarMessage] = useState("");

  // Function to show a Snackbar message
  const showSnackbar = (severity, message) => {
    setSnackbarSeverity(severity);
    setSnackbarMessage(message);
    setSnackbarOpen(true);
  };

  // Function to close the Snackbar
  const closeSnackbar = () => {
    setSnackbarOpen(false);
  };

  return (
    <SnackbarContext.Provider value={{ showSnackbar }}>
      {children}
      <CustomSnackbar
        open={snackbarOpen}
        onClose={closeSnackbar}
        severity={snackbarSeverity}
        message={snackbarMessage}
      />
    </SnackbarContext.Provider>
  );
}
