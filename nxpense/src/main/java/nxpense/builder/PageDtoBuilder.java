package nxpense.builder;

import nxpense.dto.PageDTO;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PageDtoBuilder <E> {

    private int pageSize;
    private int pageNumber;
    private int numberOfItems;
    private List<E> items;
    private String sortProperty;
    private Sort.Direction sortDirection;

    public PageDtoBuilder<E> setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public PageDtoBuilder<E> setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public PageDtoBuilder<E> setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
        return this;
    }

    public PageDtoBuilder<E> setItems(List<E> items) {
        this.items = items;
        return this;
    }

    public PageDtoBuilder<E> setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
        return this;
    }

    public PageDtoBuilder<E> setSortDirection(Sort.Direction sortDirection) {
        this.sortDirection = sortDirection;
        return this;
    }

    public PageDTO<E> build() {
        PageDTO<E> pageDto = new PageDTO<E>();

        pageDto.setPageSize(pageSize);
        pageDto.setPageNumber(pageNumber);
        pageDto.setNumberOfItems(numberOfItems);
        pageDto.setItems(items);
        pageDto.setSortProperty(sortProperty);
        pageDto.setSortDirection(sortDirection);

        return pageDto;
    }
}
