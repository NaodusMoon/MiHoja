import { DashboardShell } from "@/components/dashboard-shell";
import { getDashboardOverviewData } from "@/lib/people-service";

export default async function HomePage() {
  const overview = await getDashboardOverviewData();

  return <DashboardShell overview={overview} />;
}
