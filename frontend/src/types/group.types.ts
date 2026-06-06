export interface PageResponse<T> {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface GroupSummary {
  id: number;
  name: string;
  description: string;
  category: string;
  maxMemberCount: number;
  currentMemberCount: number;
  createdAt: string;
}

export interface GroupDetail extends GroupSummary {
  ownerId: number;
  updatedAt: string;
}

export interface GroupFormData {
  name: string;
  description: string;
  category: string;
  maxMemberCount: number;
}

export interface GroupSearchParams {
  keyword?: string;
  page?: number;
  size?: number;
}
