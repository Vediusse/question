// Header.tsx
import React, { useState } from 'react';
import styles from './Header.module.css';
import logo from '../../../assets/svg/311811.svg';
import PopUp from '../Popup/PopUp';
import { useUser } from '../../../services/UserContext'; // Путь к вашему контексту

const Header: React.FC = () => {
    const [isPopupOpen, setIsPopupOpen] = useState(false);
    const [popupType, setPopupType] = useState<'login' | 'signup'>('login');
    const { user, logout } = useUser();

    const openLoginPopup = () => {
        setPopupType('login');
        setIsPopupOpen(true);
    };

    const openSignupPopup = () => {
        setPopupType('signup');
        setIsPopupOpen(true);
    };

    const closePopup = () => {
        setIsPopupOpen(false);
    };

    return (
        <>
            <header className={styles.header}>
                <nav className={styles.nav}>
                    <div className={styles.logo}>
                        <img src={logo} className={styles.logoImage} alt="SVG из файла" />
                        <p className={styles.logoText}>Kiriehki<span className={styles.logoSubtext}>Kotiki</span></p>
                    </div>
                    <div className={styles.signup}>
                        {user ? (
                            <>
                                <div className={`${styles.signupButton} ${styles.signupButtonSignup}`}>
                                    <p className={styles.userName}>{user.username}</p>
                                </div>
                                <div className={`${styles.signupButton} ${styles.signupButtonSignup}`} onClick={logout}>
                                    <p className={styles.exitButton}>Log out</p>
                                </div>
                            </>
                        ) : (
                            <>
                                <div className={`${styles.signupButton} ${styles.signupButtonSignup}`} onClick={openLoginPopup}>
                                    <p className={styles.signupText}>Log in</p>
                                </div>
                                <div className={`${styles.signupButton} ${styles.signupButtonSignup}`} onClick={openSignupPopup}>
                                    <p className={styles.signupText}>Sign up</p>
                                </div>
                            </>
                        )}
                    </div>
                </nav>
            </header>
            {isPopupOpen && <PopUp closePopup={closePopup} openLoginPopup={openLoginPopup} type={popupType} />}
        </>
    );
};

export default Header;
