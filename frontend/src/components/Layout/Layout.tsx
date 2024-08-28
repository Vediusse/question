import React, { ReactNode } from 'react';
import Header from './Header/Header';
import Sidebar from './Sidebar/Sidebar';
import styles from './Layout.module.css'; // Импортируйте CSS Module

interface LayoutProps {
    children: ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => (
    <div className={styles.pageBody}>
        <Header />

        <main className={styles.mainContent}>
            <Sidebar />
            <section className={styles.mainSection}>
                {children}
            </section>
        </main>

    </div>

);

export default Layout;
