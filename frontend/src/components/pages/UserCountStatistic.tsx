import { Statistic } from "antd";

import { useUserCount } from "@hooks/useUserCount";

const UserCountStatistic = () => {
  const { data } = useUserCount();

  return (
    <>
      <Statistic
        title="Active Users"
        value={data?.activeUserCount}
        valueStyle={{ fontSize: "24px", fontWeight: "bold" }}
      />
      <Statistic
        title="Total Users"
        value={data?.totalUserCount}
        valueStyle={{ fontSize: "24px", fontWeight: "bold" }}
      />
    </>
  );
};

export default UserCountStatistic;
