import { Table } from "antd";

import { useUsers } from "@hooks/useUsers";

const UserTable = () => {
  const { data, fetchNextPage } = useUsers();
  const users = data?.pages.flatMap((page) => page.users) ?? [];
  const lastId = data?.pages[data?.pages.length - 1]?.lastId ?? -1;

  const columns = [
    {
      title: "ID",
      dataIndex: "id",
      key: "id",
      width: 80,
      fixed: "left" as const,
    },
    {
      title: "Name",
      dataIndex: ["profile", "name"],
      key: "name",
      width: 150,
    },
    {
      title: "Email",
      dataIndex: "email",
      key: "email",
      width: 200,
    },
    {
      title: "Phone",
      dataIndex: ["profile", "phoneNumber"],
      key: "phone",
      width: 200,
    },
    {
      title: "Country",
      dataIndex: ["profile", "country"],
      key: "country",
      width: 100,
      fixed: "right" as const,
    },
  ];

  return (
    <div
      style={{
        height: "calc(100vh - 300px)",
        overflow: "hidden",
      }}
    >
      <Table
        columns={columns}
        dataSource={users}
        rowKey="id"
        pagination={false}
        scroll={{ x: "max-content", y: "calc(100vh - 300px)" }}
        sticky={{ offsetHeader: 0 }}
        loading={false}
        onScroll={({ currentTarget }) => {
          const { scrollTop, scrollHeight, clientHeight } = currentTarget;
          if (scrollHeight - scrollTop === clientHeight && lastId !== -1) {
            fetchNextPage();
          }
        }}
        style={{
          height: "100vh",
        }}
      />
    </div>
  );
};

export default UserTable;
