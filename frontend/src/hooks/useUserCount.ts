import { useQuery } from "@tanstack/react-query";

import { client } from "@apis/client";

import { API_ENDPOINTS } from "@constants/endpoint";

interface UserCountResponse {
  totalUserCount: number;
  activeUserCount: number;
  inactiveUserCount: number;
}

export const useUserCount = () => {
  return useQuery<UserCountResponse>({
    queryKey: ["userCount"],
    queryFn: () => client.get(API_ENDPOINTS.USER_COUNT).then((res) => res.data),
  });
};
