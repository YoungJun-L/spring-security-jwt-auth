import { RouterProvider } from "react-router-dom";

import { App } from "antd";
import { ThemeProvider } from "antd-style";

import UserProvider from "@contexts/UserProvider";

import { useAppState } from "@hooks/useAppState";

import { router } from "./router";

const AdminApp = () => {
  const { isDarkMode } = useAppState();
  return (
    <ThemeProvider defaultThemeMode={isDarkMode ? "dark" : "light"}>
      <App>
        <UserProvider>
          <RouterProvider router={router} />
        </UserProvider>
      </App>
    </ThemeProvider>
  );
};

export default AdminApp;
