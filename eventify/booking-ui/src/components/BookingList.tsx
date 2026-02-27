import React, { useState, useEffect, useCallback } from 'react';
import { Booking } from '../types';
import { apiService } from '../services/api';
import { Calendar, Users, Clock, Trash2, Check, X } from 'lucide-react';
import toast from 'react-hot-toast';

const BookingList: React.FC = () => {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [loading, setLoading] = useState(true);

  const loadBookings = useCallback(async () => {
    try {
      setLoading(true);
      const bookingsData = await apiService.getBookings();
      setBookings(bookingsData);
    } catch (error) {
      console.error('Error loading bookings:', error);
      toast.error('Ошибка при загрузке бронирований');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadBookings();
  }, [loadBookings]);

  const handleDeleteBooking = async (bookingId: number, eventTitle: string, ticketCount: number) => {
    if (!window.confirm(`Отменить вашу бронь на ${ticketCount} билетов?`)) {
      return;
    }

    try {
      await apiService.deleteBooking(bookingId);
      toast.success('Бронирование отменено');
      loadBookings();
    } catch (error) {
      console.error('Error deleting booking:', error);
      toast.error('Ошибка при отмене бронирования');
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
        <h1 className="text-3xl font-bold text-gray-900">Мои бронирования</h1>
      </div>

      {bookings.length === 0 ? (
        <div className="text-center py-12">
          <Calendar className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-2 text-sm font-medium text-gray-900">Нет бронирований</h3>
          <p className="mt-1 text-sm text-gray-500">
            У вас пока нет забронированных билетов.
          </p>
        </div>
      ) : (
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
                            {booking.event.title}
                          </p>
                          <div className="ml-2">
                            {getStatusBadge(booking.confirmed)}
                          </div>
                        </div>
                        <div className="mt-2 flex items-center text-sm text-gray-500 space-x-4">
                          <div className="flex items-center">
                            <Calendar className="w-4 h-4 mr-1" />
                            {formatDateTime(booking.event.dateTime)}
                          </div>
                          <div className="flex items-center">
                            <Users className="w-4 h-4 mr-1" />
                            {booking.ticketCount} билетов
                          </div>
                          <div className="flex items-center">
                            <Clock className="w-4 h-4 mr-1" />
                            Создано: {formatDateTime(booking.createdAt)}
                          </div>
                        </div>
                        {booking.expiryTime && !booking.confirmed && (
                          <div className="mt-2 text-sm text-orange-600">
                            Время на подтверждение: {formatDateTime(booking.expiryTime)}
                          </div>
                        )}
                      </div>
                    </div>
                    <div className="flex items-center space-x-2">
                      {!booking.confirmed && (
                        <button
                          onClick={() => handleDeleteBooking(booking.id, booking.event.title, booking.ticketCount)}
                          className="inline-flex items-center p-2 border border-transparent text-sm leading-4 font-medium rounded-md text-red-700 bg-red-100 hover:bg-red-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                          title="Отменить бронирование"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default BookingList; 