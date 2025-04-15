import { Line } from "@ant-design/charts";

import { useDailyUserCount } from "@hooks/useDaiylUserCount";

const DailyUserCountLine = () => {
  const { data } = useDailyUserCount();

  return <Line data={data?.dailyUserStats} xField="statDate" yField="activeUser" height={200} />;
};

export default DailyUserCountLine;
