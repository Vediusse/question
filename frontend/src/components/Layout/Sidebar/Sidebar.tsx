import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import styles from './Sidebar.module.css';

import homeIcon from '../../../assets/svg/home-icon-silhouette-svgrepo-com.svg';
import quoraIcon from '../../../assets/svg/quora-svgrepo-com.svg';
import usersIcon from '../../../assets/svg/users-svgrepo-com.svg';

const Sidebar: React.FC = () => {
    const location = useLocation();

    const getActiveClass = (path: string, exact: boolean = false) => {
        if (exact) {
            return location.pathname === path ? styles.buttonActive : '';
        }
        return location.pathname.startsWith(path) ? styles.buttonActive : '';
    };

    return (
        <section className={styles.sidebar} id="sidebar">
            <div className={styles.wrapper}>
                <div className={styles.menu}>
                    <Link to="/" className={`${styles.button} ${getActiveClass('/', true)}`}>
                        <img src={homeIcon} className={styles.buttonImage} alt="Главная" />
                        <p className={styles.buttonText}>Главная</p>
                    </Link>
                    <Link to="/questions" className={`${styles.button} ${getActiveClass('/questions')}`}>
                        <img src={quoraIcon} className={styles.buttonImage} alt="Вопросы" />
                        <p className={styles.buttonText}>Вопросы</p>
                    </Link>
                    <Link to="/users" className={`${styles.button} ${getActiveClass('/users')}`}>
                        <img src={usersIcon} className={styles.buttonImage} alt="Пользователи" />
                        <p className={styles.buttonText}>Пользователи</p>
                    </Link>
                    <Link to="/consultation" className={`${styles.button} ${getActiveClass('/consultation')}`}>
                        <img src={homeIcon} className={styles.buttonImage} alt="Консультация" />
                        <p className={styles.buttonText}>Консультация</p>
                    </Link>
                </div>
            </div>
        </section>
    );
};

export default Sidebar;
