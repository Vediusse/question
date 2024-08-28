import React, { useState, useEffect } from 'react';
import styles from './FormPopUp.module.css';
import MarkdownEditor from "../Code/MarkdownEditor";
import { Tooltip } from 'react-tooltip';
import {useNavigate} from "react-router-dom";

interface QuestionPopUpProps {
    closePopup: () => void;
}

const subjectLabCounts: Record<string, number> = {
    'INFORMATIK': 5,
    'PROGA': 8,
    'OPD': 7,
    'DB': 4,
    'WEB': 4,
    'LP': 5,
};

const escapeMarkdownForJSON = (markdownText: string) => {
    return markdownText
        .replace(/\\/g, '\\\\') // Escape backslashes
        .replace(/"/g, '\\"') // Escape double quotes
        .replace(/\n/g, '\\n') // Replace newlines with \n
        .replace(/\r/g, '\\r'); // Replace carriage returns with \r (if needed) если на русском то взял с чата гпт потому что ваще не понимаю почему мой вариант ваще не робит
};

const QuestionPopUp: React.FC<QuestionPopUpProps> = ({ closePopup }) => {
    const [question, setQuestion] = useState('');
    const [subject, setSubject] = useState<string | null>(null);
    const [labNumber, setLabNumber] = useState<number | null>(null);
    const [description, setDescription] = useState('');
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate(); // Создайте navigate для редиректа


    useEffect(() => {
        if (subject && labNumber && (labNumber > subjectLabCounts[subject] || labNumber < 1)) {
            setLabNumber(null);
        }
    }, [subject]);

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setError(null);

        if (!question || !subject || !labNumber || !description) {
            setError('All fields are required.');
            return;
        }

        if (labNumber > subjectLabCounts[subject] || labNumber < 1) {
            setError('Lab number is out of range for the selected subject.');
            return;
        }

        const escapedDescription = escapeMarkdownForJSON(description);

        const payload = {
            question,
            subject,
            answers: null,
            labNumber,
            description: escapedDescription,
        };

        try {
            const response = await fetch('http://127.0.0.1:8090/questions', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                },
                body: JSON.stringify(payload),
            });

            if (response.ok) {

                const data = await response.json();
                const createdQuestionId = data.question.id;

                // Редирект на страницу с вопросом после успешного создания
                navigate(`/questions/${createdQuestionId}`);


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
                <h2 className={styles.title}>Задать Вопрос</h2>
                <form className={styles.form} onSubmit={handleSubmit}>
                    <label className={styles.label} htmlFor="question" title="Select the relevant subject for the question">Вопрос</label>
                    <input
                        className={styles.input}
                        type="text"
                        id="question"
                        name="question"
                        value={question}
                        onChange={(e) => setQuestion(e.target.value)}
                        required
                    />

                    <label className={styles.label} htmlFor="subject">Предмет</label>
                    <select
                        className={styles.select}
                        id="subject"
                        name="subject"
                        value={subject || ''}
                        onChange={(e) => setSubject(e.target.value)}
                        required
                    >
                        <option value="">Select subject</option>
                        <option value="INFORMATIK">Информатика</option>
                        <option value="PROGA">Прога</option>
                        <option value="OPD">ОПД</option>
                        <option value="DB">Базы Данных</option>
                        <option value="WEB">Веб</option>
                        <option value="LP">Языки Программирования</option>
                    </select>

                    <label className={styles.label} htmlFor="labNumber">Lab Number</label>
                    <input
                        className={styles.input}
                        type="number"
                        id="labNumber"
                        name="labNumber"
                        value={labNumber || ''}
                        min="1"
                        max={subject ? subjectLabCounts[subject] : undefined}
                        onChange={(e) => setLabNumber(parseInt(e.target.value, 10))}
                        required
                    />

                    <label className={styles.label} htmlFor="description">Description</label>
                    <div className={styles.askForm}>
                        <MarkdownEditor markdownText={description} setMarkdownText={setDescription}/>
                    </div>

                    {error && <p className={styles.error}>{error}</p>}

                    <button type="submit" className={styles.submitButton}>Submit</button>
                </form>
            </div>
        </div>
    );
};

export default QuestionPopUp;
