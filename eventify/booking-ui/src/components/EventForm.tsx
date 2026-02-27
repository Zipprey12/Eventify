import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Event, EventCreateRequest, EventUpdateRequest } from '../types';
import { apiService } from '../services/api';
import { ArrowLeft, Save, Plus } from 'lucide-react';
import toast from 'react-hot-toast';

const schema = yup.object({
  title: yup.string().required('Название обязательно'),
  description: yup.string().required('Описание обязательно'),
  dateTime: yup.string().required('Дата и время обязательны'),
  totalTickets: yup.number().positive('Количество билетов должно быть положительным').required('Количество билетов обязательно'),
}).required();

type FormData = yup.InferType<typeof schema> & {
  coverUrl?: string;
};

interface EventFormProps {
  mode: 'create' | 'edit';
}

const EventForm: React.FC<EventFormProps> = ({ mode }) => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [loading, setLoading] = useState(false);
  const [event, setEvent] = useState<Event | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<FormData>({
    resolver: yupResolver(schema),
  });

  useEffect(() => {
    if (mode === 'edit' && id) {
      loadEvent();
    }
  }, [mode, id]);

  const loadEvent = async () => {
    try {
      setLoading(true);
      const eventData = await apiService.getEvent(parseInt(id!));
      setEvent(eventData);
      reset({
        title: eventData.title,
        description: eventData.description,
        dateTime: new Date(eventData.dateTime).toISOString().slice(0, 16),
        totalTickets: eventData.totalTickets,
        coverUrl: eventData.coverUrl,
      });
    } catch (error) {
      console.error('Error loading event:', error);
      toast.error('Ошибка при загрузке мероприятия');
      navigate('/events');
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data: FormData) => {
    try {
      setLoading(true);
      
      if (mode === 'create') {
        const createData: EventCreateRequest = {
          title: data.title,
          description: data.description,
          dateTime: new Date(data.dateTime).toISOString(),
          totalTickets: data.totalTickets,
          coverUrl: data.coverUrl,
        };
        
        await apiService.createEvent(createData);
        toast.success('Мероприятие создано');
      } else {
        const updateData: EventUpdateRequest = {
          title: data.title,
          description: data.description,
          dateTime: new Date(data.dateTime).toISOString(),
          totalTickets: data.totalTickets,
          coverUrl: data.coverUrl,
        };
        
        await apiService.updateEvent(parseInt(id!), updateData);
        toast.success('Мероприятие обновлено');
      }
      
      navigate('/events');
    } catch (error: any) {
      console.error('Error saving event:', error);
      const message = error.response?.data?.message || 'Ошибка при сохранении мероприятия';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  if (loading && mode === 'edit') {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-indigo-600"></div>
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
        <div className="px-6 py-4 border-b border-gray-200">
          <h1 className="text-2xl font-bold text-gray-900">
            {mode === 'create' ? 'Создать мероприятие' : 'Редактировать мероприятие'}
          </h1>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="p-6 space-y-6">
          <div>
            <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-2">
              Название *
            </label>
            <input
              type="text"
              id="title"
              {...register('title')}
              className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 ${
                errors.title ? 'border-red-300' : 'border-gray-300'
              }`}
              placeholder="Введите название мероприятия"
            />
            {errors.title && (
              <p className="mt-1 text-sm text-red-600">{errors.title.message}</p>
            )}
          </div>

          <div>
            <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
              Описание *
            </label>
            <textarea
              id="description"
              {...register('description')}
              rows={4}
              className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 ${
                errors.description ? 'border-red-300' : 'border-gray-300'
              }`}
              placeholder="Введите описание мероприятия"
            />
            {errors.description && (
              <p className="mt-1 text-sm text-red-600">{errors.description.message}</p>
            )}
          </div>

          <div>
            <label htmlFor="dateTime" className="block text-sm font-medium text-gray-700 mb-2">
              Дата и время *
            </label>
            <input
              type="datetime-local"
              id="dateTime"
              {...register('dateTime')}
              className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 ${
                errors.dateTime ? 'border-red-300' : 'border-gray-300'
              }`}
            />
            {errors.dateTime && (
              <p className="mt-1 text-sm text-red-600">{errors.dateTime.message}</p>
            )}
          </div>

          <div>
            <label htmlFor="totalTickets" className="block text-sm font-medium text-gray-700 mb-2">
              Общее количество билетов *
            </label>
            <input
              type="number"
              id="totalTickets"
              {...register('totalTickets', { valueAsNumber: true })}
              min="1"
              className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 ${
                errors.totalTickets ? 'border-red-300' : 'border-gray-300'
              }`}
              placeholder="Введите количество билетов"
            />
            {errors.totalTickets && (
              <p className="mt-1 text-sm text-red-600">{errors.totalTickets.message}</p>
            )}
          </div>

          <div>
            <label htmlFor="coverUrl" className="block text-sm font-medium text-gray-700 mb-2">
              URL обложки
            </label>
            <input
              type="text"
              id="coverUrl"
              {...register('coverUrl')}
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              placeholder="Введите URL обложки (необязательно)"
            />
          </div>

          <div className="flex justify-end space-x-4 pt-6 border-t border-gray-200">
            <button
              type="button"
              onClick={() => navigate('/events')}
              className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              Отмена
            </button>
            <button
              type="submit"
              disabled={loading}
              className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
            >
              {loading ? (
                <>
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                  Сохранение...
                </>
              ) : (
                <>
                  {mode === 'create' ? <Plus className="w-4 h-4 mr-2" /> : <Save className="w-4 h-4 mr-2" />}
                  {mode === 'create' ? 'Создать' : 'Сохранить'}
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EventForm; 