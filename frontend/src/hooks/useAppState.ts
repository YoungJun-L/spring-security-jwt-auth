import { useEffect, useState } from "react";

import { useTheme } from "antd-style";

import { STORAGE_KEYS } from "@constants/storage";

interface AppState {
  isMenuCollapsed: boolean;
  isDarkMode: boolean;
  selectedMenuKey: string;
}

const defaultAppState: AppState = {
  isMenuCollapsed: false,
  isDarkMode: false,
  selectedMenuKey: "1",
};

export const useAppState = () => {
  const { isDarkMode, setThemeMode } = useTheme();

  const [appState, setAppState] = useState<AppState>(() => {
    const menuCollapsed = localStorage.getItem(STORAGE_KEYS.MENU_COLLAPSED);
    const darkMode = localStorage.getItem(STORAGE_KEYS.DARK_MODE);
    const selectedMenu = localStorage.getItem(STORAGE_KEYS.SELECTED_MENU);

    return {
      isMenuCollapsed: menuCollapsed ? JSON.parse(menuCollapsed) : defaultAppState.isMenuCollapsed,
      isDarkMode: darkMode ? JSON.parse(darkMode) : isDarkMode,
      selectedMenuKey: selectedMenu ? JSON.parse(selectedMenu) : defaultAppState.selectedMenuKey,
    };
  });

  useEffect(() => {
    localStorage.setItem(STORAGE_KEYS.MENU_COLLAPSED, JSON.stringify(appState.isMenuCollapsed));
  }, [appState.isMenuCollapsed]);

  useEffect(() => {
    localStorage.setItem(STORAGE_KEYS.DARK_MODE, JSON.stringify(appState.isDarkMode));
  }, [appState.isDarkMode]);

  useEffect(() => {
    localStorage.setItem(STORAGE_KEYS.SELECTED_MENU, JSON.stringify(appState.selectedMenuKey));
  }, [appState.selectedMenuKey]);

  const toggleMenu = () => {
    setAppState((prev) => ({ ...prev, isMenuCollapsed: !prev.isMenuCollapsed }));
  };

  const toggleDarkMode = () => {
    const newDarkMode = !appState.isDarkMode;
    setAppState((prev) => ({ ...prev, isDarkMode: newDarkMode }));
    setThemeMode(newDarkMode ? "dark" : "light");
  };

  const setSelectedMenuKey = (key: string) => {
    setAppState((prev) => ({ ...prev, selectedMenuKey: key }));
  };

  return {
    ...appState,
    toggleMenu,
    toggleDarkMode,
    setSelectedMenuKey,
  };
};
