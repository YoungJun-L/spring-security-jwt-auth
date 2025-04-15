import { useQuery } from "@tanstack/react-query";

import { client } from "@apis/client";

import { API_ENDPOINTS } from "@constants/endpoint";

interface DailyUserCountsResponse {
  dailyUserStats: DailyUserCountResponse[];
}

interface DailyUserCountResponse {
  statDate: Date;
  activeUser: number;
}

export const useDailyUserCount = () => {
  return useQuery<DailyUserCountsResponse>({
    queryKey: ["dailyUserCount"],
    queryFn: () => client.get(API_ENDPOINTS.DAILY_USER_COUNT).then((res) => res.data),
  });
};
