// src/Paginator/Paginator.tsx
import React from 'react';
import './paginator.css';

interface PaginatorProps {
    currentPage: number;
    totalPages: number;
    hasNext: boolean;
    hasPrevious: boolean;
    onPageChange: (page: number) => void;
}

const Paginator: React.FC<PaginatorProps> = ({
                                                 currentPage,
                                                 totalPages,
                                                 hasNext,
                                                 hasPrevious,
                                                 onPageChange,
                                             }) => {
    const getPageButtons = () => {
        const pageButtons = [];
        const range = 2; // Number of pages to show before and after the current page

        if (totalPages <= 1) return [];

        // Show first page
        if (currentPage > range) {
            pageButtons.push(
                <button key={0} className="paginator__button" onClick={() => onPageChange(0)}>
                    1
                </button>
            );
            if (currentPage > range + 1) {
                pageButtons.push(<span key="start-ellipsis" className="paginator__ellipsis">...</span>);
            }
        }

        // Show page buttons around the current page
        for (let i = Math.max(0, currentPage - range); i <= Math.min(totalPages - 1, currentPage + range); i++) {
            pageButtons.push(
                <button
                    key={i}
                    className={`paginator__button ${i === currentPage ? 'paginator__button--active' : ''}`}
                    onClick={() => onPageChange(i)}
                >
                    {i + 1}
                </button>
            );
        }

        // Show last page
        if (currentPage < totalPages - range - 1) {
            if (currentPage < totalPages - range - 2) {
                pageButtons.push(<span key="end-ellipsis" className="paginator__ellipsis">...</span>);
            }
            pageButtons.push(
                <button key={totalPages - 1} className="paginator__button" onClick={() => onPageChange(totalPages - 1)}>
                    {totalPages}
                </button>
            );
        }

        return pageButtons;
    };

    return (
        <section className="paginator">
            <button
                className="paginator__button"
                disabled={!hasPrevious}
                onClick={() => onPageChange(currentPage - 1)}
            >
                Previous
            </button>
            {getPageButtons()}
            <button
                className="paginator__button"
                disabled={!hasNext}
                onClick={() => onPageChange(currentPage + 1)}
            >
                Next
            </button>
        </section>
    );
};

export default Paginator;
