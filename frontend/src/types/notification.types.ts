export type NotificationType =
  | 'SCHEDULE_CREATED'
  | 'SCHEDULE_UPDATED'
  | 'SCHEDULE_DELETED'
  | 'SCHEDULE_REMINDER'
  | 'MEMBERSHIP_REQUESTED'
  | 'MEMBERSHIP_APPROVED'
  | 'MEMBERSHIP_REJECTED'
  | 'COMMENT_CREATED';

export interface Notification {
  id: number;
  groupId: number;
  groupName: string;
  link: string | null;
  type: NotificationType;
  message: string;
  read: boolean;
  createdAt: string;
}

export interface UnreadCount {
  unreadCount: number;
}
