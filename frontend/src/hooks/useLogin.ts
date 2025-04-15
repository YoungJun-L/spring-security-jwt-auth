import { useContext } from "react";
import { useNavigate } from "react-router-dom";

import { App } from "antd";
import axios, { AxiosError, AxiosResponse } from "axios";

import { useMutation } from "@tanstack/react-query";

import { ApiResponse } from "@type/api/ApiResponse";
import { UserToken } from "@type/domain/user";

import { UserContext } from "@contexts/userContexts";

import { API_ENDPOINTS } from "@constants/endpoint";
import { ROUTE_PATHS } from "@constants/route";

interface LoginForm {
  username: string;
  password: string;
}

export const useLogin = () => {
  const navigate = useNavigate();
  const { message } = App.useApp();
  const { saveUser } = useContext(UserContext);

  return useMutation({
    mutationFn: (values: LoginForm) => {
      return axios.post(process.env.REACT_APP_API_URL + API_ENDPOINTS.LOGIN, values);
    },
    onSuccess: async (res: AxiosResponse<ApiResponse<UserToken>>) => {
      const data = res.data.data;
      saveUser({
        userId: data.userId,
        tokens: {
          accessToken: data.tokens.accessToken,
          accessTokenExpiration: data.tokens.accessTokenExpiration,
          refreshToken: data.tokens.refreshToken,
          refreshTokenExpiration: data.tokens.refreshTokenExpiration,
        },
      });
      navigate(ROUTE_PATHS.ROOT);
    },
    onError: (err: AxiosError<ApiResponse<null>>) => {
      message.error(err.response?.data.error?.message);
      navigate(ROUTE_PATHS.LOGIN);
    },
  });
};
