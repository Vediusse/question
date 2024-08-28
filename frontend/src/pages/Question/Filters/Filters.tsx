import React, { useState, useEffect } from 'react';
import './filters.css';
import QuestionPopUp from '../../../components/FormPopUp/FormPopUp'; // Adjust the import path if necessary

type SubjectEnumKeys = 'INFORMATIK' | 'PROGA' | 'OPD' | 'DB' | 'WEB' | 'LP';

const subjectEnum: Record<string, SubjectEnumKeys> = {
    'Информатика': 'INFORMATIK',
    'ОПД': 'OPD',
    'Прога': 'PROGA',
    'БД': 'DB',
    'ЯПы': 'LP',
    'Веб': 'WEB',
};

const subjectLabCounts: Record<SubjectEnumKeys, number> = {
    'INFORMATIK': 5,
    'PROGA': 8,
    'OPD': 7,
    'DB': 4,
    'WEB': 4,
    'LP': 5,
};

interface FiltersProps {
    selectedSubject: string | null;
    setSelectedSubject: (subject: string | null) => void;
    selectedLabNumber: number | null;
    setSelectedLabNumber: (labNumber: number | null) => void;
    selectedIsMe: boolean | null;
    setSelectedIsMe: (isMe: boolean | null) => void;
    selectedIsPublic: boolean | null;
    setSelectedIsPublic: (isPublic: boolean | null) => void;
    onFilterChange: () => void;
}

const Filters: React.FC<FiltersProps> = ({
                                             selectedSubject,
                                             setSelectedSubject,
                                             selectedLabNumber,
                                             setSelectedLabNumber,
                                             selectedIsMe,
                                             setSelectedIsMe,
                                             selectedIsPublic,
                                             setSelectedIsPublic,
                                             onFilterChange
                                         }) => {
    const [activeSubject, setActiveSubject] = useState<string | null>(selectedSubject);
    const [activeLabNumber, setActiveLabNumber] = useState<number | null>(selectedLabNumber);
    const [activeIsMe, setActiveIsMe] = useState<boolean | null>(selectedIsMe);
    const [activeIsPublic, setActiveIsPublic] = useState<boolean | null>(selectedIsPublic);
    const [isPopUpOpen, setIsPopUpOpen] = useState(false);

    const handleOpenPopUp = () => setIsPopUpOpen(true);
    const handleClosePopUp = () => setIsPopUpOpen(false);

    useEffect(() => {
        setActiveSubject(selectedSubject);
    }, [selectedSubject]);

    useEffect(() => {
        setActiveLabNumber(selectedLabNumber);
    }, [selectedLabNumber]);

    useEffect(() => {
        setActiveIsMe(selectedIsMe);
    }, [selectedIsMe]);

    useEffect(() => {
        setActiveIsPublic(selectedIsPublic);
    }, [selectedIsPublic]);

    const handleSubjectClick = (subject: string) => {
        const enumKey = subjectEnum[subject];
        if (selectedSubject === enumKey) {
            setSelectedSubject(null);
            setSelectedLabNumber(null);
        } else {
            setSelectedSubject(enumKey);
        }
        onFilterChange();
    };

    const handleLabNumberClick = (labNumber: number) => {
        const newLabNumber = selectedLabNumber === labNumber ? null : labNumber;
        setSelectedLabNumber(newLabNumber);
        onFilterChange();
    };

    const handleIsMeClick = () => {
        setSelectedIsMe(!selectedIsMe);
        onFilterChange();
    };

    const handleIsPublicClick = () => {
        setSelectedIsPublic(!selectedIsPublic);
        onFilterChange();
    };

    const renderButtons = (buttons: string[], startIndex: number) => (
        <div className="filter__wrapper">
            {buttons.map((label, buttonIndex) => (
                <button
                    key={buttonIndex}
                    className={`filter__button ${
                        activeSubject === subjectEnum[label] ? 'filter__button-active' : ''
                    }`}
                    onClick={() => handleSubjectClick(label)}
                >
                    {label}
                </button>
            ))}
        </div>
    );

    const renderLabCountButtons = () => {
        if (!selectedSubject) return null;

        const labCount = subjectLabCounts[selectedSubject as SubjectEnumKeys];
        return (
            <div className="filter__wrapper">
                {Array.from({ length: labCount }, (_, i) => (
                    <button
                        key={i}
                        className={`filter__button ${
                            activeLabNumber === (i + 1) ? 'filter__button-active' : ''
                        }`}
                        onClick={() => handleLabNumberClick(i + 1)}
                    >
                        {i + 1}
                    </button>
                ))}
            </div>
        );
    };

    const renderFilterOptions = () => (
        <div className="filter__wrapper">
            <button
                className={`filter__button ${activeIsMe ? 'filter__button-active' : ''}`}
                onClick={handleIsMeClick}
            >
                Мои записи
            </button>
            <button
                className={`filter__button ${activeIsPublic === false ? 'filter__button-active' : ''}`}
                onClick={handleIsPublicClick}
            >
                Утвержденные
            </button>

        </div>
    );

    return (
        <>
            <div className="main__header">
                <div className="header__title">
                    <h2>Вопросы</h2>
                </div>
                <div className="header__button">
                    <button onClick={handleOpenPopUp}>Задать вопрос</button>
                </div>
            </div>
            <div className="main__filter">
                <div className="filter__row">
                    {renderFilterOptions()}
                </div>
                <div className="filter__row">
                    {renderButtons(['Информатика', 'ОПД', 'Прога', 'БД'], 0)}
                    {renderButtons(['ЯПы', 'Веб'], 4)}
                </div>
                <div className="filter__row">
                    {renderLabCountButtons()}
                </div>
            </div>
            {isPopUpOpen && <QuestionPopUp closePopup={handleClosePopUp}/>}
        </>
    );
};

export default Filters;
