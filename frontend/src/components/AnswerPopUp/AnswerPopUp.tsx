import React, { useState } from 'react';
import axios from 'axios';
import styles from './AnswerPopUp.module.css';
import MarkdownEditor from "../Code/MarkdownEditor";
import MarkdownRenderer from "../Code/MarkdownRenderer";
import {useNavigate} from "react-router-dom";


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


interface AnswerPopUpProps {
    question: Question;
    closePopup: () => void;
}


const escapeMarkdownForJSON = (markdownText: string) => {
    return markdownText
        .replace(/\\/g, '\\\\') // Escape backslashes
        .replace(/"/g, '\\"') // Escape double quotes
        .replace(/\n/g, '\\n') // Replace newlines with \n
        .replace(/\r/g, '\\r'); // Replace carriage returns with \r (if needed) если на русском то взял с чата гпт потому что ваще не понимаю почему мой вариант ваще не робит
};


const AnswerPopUp: React.FC<AnswerPopUpProps> = ({ question, closePopup }) => {
    const [answerText, setAnswer] = useState('');
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate(); // Создайте navigate для редиректа

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!answerText.trim()) {
            setError('Ответ не может быть пустым');
            return;
        }


        const answer = escapeMarkdownForJSON(answerText);

        const payload = {
            answer,
            bestAnswer: false, // Always false
        };


        try {
            const response = await fetch(`http://127.0.0.1:8093/answers/question/${question.id}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload),
            });
            console.log(payload)
            if (response.ok) {
                navigate(`/questions/${question.id}`);
                closePopup();
            } else {
                const data = await response.json();
                setError(data.resultRequest || 'Failed to submit question');
            }
        } catch (error) {
            setError('An error occurred while submitting the question.');
        }
    };

    return (
        <div className={styles.overlay}>
            <div className={styles.popup}>
                <button className={styles.closeButton} onClick={closePopup}>X</button>
                <h2 className={styles.title}>Ответить на вопрос Вопрос</h2>
                <form className={styles.form} onSubmit={handleSubmit}>

                    <h3>Вопрос: {question.question}</h3>
                    <div className={styles.askText}>
                        <MarkdownRenderer
                            markdownText={question.description}/>
                    </div>


                    <label className={styles.label} htmlFor="description">Description</label>
                    <div className={styles.askForm}>
                        <MarkdownEditor markdownText={answerText} setMarkdownText={setAnswer}/>
                    </div>

                    {error && <p className={styles.error}>{error}</p>}

                    <button type="submit" className={styles.submitButton}>Submit</button>
                </form>
            </div>
        </div>
    );
};

export default AnswerPopUp;
