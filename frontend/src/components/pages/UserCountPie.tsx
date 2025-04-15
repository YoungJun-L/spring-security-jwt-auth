import { Pie } from "@ant-design/charts";

import { useUserCount } from "@hooks/useUserCount";

const UserCountPie = () => {
  const { data } = useUserCount();
  const userStats = [
    { type: "Inactive Users", value: data?.inactiveUserCount },
    { type: "Active Users", value: data?.activeUserCount },
  ];

  return (
    <div style={{ height: "100%", width: "100%" }}>
      <Pie
        data={userStats}
        angleField="value"
        colorField="type"
        legend={false}
        interactions={[
          {
            type: "element-active",
          },
        ]}
      />
    </div>
  );
};

export default UserCountPie;
