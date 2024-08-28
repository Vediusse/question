import React, { useEffect, useState, useCallback } from 'react';
import axios from 'axios';
import './questions.css';
import {useNavigate} from "react-router-dom";
import {useUser} from "../../../services/UserContext";

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
    answerer: User;
    comments: any[];
}

interface Question {
    id: number;
    question: string;
    user: User;
    subject: string;
    description: string;
    answers: Answer[];
    comments: any[];
}

interface ApiResponse {
    questionList: Question[];
    currentPage: number;
    totalPages: number;
    hasNext: boolean;
    hasPrevious: boolean;
    resultRequest: string;
    status: string;
}

interface QuestionListProps {
    subject: string | null;
    labNumber: number | null;
    currentPage: number;
    isMe: boolean | null;
    isPublic: boolean | null;
    onPageChange: (page: number, pages: { totalPages: number; hasNext: boolean; hasPrevious: boolean }) => void;
    onNoQuestionsFound: () => void;
}





const QuestionList: React.FC<QuestionListProps> = ({ subject, labNumber, currentPage,isMe, isPublic, onPageChange, onNoQuestionsFound }) => {
    const [questions, setQuestions] = useState<Question[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const navigate = useNavigate();

    const { user } = useUser();

    const fetchQuestions = useCallback(async () => {
        setLoading(true);
        try {
            const params: any = {
                page: currentPage,
                size: 10,
                ...(subject && { subject }),
                ...(labNumber !== null && { labNumber }),
                ...(isMe !== null && { isMe }),
                ...(isPublic !== null && { isPublic }),
            };

            const response = await axios.get<ApiResponse>('http://127.0.0.1:8090/questions/paginated', {
                params,
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                    'Accept': 'application/json'
                }
            });

            console.log(response);

            const { questionList, currentPage: newCurrentPage, totalPages, hasNext, hasPrevious } = response.data;
            if (questionList.length === 0) {
                onNoQuestionsFound();
            } else {
                setQuestions(questionList);
                onPageChange(newCurrentPage, {
                    totalPages,
                    hasNext,
                    hasPrevious
                });
            }
        } catch (error) {
            console.error('Error fetching questions:', error);
        } finally {
            setLoading(false);
        }
    }, [subject, labNumber, isMe, isPublic, currentPage, onPageChange, onNoQuestionsFound]);


    useEffect(() => {
        fetchQuestions();
    }, [fetchQuestions]);

    const handleQuestionClick = (id: number) => {
        navigate(`/questions/${id}`);
    };

    return (
        <div className="main__questions">
            {loading ? (
                <div className="loading">Загрузка...</div>
            ) : (
                questions.length > 0 ? (
                    questions.map((question) => (
                        <div key={question.id} className="question" onClick={() => handleQuestionClick(question.id)}>
                            <div className="question__info">
                                <div className="info">
                                    <div className="question__answer">
                                        <p>{question.answers.length} ответов</p>
                                    </div>
                                    <div className="question__answer question__answer-decor">
                                        <p>{question.comments.length} комментариев</p>
                                    </div>
                                </div>
                            </div>
                            <div className="question_card">
                                <div className="question__text">
                                    <div className="question__title">
                                        <p>{question.question}</p>
                                    </div>
                                    <div className="question__description">
                                        <p>{question.description}</p>
                                    </div>
                                </div>
                                <div className="question__admin">
                                    <div className="question__asker">
                                        <p>Вопрос от: {question.user.username}</p>
                                    </div>
                                    {user?.role.name=="ADMIN" ? (
                                        <div className="question__form">
                                        <button className="question__button question__button-agree">
                                            Принять
                                        </button>
                                        <button className="question__button question__button-otkaj">
                                            Откажj
                                        </button>
                                    </div>
                                    ) : null}
                                </div>
                            </div>
                        </div>
                    ))
                ) : (
                    <div className="no-questions">Не удалось найти вопросы.</div>
                )
            )}
        </div>
    );
};

export default QuestionList;
