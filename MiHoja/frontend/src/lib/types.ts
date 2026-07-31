export type DashboardMetric = {
  id: string;
  label: string;
  value: string;
  tone: string;
};

export type DashboardPersonCard = {
  id: number;
  numero: number | null;
  nombres: string;
  apellidos: string;
  cedula: string;
  cargo: string | null;
  dependencia: string | null;
  correoInstitucional: string | null;
  telefonoInstitucional: string | null;
  estado: string | null;
  imagenUrl: string | null;
};

export type DashboardOverview = {
  metrics: DashboardMetric[];
  recentPeople: DashboardPersonCard[];
  highlights: string[];
};

export type DashboardFilterOptions = {
  sexo: string[];
  lugarExpedicion: string[];
  formacion: string[];
  dependencia: string[];
  cargo: string[];
};

export type DashboardPeopleResponse = {
  people: DashboardPersonCard[];
  metrics: DashboardMetric[];
  filterOptions: DashboardFilterOptions;
  total: number;
  page: number;
  size: number;
  totalPages: number;
  duplicateCount: number;
  activeFilterCount: number;
  query: string;
};

export type DashboardFilters = {
  sexo: string[];
  lugarExpedicion: string[];
  formacion: string[];
  dependencia: string[];
  cargo: string[];
};

export type DashboardDeleteResponse = {
  message: string;
  deletedCount: number;
  failedIds: number[];
};

export type DashboardCleanupResponse = {
  message: string;
  personasRevisadas: number;
  alergiasEliminadas: number;
  medicamentosEliminados: number;
  enfermedadesEliminadas: number;
};

export type DashboardNotification = {
  id: string;
  title: string;
  description: string;
  tone: string;
};

export type DashboardNotificationsResponse = {
  notifications: DashboardNotification[];
  unreadCount: number;
  message?: string;
};
