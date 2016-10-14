package ru.radom.kabinet.web.jcr;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.jcr.JcrElFinderService;
import ru.radom.kabinet.services.jcr.dto.ElFinderCommand;
import ru.radom.kabinet.services.jcr.dto.JcrElFinderRequestDto;
import ru.radom.kabinet.services.jcr.dto.JcrElFinderResponseDto;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.utils.WebUtils;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 *
 * Created by vgusev on 26.02.2016.
 */
@Controller
public class JcrElFinderController {

    @Autowired
    private JcrElFinderService jcrElFinderService;

    @Autowired
    private SerializeService serializeService;

    private JcrElFinderRequestDto resolveElFinderRequest(HttpServletRequest request, String entityType, Long entityId) {
        List<FileItem> fileItems = null;
        String command = null;
        String current = null;
        String target = null;
        Integer tree = null;
        String name = null;
        Integer init = null;
        String destination = null;
        List<String> targets = null;
        Integer cut = null;
        String source = null;
        String content = null;
        String type = null;
        Integer download = null;

        List<FileItem> allFileItems = null;
        try {
            allFileItems = WebUtils.parseMultipartRequest(request);
        } catch (Exception e) {
            // do nothing
        }
        if (allFileItems != null && allFileItems.size() > 0) { // Если есть fileItems значит это multipart запрос
            Map<String, String> requestParams = new HashMap<>();
            fileItems = new ArrayList<>();
            for (FileItem fileItem : allFileItems) {
                if (fileItem.getFieldName().equals("upload[]")) { // Загружаемые файлы
                    fileItems.add(fileItem);
                } else {
                    requestParams.put(fileItem.getFieldName(), fileItem.getString());
                }
            }
            current = requestParams.get("current");
            command = requestParams.get("cmd");
        } else {
            String[] targetsArr = request.getParameterMap().get("targets[]");

            command = request.getParameter("cmd");
            current = request.getParameter("current");
            target = request.getParameter("target");
            tree = VarUtils.getInt(request.getParameter("tree"), null);
            name = request.getParameter("name");
            init = VarUtils.getInt(request.getParameter("init"), null);
            destination = request.getParameter("dst");
            targets = targetsArr != null ? Arrays.asList(targetsArr) : null;
            cut = VarUtils.getInt(request.getParameter("cut"), null);
            source = request.getParameter("source");
            content = request.getParameter("content");
            type = request.getParameter("type");
            download = VarUtils.getInt(request.getParameter("download"), null);
        }

        JcrElFinderRequestDto jcrElFinderRequestDto = new JcrElFinderRequestDto();
        jcrElFinderRequestDto.setCommand(ElFinderCommand.valueOf(command));
        jcrElFinderRequestDto.setTarget(target);
        jcrElFinderRequestDto.setCurrent(current);
        jcrElFinderRequestDto.setName(name);
        jcrElFinderRequestDto.setCurrentUser(SecurityUtils.getUser());
        jcrElFinderRequestDto.setEntityType(entityType);
        jcrElFinderRequestDto.setEntityId(entityId);
        jcrElFinderRequestDto.setInit(init != null && init == 1);
        jcrElFinderRequestDto.setTree(tree != null && tree == 1);
        jcrElFinderRequestDto.setCut(cut != null && cut == 1);
        jcrElFinderRequestDto.setTargets(targets);
        jcrElFinderRequestDto.setDestination(destination);
        jcrElFinderRequestDto.setSource(source);
        jcrElFinderRequestDto.setContent(content);
        jcrElFinderRequestDto.setType(type);
        jcrElFinderRequestDto.setFileItems(fileItems);
        jcrElFinderRequestDto.setDownload(download != null && download == 1);

        return jcrElFinderRequestDto;
    }

    @RequestMapping(value = "/files/el_finder/{entitytype}/{entityid}/handle.json", headers = "content-type=*", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void handleElFinderGetRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable("entitytype") String entityType,
            @PathVariable("entityid") Long entityId
            ) throws IOException {
        JcrElFinderRequestDto jcrElFinderRequestDto =resolveElFinderRequest(request, entityType, entityId);
        JcrElFinderResponseDto responseDto = jcrElFinderService.handleRequest(jcrElFinderRequestDto);

        if (responseDto.getFileContentWrapper() != null) {
            String fileName = MimeUtility.encodeText(responseDto.getFileContentWrapper().getFileName(), "UTF-8", null);

            response.setContentType(responseDto.getFileContentWrapper().getMimeType());
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentLength(responseDto.getFileContentWrapper().getFileSize());
            IOUtils.copyLarge(responseDto.getFileContentWrapper().getInputStream(), response.getOutputStream());
        } else {
            String json = serializeService.toJson(responseDto);
            response.setContentType(CommonConstants.RESPONSE_JSON_MEDIA_TYPE);
            response.getWriter().write(json);
        }
    }
}
