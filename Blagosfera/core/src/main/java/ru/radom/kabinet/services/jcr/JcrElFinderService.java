package ru.radom.kabinet.services.jcr;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.data.jpa.repositories.DialogsRepository;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.services.jcr.dto.*;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.web.jcr.dto.ResponseJcrFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * Created by vgusev on 26.02.2016.
 */
@Service
public class JcrElFinderService {

    @Autowired
    private JcrFilesService jcrFilesService;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private DialogsRepository dialogsRepository;

    private static final List<ElFinderArchiveType> AVAILABLE_ARCHIVE_TYPES = Arrays.asList(ElFinderArchiveType.ZIP);

    private LongIdentifiable getFileEntity(JcrElFinderRequestDto jcrElFinderRequestDto) {
        LongIdentifiable longIdentifiable = null;
        switch (jcrElFinderRequestDto.getEntityType()) {
            case Discriminators.COMMUNITY:
                longIdentifiable = communityDao.loadById(jcrElFinderRequestDto.getEntityId());
                break;
            case Discriminators.SHARER:
                longIdentifiable = sharerDao.loadById(jcrElFinderRequestDto.getEntityId());
                break;
            case Discriminators.DIALOG:
                longIdentifiable = dialogsRepository.getOne(jcrElFinderRequestDto.getEntityId());
                break;
        }
        ExceptionUtils.check(longIdentifiable == null, "Не определён тип сущности для файлов");
        return longIdentifiable;
    }

    private String getRootFolderName(JcrElFinderRequestDto jcrElFinderRequestDto) {
        String result = null;
        switch (jcrElFinderRequestDto.getEntityType()) {
            case Discriminators.COMMUNITY:
                result = "Файлы объединения";
                break;
            case Discriminators.SHARER:
                result = "Мои файлы";
                break;
            case Discriminators.DIALOG:
                result = "Файлы диалога";
                break;
        }
        ExceptionUtils.check(result == null, "Не определён тип сущности для файлов");
        return result;
    }

    public JcrElFinderResponseDto handleRequest(JcrElFinderRequestDto jcrElFinderRequestDto) {
        JcrElFinderResponseDto result = null;
        switch (jcrElFinderRequestDto.getCommand()) {
            case mkdir:
                result = createFolder(jcrElFinderRequestDto);
                break;
            case mkfile:
                result = createFile(jcrElFinderRequestDto);
                break;
            case open:
                result = openFolder(jcrElFinderRequestDto);
                break;
            case tree:
                result = treeFolder(jcrElFinderRequestDto);
                break;
            case rename:
                result = renameFile(jcrElFinderRequestDto);
                break;
            case upload:
                result = uploadFiles(jcrElFinderRequestDto);
                break;
            case ls:
                result = listFolder(jcrElFinderRequestDto);
                break;
            case rm:
                result = removeFiles(jcrElFinderRequestDto);
                break;
            case paste:
                result = cutAndPasteFiles(jcrElFinderRequestDto);
                break;
            case put:
                result = putContentToFile(jcrElFinderRequestDto);
                break;
            case get:
                result = getContentTextFile(jcrElFinderRequestDto);
                break;
            case file:
                result = getContentFile(jcrElFinderRequestDto);
                break;
            case archive:
                result = archiveFiles(jcrElFinderRequestDto);
                break;
            case duplicate:
                result = duplicateFiles(jcrElFinderRequestDto);
                break;
            case parents: // TODO Необходимо реализовать
                result = new JcrElFinderResponseDto();
                break;
        }
        ExceptionUtils.check(result == null, "Переменная с ответом не определена");
        return result;
    }

    /**
     * Откыть каталог с файлами
     * @param jcrElFinderRequestDto
     * @return
     */
    private JcrElFinderResponseDto openFolder(JcrElFinderRequestDto jcrElFinderRequestDto) {
        JcrElFinderResponseDto result = new JcrElFinderResponseDto();
        if (jcrElFinderRequestDto.isDownload()) { // Скачать файл
            try {
                JcrFilesService.FileContentWrapper fileContentWrapper = jcrFilesService.getFileContentByNodeId(jcrElFinderRequestDto.getTarget(), jcrElFinderRequestDto.getCurrentUser());
                result.setFileContentWrapper(fileContentWrapper);
            } catch (Exception e) {
                ExceptionUtils.check(true, e.getMessage());
            }
        } else {
            String target = jcrElFinderRequestDto.getTarget();
            LongIdentifiable longIdentifiable = getFileEntity(jcrElFinderRequestDto);
            ResponseJcrFile rootResponseJcrFile = jcrFilesService.getRootFile(longIdentifiable, jcrElFinderRequestDto.getCurrentUser());
            rootResponseJcrFile.setFileName(getRootFolderName(jcrElFinderRequestDto));
            if (StringUtils.isBlank(target)) {
                ExceptionUtils.check(rootResponseJcrFile == null, "TODO");
                target = rootResponseJcrFile.getNodeIdentifire();
            }

            ResponseJcrFile currentJcrFile = jcrFilesService.getFileById(target);
            ElFinderFile currentFile = getFile(currentJcrFile, jcrElFinderRequestDto.getCurrentUser(), currentJcrFile.getParentNodeIdentifire(), false);

            result.setCwd(currentFile);
            if (jcrElFinderRequestDto.isInit()) {
                result.setUplMaxSize("32M");
                result.setOptions(new ElFinderFileOptions());
                result.getOptions().setArchivers(new ElFinderArchiveOptions());
                for (ElFinderArchiveType elFinderArchiveType : AVAILABLE_ARCHIVE_TYPES) {
                    result.getOptions().getArchivers().getCreate().add(elFinderArchiveType.getType());
                    result.getOptions().getArchivers().getCreateext().put(elFinderArchiveType.getType(), elFinderArchiveType.getExtension());
                }
                result.getOptions().setDisabled(Collections.singletonList("extract"));
            }

            if (jcrElFinderRequestDto.isTree()) { // Нужно загрузить всю иерархию до домашнего каталога
                String currentId = currentJcrFile.getNodeIdentifire();
                ResponseJcrFile parentJcrFile = currentJcrFile;
                LinkedList<ResponseJcrFile> elFinderFiles = new LinkedList<>();
                while (!currentId.equals(rootResponseJcrFile.getNodeIdentifire())) {
                    List<ResponseJcrFile> childList = jcrFilesService.getFileList(parentJcrFile.getParentNodeIdentifire(), jcrElFinderRequestDto.getCurrentUser());
                    for (ResponseJcrFile childFile : childList) {
                        elFinderFiles.addFirst(childFile);
                    }
                    currentId = parentJcrFile.getParentNodeIdentifire();
                    parentJcrFile = jcrFilesService.getFileById(parentJcrFile.getParentNodeIdentifire());
                }
                elFinderFiles.addFirst(rootResponseJcrFile);

                int i = 0;
                for (ResponseJcrFile responseJcrFile : elFinderFiles) {
                    ElFinderFile elFinderFile = getFile(responseJcrFile, jcrElFinderRequestDto.getCurrentUser(), responseJcrFile.getParentNodeIdentifire(), true);
                    if (i == 0) {
                        elFinderFile.setVolumeid(elFinderFile.getHash());
                        elFinderFile.setPhash(null);
                    }
                    result.getFiles().add(elFinderFile);
                    i++;
                }
            }

            List<ResponseJcrFile> responseJcrFiles = jcrFilesService.getFileList(target, jcrElFinderRequestDto.getCurrentUser());
            for (ResponseJcrFile responseJcrFile : responseJcrFiles) {
                result.getFiles().add(getFile(responseJcrFile, jcrElFinderRequestDto.getCurrentUser(), target, true));
            }
        }
        return result;
    }

    private ElFinderFile getFileById(String fileId, User user, String parentFileId, boolean isTree) {
        return getFile(jcrFilesService.getFileById(fileId), user, parentFileId, isTree);
    }

    private ElFinderFile getFile(ResponseJcrFile currentFile, User user, String parentFileId, boolean isTree) {
        ElFinderFile elFinderFile = new ElFinderFile();
        elFinderFile.setName(currentFile.getFileName());
        elFinderFile.setHash(currentFile.getNodeIdentifire());
        elFinderFile.setPhash(parentFileId);
        elFinderFile.setMime(currentFile.getMimeType());
        elFinderFile.setTs(currentFile.getModifiedTimeStamp());

        elFinderFile.setSize(currentFile.getFileSize() == null ? 0l : currentFile.getFileSize());
        elFinderFile.setRead(1);
        elFinderFile.setWrite(1);
        //elFinderFile.setLocked(1);
        if (currentFile.isFolder() && isTree) {
            //elFinderFile.setVolumeid(currentFile.getNodeIdentifire());
            List<ResponseJcrFile> child = jcrFilesService.getFileList(currentFile.getNodeIdentifire(), user);
            boolean hasFolders = false;
            if (child != null && child.size() > 0) {
                for (ResponseJcrFile responseJcrFile : child) {
                    if (responseJcrFile.isFolder()) {
                        hasFolders = true;
                        break;
                    }
                }
            }
            elFinderFile.setDirs(hasFolders ? 1 : 0);
        }
        return elFinderFile;
    }

    /**
     * Создать каталог
     * @param jcrElFinderRequestDto
     * @return
     */
    private JcrElFinderResponseDto createFolder(JcrElFinderRequestDto jcrElFinderRequestDto) {
        JcrElFinderResponseDto result = new JcrElFinderResponseDto();
        ResponseJcrFile responseJcrFile = jcrFilesService.saveFolder(jcrElFinderRequestDto.getTarget(), jcrElFinderRequestDto.getName(), jcrElFinderRequestDto.getCurrentUser());
        ElFinderFile elFinderFile = new ElFinderFile();
        elFinderFile.setName(responseJcrFile.getFileName());
        elFinderFile.setHash(responseJcrFile.getNodeIdentifire());
        elFinderFile.setPhash(jcrElFinderRequestDto.getTarget());
        elFinderFile.setMime(responseJcrFile.getMimeType());
        elFinderFile.setTs(responseJcrFile.getModifiedTimeStamp());
        elFinderFile.setSize(responseJcrFile.getFileSize() == null ? 0l : responseJcrFile.getFileSize());
        elFinderFile.setRead(1);
        elFinderFile.setWrite(1);
        result.getAdded().add(elFinderFile);
        return result;
    }

    /**
     * Создать файл
     * @param jcrElFinderRequestDto
     * @return
     */
    private JcrElFinderResponseDto createFile(JcrElFinderRequestDto jcrElFinderRequestDto) {
        JcrElFinderResponseDto result = new JcrElFinderResponseDto();

        InputStream inputStream =  new ByteArrayInputStream(new byte[]{});
        ResponseJcrFile responseJcrFile = jcrFilesService.saveFile(jcrElFinderRequestDto.getTarget(), jcrElFinderRequestDto.getName(), 0l, inputStream, null, null, jcrElFinderRequestDto.getCurrentUser());
        ElFinderFile elFinderFile = new ElFinderFile();
        elFinderFile.setName(responseJcrFile.getFileName());
        elFinderFile.setHash(responseJcrFile.getNodeIdentifire());
        elFinderFile.setPhash(jcrElFinderRequestDto.getTarget());
        elFinderFile.setMime(responseJcrFile.getMimeType());
        elFinderFile.setTs(responseJcrFile.getModifiedTimeStamp());
        elFinderFile.setSize(responseJcrFile.getFileSize() == null ? 0l : responseJcrFile.getFileSize());
        elFinderFile.setRead(1);
        elFinderFile.setWrite(1);
        result.getAdded().add(elFinderFile);
        return result;
    }

    /**
     * Получить иерархию каталогов
     * @param jcrElFinderRequestDto
     * @return
     */
    private JcrElFinderResponseDto treeFolder(JcrElFinderRequestDto jcrElFinderRequestDto) {
        JcrElFinderResponseDto result = new JcrElFinderResponseDto();
        ElFinderFile currentFile = getFileById(jcrElFinderRequestDto.getTarget(), jcrElFinderRequestDto.getCurrentUser(), null, true);
        result.getTree().add(currentFile);
        List<ResponseJcrFile> responseJcrFiles = jcrFilesService.getFileList(jcrElFinderRequestDto.getTarget(), jcrElFinderRequestDto.getCurrentUser());
        for (ResponseJcrFile responseJcrFile : responseJcrFiles) {
            result.getTree().add(getFile(responseJcrFile, jcrElFinderRequestDto.getCurrentUser(), jcrElFinderRequestDto.getTarget(), true));
        }
        return result;
    }

    private JcrElFinderResponseDto renameFile(JcrElFinderRequestDto jcrElFinderRequestDto) {
        JcrElFinderResponseDto result = new JcrElFinderResponseDto();
        try {
            ResponseJcrFile responseJcrFile = jcrFilesService.renameNode(jcrElFinderRequestDto.getTarget(), jcrElFinderRequestDto.getName(), jcrElFinderRequestDto.getCurrentUser());
            result.getAdded().add(getFile(responseJcrFile, jcrElFinderRequestDto.getCurrentUser(), responseJcrFile.getParentNodeIdentifire(), true));
            result.getRemoved().add(responseJcrFile.getNodeIdentifire());
        } catch (Exception e) {
            ExceptionUtils.check(true, e.getMessage());
        }
        return result;
    }

    /**
     * Загрузка файлов
     * @param jcrElFinderRequestDto
     * @return
     */
    private JcrElFinderResponseDto uploadFiles(JcrElFinderRequestDto jcrElFinderRequestDto) {
        JcrElFinderResponseDto result = new JcrElFinderResponseDto();
        try {
            ExceptionUtils.check(jcrElFinderRequestDto.getFileItems() == null || jcrElFinderRequestDto.getFileItems().size() == 0, "Не переданы файлы для загрузки");
            for (FileItem fileItem : jcrElFinderRequestDto.getFileItems()) {
                ResponseJcrFile responseJcrFile = jcrFilesService.saveFile(jcrElFinderRequestDto.getCurrent(), fileItem.getName(), fileItem.getSize(), fileItem.getInputStream(), null, null, jcrElFinderRequestDto.getCurrentUser());
                ElFinderFile elFinderFile = getFile(responseJcrFile, jcrElFinderRequestDto.getCurrentUser(), responseJcrFile.getParentNodeIdentifire(), false);
                result.getAdded().add(elFinderFile);
            }
        } catch (Exception e) {
            ExceptionUtils.check(true, e.getMessage());
        }
        return result;
    }

    /**
     * Получить список файлов каталога
     * @param jcrElFinderRequestDto
     * @return
     */
    private JcrElFinderResponseDto listFolder(JcrElFinderRequestDto jcrElFinderRequestDto) {
        JcrElFinderResponseDto result = new JcrElFinderResponseDto();
        List<ResponseJcrFile> responseJcrFiles = jcrFilesService.getFileList(jcrElFinderRequestDto.getTarget(), jcrElFinderRequestDto.getCurrentUser());
        for (ResponseJcrFile responseJcrFile : responseJcrFiles) {
            result.getList().add(responseJcrFile.getFileName());
        }
        return result;
    }

    /**
     * Вырезать и вставить файлы
     * @param jcrElFinderRequestDto
     * @return
     */
    private JcrElFinderResponseDto cutAndPasteFiles(JcrElFinderRequestDto jcrElFinderRequestDto) {
        JcrElFinderResponseDto result = new JcrElFinderResponseDto();
        List<ResponseJcrFile> responseJcrFiles;
        if (jcrElFinderRequestDto.isCut()) {
            responseJcrFiles = jcrFilesService.cutAndPast(jcrElFinderRequestDto.getTargets(), jcrElFinderRequestDto.getDestination());
            result.getRemoved().addAll(jcrElFinderRequestDto.getTargets());
        } else {
            responseJcrFiles = jcrFilesService.copyAndPast(jcrElFinderRequestDto.getTargets(), jcrElFinderRequestDto.getDestination());
        }
        for (ResponseJcrFile responseJcrFile : responseJcrFiles) {
            result.getAdded().add(getFile(responseJcrFile, jcrElFinderRequestDto.getCurrentUser(), jcrElFinderRequestDto.getDestination(), false));
        }
        return result;
    }

    /**
     * Удалить файлы
     * @param jcrElFinderRequestDto
     * @return
     */
    private JcrElFinderResponseDto removeFiles(JcrElFinderRequestDto jcrElFinderRequestDto) {
        JcrElFinderResponseDto result = new JcrElFinderResponseDto();
        List<ResponseJcrFile> responseJcrFiles = jcrFilesService.remove(jcrElFinderRequestDto.getTargets());
        for (ResponseJcrFile responseJcrFile : responseJcrFiles) {
            result.getRemoved().add(responseJcrFile.getNodeIdentifire());
        }
        return result;
    }

    /**
     * Изменить контент файла
     * @param jcrElFinderRequestDto
     * @return
     */
    private JcrElFinderResponseDto putContentToFile(JcrElFinderRequestDto jcrElFinderRequestDto) {
        JcrElFinderResponseDto result = new JcrElFinderResponseDto();
        String content = StringUtils.isBlank(jcrElFinderRequestDto.getContent()) ? "" : jcrElFinderRequestDto.getContent();
        InputStream inputStream = IOUtils.toInputStream(jcrElFinderRequestDto.getContent());
        Long fileSize = (long)content.length();
        ResponseJcrFile responseJcrFile = jcrFilesService.changeFile(jcrElFinderRequestDto.getTarget(), inputStream, fileSize);
        result.getChanged().add(getFile(responseJcrFile, jcrElFinderRequestDto.getCurrentUser(), jcrElFinderRequestDto.getDestination(), false));
        return result;
    }

    /**
     * Получить контент тектового файла
     * @param jcrElFinderRequestDto
     * @return
     */
    private JcrElFinderResponseDto getContentTextFile(JcrElFinderRequestDto jcrElFinderRequestDto) {
        JcrElFinderResponseDto jcrElFinderResponseDto = new JcrElFinderResponseDto();
        try {
            JcrFilesService.FileContentWrapper fileContentWrapper = jcrFilesService.getFileContentByNodeId(jcrElFinderRequestDto.getTarget(), jcrElFinderRequestDto.getCurrentUser());
            String content = IOUtils.toString(fileContentWrapper.getInputStream());
            jcrElFinderResponseDto.setContent(content);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return jcrElFinderResponseDto;
    }

    /**
     * Получить контент файла
     * @param jcrElFinderRequestDto
     * @return
     */
    private JcrElFinderResponseDto getContentFile(JcrElFinderRequestDto jcrElFinderRequestDto) {
        JcrElFinderResponseDto jcrElFinderResponseDto = new JcrElFinderResponseDto();
        try {
            JcrFilesService.FileContentWrapper fileContentWrapper = jcrFilesService.getFileContentByNodeId(jcrElFinderRequestDto.getTarget(), jcrElFinderRequestDto.getCurrentUser());
            jcrElFinderResponseDto.setFileContentWrapper(fileContentWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return jcrElFinderResponseDto;
    }

    /**
     * Архивация файлов
     * @param jcrElFinderRequestDto
     * @return
     */
    private JcrElFinderResponseDto archiveFiles(JcrElFinderRequestDto jcrElFinderRequestDto) {
        JcrElFinderResponseDto result = new JcrElFinderResponseDto();
        ExceptionUtils.check(jcrElFinderRequestDto.getTargets() == null || jcrElFinderRequestDto.getTargets().size() == 0, "Не переданы файлы для архивации");
        ExceptionUtils.check(jcrElFinderRequestDto.getType() == null, "Не передан тип архива");
        ElFinderArchiveType foundElFinderArchiveType = null;
        for (ElFinderArchiveType elFinderArchiveType : AVAILABLE_ARCHIVE_TYPES) {
            if (elFinderArchiveType.getType().equals(jcrElFinderRequestDto.getType())) {
                foundElFinderArchiveType = elFinderArchiveType;
                break;
            }
        }
        ExceptionUtils.check(foundElFinderArchiveType == null, "Тип архивации: " + jcrElFinderRequestDto.getType() + " не поддерживается");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writeZipToStream(jcrElFinderRequestDto.getTargets(), jcrElFinderRequestDto.getCurrentUser(), byteArrayOutputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ResponseJcrFile responseJcrFile = jcrFilesService.saveFile(jcrElFinderRequestDto.getTarget(), jcrElFinderRequestDto.getName(), byteArrayOutputStream.size(), inputStream, null, null, jcrElFinderRequestDto.getCurrentUser());

        ElFinderFile elFinderFile = getFile(responseJcrFile, jcrElFinderRequestDto.getCurrentUser(), responseJcrFile.getParentNodeIdentifire(), false);
        result.getAdded().add(elFinderFile);
        return result;
    }

    /**
     * Записать файлы в zip архив
     * @param nodeIds
     * @param user
     * @param outputStream
     */
    private void writeZipToStream(List<String> nodeIds, User user, OutputStream outputStream) {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(outputStream, Charset.forName("CP866"));
            insertFilesIntoZip("", nodeIds, user, zos);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                zos.closeEntry();
                zos.close();
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    private void insertFilesIntoZip(String path, List<String> nodeIds, User user, ZipOutputStream zos) throws Exception {
        for (String nodeId : nodeIds) {
            ResponseJcrFile responseJcrFile = jcrFilesService.getFileById(nodeId);
            ZipEntry ze;
            if (responseJcrFile.isFolder()) {
                ze = new ZipEntry(path + responseJcrFile.getFileName() + "/");
            } else {
                JcrFilesService.FileContentWrapper fileContentWrapper = jcrFilesService.getFileContentByNodeId(nodeId, user);
                ze = new ZipEntry(path + fileContentWrapper.getFileName());
            }
            zos.putNextEntry(ze);
            if (responseJcrFile.isFolder()) {
                List<ResponseJcrFile> fileList = jcrFilesService.getFileList(nodeId, user);
                if (fileList != null && fileList.size() > 0) {
                    List<String> child = new ArrayList<>();
                    for (ResponseJcrFile childFile : fileList) {
                        child.add(childFile.getNodeIdentifire());
                    }
                    insertFilesIntoZip(path + responseJcrFile.getFileName() + "/", child, user, zos);
                }
            } else {
                JcrFilesService.FileContentWrapper fileContentWrapper = jcrFilesService.getFileContentByNodeId(nodeId, user);
                IOUtils.copyLarge(fileContentWrapper.getInputStream(), zos);
            }
        }
    }

    /**
     * Клонирование файла в текущий каталог
     * @param jcrElFinderRequestDto
     * @return
     */
    private JcrElFinderResponseDto duplicateFiles(JcrElFinderRequestDto jcrElFinderRequestDto) {
        JcrElFinderResponseDto result = new JcrElFinderResponseDto();
        ExceptionUtils.check(jcrElFinderRequestDto.getTargets() == null || jcrElFinderRequestDto.getTargets().size() == 0, "Не выбраны файлы для клонирования");
        ResponseJcrFile responseJcrFile = jcrFilesService.getFileById(jcrElFinderRequestDto.getTargets().get(0));

        List<ResponseJcrFile> responseJcrFiles = jcrFilesService.copyAndPast(jcrElFinderRequestDto.getTargets(), responseJcrFile.getParentNodeIdentifire());
        for (ResponseJcrFile jcrFile : responseJcrFiles) {
            result.getAdded().add(getFile(jcrFile, jcrElFinderRequestDto.getCurrentUser(), responseJcrFile.getParentNodeIdentifire(), false));
        }
        return result;
    }
}
