import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Event, CreateBookingRequest } from '../types';
import { apiService } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { Calendar, Users, MapPin, ArrowLeft, Clock } from 'lucide-react';
import toast from 'react-hot-toast';

const EventDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [event, setEvent] = useState<Event | null>(null);
  const [loading, setLoading] = useState(true);
  const [bookingLoading, setBookingLoading] = useState(false);
  const [ticketCount, setTicketCount] = useState(1);
  const [showBookingTimer, setShowBookingTimer] = useState(false);
  const [expiryTime, setExpiryTime] = useState<string | null>(null);

  const loadEvent = useCallback(async () => {
    if (!id) return;
    
    try {
      setLoading(true);
      const eventData = await apiService.getEvent(parseInt(id));
      setEvent(eventData);
    } catch (error) {
      console.error('Error loading event:', error);
      toast.error('Ошибка при загрузке мероприятия');
      navigate('/events');
    } finally {
      setLoading(false);
    }
  }, [id, navigate]);

  useEffect(() => {
    loadEvent();
  }, [loadEvent]);

  const handleBooking = async () => {
    if (!event || !user) return;

    if (ticketCount > event.availableTickets) {
      toast.error('Недостаточно доступных билетов');
      return;
    }

    setBookingLoading(true);
    try {
      const bookingData: CreateBookingRequest = {
        eventId: event.id,
        ticketCount,
      };

      const booking = await apiService.createBooking(bookingData);
      
      if (booking.expiryTime) {
        setExpiryTime(booking.expiryTime);
        setShowBookingTimer(true);
        toast.success(`Бронирование создано! У вас есть время до ${new Date(booking.expiryTime).toLocaleString('ru-RU')} для подтверждения.`);
      } else {
        toast.success('Бронирование создано!');
        navigate('/bookings');
      }
    } catch (error: any) {
      const message = error.response?.data?.message || 'Ошибка при создании бронирования';
      toast.error(message);
    } finally {
      setBookingLoading(false);
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

  const getStatusColor = (availableTickets: number, totalTickets: number) => {
    const percentage = (availableTickets / totalTickets) * 100;
    if (percentage === 0) return 'text-red-600';
    if (percentage <= 25) return 'text-orange-600';
    return 'text-green-600';
  };

  const getStatusText = (availableTickets: number, totalTickets: number) => {
    if (availableTickets === 0) return 'Мест нет';
    if (availableTickets <= totalTickets * 0.25) return 'Осталось мало мест';
    return 'Места доступны';
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (!event) {
    return (
      <div className="text-center py-12">
        <h3 className="text-lg font-medium text-gray-900">Мероприятие не найдено</h3>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-6">
        <button
          onClick={() => navigate('/events')}
          className="inline-flex items-center text-sm text-indigo-600 hover:text-indigo-500"
        >
          <ArrowLeft className="w-4 h-4 mr-2" />
          Назад к мероприятиям
        </button>
      </div>

      <div className="bg-white shadow-lg rounded-lg overflow-hidden">
        {event.coverUrl && (
          <div className="h-64 bg-gray-200">
            <img
              src={event.coverUrl}
              alt={event.title}
              className="w-full h-full object-cover"
            />
          </div>
        )}

        <div className="p-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-4">{event.title}</h1>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div>
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Описание</h2>
              <p className="text-gray-600 leading-relaxed">{event.description}</p>
            </div>

            <div>
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Информация</h2>
              <div className="space-y-4">
                <div className="flex items-center text-gray-600">
                  <Calendar className="w-5 h-5 mr-3" />
                  <span>{formatDateTime(event.dateTime)}</span>
                </div>
                
                <div className="flex items-center text-gray-600">
                  <Users className="w-5 h-5 mr-3" />
                  <span className={getStatusColor(event.availableTickets, event.totalTickets)}>
                    {event.availableTickets} из {event.totalTickets} мест
                  </span>
                </div>

                <div className="flex items-center text-gray-600">
                  <MapPin className="w-5 h-5 mr-3" />
                  <span>{getStatusText(event.availableTickets, event.totalTickets)}</span>
                </div>
              </div>

              {user && event.availableTickets > 0 && (
                <div className="mt-6 p-4 bg-gray-50 rounded-lg">
                  <h3 className="text-lg font-medium text-gray-900 mb-4">Забронировать билеты</h3>
                  
                  <div className="flex items-center space-x-4 mb-4">
                    <label htmlFor="ticketCount" className="text-sm font-medium text-gray-700">
                      Количество:
                    </label>
                    <select
                      id="ticketCount"
                      value={ticketCount}
                      onChange={(e) => setTicketCount(parseInt(e.target.value))}
                      className="border border-gray-300 rounded-md px-3 py-2 text-sm"
                    >
                      {Array.from({ length: Math.min(10, event.availableTickets) }, (_, i) => (
                        <option key={i + 1} value={i + 1}>
                          {i + 1}
                        </option>
                      ))}
                    </select>
                  </div>

                  <button
                    onClick={handleBooking}
                    disabled={bookingLoading || event.availableTickets === 0}
                    className="w-full inline-flex justify-center items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
                  >
                    {bookingLoading ? 'Загрузка...' : 'Забронировать'}
                  </button>
                </div>
              )}

              {!user && (
                <div className="mt-6 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
                  <p className="text-yellow-800">
                    Для бронирования билетов необходимо войти в систему.
                  </p>
                </div>
              )}

              {event.availableTickets === 0 && (
                <div className="mt-6 p-4 bg-red-50 border border-red-200 rounded-lg">
                  <p className="text-red-800">
                    К сожалению, все билеты уже забронированы.
                  </p>
                </div>
              )}
            </div>
          </div>

          {showBookingTimer && expiryTime && (
            <div className="mt-8 p-4 bg-blue-50 border border-blue-200 rounded-lg">
              <div className="flex items-center">
                <Clock className="w-5 h-5 mr-2 text-blue-600" />
                <span className="text-blue-800">
                  Время на подтверждение бронирования: {new Date(expiryTime).toLocaleString('ru-RU')}
                </span>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default EventDetail; 