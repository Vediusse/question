package entities.question;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import entities.question.QuestionDTO;

import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginatedQuestion {

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("page_size")
    private int pageSize;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("total_items")
    private long totalItems;

    @JsonProperty("has_next")
    private boolean hasNext;

    @JsonProperty("has_prev")
    private boolean hasPrev;

    @JsonProperty("questions")
    private List<QuestionDTO> questions;

    /**
     * Конструктор, который принимает список всех вопросов, номер страницы и размер страницы.
     * Он создает пагинированный список вопросов и вычисляет параметры пагинации.
     */
    public PaginatedQuestion(List<QuestionDTO> allQuestions, int currentPage, int pageSize) {
        // Общее количество элементов
        this.totalItems = allQuestions.size();

        // Вычисление общего количества страниц
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);

        // Проверка границ текущей страницы
        this.currentPage = Math.max(0, Math.min(currentPage, totalPages - 1));
        this.pageSize = pageSize;

        // Вычисление наличия следующей и предыдущей страниц
        this.hasNext = currentPage < totalPages - 1;
        this.hasPrev = currentPage > 0;

        // Вычисление индексов для подмножества списка
        int fromIndex = currentPage * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, (int) totalItems);

        // Создание пагинированного списка вопросов
        if (fromIndex < totalItems) {
            this.questions = allQuestions.subList(fromIndex, toIndex);
        } else {
            this.questions = Collections.emptyList();  // Пустой список, если текущая страница выходит за границы
        }
    }

    // Getters and setters
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrev() {
        return hasPrev;
    }

    public void setHasPrev(boolean hasPrev) {
        this.hasPrev = hasPrev;
    }

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }
}

