import { useCallback, useMemo, useState } from "react";

import { UserToken } from "@type/domain/user";

import { STORAGE_KEYS } from "@constants/storage";

import { UserContext } from "./userContexts";

const UserProvider = ({ children }: React.PropsWithChildren) => {
  const [user, setUser] = useState<UserToken>(() =>
    JSON.parse(localStorage.getItem(STORAGE_KEYS.USER) ?? "{}"),
  );
  const saveUser = useCallback((user: UserToken) => {
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(user));
    setUser(user);
  }, []);

  const userValue = useMemo(() => ({ user, saveUser }), [user, saveUser]);
  return <UserContext.Provider value={userValue}>{children}</UserContext.Provider>;
};

export default UserProvider;
