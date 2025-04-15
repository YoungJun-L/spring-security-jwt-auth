import { useContext } from "react";
import { useNavigate } from "react-router-dom";

import { App } from "antd";
import { AxiosError, AxiosResponse } from "axios";

import { useMutation } from "@tanstack/react-query";

import { ApiResponse } from "@type/api/ApiResponse";
import { UserToken } from "@type/domain/user";

import { client } from "@apis/client";

import { UserContext } from "@contexts/userContexts";

import { API_ENDPOINTS } from "@constants/endpoint";
import { ROUTE_PATHS } from "@constants/route";

interface RefreshTokenRequest {
  refreshToken: string;
}

export const useReissueToken = () => {
  const navigate = useNavigate();
  const { message } = App.useApp();
  const { user, saveUser } = useContext(UserContext);

  return useMutation({
    mutationFn: (values: RefreshTokenRequest) => {
      return client.post(API_ENDPOINTS.REISSUE_TOKEN, values);
    },
    onSuccess: (res: AxiosResponse<ApiResponse<UserToken>>) => {
      const data = res.data.data;
      saveUser({
        userId: user.userId,
        tokens: {
          accessToken: data.tokens.accessToken,
          accessTokenExpiration: data.tokens.accessTokenExpiration,
          refreshToken: data.tokens.refreshToken,
          refreshTokenExpiration: data.tokens.refreshTokenExpiration,
        },
      });
    },
    onError: (err: AxiosError<ApiResponse<null>>) => {
      message.error(err.response?.data.error?.message);
      navigate(ROUTE_PATHS.LOGIN);
    },
  });
};
