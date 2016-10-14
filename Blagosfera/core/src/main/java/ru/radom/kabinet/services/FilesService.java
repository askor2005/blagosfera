package ru.radom.kabinet.services;

import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.radom.kabinet.services.image.ImagesService;
import ru.radom.kabinet.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Загрузка файлов на файл сервер
 * Created by vgusev on 09.10.2015.
 */
@Service
public class FilesService {

    @Autowired
    private ImagesService imagesService;

    // Загружает картинку из формы на сервер картинок и возвращает на него ссылку, публикует событие
    public String uploadFromClient(final HttpServletRequest request) {
        System.err.println(request.getParameter("attachedFile"));
        String result;
        try {
            List<FileItem> items = WebUtils.parseMultipartRequest(request);
            FileItem item = items != null && items.size() > 0 ? items.get(0) : null;

            if (item == null) {
                throw new ImageException("Необходимо выбрать файл");
            }
            byte[] fileBuffer = item.get();
            String fileName = item.getName();

            result = imagesService.uploadToImageServer(fileName, fileBuffer);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }
}
