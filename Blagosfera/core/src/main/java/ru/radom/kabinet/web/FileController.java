package ru.radom.kabinet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.radom.kabinet.services.FilesService;

import javax.servlet.http.HttpServletRequest;

/**
 * Контроллер для загрузки файлов
 * Created by vgusev on 09.10.2015.
 */
@Controller
public class FileController {

    @Autowired
    private FilesService filesService;

    @RequestMapping(value = "/files/upload.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public FileUploadDto uploadFile(HttpServletRequest request) {
        FileUploadDto fileUploadDto = new FileUploadDto();
        fileUploadDto.url = filesService.uploadFromClient(request);
        return fileUploadDto;
    }
}
