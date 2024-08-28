import React, { useState } from 'react';
import Filters from "./Filters/Filters";
import QuestionList from "./QuestionList/QuestionList";
import Paginator from "./Paginator/Paginator";

const Questions: React.FC = () => {
    const [selectedSubject, setSelectedSubject] = useState<string | null>(null);
    const [selectedLabNumber, setSelectedLabNumber] = useState<number | null>(null);
    const [currentPage, setCurrentPage] = useState<number>(0); // Начальная страница 1
    const [totalPages, setTotalPages] = useState<number>(1);
    const [hasNext, setHasNext] = useState<boolean>(false);
    const [hasPrevious, setHasPrevious] = useState<boolean>(false);
    const [noQuestionsFound, setNoQuestionsFound] = useState<boolean>(false);
    const [showPaginator, setShowPaginator] = useState<boolean>(false);

    const [selectedIsMe, setSelectedIsMe] = useState<boolean | null>(false);
    const [selectedIsPublic, setSelectedIsPublic] = useState<boolean | null>(true);



    const handlePageChange = (page: number) => {
        setCurrentPage(page);
    };

    const handlePageChangeFromQuestionList = (page: number, pages: { totalPages: number; hasNext: boolean; hasPrevious: boolean }) => {
        setCurrentPage(page);
        setTotalPages(pages.totalPages);
        setHasNext(pages.hasNext);
        setHasPrevious(pages.hasPrevious);
        setShowPaginator(true); // Показываем пагинатор после загрузки данных
    };

    const handleNoQuestionsFound = () => {
        setNoQuestionsFound(true);
        setShowPaginator(false); // Скрываем пагинатор, если нет вопросов
    };

    return (
        <div className="main-section">
            <Filters
                selectedSubject={selectedSubject}
                setSelectedSubject={setSelectedSubject}
                selectedLabNumber={selectedLabNumber}
                setSelectedLabNumber={setSelectedLabNumber}
                selectedIsMe={selectedIsMe}
                setSelectedIsMe={setSelectedIsMe}
                selectedIsPublic={selectedIsPublic}
                setSelectedIsPublic={setSelectedIsPublic}
                onFilterChange={() => {
                    setNoQuestionsFound(false); // Сбрасываем состояние, когда фильтр меняется
                    setShowPaginator(false);    // Скрываем пагинатор при изменении фильтра
                }}
            />
            {noQuestionsFound ? (
                <div className="no-questions">Не удалось найти вопросы.</div>
            ) : (
                <>
                    <QuestionList
                        subject={selectedSubject}
                        labNumber={selectedLabNumber}
                        currentPage={currentPage}
                        isMe={selectedIsMe}
                        isPublic={selectedIsPublic}
                        onPageChange={handlePageChangeFromQuestionList}
                        onNoQuestionsFound={handleNoQuestionsFound}
                    />
                    {showPaginator && (
                        <Paginator
                            currentPage={currentPage}
                            totalPages={totalPages}
                            hasNext={hasNext}
                            hasPrevious={hasPrevious}
                            onPageChange={handlePageChange}
                        />
                    )}
                </>
            )}
        </div>
    );
};

export default Questions;
