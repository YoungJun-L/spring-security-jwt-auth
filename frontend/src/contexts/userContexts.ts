import { createContext } from "react";

import { UserToken } from "@type/domain/user";

export interface UserContextProps {
  user: UserToken;
  saveUser: (userInfo: UserToken) => void;
}

export const UserContext = createContext({} as UserContextProps);
