import {
  LogoutOutlined,
  MenuUnfoldOutlined,
  PieChartOutlined,
  UserOutlined,
} from "@ant-design/icons";
import { Button, Col, Layout, Menu, Row, theme } from "antd";

import { client } from "@apis/client";

import UserCountPie from "@components/pages/UserCountPie";

import { useAppState } from "@hooks/useAppState";

import { API_ENDPOINTS } from "@constants/endpoint";
import { ROUTE_PATHS } from "@constants/route";
import { STORAGE_KEYS } from "@constants/storage";

import DailyUserCountLine from "./DailyUserCountLine";
import UserCountStatistic from "./UserCountStatistic";
import UserTable from "./UserTable";

const { Sider, Header, Content } = Layout;
const items = [
  {
    key: "1",
    label: "Dashboard",
    icon: <PieChartOutlined />,
  },
  {
    key: "2",
    label: "Users",
    icon: <UserOutlined />,
  },
];

const logout = () => {
  localStorage.removeItem(STORAGE_KEYS.USER);
  window.location.href = ROUTE_PATHS.LOGIN;
  client.post(API_ENDPOINTS.LOGOUT);
};

const MainPage = () => {
  const { isMenuCollapsed, selectedMenuKey, toggleMenu, setSelectedMenuKey } = useAppState();
  const {
    token: { colorBgContainer },
  } = theme.useToken();

  return (
    <Layout style={{ height: "100vh" }}>
      <Sider trigger={null} collapsible collapsed={isMenuCollapsed}>
        <div style={{ height: 64 }} />
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            flexDirection: "column",
            height: "calc(100vh - 64px)",
          }}
        >
          <Menu
            theme="dark"
            mode="inline"
            defaultSelectedKeys={[selectedMenuKey]}
            items={items}
            onClick={({ key }) => setSelectedMenuKey(key)}
          />
          <div style={{ bottom: 0 }}>
            <Menu
              theme="dark"
              mode="inline"
              items={[
                {
                  key: "logout",
                  label: "Logout",
                  icon: <LogoutOutlined />,
                  onClick: () => logout(),
                },
              ]}
            />
          </div>
        </div>
      </Sider>
      <Layout>
        <Header style={{ padding: 0, height: 64, background: colorBgContainer }}>
          <Button
            type="text"
            icon={<MenuUnfoldOutlined />}
            onClick={toggleMenu}
            style={{
              fontSize: "16px",
              width: 64,
              height: 64,
            }}
          />
        </Header>
        <Content style={{ margin: "24px 24px" }}>
          <Row gutter={[16, 24]}>
            <Col span={8}>
              <Col
                style={{
                  height: "200px",
                  background: colorBgContainer,
                  borderRadius: "8px",
                  padding: "16px",
                  display: "flex",
                }}
              >
                <Col
                  span={8}
                  style={{
                    display: "flex",
                    flexDirection: "column",
                    justifyContent: "center",
                  }}
                >
                  <UserCountStatistic />
                </Col>
                <Col span={16}>
                  <UserCountPie />
                </Col>
              </Col>
            </Col>
            <Col
              span={16}
              style={{
                height: "200px",
                background: colorBgContainer,
                borderRadius: "8px",
                padding: "16px",
              }}
            >
              <DailyUserCountLine />
            </Col>
            <Col span={24}>
              <UserTable />
            </Col>
          </Row>
        </Content>
      </Layout>
    </Layout>
  );
};

export default MainPage;
