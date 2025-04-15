export interface User {
  userId: number;
}

export interface Token {
  accessToken: string;
  accessTokenExpiration: number;
  refreshToken: string;
  refreshTokenExpiration: number;
}

export interface UserToken {
  userId: number;
  tokens: Token;
}
