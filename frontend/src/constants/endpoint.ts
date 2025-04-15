export const API_ENDPOINTS = {
  LOGIN: "/auth/login",
  REISSUE_TOKEN: "/auth/token",
  LOGOUT: "/auth/logout",
  USERS: "/users",
  USER_COUNT: "/users/count",
  DAILY_USER_COUNT: "/stats/daily-user-stats",
} as const;
