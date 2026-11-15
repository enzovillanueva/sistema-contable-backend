package com.sistemacontable.shared.response;

import lombok.Data;

import java.util.List;

@Data
public class PagedResponse<T> {
    private List<T> items;
    private int page;
    private int pageSize;
    private int totalItems;
    private int totalPages;

    public int getTotalPages(){
        return (int) Math.ceil( (double) totalItems / pageSize );
    }
}
