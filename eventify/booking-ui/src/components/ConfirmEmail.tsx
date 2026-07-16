import React, { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { apiService } from '../services/api';
import { CheckCircle, XCircle, Loader2 } from 'lucide-react';

type Status = 'loading' | 'success' | 'error';

// Публичная страница — доступна без авторизации, потому что переход по
// ссылке из письма может произойти на устройстве/в браузере без активной
// сессии. Сама проверка кода происходит на бэкенде (эндпоинт тоже публичный),
// эта страница только читает email/code из URL и показывает результат.
const ConfirmEmail: React.FC = () => {
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState<Status>('loading');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const email = searchParams.get('email');
  const code = searchParams.get('code');

  useEffect(() => {
    if (!email || !code) {
      setStatus('error');
      setErrorMessage('В ссылке не хватает данных для подтверждения. Проверьте, что вы перешли по ссылке из письма целиком.');
      return;
    }

    apiService.confirmEmail(email, code)
      .then(() => setStatus('success'))
      .catch(() => {
        setStatus('error');
        setErrorMessage('Не удалось подтвердить email. Возможно, ссылка устарела или уже была использована.');
      });
  }, [email, code]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="max-w-md w-full bg-white shadow rounded-lg p-8 text-center">
        {status === 'loading' && (
          <>
            <Loader2 className="w-12 h-12 text-indigo-600 mx-auto mb-4 animate-spin" />
            <h1 className="text-xl font-semibold text-gray-900">Подтверждаем email...</h1>
          </>
        )}

        {status === 'success' && (
          <>
            <CheckCircle className="w-12 h-12 text-green-500 mx-auto mb-4" />
            <h1 className="text-xl font-semibold text-gray-900 mb-2">Email подтверждён</h1>
            <p className="text-sm text-gray-600 mb-6">
              Теперь вы будете получать уведомления на этот адрес.
            </p>
            <Link
              to="/"
              className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
            >
              Перейти в приложение
            </Link>
          </>
        )}

        {status === 'error' && (
          <>
            <XCircle className="w-12 h-12 text-red-500 mx-auto mb-4" />
            <h1 className="text-xl font-semibold text-gray-900 mb-2">Не получилось подтвердить email</h1>
            <p className="text-sm text-gray-600">{errorMessage}</p>
          </>
        )}
      </div>
    </div>
  );
};

export default ConfirmEmail;
