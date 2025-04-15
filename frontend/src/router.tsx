import { createBrowserRouter } from "react-router-dom";

import ProtectedRoute from "@components/common/ProtectedRoute";
import AppLayout from "@components/layout/AppLayout";
import LoginPage from "@components/pages/LoginPage";
import MainPage from "@components/pages/MainPage";

import { ROUTE_PATHS } from "./constants/route";

export const router = createBrowserRouter([
  {
    element: <AppLayout />,
    children: [
      {
        path: ROUTE_PATHS.LOGIN,
        element: <LoginPage />,
      },
      {
        element: <ProtectedRoute />,
        children: [
          {
            path: ROUTE_PATHS.ROOT,
            element: <MainPage />,
          },
        ],
      },
    ],
  },
]);
