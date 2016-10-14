package ru.askor.blagosfera.domain.document;

import lombok.Data;

import java.util.List;

/**
 *
 *
 * Created by vgusev on 06.04.2016.
 */
@Data
public class DocumentClass {

    private Long id;

    private String name;

    private String key;

    private int position;

    private List<DocumentClassDataSource> dataSources;
}
