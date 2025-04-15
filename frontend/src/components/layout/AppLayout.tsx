import { Outlet } from "react-router-dom";

import { ConfigProvider, Layout, theme } from "antd";
import { useTheme } from "antd-style";

import ThemeToggleButton from "@components/common/ThemeToggleButton";

const customDarkTheme = {
  algorithm: theme.darkAlgorithm,
  token: {
    colorBgLayout: "#1a1a1a",
    colorBgContainer: "#262626",
    colorBgElevated: "#333333",
  },
};

const AppLayout = () => {
  const { isDarkMode } = useTheme();

  return (
    <ConfigProvider theme={isDarkMode ? customDarkTheme : undefined}>
      <Layout style={{ minHeight: "100vh" }}>
        <ThemeToggleButton />
        <Outlet />
      </Layout>
    </ConfigProvider>
  );
};

export default AppLayout;
