interface ApiErrorResponse {
  code: string;
  data: unknown;
  message: string;
}

export interface ApiResponse<T> {
  data: T;
  error: ApiErrorResponse | null;
  status: "ERROR" | "SUCCESS";
}
