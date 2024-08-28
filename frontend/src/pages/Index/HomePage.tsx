import React, {useContext, useEffect, useState} from 'react';
import './Index.css';

const HomePage: React.FC = () => {


    return (
        <section className="main-section">
            <section className="main__header__section">
                <div>
                    <h1 id="top-text">Сайт для Хранения всяких интересных вопросов</h1>
                    <p>Одним из пунктов лабораторных работ является защита, в который входит ответы на вопросы от практиков. Некоторые эти вопросы очень цены, потому что их часто спрашивают на собесах в крупные компании. На этом сайте можно посмотреть и записать вопрос и пообщаться на тему учебы</p>
                </div>
                <div className="left">
                    <a href="#" className="utochka">Пичка Жёсткой уточки как симбол ВТ (не прогрузилась)</a>
                </div>
            </section>
            <section className="platforma__section">
                <div className="main__header">
                    <div className="header__title">
                        <h2>Платформа</h2>
                    </div>
                </div>
                <div className="boxed-text">
                    <p>Добро пожаловать на нашу не очень уникальную <span className="text__point">платформу</span>, где все о ваших вопросах и ответах
                        сходятся в одном месте! Здесь вы <span className="text__point">можете</span>  не только находить
                        ответы на самые разные вопросы, но и <span className="text__point">делиться</span>  своим мнением и экспертными <span className="text__point">знаниями</span> .
                        <br/><br/>
                        Что особенно здорово — все посетители могут свободно просматривать вопросы, ответы и
                        комментарии. Это отличная возможность погрузиться в разнообразие тем и точек зрения на любые
                        вопросы, которые вас интересуют.
                        <br/><br/>
                        Авторизированные пользователи получают дополнительные привилегии: они могут не только создавать
                        свои вопросы и делиться своими ответами, но и участвовать в живых обсуждениях с другими
                        участниками сообщества.
                    </p>

                </div>
            </section>
        </section>
    );
};

export default HomePage;
