import axios from "axios";

import { handleApiError, handlePreviousRequest } from "@apis/interceptor";

export const client = axios.create({
  baseURL: process.env.REACT_APP_API_URL,
});

client.interceptors.request.use(handlePreviousRequest);
client.interceptors.response.use((response) => response, handleApiError);
