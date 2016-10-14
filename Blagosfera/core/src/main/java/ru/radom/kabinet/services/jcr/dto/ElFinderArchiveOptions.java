package ru.radom.kabinet.services.jcr.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 29.02.2016.
 */
@Data
public class ElFinderArchiveOptions {

    private List<String> create = new ArrayList<>();

    private Map<String, String> createext = new HashMap<>();

    private List<String> extract = new ArrayList<>();
}
