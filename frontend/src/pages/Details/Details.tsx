import React, { useEffect, useState, useContext } from 'react';
import axios from 'axios';
import './Details.css';
import { useParams } from 'react-router-dom';
import { UserContext } from '../../services/UserContext'; // Import UserContext

import MarkdownRenderer from "../../components/Code/MarkdownRenderer"
import AnswerPopUp from "../../components/AnswerPopUp/AnswerPopUp";




interface Comment {
    id: number;
    content: string;
    createdAt: number;
    user: {
        username: string;
    };
    questionId?: number;
    answerId?: number;
}

interface User {
    id: number;
    username: string;
    role: {
        name: string;
        local: string;
        description: string;
    };
}

interface Answer {
    id: number;
    answer: string;
    rating: number;
    answerer: {
        username: string;
    };
    comments: Comment[];
}

interface Question {
    id: number;
    question: string;
    description: string;
    answers: Answer[];
    user: {
        username: string;
    };
    comments: Comment[];
}

const Details: React.FC = () => {
    const [questionData, setQuestionData] = useState<Question | null>(null);
    const [error, setError] = useState<string | null>(null);
    const { id } = useParams<{ id: string }>();
    const userContext = useContext(UserContext);

    const [isAnswerPopUpOpen, setIsAnswerPopUpOpen] = useState(false);

    const handleOpenAnswerPopUp = () => setIsAnswerPopUpOpen(true);
    const handleCloseAnswerPopUp = () => setIsAnswerPopUpOpen(false);

    useEffect(() => {
        axios.get(`http://127.0.0.1:8090/questions/${id}`)
            .then(response => {
                if (response.data.status === 'OK') {
                    setQuestionData(response.data.question);
                } else {
                    setError('Вопрос не найден');
                }
            })
            .catch(() => {
                setError('Ошибка при загрузке вопроса');
            });
    }, [id]);

    const handleCommentSubmit = (comment: string, entityId: number, isQuestion: boolean) => {
        if (!userContext?.user) return;

        const endpoint = isQuestion
            ? `http://127.0.0.1:8094/comments/question/${entityId}`
            : `http://127.0.0.1:8094/comments/answer/${entityId}`;
        axios.post(endpoint, {
            content: comment,
        }, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
                'Content-Type': 'application/json',
            }
        }).then(() => {
            if (isQuestion) {
                axios.get(`http://127.0.0.1:8090/questions/${id}`)
                    .then(response => {
                        if (response.data.status === 'OK') {
                            setQuestionData(response.data.question);
                        }
                    })
                    .catch(error => {
                        console.error('Error fetching updated question:', error);
                    });
            } else {
                axios.get(`http://127.0.0.1:8093/answers/${entityId}`)
                    .then(response => {
                        const updatedAnswer = response.data.answer;
                        setQuestionData(prevData => {
                            if (!prevData) return null;
                            const updatedAnswers = prevData.answers.map(answer =>
                                answer.id === updatedAnswer.id ? updatedAnswer : answer
                            );
                            return { ...prevData, answers: updatedAnswers };
                        });
                    })
                    .catch(error => {
                        console.error('Error fetching updated answer:', error);
                    });
            }
        }).catch(error => {
            console.error('Error submitting comment:', error);
        });
    };

    const handleKeyDown = (event: React.KeyboardEvent<HTMLDivElement>, entityId: number, isQuestion: boolean) => {
        const element = event.currentTarget;

        if (event.key === 'Enter' && element.innerText.trim() !== '') {
            event.preventDefault();
            const comment = element.innerText;
            handleCommentSubmit(comment, entityId, isQuestion);
            element.innerText = ''; // Clear the input after submitting
        }
    };

    if (error) {
        return <p>{error}</p>;
    }

    if (!questionData) {
        return <p>Загрузка...</p>;
    }

    return (
        <section className="main-section">
            <div className="main__header">
                <div className="header__title">
                    <h2>{questionData.question}</h2>
                </div>
                <div className="header__button">
                    <button onClick={handleOpenAnswerPopUp}>Оставить ответ</button>
                </div>
            </div>
            <div className="main__ask">
                <div className="ask__wrapper">
                    <div className="ask__info">
                        <div className="ask__button">
                            <button>^</button>
                        </div>
                        <p>{questionData.answers[0]?.rating || 0}</p>
                    </div>
                    <div className="ask__card">
                        <div className="ask__text">
                            <MarkdownRenderer
                                markdownText={questionData.description} />
                        </div>
                        <div className="ask__author">
                            <p>Вопрос от: {questionData.user.username}</p>
                        </div>

                        {questionData.comments.length > 0 && (
                            <div className="ask__comment">
                                {questionData.comments
                                    .slice()
                                    .sort((a, b) => a.createdAt - b.createdAt)  // Sort comments here
                                    .map((comment) => (
                                        <div key={comment.id} className="comment__wrapper">
                                            <p>{comment.content}</p>
                                        </div>
                                    ))}
                            </div>
                        )}
                        <div className="ask__form">
                            <div
                                contentEditable="true"
                                className="editable-div"
                                data-placeholder="Добавить комментарий"
                                onKeyDown={(e) => handleKeyDown(e, questionData.id, true)}
                            ></div>
                            <div className="input-border"></div>
                        </div>
                    </div>
                </div>
                {/* Обработка ответов */}
                <div className="ask__header">
                    <p>{questionData.answers.length} Ответов</p>
                </div>
                {questionData.answers.map((answer) => (
                    <div key={answer.id} className="ask__wrapper ask__answer">
                        <div className="ask__info">
                            <div className="ask__button">
                                <button>^</button>
                            </div>
                            <p>{answer.rating}</p>
                        </div>
                        <div className="ask__card">
                            <div className="ask__text">
                                <MarkdownRenderer
                                    markdownText={answer.answer} />
                            </div>
                            <div className="ask__author">
                                <p>Ответ от: {answer.answerer.username}</p>
                            </div>
                            {answer.comments.length > 0 && (
                                <div className="ask__comment">
                                    {answer.comments
                                        .slice()
                                        .sort((a, b) => a.createdAt - b.createdAt)  // Sort comments here
                                        .map((comment) => (
                                        <div key={comment.id} className="comment__wrapper">
                                            <p>{comment.content}</p>
                                        </div>
                                    ))}
                                </div>
                            )}
                            <div className="ask__form">
                                <div
                                    contentEditable="true"
                                    className="editable-div"
                                    data-placeholder="Добавить комментарий"
                                    onKeyDown={(e) => handleKeyDown(e, answer.id, false)}
                                ></div>
                                <div className="input-border"></div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            {isAnswerPopUpOpen && (
                <AnswerPopUp
                    question={questionData}
                    closePopup={handleCloseAnswerPopUp}
                />
            )}
        </section>
    );
};

export default Details;
