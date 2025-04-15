import { InfiniteData, useInfiniteQuery } from "@tanstack/react-query";

import { client } from "@apis/client";

import { API_ENDPOINTS } from "@constants/endpoint";

interface UserResponse {
  users: UserInfo[];
  lastId: number;
}

interface UserInfo {
  id: number;
  email: string;
  profile: {
    name: string;
    nickname: string;
    phoneNumber: string;
    country: string;
  };
}

interface PageParam {
  lastId: number;
  pageSize: number;
}

export const useUsers = () => {
  return useInfiniteQuery<UserResponse, Error, InfiniteData<UserResponse>, string[], PageParam>({
    queryKey: ["users"],
    queryFn: ({ pageParam: { lastId, pageSize } }) =>
      client
        .get(API_ENDPOINTS.USERS, { params: { nextId: lastId, pageSize: pageSize } })
        .then((res) => res.data),
    getNextPageParam: (res, _, lastPageParam) => ({
      lastId: res.lastId,
      pageSize: lastPageParam.pageSize,
    }),
    initialPageParam: { lastId: Number.MAX_SAFE_INTEGER, pageSize: 20 },
  });
};
