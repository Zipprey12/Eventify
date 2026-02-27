import React, { useState, useEffect, useCallback } from 'react';
import { Booking, Pageable, Event } from '../types';
import { apiService } from '../services/api';
import { Calendar, Users, Check, X, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';

const AdminBookingList: React.FC = () => {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [pageSize] = useState(10);
  const [selectedEventId, setSelectedEventId] = useState<number | undefined>();
  const [unconfirmedOnly, setUnconfirmedOnly] = useState(false);

  const loadEvents = useCallback(async () => {
    try {
      const pageable: Pageable = {
        page: 0,
        size: 100, // Загружаем все мероприятия для dropdown
        sort: ['title,asc']
      };
      
      const response = await apiService.getEvents(pageable);
      setEvents(response.content);
    } catch (error) {
      console.error('Error loading events:', error);
      toast.error('Ошибка при загрузке мероприятий');
    }
  }, []);

  const loadBookings = useCallback(async () => {
    try {
      setLoading(true);
      const pageable: Pageable = {
        page: currentPage,
        size: pageSize,
        sort: ['createdAt,desc']
      };
      
      const response = await apiService.getAdminBookings(pageable, selectedEventId, unconfirmedOnly);
      setBookings(response.content);
      setTotalPages(response.totalPages);
    } catch (error) {
      console.error('Error loading bookings:', error);
      toast.error('Ошибка при загрузке бронирований');
    } finally {
      setLoading(false);
    }
  }, [currentPage, pageSize, selectedEventId, unconfirmedOnly]);

  useEffect(() => {
    loadEvents();
  }, [loadEvents]);

  useEffect(() => {
    loadBookings();
  }, [loadBookings]);

  const handleConfirmBooking = async (bookingId: number) => {
    try {
      await apiService.confirmBooking(bookingId);
      toast.success('Бронирование подтверждено');
      loadBookings();
    } catch (error) {
      console.error('Error confirming booking:', error);
      toast.error('Ошибка при подтверждении бронирования');
    }
  };

  const handleDeleteBooking = async (bookingId: number) => {
    if (!window.confirm('Удалить это бронирование?')) {
      return;
    }

    try {
      await apiService.deleteAdminBooking(bookingId);
      toast.success('Бронирование удалено');
      loadBookings();
    } catch (error) {
      console.error('Error deleting booking:', error);
      toast.error('Ошибка при удалении бронирования');
    }
  };

  const formatDateTime = (dateTime: string) => {
    return new Date(dateTime).toLocaleString('ru-RU', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getStatusBadge = (confirmed: boolean) => {
    return confirmed ? (
      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
        <Check className="w-3 h-3 mr-1" />
        Подтверждено
      </span>
    ) : (
      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
        <X className="w-3 h-3 mr-1" />
        Ожидает подтверждения
      </span>
    );
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Управление бронированиями</h1>
      </div>

      {/* Filters */}
      <div className="bg-white p-4 rounded-lg shadow mb-6">
        <div className="flex flex-col md:flex-row gap-4 items-end">
          <div className="flex-1">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Мероприятие
            </label>
            <select
              value={selectedEventId || ''}
              onChange={(e) => setSelectedEventId(e.target.value ? parseInt(e.target.value) : undefined)}
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
            >
              <option value="">Все мероприятия</option>
              {events.map((event) => (
                <option key={event.id} value={event.id}>
                  {event.title}
                </option>
              ))}
            </select>
          </div>
          
          <div className="flex items-center h-10">
            <label className="flex items-center">
              <input
                type="checkbox"
                checked={unconfirmedOnly}
                onChange={(e) => setUnconfirmedOnly(e.target.checked)}
                className="rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
              />
              <span className="ml-2 text-sm text-gray-700">
                Только неподтвержденные
              </span>
            </label>
          </div>
          
          <div>
            <button
              onClick={() => {
                setSelectedEventId(undefined);
                setUnconfirmedOnly(false);
              }}
              className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500"
            >
              Сбросить фильтры
            </button>
          </div>
        </div>
      </div>

      {bookings.length === 0 ? (
        <div className="text-center py-12">
          <Calendar className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-2 text-sm font-medium text-gray-900">Нет бронирований</h3>
          <p className="mt-1 text-sm text-gray-500">
            По выбранным критериям бронирования не найдены.
          </p>
        </div>
      ) : (
        <>
          <div className="bg-white shadow overflow-hidden sm:rounded-md">
            <ul className="divide-y divide-gray-200">
              {bookings.map((booking) => (
                <li key={booking.id}>
                  <div className="px-4 py-4 sm:px-6">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center">
                        <div className="flex-shrink-0">
                          <Users className="h-8 w-8 text-gray-400" />
                        </div>
                        <div className="ml-4">
                          <div className="flex items-center">
                            <p className="text-sm font-medium text-gray-900">
                              {booking.customerEmail}
                            </p>
                            <div className="ml-2">
                              {getStatusBadge(booking.confirmed)}
                            </div>
                          </div>
                          <div className="mt-1 flex items-center text-sm text-gray-500">
                            <Calendar className="flex-shrink-0 mr-1.5 h-4 w-4" />
                            <p>
                              {booking.event.title} • {booking.ticketCount} билетов
                            </p>
                          </div>
                          <div className="mt-1 text-sm text-gray-500">
                            Создано: {formatDateTime(booking.createdAt)}
                          </div>
                          {booking.expiryTime && !booking.confirmed && (
                            <div className="mt-1 text-sm text-red-500">
                              Истекает: {formatDateTime(booking.expiryTime)}
                            </div>
                          )}
                        </div>
                      </div>
                      
                      <div className="flex items-center space-x-2">
                        {!booking.confirmed && (
                          <button
                            onClick={() => handleConfirmBooking(booking.id)}
                            className="inline-flex items-center p-2 border border-transparent text-sm leading-4 font-medium rounded-md text-green-700 bg-green-100 hover:bg-green-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
                            title="Подтвердить"
                          >
                            <Check className="w-4 h-4" />
                          </button>
                        )}
                        
                        <button
                          onClick={() => handleDeleteBooking(booking.id)}
                          className="inline-flex items-center p-2 border border-transparent text-sm leading-4 font-medium rounded-md text-red-700 bg-red-100 hover:bg-red-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                          title="Удалить"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                  </div>
                </li>
              ))}
            </ul>
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="mt-8 flex justify-center">
              <nav className="flex items-center space-x-2">
                <button
                  onClick={() => setCurrentPage(currentPage - 1)}
                  disabled={currentPage === 0}
                  className="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Предыдущая
                </button>
                
                {Array.from({ length: totalPages }, (_, i) => (
                  <button
                    key={i}
                    onClick={() => setCurrentPage(i)}
                    className={`px-3 py-2 text-sm font-medium rounded-md ${
                      currentPage === i
                        ? 'text-white bg-indigo-600'
                        : 'text-gray-500 bg-white border border-gray-300 hover:bg-gray-50'
                    }`}
                  >
                    {i + 1}
                  </button>
                ))}
                
                <button
                  onClick={() => setCurrentPage(currentPage + 1)}
                  disabled={currentPage === totalPages - 1}
                  className="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Следующая
                </button>
              </nav>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default AdminBookingList; 