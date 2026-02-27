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
  User,
} from '../types';

// Mock data
let mockEvents: Event[] = [
  {
    id: 1,
    title: 'Конференция по веб-разработке',
    description: 'Ежегодная конференция для веб-разработчиков с докладами от ведущих специалистов индустрии.',
    dateTime: '2024-03-15T10:00:00Z',
    totalTickets: 100,
    availableTickets: 87,
    coverUrl: 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=400&h=300&fit=crop',
  },
  {
    id: 2,
    title: 'Мастер-класс по React',
    description: 'Практический мастер-класс по созданию современных веб-приложений с использованием React.',
    dateTime: '2024-03-20T14:00:00Z',
    totalTickets: 50,
    availableTickets: 40,
    coverUrl: 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=400&h=300&fit=crop',
  },
  {
    id: 3,
    title: 'Встреча IT-сообщества',
    description: 'Неформальная встреча разработчиков для обмена опытом и нетворкинга.',
    dateTime: '2024-03-25T18:00:00Z',
    totalTickets: 30,
    availableTickets: 0,
    coverUrl: 'https://images.unsplash.com/photo-1515187029135-18ee286d815b?w=400&h=300&fit=crop',
  },
];

// Расширенный список пользователей
const mockUsers: User[] = [
  {
    id: 1,
    email: 'user@example.com',
    firstName: 'Иван',
    lastName: 'Иванов',
    role: 'USER',
  },
  {
    id: 2,
    email: 'admin@example.com',
    firstName: 'Админ',
    lastName: 'Админов',
    role: 'ADMIN',
  },
  {
    id: 3,
    email: 'maria@example.com',
    firstName: 'Мария',
    lastName: 'Петрова',
    role: 'USER',
  },
  {
    id: 4,
    email: 'alex@example.com',
    firstName: 'Алексей',
    lastName: 'Сидоров',
    role: 'USER',
  },
  {
    id: 5,
    email: 'elena@example.com',
    firstName: 'Елена',
    lastName: 'Козлова',
    role: 'USER',
  },
  {
    id: 6,
    email: 'dmitry@example.com',
    firstName: 'Дмитрий',
    lastName: 'Волков',
    role: 'USER',
  },
  {
    id: 7,
    email: 'anna@example.com',
    firstName: 'Анна',
    lastName: 'Морозова',
    role: 'USER',
  },
  {
    id: 8,
    email: 'sergey@example.com',
    firstName: 'Сергей',
    lastName: 'Новиков',
    role: 'USER',
  },
];

// Единый массив для всех бронирований с большим количеством данных
let mockBookings: Booking[] = [
  // Бронирования для Конференции по веб-разработке (100 мест, 13 забронировано)
  {
    id: 1,
    event: mockEvents[0],
    customerEmail: mockUsers[0].email, // Иван Иванов
    ticketCount: 2,
    confirmed: true,
    createdAt: '2024-02-15T10:00:00Z',
  },
  {
    id: 2,
    event: mockEvents[0],
    customerEmail: mockUsers[2].email, // Мария Петрова
    ticketCount: 1,
    confirmed: true,
    createdAt: '2024-02-16T14:30:00Z',
  },
  {
    id: 3,
    event: mockEvents[0],
    customerEmail: mockUsers[3].email, // Алексей Сидоров
    ticketCount: 3,
    confirmed: false,
    createdAt: '2024-02-17T09:15:00Z',
    expiryTime: '2024-02-24T09:15:00Z',
  },
  {
    id: 4,
    event: mockEvents[0],
    customerEmail: mockUsers[4].email, // Елена Козлова
    ticketCount: 1,
    confirmed: true,
    createdAt: '2024-02-18T16:45:00Z',
  },
  {
    id: 5,
    event: mockEvents[0],
    customerEmail: mockUsers[5].email, // Дмитрий Волков
    ticketCount: 2,
    confirmed: false,
    createdAt: '2024-02-19T11:20:00Z',
    expiryTime: '2024-02-26T11:20:00Z',
  },
  {
    id: 6,
    event: mockEvents[0],
    customerEmail: mockUsers[6].email, // Анна Морозова
    ticketCount: 1,
    confirmed: true,
    createdAt: '2024-02-20T13:10:00Z',
  },
  {
    id: 7,
    event: mockEvents[0],
    customerEmail: mockUsers[7].email, // Сергей Новиков
    ticketCount: 4,
    confirmed: false,
    createdAt: '2024-02-21T08:30:00Z',
    expiryTime: '2024-02-28T08:30:00Z',
  },

  // Бронирования для Мастер-класса по React (50 мест, 10 забронировано)
  {
    id: 8,
    event: mockEvents[1],
    customerEmail: mockUsers[0].email, // Иван Иванов
    ticketCount: 1,
    confirmed: false,
    createdAt: '2024-02-20T14:00:00Z',
    expiryTime: '2024-02-27T14:00:00Z',
  },
  {
    id: 9,
    event: mockEvents[1],
    customerEmail: mockUsers[2].email, // Мария Петрова
    ticketCount: 2,
    confirmed: true,
    createdAt: '2024-02-21T10:30:00Z',
  },
  {
    id: 10,
    event: mockEvents[1],
    customerEmail: mockUsers[3].email, // Алексей Сидоров
    ticketCount: 1,
    confirmed: true,
    createdAt: '2024-02-22T15:45:00Z',
  },
  {
    id: 11,
    event: mockEvents[1],
    customerEmail: mockUsers[4].email, // Елена Козлова
    ticketCount: 3,
    confirmed: false,
    createdAt: '2024-02-23T12:20:00Z',
    expiryTime: '2024-03-02T12:20:00Z',
  },
  {
    id: 12,
    event: mockEvents[1],
    customerEmail: mockUsers[5].email, // Дмитрий Волков
    ticketCount: 1,
    confirmed: true,
    createdAt: '2024-02-24T09:15:00Z',
  },
  {
    id: 13,
    event: mockEvents[1],
    customerEmail: mockUsers[6].email, // Анна Морозова
    ticketCount: 2,
    confirmed: false,
    createdAt: '2024-02-25T16:30:00Z',
    expiryTime: '2024-03-04T16:30:00Z',
  },

  // Бронирования для Встречи IT-сообщества (30 мест, все забронированы)
  {
    id: 14,
    event: mockEvents[2],
    customerEmail: mockUsers[0].email, // Иван Иванов
    ticketCount: 5,
    confirmed: true,
    createdAt: '2024-02-10T10:00:00Z',
  },
  {
    id: 15,
    event: mockEvents[2],
    customerEmail: mockUsers[2].email, // Мария Петрова
    ticketCount: 3,
    confirmed: true,
    createdAt: '2024-02-11T14:30:00Z',
  },
  {
    id: 16,
    event: mockEvents[2],
    customerEmail: mockUsers[3].email, // Алексей Сидоров
    ticketCount: 4,
    confirmed: true,
    createdAt: '2024-02-12T09:15:00Z',
  },
  {
    id: 17,
    event: mockEvents[2],
    customerEmail: mockUsers[4].email, // Елена Козлова
    ticketCount: 2,
    confirmed: true,
    createdAt: '2024-02-13T16:45:00Z',
  },
  {
    id: 18,
    event: mockEvents[2],
    customerEmail: mockUsers[5].email, // Дмитрий Волков
    ticketCount: 6,
    confirmed: true,
    createdAt: '2024-02-14T11:20:00Z',
  },
  {
    id: 19,
    event: mockEvents[2],
    customerEmail: mockUsers[6].email, // Анна Морозова
    ticketCount: 3,
    confirmed: true,
    createdAt: '2024-02-15T13:10:00Z',
  },
  {
    id: 20,
    event: mockEvents[2],
    customerEmail: mockUsers[7].email, // Сергей Новиков
    ticketCount: 7,
    confirmed: true,
    createdAt: '2024-02-16T08:30:00Z',
  },
];

let mockNotificationPreferences: NotificationPreferences = {
  notifyNewEvents: true,
  notifyUpcoming: true,
  notifyBeforeHours: 24,
};

// Simulate API delay
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

class MockApiService {
  // Auth endpoints
  async login(data: LoginRequest): Promise<AuthResponse> {
    await delay(500);
    
    if (data.email === 'admin@example.com' && data.password === 'password123') {
      return {
        token: 'mock-admin-token',
        role: 'ADMIN',
      };
    } else if (data.email === 'user@example.com' && data.password === 'password123') {
      return {
        token: 'mock-user-token',
        role: 'USER',
      };
    } else {
      throw new Error('Invalid credentials');
    }
  }

  async register(data: RegisterRequest): Promise<AuthResponse> {
    await delay(500);
    
    // Simulate successful registration
    return {
      token: 'mock-new-user-token',
      role: 'USER',
    };
  }

  // Events endpoints
  async getEvents(pageable: Pageable, from?: string, to?: string): Promise<PageableEventResponse> {
    await delay(300);
    
    const startIndex = pageable.page * pageable.size;
    const endIndex = startIndex + pageable.size;
    const filteredEvents = mockEvents.slice(startIndex, endIndex);
    
    return {
      content: filteredEvents,
      totalElements: mockEvents.length,
      totalPages: Math.ceil(mockEvents.length / pageable.size),
      size: pageable.size,
      number: pageable.page,
      first: pageable.page === 0,
      last: endIndex >= mockEvents.length,
      empty: filteredEvents.length === 0,
    };
  }

  async getEvent(id: number): Promise<Event> {
    await delay(200);
    
    const event = mockEvents.find(e => e.id === id);
    if (!event) {
      throw new Error('Event not found');
    }
    return event;
  }

  // Admin events endpoints
  async createEvent(data: EventCreateRequest): Promise<Event> {
    await delay(500);
    
    const newEvent: Event = {
      id: Math.max(...mockEvents.map(e => e.id)) + 1,
      title: data.title,
      description: data.description || '',
      dateTime: data.dateTime,
      totalTickets: data.totalTickets,
      availableTickets: data.totalTickets,
      coverUrl: data.coverUrl || 'https://images.unsplash.com/photo-1515187029135-18ee286d815b?w=400&h=300&fit=crop', // Default image if not provided
    };
    
    mockEvents.push(newEvent);
    return newEvent;
  }

  async updateEvent(id: number, data: EventUpdateRequest): Promise<Event> {
    await delay(500);
    
    const eventIndex = mockEvents.findIndex(e => e.id === id);
    if (eventIndex === -1) {
      throw new Error('Event not found');
    }
    
    const updatedEvent = { 
      ...mockEvents[eventIndex], 
      ...data,
      // Preserve coverUrl if not provided in update
      coverUrl: data.coverUrl !== undefined ? data.coverUrl : mockEvents[eventIndex].coverUrl
    };
    mockEvents[eventIndex] = updatedEvent;
    return updatedEvent;
  }

  async deleteEvent(id: number): Promise<void> {
    await delay(300);
    
    const eventIndex = mockEvents.findIndex(e => e.id === id);
    if (eventIndex === -1) {
      throw new Error('Event not found');
    }
    
    mockEvents.splice(eventIndex, 1);
  }

  // Bookings endpoints - возвращает только бронирования текущего пользователя
  async getBookings(): Promise<Booking[]> {
    await delay(300);
    // В реальном API здесь был бы фильтр по текущему пользователю
    // Для демо возвращаем бронирования пользователя user@example.com
    return mockBookings.filter(b => b.customerEmail === 'user@example.com');
  }

  async createBooking(data: CreateBookingRequest): Promise<Booking> {
    await delay(500);
    
    const event = mockEvents.find(e => e.id === data.eventId);
    if (!event) {
      throw new Error('Event not found');
    }
    
    if (event.availableTickets < data.ticketCount) {
      throw new Error('Not enough available tickets');
    }
    
    // Update available tickets
    event.availableTickets -= data.ticketCount;
    
    const newBooking: Booking = {
      id: Math.max(...mockBookings.map(b => b.id)) + 1,
      event,
      customerEmail: mockUsers[0].email, // Иван Иванов (текущий пользователь)
      ticketCount: data.ticketCount,
      confirmed: false,
      createdAt: new Date().toISOString(),
      expiryTime: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(), // 7 days from now
    };
    
    mockBookings.push(newBooking);
    return newBooking;
  }

  async updateBooking(id: number, data: BookingUpdateRequest): Promise<Booking> {
    await delay(500);
    
    const bookingIndex = mockBookings.findIndex(b => b.id === id);
    if (bookingIndex === -1) {
      throw new Error('Booking not found');
    }
    
    const updatedBooking = { ...mockBookings[bookingIndex], ...data };
    mockBookings[bookingIndex] = updatedBooking;
    return updatedBooking;
  }

  async deleteBooking(id: number): Promise<void> {
    await delay(300);
    
    const bookingIndex = mockBookings.findIndex(b => b.id === id);
    if (bookingIndex === -1) {
      throw new Error('Booking not found');
    }
    
    const booking = mockBookings[bookingIndex];
    // Restore available tickets
    const event = mockEvents.find(e => e.id === booking.event.id);
    if (event) {
      event.availableTickets += booking.ticketCount;
    }
    
    mockBookings.splice(bookingIndex, 1);
  }

  // Admin bookings endpoints - возвращает все бронирования
  async getAdminBookings(pageable: Pageable, eventId?: number, unconfirmedOnly?: boolean): Promise<PageableBookingResponse> {
    await delay(300);
    
    let filteredBookings = [...mockBookings]; // Копия массива
    
    if (eventId) {
      filteredBookings = filteredBookings.filter(b => b.event.id === eventId);
    }
    
    if (unconfirmedOnly) {
      filteredBookings = filteredBookings.filter(b => !b.confirmed);
    }
    
    const startIndex = pageable.page * pageable.size;
    const endIndex = startIndex + pageable.size;
    const paginatedBookings = filteredBookings.slice(startIndex, endIndex);
    
    return {
      content: paginatedBookings,
      totalElements: filteredBookings.length,
      totalPages: Math.ceil(filteredBookings.length / pageable.size),
      size: pageable.size,
      number: pageable.page,
      first: pageable.page === 0,
      last: endIndex >= filteredBookings.length,
      empty: paginatedBookings.length === 0,
    };
  }

  async confirmBooking(id: number): Promise<void> {
    await delay(300);
    
    const bookingIndex = mockBookings.findIndex(b => b.id === id);
    if (bookingIndex === -1) {
      throw new Error('Booking not found');
    }
    
    mockBookings[bookingIndex].confirmed = true;
  }

  async deleteAdminBooking(id: number): Promise<void> {
    await delay(300);
    
    const bookingIndex = mockBookings.findIndex(b => b.id === id);
    if (bookingIndex === -1) {
      throw new Error('Booking not found');
    }
    
    const booking = mockBookings[bookingIndex];
    // Restore available tickets if not confirmed
    if (!booking.confirmed) {
      const event = mockEvents.find(e => e.id === booking.event.id);
      if (event) {
        event.availableTickets += booking.ticketCount;
      }
    }
    
    mockBookings.splice(bookingIndex, 1);
  }

  // User notifications endpoints
  async getNotificationPreferences(): Promise<NotificationPreferences> {
    await delay(200);
    return mockNotificationPreferences;
  }

  async updateNotificationPreferences(data: NotificationPreferences): Promise<void> {
    await delay(300);
    mockNotificationPreferences = data;
  }

  async deleteNotificationPreferences(): Promise<void> {
    await delay(300);
    mockNotificationPreferences = {
      notifyNewEvents: false,
      notifyUpcoming: false,
      notifyBeforeHours: 24,
    };
  }

  // Telegram link endpoint
  async linkTelegram(): Promise<string> {
    await delay(500);
    return 'https://t.me/your_bot?start=link123';
  }
}

export const mockApiService = new MockApiService(); 