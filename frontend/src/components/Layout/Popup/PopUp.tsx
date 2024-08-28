// PopUp.tsx
import React, { useState } from 'react';
import styles from './PopUp.module.css';
import { useUser } from '../../../services/UserContext';
import {useNavigate} from "react-router-dom"; // Путь к вашему контексту

interface PopUpProps {
    closePopup: () => void;
    openLoginPopup: () => void;
    type: 'login' | 'signup';
}
const PopUp: React.FC<PopUpProps> = ({ closePopup, openLoginPopup, type }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<string | null>(null);
    const { setUser } = useUser();
    const navigate = useNavigate(); // Создаем navigate для редиректа

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setError(null);

        const url = type === 'login' ? 'http://127.0.0.1:8091/users/login' : 'http://127.0.0.1:8091/users/auth';
        const successMessage = type === 'login' ? 'Login successful' : 'User successfully registered';
        const conflictMessage = type === 'signup' ? 'User already exists' : 'Authentication failed';

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, password }),
            });

            const data = await response.json();

            if (response.ok && data.status === (type === 'login' ? 'OK' : 'CREATED')) {
                if (type === 'signup') {
                    closePopup();
                    setPassword('');
                    openLoginPopup();
                    return;
                }

                const token = data.token;
                localStorage.setItem('token', token);

                const userResponse = await fetch('http://127.0.0.1:8091/users/me', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                });

                const userData = await userResponse.json();

                if (userResponse.ok && userData.status === 'OK') {
                    setUser(userData.user);
                    localStorage.setItem('user', JSON.stringify(userData.user));
                    closePopup();
                } else {
                    setError(userData.resultRequest || 'Ошибка получения пользователя');
                }

                if (data.status === 'CREATED' && data.question) {
                    const questionId = data.question.id;

                }
            } else {
                setError(data.resultRequest || conflictMessage);
            }
        } catch (error) {
            setError('Произошла ошибка при попытке входа. Попробуйте еще раз.');
        }
    };

    return (
        <div className={styles.overlay}>
            <div className={styles.popup}>
                <button className={styles.closeButton} onClick={closePopup}>X</button>
                <h2 className={styles.title}>{type === 'login' ? 'Login' : 'Sign up'}</h2>
                <form className={styles.form} onSubmit={handleSubmit}>
                    <label className={styles.label} htmlFor="username">Username</label>
                    <input
                        className={styles.input}
                        type="text"
                        id="username"
                        name="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />

                    <label className={styles.label} htmlFor="password">Password</label>
                    <input
                        className={styles.input}
                        type="password"
                        id="password"
                        name="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />

                    {error && <p className={styles.error}>{error}</p>}

                    <button type="submit" className={styles.submitButton}>{type === 'login' ? 'Submit' : 'Sign up'}</button>
                </form>
            </div>
        </div>
    );
};

export default PopUp;
