import React, { useState, useEffect } from 'react';
import { NotificationPreferences } from '../types';
import { apiService } from '../services/api';
import { Bell, Save, Trash2, MessageCircle } from 'lucide-react';
import toast from 'react-hot-toast';

const NotificationSettings: React.FC = () => {
  const [preferences, setPreferences] = useState<NotificationPreferences>({
    notifyNewEvents: false,
    notifyUpcoming: false,
    notifyBeforeHours: 24,
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [telegramCode, setTelegramCode] = useState<string | null>(null);

  useEffect(() => {
    loadPreferences();
  }, []);

  const loadPreferences = async () => {
    try {
      setLoading(true);
      const prefs = await apiService.getNotificationPreferences();
      setPreferences(prefs);
    } catch (error) {
      console.error('Error loading preferences:', error);
      // If preferences don't exist, use defaults
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    try {
      setSaving(true);
      await apiService.updateNotificationPreferences(preferences);
      toast.success('Настройки уведомлений сохранены');
    } catch (error) {
      console.error('Error saving preferences:', error);
      toast.error('Ошибка при сохранении настроек');
    } finally {
      setSaving(false);
    }
  };

  const handleReset = async () => {
    if (!window.confirm('Сбросить все настройки уведомлений?')) {
      return;
    }

    try {
      setSaving(true);
      await apiService.deleteNotificationPreferences();
      setPreferences({
        notifyNewEvents: false,
        notifyUpcoming: false,
        notifyBeforeHours: 24,
      });
      toast.success('Настройки уведомлений сброшены');
    } catch (error) {
      console.error('Error resetting preferences:', error);
      toast.error('Ошибка при сбросе настроек');
    } finally {
      setSaving(false);
    }
  };

  const handleLinkTelegram = async () => {
    try {
      const code = await apiService.linkTelegram();
      setTelegramCode(code);
      toast.success('Код для привязки Telegram получен');
    } catch (error) {
      console.error('Error linking telegram:', error);
      toast.error('Ошибка при получении кода для Telegram');
    }
  };

  const handlePreferenceChange = (key: keyof NotificationPreferences, value: any) => {
    setPreferences(prev => ({
      ...prev,
      [key]: value
    }));
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Настройки уведомлений</h1>
        <p className="mt-2 text-sm text-gray-600">
          Настройте, какие уведомления вы хотите получать
        </p>
      </div>

      <div className="bg-white shadow rounded-lg">
        <div className="px-6 py-4 border-b border-gray-200">
          <h2 className="text-lg font-medium text-gray-900 flex items-center">
            <Bell className="w-5 h-5 mr-2" />
            Настройки уведомлений
          </h2>
        </div>

        <div className="px-6 py-4 space-y-6">
          {/* Email Notifications */}
          <div>
            <h3 className="text-md font-medium text-gray-900 mb-4">Email уведомления</h3>
            
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <label className="text-sm font-medium text-gray-700">
                    Уведомления о новых мероприятиях
                  </label>
                  <p className="text-sm text-gray-500">
                    Получать уведомления когда добавляются новые мероприятия
                  </p>
                </div>
                <label className="relative inline-flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    checked={preferences.notifyNewEvents}
                    onChange={(e) => handlePreferenceChange('notifyNewEvents', e.target.checked)}
                    className="sr-only peer"
                  />
                  <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-indigo-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-indigo-600"></div>
                </label>
              </div>

              <div className="flex items-center justify-between">
                <div>
                  <label className="text-sm font-medium text-gray-700">
                    Напоминания о предстоящих мероприятиях
                  </label>
                  <p className="text-sm text-gray-500">
                    Получать напоминания о ваших забронированных мероприятиях
                  </p>
                </div>
                <label className="relative inline-flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    checked={preferences.notifyUpcoming}
                    onChange={(e) => handlePreferenceChange('notifyUpcoming', e.target.checked)}
                    className="sr-only peer"
                  />
                  <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-indigo-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-indigo-600"></div>
                </label>
              </div>

              {preferences.notifyUpcoming && (
                <div className="ml-6">
                  <label htmlFor="notifyBeforeHours" className="block text-sm font-medium text-gray-700 mb-2">
                    Напоминать за (часов):
                  </label>
                  <select
                    id="notifyBeforeHours"
                    value={preferences.notifyBeforeHours}
                    onChange={(e) => handlePreferenceChange('notifyBeforeHours', parseInt(e.target.value))}
                    className="border border-gray-300 rounded-md px-3 py-2 text-sm w-32"
                  >
                    {Array.from({ length: 24 }, (_, i) => i + 1).map(hours => (
                      <option key={hours} value={hours}>
                        {hours}
                      </option>
                    ))}
                  </select>
                </div>
              )}
            </div>
          </div>

          {/* Telegram Integration */}
          <div className="border-t border-gray-200 pt-6">
            <h3 className="text-md font-medium text-gray-900 mb-4 flex items-center">
              <MessageCircle className="w-5 h-5 mr-2" />
              Интеграция с Telegram
            </h3>
            
            <div className="space-y-4">
              <p className="text-sm text-gray-600">
                Привяжите ваш Telegram аккаунт для получения уведомлений в мессенджере
              </p>
              
              <button
                onClick={handleLinkTelegram}
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                Привязать Telegram
              </button>

              {telegramCode && (
                <div className="p-4 bg-blue-50 border border-blue-200 rounded-lg">
                  <p className="text-sm text-blue-800 mb-2">
                    Отправьте этот код боту @your_bot_name:
                  </p>
                  <div className="bg-white p-3 rounded border font-mono text-lg text-center">
                    {telegramCode}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="px-6 py-4 border-t border-gray-200 bg-gray-50 flex justify-between">
          <button
            onClick={handleReset}
            disabled={saving}
            className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
          >
            <Trash2 className="w-4 h-4 mr-2" />
            Сбросить настройки
          </button>
          
          <button
            onClick={handleSave}
            disabled={saving}
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
          >
            <Save className="w-4 h-4 mr-2" />
            {saving ? 'Сохранение...' : 'Сохранить'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default NotificationSettings; 