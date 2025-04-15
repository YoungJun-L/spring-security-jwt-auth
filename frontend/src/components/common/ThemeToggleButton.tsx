import { MoonOutlined, SunOutlined } from "@ant-design/icons";
import { Button } from "antd";

import { useAppState } from "@hooks/useAppState";

import { useStyles } from "./ThemeToggleButton.styles";

const ThemeToggleButton = () => {
  const { isDarkMode, toggleDarkMode } = useAppState();
  const { styles } = useStyles();

  return (
    <Button
      type="text"
      icon={isDarkMode ? <SunOutlined /> : <MoonOutlined />}
      onClick={toggleDarkMode}
      className={styles.button}
    />
  );
};

export default ThemeToggleButton;
