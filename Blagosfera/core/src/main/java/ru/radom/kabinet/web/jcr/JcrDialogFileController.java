package ru.radom.kabinet.web.jcr;

import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.askor.blagosfera.data.jpa.repositories.DialogsRepository;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.chat.DialogEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.ChatService;
import ru.radom.kabinet.services.jcr.JcrFilesService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.WebUtils;
import ru.radom.kabinet.web.jcr.dto.ResponseJcrFile;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * Created by vgusev on 13.11.2015.
 */
@Controller
public class JcrDialogFileController {

    @Autowired
    private DialogsRepository dialogsRepository;

    @Autowired
    private JcrFilesService jcrFilesService;

    @Autowired
    private ChatService chatService;

    /**
     * Метод загрузки файлов диалога в jcr
     * @param request
     * @param dialogId
     * @return
     * @throws RepositoryException
     */
    @RequestMapping(value = "/dialogfiles/upload.json", method = RequestMethod.POST, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public ResponseJcrFile uploadFile(HttpServletRequest request, @RequestParam(value = "dialogId", required = true) Long dialogId) throws RepositoryException {
        DialogEntity dialog = dialogsRepository.findOne(dialogId);
        if (dialog == null) {
            throw new RuntimeException("Не выбран диалог в который загружается файл");
        }
        List<FileItem> items = WebUtils.parseMultipartRequest(request);
        if (items == null || items.size() == 0) {
            throw new RuntimeException("Необходимо выбрать файл");
        }

        boolean found = false;

        for (UserEntity user : dialog.getUsers()) {
            if (user.getId().equals(SecurityUtils.getUser().getId())) {
                found = true;
                break;
            }
        }

        if (!found) throw new RuntimeException("Вы не являетесь участником диалога");

        FileItem fileItem = items.get(0);
        String[] extensions = chatService.getFileExtensions();
        Long maxFileSize = chatService.getMaxFileSize();

        return jcrFilesService.saveFile(fileItem, dialog, extensions, maxFileSize, SecurityUtils.getUser());
    }
}
