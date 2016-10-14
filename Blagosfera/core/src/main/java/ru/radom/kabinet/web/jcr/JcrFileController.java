package ru.radom.kabinet.web.jcr;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.jcr.JcrFilesService;

import javax.jcr.ItemNotFoundException;
import javax.mail.internet.MimeUtility;
import java.io.InputStream;

/**
 * Контроллер для работы с файлами JCR
 * Created by vgusev on 27.08.2015.
 */
@Controller
public class JcrFileController {

    @Autowired
    private JcrFilesService jcrFilesService;

    /**
     * Получить файл по ИД ноды описания файла
     * @param nodeId
     * @return
     */
    @RequestMapping(value = "/files/{nodeId}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getFileById(@PathVariable("nodeId") String nodeId) {
        ResponseEntity<byte[]> result = null;
        InputStream inputStream = null;
        try {
            JcrFilesService.FileContentWrapper fileContentWrapper = jcrFilesService.getFileContentByNodeId(nodeId, SecurityUtils.getUser());
            inputStream = fileContentWrapper.getInputStream();
            byte[] resultData = IOUtils.toByteArray(inputStream);

            final HttpHeaders headers = new HttpHeaders();

            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            try {
                mediaType = MediaType.parseMediaType(fileContentWrapper.getMimeType());
            } catch (Exception e) {
                // do nothing
            }

            String fileName = MimeUtility.encodeText(fileContentWrapper.getFileName(), "UTF-8", null);
            headers.setContentType(mediaType);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.setContentLength(resultData.length);

            result = new ResponseEntity<>(resultData, headers, HttpStatus.OK);
        } catch (ItemNotFoundException e) {
            System.err.println("Файл с ИД: " + nodeId + " не найден!");
            result = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return result;
    }

    /*@RequestMapping(value = "/files/list/{nodeId}.json", method = RequestMethod.GET, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public List<ResponseJcrFile> getJcrFiles(@PathVariable("nodeId") String nodeId) {
        return jcrFilesService.getFileList(nodeId, radomRequestContext.getCurrentSharer());
    }

    @RequestMapping(value = "/files/community_root/{communityId}.json", method = RequestMethod.GET, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public ResponseJcrFile getCommunityRootNodeId(@PathVariable("communityId") Long communityId) {
        return jcrFilesService.getCommunityRootFile(communityDao.loadById(communityId), radomRequestContext.getCurrentSharer());
    }

    @RequestMapping(value = "/files/folder/create.json", method = RequestMethod.GET, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public ResponseJcrFile createFolder(
            @RequestParam(value = "node_id") String parentNodeId,
            @RequestParam(value = "name") String folderName) {
        return jcrFilesService.saveFolder(parentNodeId, folderName, radomRequestContext.getCurrentSharer());
    }*/
}
