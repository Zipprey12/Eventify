export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: 'USER' | 'ADMIN';
}

export interface Event {
  id: number;
  title: string;
  description: string;
  dateTime: string;
  totalTickets: number;
  availableTickets: number;
  coverUrl?: string;
}

export interface Booking {
  id: number;
  event: Event;
  customerEmail: string;
  ticketsCount: number;
  confirmed: boolean;
  createdAt: string;
  expiryTime?: string;
}

export interface NotificationPreferences {
  notifyNewEvents: boolean;
  notifyUpcoming: boolean;
  notifyBeforeHours: number;
  emailConfirmed?: boolean;
}

export interface AuthResponse {
  token: string;
  role: 'USER' | 'ADMIN';
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
}

export interface CreateBookingRequest {
  eventId: number;
  ticketsCount: number;
}

export interface BookingUpdateRequest {
  ticketsCount: number;
}

export interface EventCreateRequest {
  title: string;
  description?: string;
  dateTime: string;
  totalTickets: number;
  coverUrl?: string;
}

export interface EventUpdateRequest {
  title?: string;
  description?: string;
  dateTime?: string;
  totalTickets?: number;
  coverUrl?: string;
}

export interface Pageable {
  page: number;
  size: number;
  sort?: string[];
}

export interface PageableEventResponse {
  content: Event[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface PageableBookingResponse {
  content: Booking[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface ErrorResponse {
  code: string;
  level: 'info' | 'warning' | 'error';
  message: string;
  details?: Array<{
    field: string;
    message: string;
  }>;
}