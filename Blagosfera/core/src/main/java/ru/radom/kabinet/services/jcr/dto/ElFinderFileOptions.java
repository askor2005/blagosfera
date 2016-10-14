package ru.radom.kabinet.services.jcr.dto;

import lombok.Data;

import java.util.List;

/**
 *
 * Created by vgusev on 27.02.2016.
 */
@Data
public class ElFinderFileOptions {

    private ElFinderArchiveOptions archivers; //: {create: ["application/x-tar", "application/x-gzip", "application/x-bzip2", "application/x-xz",…],…}
    private List<String> create; //: ["application/x-tar", "application/x-gzip", "application/x-bzip2", "application/x-xz",…]
    private List<String> extract; //: []
    private Integer copyOverwrite; //: 1
    private List<String> disabled; //: ["extract"]
    private String path; //: "Test here/123123/test2"
    private String separator; //: "/"
    private String tmbUrl; //: "http://elfinder.org/files/test2/.tmb/"
    private String url; //: "http://elfinder.org/files/test2/"
}
