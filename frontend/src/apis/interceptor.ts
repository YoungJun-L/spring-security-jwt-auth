import axios, { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from "axios";

import { ApiResponse } from "@type/api/ApiResponse";
import { Token, UserToken } from "@type/domain/user";

import { API_ENDPOINTS } from "@constants/endpoint";
import { ERROR_CODES } from "@constants/errorCodes";
import { HTTP_STATUS_CODES } from "@constants/httpStatusCode";
import { ROUTE_PATHS } from "@constants/route";
import { STORAGE_KEYS } from "@constants/storage";

const setAuthorizationHeader = (config: InternalAxiosRequestConfig, accessToken: string | null) => {
  if (!config.headers || config.headers.Authorization || !accessToken) return config;

  config.headers.Authorization = `Bearer ${accessToken}`;
  return config;
};

export const handlePreviousRequest = (config: InternalAxiosRequestConfig) => {
  const user = JSON.parse(localStorage.getItem(STORAGE_KEYS.USER) ?? "{}");
  return setAuthorizationHeader(config, user?.tokens?.accessToken ?? "");
};

export const handleApiError = async (error: AxiosError<ApiResponse<null>>) => {
  if (
    error.response?.status === HTTP_STATUS_CODES.UNAUTHORIZED &&
    (error.response?.data.error?.code === ERROR_CODES.EXPIRED_TOKEN_ERROR ||
      error.response?.data.error?.code === ERROR_CODES.INVALID_TOKEN_ERROR)
  ) {
    try {
      if (localStorage.getItem(STORAGE_KEYS.USER) === null) {
        handleUserLogout();
        return;
      }

      const user: UserToken = JSON.parse(localStorage.getItem(STORAGE_KEYS.USER) ?? "{}");

      axios.defaults.baseURL = process.env.REACT_APP_API_URL;
      const response: AxiosResponse<ApiResponse<Token>> = await axios.post(
        API_ENDPOINTS.REISSUE_TOKEN,
        { refreshToken: user?.tokens.refreshToken },
      );

      const { accessToken, accessTokenExpiration, refreshToken, refreshTokenExpiration } =
        response.data.data;

      localStorage.setItem(
        STORAGE_KEYS.USER,
        JSON.stringify({
          userId: user.userId,
          tokens: {
            accessToken: accessToken,
            accessTokenExpiredAt: accessTokenExpiration,
            refreshToken: refreshToken.length > 0 ? refreshToken : user.tokens.refreshToken,
            refreshTokenExpiration:
              refreshTokenExpiration !== 0
                ? refreshTokenExpiration
                : user.tokens.refreshTokenExpiration,
          },
        }),
      );

      if (error.config?.headers) {
        error.config.headers.Authorization = `Bearer ${accessToken}`;
        return axios.request(error.config);
      }
    } catch (refreshTokenError) {
      handleUserLogout();
      return;
    }
  }
  throw error;
};

const handleUserLogout = () => {
  localStorage.removeItem(STORAGE_KEYS.USER);
  window.location.href = ROUTE_PATHS.LOGIN;
};
