import axios from 'axios';
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  Event,
  Booking,
  CreateBookingRequest,
  BookingUpdateRequest,
  EventCreateRequest,
  EventUpdateRequest,
  NotificationPreferences,
  Pageable,
  PageableEventResponse,
  PageableBookingResponse,
} from '../types';
import { mockApiService } from './mockApi';

class ApiService {
  private api: any;
  private baseURL: string;
  private useMocks: boolean;

  constructor() {
    this.baseURL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
    this.useMocks = process.env.REACT_APP_USE_MOCKS === 'true' || !process.env.REACT_APP_API_URL;
    
    if (this.useMocks) {
      console.log('🔧 Using mock API service for demonstration');
      return;
    }

    this.api = axios.create({
      baseURL: this.baseURL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Request interceptor to add auth token
    this.api.interceptors.request.use(
      (config: any) => {
        const token = localStorage.getItem('token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error: any) => {
        return Promise.reject(error);
      }
    );

    // Response interceptor to handle auth errors
    this.api.interceptors.response.use(
      (response: any) => response,
      (error: any) => {
        if (error.response?.status === 401) {
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          window.location.href = '/';
        }
        return Promise.reject(error);
      }
    );
  }

  // Auth endpoints
  async login(data: LoginRequest): Promise<AuthResponse> {
    if (this.useMocks) {
      return mockApiService.login(data);
    }
    const response = await this.api.post('/auth/login', data);
    return response.data;
  }

  async register(data: RegisterRequest): Promise<AuthResponse> {
    if (this.useMocks) {
      return mockApiService.register(data);
    }
    const response = await this.api.post('/auth/register', data);
    return response.data;
  }

  // Events endpoints
  async getEvents(pageable: Pageable, from?: string, to?: string): Promise<PageableEventResponse> {
    if (this.useMocks) {
      return mockApiService.getEvents(pageable, from, to);
    }
    const params = new URLSearchParams();
    params.append('pageable', JSON.stringify(pageable));
    if (from) params.append('from', from);
    if (to) params.append('to', to);
    
    const response = await this.api.get(`/events?${params}`);
    return response.data;
  }

  async getEvent(id: number): Promise<Event> {
    if (this.useMocks) {
      return mockApiService.getEvent(id);
    }
    const response = await this.api.get(`/events/${id}`);
    return response.data;
  }

  // Admin events endpoints
  async createEvent(data: EventCreateRequest): Promise<Event> {
    if (this.useMocks) {
      return mockApiService.createEvent(data);
    }
    const response = await this.api.post('/admin/events', data);
    return response.data;
  }

  async updateEvent(id: number, data: EventUpdateRequest): Promise<Event> {
    if (this.useMocks) {
      return mockApiService.updateEvent(id, data);
    }
    const response = await this.api.put(`/admin/events/${id}`, data);
    return response.data;
  }

  async deleteEvent(id: number): Promise<void> {
    if (this.useMocks) {
      return mockApiService.deleteEvent(id);
    }
    await this.api.delete(`/admin/events/${id}`);
  }

  // Bookings endpoints
  async getBookings(): Promise<Booking[]> {
    if (this.useMocks) {
      return mockApiService.getBookings();
    }
    const response = await this.api.get('/bookings');
    return response.data;
  }

  async createBooking(data: CreateBookingRequest): Promise<Booking> {
    if (this.useMocks) {
      return mockApiService.createBooking(data);
    }
    const response = await this.api.post('/bookings', data);
    return response.data;
  }

  async updateBooking(id: number, data: BookingUpdateRequest): Promise<Booking> {
    if (this.useMocks) {
      return mockApiService.updateBooking(id, data);
    }
    const response = await this.api.put(`/bookings/${id}`, data);
    return response.data;
  }

  async deleteBooking(id: number): Promise<void> {
    if (this.useMocks) {
      return mockApiService.deleteBooking(id);
    }
    await this.api.delete(`/bookings/${id}`);
  }

  // Admin bookings endpoints
  async getAdminBookings(pageable: Pageable, eventId?: number, unconfirmedOnly?: boolean): Promise<PageableBookingResponse> {
    if (this.useMocks) {
      return mockApiService.getAdminBookings(pageable, eventId, unconfirmedOnly);
    }
    const params = new URLSearchParams();
    params.append('pageable', JSON.stringify(pageable));
    if (eventId) params.append('eventId', eventId.toString());
    if (unconfirmedOnly !== undefined) params.append('unconfirmedOnly', unconfirmedOnly.toString());
    
    const response = await this.api.get(`/admin/bookings?${params}`);
    return response.data;
  }

  async confirmBooking(id: number): Promise<void> {
    if (this.useMocks) {
      return mockApiService.confirmBooking(id);
    }
    await this.api.put(`/admin/bookings/${id}/confirm`);
  }

  async deleteAdminBooking(id: number): Promise<void> {
    if (this.useMocks) {
      return mockApiService.deleteAdminBooking(id);
    }
    await this.api.delete(`/admin/bookings/${id}`);
  }

  // User notifications endpoints
  async getNotificationPreferences(): Promise<NotificationPreferences> {
    if (this.useMocks) {
      return mockApiService.getNotificationPreferences();
    }
    const response = await this.api.get('/user/notifications');
    return response.data;
  }

  async updateNotificationPreferences(data: NotificationPreferences): Promise<void> {
    if (this.useMocks) {
      return mockApiService.updateNotificationPreferences(data);
    }
    await this.api.put('/user/notifications', data);
  }

  async deleteNotificationPreferences(): Promise<void> {
    if (this.useMocks) {
      return mockApiService.deleteNotificationPreferences();
    }
    await this.api.delete('/user/notifications');
  }

  // Telegram link endpoint
  async linkTelegram(): Promise<string> {
    if (this.useMocks) {
      return mockApiService.linkTelegram();
    }
    const response = await this.api.post('/user/telegram/link');
    return response.data;
  }
}

export const apiService = new ApiService(); 