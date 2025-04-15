import { useContext, useEffect } from "react";
import { Outlet, useNavigate } from "react-router-dom";

import { UserContext } from "@contexts/userContexts";

import { ROUTE_PATHS } from "@constants/route";

const ProtectedRoute = () => {
  const navigate = useNavigate();
  const { user } = useContext(UserContext);

  // useEffect(() => {
  //   const isLoggedIn = user?.tokens?.accessTokenExpiration
  //     ? user.tokens.accessTokenExpiration >= Math.floor(Date.now() / 1_000)
  //     : false;

  //   if (!isLoggedIn) {
  //     navigate(ROUTE_PATHS.login);
  //   }
  // }, [user, navigate]);
  return <Outlet />;
};

export default ProtectedRoute;
