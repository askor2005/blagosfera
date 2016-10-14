package ru.radom.kabinet.services.jcr;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.jcr.JcrTemplate;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.data.jpa.repositories.DialogsRepository;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.communities.CommunityMemberDao;
import ru.radom.kabinet.model.Discriminators;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.chat.DialogEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.StringUtils;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.web.jcr.dto.NodeEntityDto;
import ru.radom.kabinet.web.jcr.dto.ResponseJcrFile;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;
import javax.jcr.*;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Сервис для работы с JCR Rabbit
 * Created by vgusev on 26.08.2015.
 */
@Service
public class JcrFilesService {

    // Класс - обёртка для данных файла из ноды
    public static class FileContentWrapper {

        private InputStream inputStream;

        private String fileName;

        private String mimeType;

        private int fileSize;

        public FileContentWrapper(InputStream inputStream, String fileName, String mimeType, int fileSize) {
            this.inputStream = inputStream;
            this.fileName = fileName;
            this.mimeType = mimeType;
            this.fileSize = fileSize;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public String getFileName() {
            return fileName;
        }

        public String getMimeType() {
            return mimeType;
        }

        public int getFileSize() {
            return fileSize;
        }
    }

    // Тип поля - поле участника
    private static final String SHARER_FIELD_TYPE_NAME = "SHARER";

    // Тип поля - поле объединения
    private static final String COMMUNITY_FIELD_TYPE_NAME = "COMMUNITY";


    // Корневой каталог с пользователями
    private static final String USERS_ROOT_NODE_NAME = "users";

    // Корневой каталог с группами
    private static final String GROUPS_ROOT_NODE_NAME = "groups";

    // Корневой каталог с файлами диалогов
    private static final String DIALOG_ROOT_NODE_NAME = "dialog";

    // Наименование аттрибута ноды - настоящее имя файла
    private static final String FILE_NAME_PROPERTY_ATTR_NAME = "nodeFileName";

    // Наименование аттрибута ноды - тип файла
    private static final String FILE_MIME_TYPE_PROPERTY_ATTR_NAME = "nodeFileMimeType";

    // Наименование аттрибута ноды - тип файла
    private static final String FILE_SIZE_PROPERTY_ATTR_NAME = "nodeFileSize";

    // Наименование объекта в рамках которого сохраняется файл
    private static final String OBJECT_CONTEXT_NAME_ATTR_NAME = "objectContextName";

    // ИД объекта в рамках которого сохраняется файл
    private static final String OBJECT_CONTEXT_ID_ATTR_NAME = "objectContextId";

    // Префикс ноды с файлами поля
    private static final String FIELD_NODE_PREFIX = "field_";

    private static final String LAST_MODIFIED_TIME_STAMP = "lastModifiedTimeStamp";

    // Мапа с типами файлов
    @Autowired
    private MimetypesFileTypeMap mimeTypesMap;

    @Resource(name = "jcrTemplate")
    private JcrTemplate jcrTemplate;

    @Autowired
    private DialogsRepository dialogsRepository;

    @Autowired
    private CommunityMemberDao communityMemberDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private SharerDao sharerDao;

    // Пока конкретные задачи таковы:
    // - Получить список файлов по полю в группе +
    // - Получить список файлов по полю в карточке участника +
    // - Просмотреть файл из списка фалов по полю (в группе и в карточке участника)
    // - Загрузить файл в список фалов по полю (в группе и в карточке участника)

    /**
     * Получить значение атрибута ноды
     * @param node
     * @param propertyName
     * @param clazz
     * @param <T>
     * @return
     * @throws RepositoryException
     */
    private <T> T getSafeNodeProperty(Node node, String propertyName, Class<T> clazz) throws RepositoryException {
        Object result = null;
        Property property = null;
        if (node.hasProperty(propertyName)) {
            property = node.getProperty(propertyName);
        }
        if (property != null) {
            if (clazz.equals(Long.class)) {
                result = property.getLong();
            } else if (clazz.equals(String.class)) {
                result = property.getString();
            }
        }
        return (T)result;
    }

    /**
     * Установить дату модификации файла
     * @param node
     * @throws RepositoryException
     */
    private void setModifiedNodeDate(Node node) throws RepositoryException {
        node.setProperty(LAST_MODIFIED_TIME_STAMP, new Date().getTime());
    }

    /**
     * Получитьт дату модификации файла
     * @param node
     * @return
     * @throws RepositoryException
     */
    private Long getModifiedNodeDate(Node node) throws RepositoryException {
        return getSafeNodeProperty(node, LAST_MODIFIED_TIME_STAMP, Long.class);
    }

    /**
     * Получить mime тип файла
     * @param node
     * @return
     * @throws RepositoryException
     */
    private String getNodeMimeType(Node node) throws RepositoryException {
        String fileName = getSafeNodeProperty(node, FILE_NAME_PROPERTY_ATTR_NAME, String.class);
        String result = getSafeNodeProperty(node, FILE_MIME_TYPE_PROPERTY_ATTR_NAME, String.class);
        if (result == null && fileName == null) {
            result = "directory";
        }
        return result;
    }

    /**
     * Размер файла
     * @param node
     * @return
     * @throws RepositoryException
     */
    private Long getNodeFileSize(Node node) throws RepositoryException {
        return getSafeNodeProperty(node, FILE_SIZE_PROPERTY_ATTR_NAME, Long.class);
    }

    /**
     * Название файла
     * @param node
     * @return
     * @throws RepositoryException
     */
    private String getNodeFileName(Node node) throws RepositoryException {
        String fileName = getSafeNodeProperty(node, FILE_NAME_PROPERTY_ATTR_NAME, String.class);
        if (fileName == null) {
            fileName = node.getName();
        }
        return fileName;
    }

    /**
     * Файл- каталог
     * @param node
     * @return
     * @throws RepositoryException
     */
    private boolean isNodeFolder(Node node) throws RepositoryException {
        String fileName = getSafeNodeProperty(node, FILE_NAME_PROPERTY_ATTR_NAME, String.class);
        return fileName == null;
    }

    /**
     * Проверка доступа к файлу - член объединения
     * @param rootEntityNode
     * @param userId
     * @return
     */
    private CommunityEntity checkPermissionToCommunityNode(Node rootEntityNode, Long userId) {
        CommunityEntity community;
        try {
            Long communityId = VarUtils.getLong(rootEntityNode.getName(), -1l);
            ExceptionUtils.check(communityId == -1l, "Объединение не найдено");
            community = communityDao.loadById(communityId);
            CommunityMemberEntity member = communityMemberDao.get(community, userId);
            if (member == null) {
                ExceptionUtils.check(true, "Вы не являетесь участником объединения");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return community;
    }

    /**
     * Проверка доступа к файлу пользователя
     * @param rootEntityNode
     * @param userIkp
     * @return
     */
    private UserEntity checkPermissionToSharerNode(Node rootEntityNode, String userIkp) {
        try {
            String nodeIkp = rootEntityNode.getName();
            ExceptionUtils.check(!userIkp.equals(nodeIkp), "У вас нет доступа к файлу");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return sharerDao.getByIkp(userIkp);
    }

    /**
     * Проверка доступа к файлу диалога
     * @param rootEntityNode
     * @param userId
     * @return
     */
    private DialogEntity checkPermissionToDialogNode(Node rootEntityNode, Long userId) {
        DialogEntity dialog = null;
        try {
            Long dialogId = VarUtils.getLong(rootEntityNode.getName(), -1l);
            ExceptionUtils.check(dialogId == -1l, "Диалог не найден");
            dialog = dialogsRepository.findOne(dialogId);
            ExceptionUtils.check(dialog == null, "Диалог не найден");

            boolean found = false;

            for (UserEntity user : dialog.getUsers()) {
                if (user.getId().equals(userId)) {
                    found = true;
                    break;
                }
            }

            ExceptionUtils.check(!found, "У вас нет доступа к диалогу");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return dialog;
    }

    /**
     * Получить доступ к ноде с проверкой прав доступа
     * @param session
     * @param nodeId
     * @param user
     * @return
     * @throws RepositoryException
     */
    private NodeEntityDto getNodeById(Session session, String nodeId, User user) throws RepositoryException {
        Node node = session.getNodeByIdentifier(nodeId);
        Node findParentNode = node;
        Node rootNode = session.getRootNode();

        List<Node> list = new ArrayList<>();
        list.add(node);

        // Ищем ноду к чему относится ветка - к объединению или пользователю или к диалогу
        while(!findParentNode.getIdentifier().equals(rootNode.getIdentifier())) {
            findParentNode = findParentNode.getParent();
            list.add(findParentNode);
        }
        if (list.size() < 3) {
            throw new RuntimeException("Нет доступа к каталогам выше корневых каталогов сущностей");
        }

        Node rootEntitiesNode = list.get(list.size() - 2);
        Node rootEntityNode = list.get(list.size() - 3);

        LongIdentifiable longIdentifiable = null;
        switch (rootEntitiesNode.getName()) {
            case GROUPS_ROOT_NODE_NAME:
                longIdentifiable = checkPermissionToCommunityNode(rootEntityNode, user.getId());
                break;
            case USERS_ROOT_NODE_NAME:
                longIdentifiable = checkPermissionToSharerNode(rootEntityNode, user.getIkp());
                break;
            case DIALOG_ROOT_NODE_NAME:
                longIdentifiable = checkPermissionToDialogNode(rootEntityNode, user.getId());
                break;
        }
        ExceptionUtils.check(longIdentifiable == null, "Не определён корневой каталог файла");
        return new NodeEntityDto(node, longIdentifiable);
    }

    /**
     * Получить дочерние файлы ноды по ИД ноды
     * @param fileParentNodeId ИД ноды
     * @return список файлов
     */
    public List<ResponseJcrFile> getFileList(final String fileParentNodeId, User currentUser) {
        Object result = jcrTemplate.execute(session -> {
            Node parentNode = getNodeById(session, fileParentNodeId, currentUser).getNode();

            List<ResponseJcrFile> resultList = new ArrayList<>();
            NodeIterator nodeIterator = parentNode.getNodes();
            while (nodeIterator.hasNext()) {
                Node node = nodeIterator.nextNode();
                ResponseJcrFile responseJcrFile = new ResponseJcrFile(
                        getNodeFileName(node),
                        node.getIdentifier(),
                        getModifiedNodeDate(node),
                        getNodeMimeType(node),
                        getNodeFileSize(node),
                        node.getParent().getIdentifier(),
                        isNodeFolder(node)
                );
                resultList.add(responseJcrFile);
            }
            return resultList;
        });
        return (List<ResponseJcrFile>)result;
    }

    /**
     * Получить контент файла по ИД ноды с описанием файла
     *
     * @param fileDefinitionNodeId
     * @param currentUser
     * @return
     */
    public FileContentWrapper getFileContentByNodeId(final String fileDefinitionNodeId, final User currentUser) throws ItemNotFoundException {
        Object resultFunc = jcrTemplate.execute(session -> {
            Object result = null;
            try {
                try {
                    Node fileDefinitionNode = getNodeById(session, fileDefinitionNodeId, currentUser).getNode();
                    String fileName = getNodeFileName(fileDefinitionNode);
                    String fileMimeType = getNodeMimeType(fileDefinitionNode);
                    Long fileSize = getNodeFileSize(fileDefinitionNode);
                    fileSize = fileSize == null ? 0l : fileSize;

                    // Если сохранён контекст файла
                    ExceptionUtils.check(!fileDefinitionNode.hasProperty(OBJECT_CONTEXT_NAME_ATTR_NAME), "Объект не является файлом");

                    Node fileNode = getFileNode(fileDefinitionNode);
                    InputStream is = JcrUtils.readFile(fileNode);
                    result = new FileContentWrapper(is, fileName, fileMimeType, fileSize.intValue());
                } catch (RepositoryException e) {
                    if (e.getMessage().contains("invalid identifier")) {
                        throw new ItemNotFoundException(fileDefinitionNodeId + " not found");
                    }
                }
            } catch (ItemNotFoundException e) {
                result = e;
            }
            return result;
        });
        if (resultFunc instanceof ItemNotFoundException) {
            throw (ItemNotFoundException) resultFunc;
        }
        return (FileContentWrapper) resultFunc;
    }

    /**
     * Удаление файла из поля участника
     *
     * @param nodeId
     */
    public void removeFieldFileById(final UserEntity userEntity, final FieldEntity field, final String nodeId) {
        // Проверяем, что поле является полем участника системы
        if (!field.getFieldsGroup().getObjectType().equalsIgnoreCase(SHARER_FIELD_TYPE_NAME)) {
            throw new RuntimeException("Данное поле не является полем участника");
        }
        jcrTemplate.execute(session -> {
            Object result = null;
            try {
                try {
                    // Ищем ноду в списке файлов поля
                    Node homeNode = getSharerHomeNode(session, userEntity);
                    Map<String, String> mapFields = getFieldNodes(homeNode, field);
                    // Если нода не найдена в списке нод поля
                    if (!mapFields.containsKey(nodeId)) {
                        throw new ItemNotFoundException(nodeId + " not found");
                    }
                    Node node = session.getNodeByIdentifier(nodeId);
                    node.remove();
                } catch (RepositoryException e) {
                    if (e.getMessage().contains("invalid identifier")) {
                        throw new ItemNotFoundException(nodeId + " not found");
                    }
                }
                session.save();
            } catch (ItemNotFoundException e) {
                result = e;
            }
            return result;
        });
    }

    /**
     * Получить мапу с файлами поля группы
     *
     * @param community
     * @param field
     * @return
     */
    /*public Map<String, String> getFilesByField(final CommunityEntity community, final FieldEntity field) {
        Object jcrResult = jcrTemplate.execute(session -> {
            Map<String, String> result = null;
            try {
                Node homeNode = getGroupHomeNode(session, community);
                result = getFieldNodes(homeNode, field);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage(), e);
            }
            return result;
        });
        return (Map<String, String>) jcrResult;
    }*/

    /**
     * Получить мапу с файлами поля пользователя
     *
     * @param sharer
     * @param field
     * @return
     */
    /*public Map<String, String> getFilesByField(final Sharer sharer, final FieldEntity field) {
        Object jcrResult = jcrTemplate.execute(session -> {
            Map<String, String> result = null;
            try {
                Node homeNode = getSharerHomeNode(session, sharer);
                result = getFieldNodes(homeNode, field);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage(), e);
            }
            return result;
        });
        return (Map<String, String>) jcrResult;
    }*/

    /**
     * Сохранить файл поля у пользователя
     *
     * @param userEntity
     * @param field
     * @param fileItem
     * @return
     */
    public Map<String, String> saveFieldFile(
            final UserEntity userEntity, final FieldEntity field, final FileItem fileItem, User currentUser) {
        try {
            String fileName = fileItem.getName();
            long fileSize = fileItem.getSize();
            InputStream inputStream = fileItem.getInputStream();
            return saveFieldFile(userEntity, field, fileName, fileSize, inputStream, currentUser);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Map<String, String> saveFieldFile(
            final UserEntity userEntity, final FieldEntity field,
            final String fileName, final long fileSize,
            final InputStream inputStream, User currentUser) {
        if (!field.getAttachedFile()) {
            throw new RuntimeException("К полю нельзя прикреплять файлы");
        }
        // Проверяем, что поле является полем участника системы
        if (!field.getFieldsGroup().getObjectType().equalsIgnoreCase(SHARER_FIELD_TYPE_NAME)) {
            throw new RuntimeException("Данное поле не является полем участника");
        }
        Object jcrResult = jcrTemplate.execute(session -> {
            Map<String, String> result = new HashMap<>();
            try {
                Node homeNode = getHomeNode(session, userEntity, currentUser);

                // Получаем каталог с файлами поля
                Node fieldNode = getFieldNode(homeNode, field);
                // Сохраняем файл
                Node fileNode = saveFile(fieldNode, fileName, fileSize, inputStream, null, null, null);

                result.put(fileNode.getIdentifier(), fileName);

                session.save();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage(), e);
            }
            return result;
        });
        return (Map<String, String>) jcrResult;
    }

    /**
     * Сохранить файл поля у группы
     *
     * @param community
     * @param field
     * @param fileItem
     * @return
     */
    public Map<String, String> saveFieldFile(
            final CommunityEntity community, final FieldEntity field, final FileItem fileItem, User currentUser) {
        try {
            String fileName = fileItem.getName();
            long fileSize = fileItem.getSize();
            InputStream inputStream = fileItem.getInputStream();
            return saveFieldFile(community, field, fileName, fileSize, inputStream, currentUser);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Map<String, String> saveFieldFile(
            final CommunityEntity community, final FieldEntity field,
            final String fileName, final long fileSize,
            final InputStream inputStream, User currentUser) {
        if (!field.getAttachedFile()) {
            throw new RuntimeException("К полю нельзя прикреплять файлы");
        }
        // Проверяем, что поле является полем объединения
        if (!field.getFieldsGroup().getObjectType().equalsIgnoreCase(COMMUNITY_FIELD_TYPE_NAME)) {
            throw new RuntimeException("Данное поле не является полем объединения");
        }
        Object jcrResult = jcrTemplate.execute(session -> {
            Map<String, String> result = new HashMap<>();
            try {
                Node homeNode = getHomeNode(session, community, currentUser);

                // Получаем каталог с файлами поля
                Node fieldNode = getFieldNode(homeNode, field);
                // Сохраняем файл
                Node fileNode = saveFile(fieldNode, fileName, fileSize, inputStream, null, null, null);

                result.put(fileNode.getIdentifier(), fileName);

                session.save();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage(), e);
            }
            return result;
        });
        return (Map<String, String>) jcrResult;
    }

    /**
     * Сохранить файл пользователя
     *
     * @param fileItem
     * @param sharer
     * @return
     */
    /*public Node saveFile(final FileItem fileItem, final Sharer sharer) {
        try {
            String fileName = fileItem.getName();
            long fileSize = fileItem.getSize();
            InputStream inputStream = fileItem.getInputStream();
            return saveFile(fileName, fileSize, inputStream, sharer);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Node saveFile(final String fileName, final long fileSize, final InputStream inputStream, final Sharer sharer) {
        return (Node) jcrTemplate.execute(new JcrCallback() {
            @Override
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node fileNode = null;
                try {
                    // Проверяем есть ли корневой каталог пользователя
                    Node homeNode = getSharerHomeNode(session, sharer);

                    // Сохранить файл
                    fileNode = saveFile(homeNode, fileName, fileSize, inputStream, sharer, null, null);

                    session.save();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage(), e);
                } finally {
                    if (inputStream != null) {
                        IOUtils.closeQuietly(inputStream);
                    }
                }
                return fileNode;
            }
        });
    }*/

    /**
     * Сохранить файл чата
     * @param fileItem
     * @param dialog
     * @param extensions - разрешённые форматы файлов
     * @return
     */
    /*public ResponseJcrFile saveFile(final FileItem fileItem, final DialogEntity dialog, final String[] extensions, Long maxFileSize) {
        try {
            String fileName = fileItem.getName();
            long fileSize = fileItem.getSize();
            InputStream inputStream = fileItem.getInputStream();
            return saveFile(fileName, fileSize, inputStream, dialog, extensions, maxFileSize);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public ResponseJcrFile saveFile(final String fileName, final long fileSize, final InputStream inputStream, final DialogEntity dialog, final String[] extensions, Long maxFileSize) {
        return (ResponseJcrFile)jcrTemplate.execute(new JcrCallback() {
            @Override
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                ResponseJcrFile result;
                try {
                    // Проверяем есть ли корневой каталог чата
                    Node homeNode = getDialogHomeNode(session, dialog);

                    // Сохранить файл
                    Node fileDefinitionNode = saveFile(homeNode, fileName, fileSize, inputStream, dialog, extensions, maxFileSize);
                    result = new ResponseJcrFile(fileDefinitionNode.getProperty(FILE_NAME_PROPERTY_ATTR_NAME).getString(), fileDefinitionNode.getIdentifier());
                    session.save();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage(), e);
                } finally {
                    if (inputStream != null) {
                        IOUtils.closeQuietly(inputStream);
                    }
                }
                return result;
            }
        });
    }*/

    /**
     * Сохранить файл группы
     * @param fileItem
     * @param community
     * @return
     */
    /*public ResponseJcrFile saveFile(final FileItem fileItem, final CommunityEntity community, Sharer currentSharer) {
        try {
            String fileName = fileItem.getName();
            long fileSize = fileItem.getSize();
            InputStream inputStream = fileItem.getInputStream();
            return saveFile(fileName, fileSize, inputStream, community, currentSharer);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public ResponseJcrFile saveFile(final String fileName, final long fileSize, final InputStream inputStream, final CommunityEntity community, Sharer currentSharer) {
        return (ResponseJcrFile)jcrTemplate.execute(session -> {
            ResponseJcrFile result = null;
            try {
                // Проверяем есть ли корневой каталог группы
                Node homeNode = getGroupHomeNode(session, community);
                checkPermissionToCommunityNode(homeNode, currentSharer);

                // Сохранить файл
                Node fileNode = saveFile(homeNode, fileName, fileSize, inputStream, community, null, null);
                result = new ResponseJcrFile(fileNode.getProperty(FILE_NAME_PROPERTY_ATTR_NAME).getString(), fileNode.getIdentifier());

                session.save();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            return result;
        });
    }*/

    /**
     * Корневой каталог
     * @param longIdentifiable объединение
     * @return
     */
    public ResponseJcrFile getRootFile(LongIdentifiable longIdentifiable, User currentUser) {
        return (ResponseJcrFile)jcrTemplate.execute(session -> {
            Node homeNode = getHomeNode(session, longIdentifiable, currentUser);
            session.save();
            return new ResponseJcrFile(
                    getNodeFileName(homeNode),
                    homeNode.getIdentifier(),
                    getModifiedNodeDate(homeNode),
                    getNodeMimeType(homeNode),
                    getNodeFileSize(homeNode),
                    homeNode.getParent().getIdentifier(),
                    isNodeFolder(homeNode)
            );
        });
    }

    public ResponseJcrFile saveFile(final String fileName, final long fileSize, final InputStream inputStream,
                                    final Community community, final String[] extensions, Long maxFileSize,
                                    User currentUser) {
        CommunityEntity communityEntity = communityDao.getById(community.getId());
        return saveFile(fileName, fileSize, inputStream, communityEntity, extensions, maxFileSize, currentUser);
    }

    /**
     *
     * @param fileItem
     * @param longIdentifiable
     * @param extensions
     * @param maxFileSize
     * @param currentUser
     * @return
     */
    public ResponseJcrFile saveFile(
            final FileItem fileItem, final LongIdentifiable longIdentifiable,
            final String[] extensions, Long maxFileSize, User currentUser) {
        try {
            String fileName = fileItem.getName();
            long fileSize = fileItem.getSize();
            InputStream inputStream = fileItem.getInputStream();
            return saveFile(fileName, fileSize, inputStream, longIdentifiable, extensions, maxFileSize, currentUser);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public ResponseJcrFile saveFile(final String fileName, final long fileSize, final InputStream inputStream,
                                    final LongIdentifiable longIdentifiable, final String[] extensions, Long maxFileSize,
                                    User currentUser) {
        return (ResponseJcrFile)jcrTemplate.execute(session -> {
            ResponseJcrFile result;
            try {
                // Получить корневой каталог
                Node homeNode = getHomeNode(session, longIdentifiable, currentUser);

                // Сохранить файл
                Node fileDefinitionNode = saveFile(homeNode, fileName, fileSize, inputStream, longIdentifiable, extensions, maxFileSize);
                setModifiedNodeDate(fileDefinitionNode);
                result = new ResponseJcrFile(
                        getNodeFileName(fileDefinitionNode),
                        fileDefinitionNode.getIdentifier(),
                        getModifiedNodeDate(fileDefinitionNode),
                        getNodeMimeType(fileDefinitionNode),
                        getNodeFileSize(fileDefinitionNode),
                        fileDefinitionNode.getParent().getIdentifier(),
                        isNodeFolder(fileDefinitionNode)
                );
                session.save();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            return result;
        });
    }

    /**
     * Сохранить файл.
     * @param parentNodeId
     * @param fileName
     * @param fileSize
     * @param inputStream
     * @param extensions
     * @param maxFileSize
     * @param currentUser
     * @return
     */
    public ResponseJcrFile saveFile(final String parentNodeId, final String fileName,
                                     final long fileSize, final InputStream inputStream,
                                     final String[] extensions, Long maxFileSize,
                                     User currentUser) {
        return saveFile(parentNodeId, fileName, fileSize, inputStream, extensions, maxFileSize, currentUser, false);
    }

    /**
     * Сохранить каталог
     * @param parentNodeId
     * @param folderName
     * @param currentUser
     * @return
     */
    public ResponseJcrFile saveFolder(final String parentNodeId, final String folderName, User currentUser) {
        return saveFile(parentNodeId, folderName, -1l, null, null, null, currentUser, true);
    }

    /**
     * Обновить имя ноды
     * @param nodeId
     * @param newName
     * @param currentUser
     * @throws RepositoryException
     */
    public ResponseJcrFile renameNode(String nodeId, String newName, User currentUser) throws RepositoryException {
        return (ResponseJcrFile)jcrTemplate.execute(session -> {
            ResponseJcrFile result;
            try {
                NodeEntityDto nodeEntityDto = getNodeById(session, nodeId, currentUser);
                Node node = nodeEntityDto.getNode();
                if (node.hasProperty(FILE_NAME_PROPERTY_ATTR_NAME)) {
                    node.setProperty(FILE_NAME_PROPERTY_ATTR_NAME, newName);
                } else {
                    node.getSession().move(node.getPath(), node.getParent().getPath() + "/" + newName);
                }
                setModifiedNodeDate(node);
                result = new ResponseJcrFile(
                        getNodeFileName(node),
                        node.getIdentifier(),
                        getModifiedNodeDate(node),
                        getNodeMimeType(node),
                        getNodeFileSize(node),
                        node.getParent().getIdentifier(),
                        isNodeFolder(node)
                );
                session.save();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return result;
        });
    }

    /**
     * Сохранить файл или каталог
     * @param parentNodeId
     * @param fileName
     * @param fileSize
     * @param inputStream
     * @param extensions
     * @param maxFileSize
     * @param currentUser
     * @param isFolder
     * @return
     */
    private ResponseJcrFile saveFile(
            final String parentNodeId, final String fileName,
            final long fileSize, final InputStream inputStream,
            final String[] extensions, Long maxFileSize,
            User currentUser, boolean isFolder) {
        return (ResponseJcrFile)jcrTemplate.execute(session -> {
            ResponseJcrFile result;
            try {
                NodeEntityDto nodeEntityDto = getNodeById(session, parentNodeId, currentUser);
                Node nodeResult;
                if (isFolder) { // Сохранить каталог
                    nodeResult = JcrUtils.getOrAddNode(nodeEntityDto.getNode(), fileName);
                } else { // Сохранить файл
                    nodeResult = saveFile(nodeEntityDto.getNode(), fileName, fileSize, inputStream, nodeEntityDto.getLongIdentifiable(), extensions, maxFileSize);
                }
                setModifiedNodeDate(nodeResult);
                result = new ResponseJcrFile(
                        getNodeFileName(nodeResult),
                        nodeResult.getIdentifier(),
                        getModifiedNodeDate(nodeResult),
                        getNodeMimeType(nodeResult),
                        getNodeFileSize(nodeResult),
                        nodeResult.getParent().getIdentifier(),
                        isNodeFolder(nodeResult)
                );
                session.save();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            return result;
        });
    }

    /**
     * Получить все файлы поля
     * @param parentNode
     * @param field
     * @return
     * @throws RepositoryException
     */
    private Map<String, String> getFieldNodes(Node parentNode, FieldEntity field) throws RepositoryException {
        // Получаем каталог с файлами поля
        Node fieldNode = getFieldNode(parentNode, field);

        return getFilesListByParentNode(fieldNode);
    }

    /**
     * Получить мапу с файлами по родительскому каталогу.
     * @param parentNode
     * @return
     * @throws RepositoryException
     */
    private Map<String, String> getFilesListByParentNode(Node parentNode) throws RepositoryException {
        Map<String, String> result = new HashMap<>();
        NodeIterator nodeIterator = parentNode.getNodes();
        while (nodeIterator.hasNext()) {
            Node fileDefinitionNode = nodeIterator.nextNode();
            String fileName = fileDefinitionNode.getProperty(FILE_NAME_PROPERTY_ATTR_NAME).getString();
            Node fileNode = getFileNode(fileDefinitionNode);

            result.put(fileNode.getIdentifier(), fileName);
        }
        return result;
    }

    /**
     * Получить ноду с файлом по ноде описания файла
     * @param fileDefinitionNode
     * @return
     * @throws RepositoryException
     */
    private Node getFileNode(Node fileDefinitionNode) throws RepositoryException {
        if (!fileDefinitionNode.getNodes().hasNext()) {
            throw new RuntimeException("Нет ноды с файлом в ноде " + fileDefinitionNode.getName());
        }
        return fileDefinitionNode.getNodes().nextNode();
    }

    /**
     * Получить каталог с файлами поля
     * @param parentNode
     * @param field
     * @return
     * @throws RepositoryException
     */
    private Node getFieldNode(Node parentNode, FieldEntity field) throws RepositoryException {
        // Получаем каталог с файлами поля
        return JcrUtils.getOrAddNode(parentNode, FIELD_NODE_PREFIX + field.getId());
    }

    /**
     * Метод сохранения файла в репозиторий.
     * Файл сохраняется в ноде с описанием, потому как нужно хранить метаданные файла,
     * а метаданные (к чему принадлежит файл - объединение или пользователь и т.п.) нельзя установить у ноды
     * с файлом плюс нода с метаданными всегда будет с уникальным именем, чтобы небыло коллизии
     * @param parentNode
     * @param fileName
     * @param fileSize
     * @param fileInputStream
     * @param objectContext
     * @param extensions
     * @param maxFileSize
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws RepositoryException
     */
    private Node saveFile(Node parentNode, String fileName, long fileSize, InputStream fileInputStream, LongIdentifiable objectContext, String[] extensions, Long maxFileSize) throws IOException, NoSuchAlgorithmException, RepositoryException {
        try {
            String extension = FilenameUtils.getExtension(fileName);
            if (extensions != null && extensions.length > 0) {
                if (!StringUtils.containsCaseInsensitive(extension, extensions)) {
                    throw new RuntimeException("Недопустимый формат файла");
                }
            }
            if (maxFileSize != null && maxFileSize > 0l && fileSize > maxFileSize) {
                throw new RuntimeException("Максимальный размер загружаемого файла должен быть не более " + maxFileSize + " байт");
            }

            String baseFileName = FilenameUtils.getBaseName(fileName);
            String uniqueMessage = baseFileName + new Date().toString();

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(uniqueMessage.getBytes("UTF-8"));
            String uniqueFileName = DatatypeConverter.printHexBinary(thedigest);

            String fileNameNode = "file." + extension;

            String mimeType = mimeTypesMap.getContentType(fileName);

            // Создаём ноду с инфой о файле, потому как в ноду с файлом аттрибуты не создать
            Node fileDefinitionNode = JcrUtils.getOrAddNode(parentNode, uniqueFileName);
            fileDefinitionNode.setProperty(FILE_NAME_PROPERTY_ATTR_NAME, fileName);
            fileDefinitionNode.setProperty(FILE_MIME_TYPE_PROPERTY_ATTR_NAME, mimeType);
            fileDefinitionNode.setProperty(FILE_SIZE_PROPERTY_ATTR_NAME, fileSize);
            if (objectContext != null) {
                String objectContextName = Discriminators.get(objectContext.getClass());
                if (objectContextName != null) {
                    fileDefinitionNode.setProperty(OBJECT_CONTEXT_NAME_ATTR_NAME, objectContextName);
                    fileDefinitionNode.setProperty(OBJECT_CONTEXT_ID_ATTR_NAME, String.valueOf(objectContext.getId()));
                }
            }

            // Файл кладём в ноду с инфой о файле
            JcrUtils.putFile(fileDefinitionNode, fileNameNode, mimeType, fileInputStream);

            return fileDefinitionNode;
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    /**
     * Коревой каталог диалога
     * @param session
     * @param dialog
     * @return
     * @throws RepositoryException
     */
    private Node getDialogHomeNode(Session session, DialogEntity dialog) throws RepositoryException {
        Node rootNode = JcrUtils.getOrAddNode(session.getRootNode(), DIALOG_ROOT_NODE_NAME);
        return JcrUtils.getOrAddNode(rootNode, String.valueOf(dialog.getId()));
    }

    /**
     * Корневой каталог пользователя
     * @param session
     * @param userEntity
     * @return
     * @throws RepositoryException
     */
    private Node getSharerHomeNode(Session session, UserEntity userEntity) throws RepositoryException {
        Node rootNode = JcrUtils.getOrAddNode(session.getRootNode(), USERS_ROOT_NODE_NAME);
        return JcrUtils.getOrAddNode(rootNode, userEntity.getIkp());
    }

    /**
     * Корневой каталог группы
     * @param session
     * @param community
     * @return
     * @throws RepositoryException
     */
    private Node getGroupHomeNode(Session session, CommunityEntity community) throws RepositoryException {
        Node rootNode = JcrUtils.getOrAddNode(session.getRootNode(), GROUPS_ROOT_NODE_NAME);
        return JcrUtils.getOrAddNode(rootNode, String.valueOf(community.getId()));
    }

    /**
     * Получить корневой каталог сущности
     * @param session
     * @param longIdentifiable
     * @param currentUser
     * @return
     * @throws RepositoryException
     */
    private Node getHomeNode(Session session, LongIdentifiable longIdentifiable, User currentUser) throws RepositoryException {
        Node result = null;
        if (longIdentifiable instanceof CommunityEntity) {
            CommunityEntity community = (CommunityEntity)longIdentifiable;
            result = getGroupHomeNode(session, community);
            checkPermissionToCommunityNode(result, currentUser.getId());
        } else if (longIdentifiable instanceof UserEntity) {
            UserEntity userEntity = (UserEntity)longIdentifiable;
            result = getSharerHomeNode(session, userEntity);
            checkPermissionToSharerNode(result, currentUser.getIkp());
        } else if (longIdentifiable instanceof DialogEntity) {
            DialogEntity dialog = (DialogEntity)longIdentifiable;
            result = getDialogHomeNode(session, dialog);
            checkPermissionToDialogNode(result, currentUser.getId());
        }
        return result;
    }

    public ResponseJcrFile getFileById(String nodeId) {
        return (ResponseJcrFile)jcrTemplate.execute(session -> {
            ResponseJcrFile result;
            try {
                Node node = session.getNodeByIdentifier(nodeId);
                result = new ResponseJcrFile(
                        getNodeFileName(node),
                        node.getIdentifier(),
                        getModifiedNodeDate(node),
                        getNodeMimeType(node),
                        getNodeFileSize(node),
                        node.getParent().getIdentifier(),
                        isNodeFolder(node)
                );
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return result;
        });
    }

    public List<ResponseJcrFile> copyAndPast(List<String> nodeIds, String destination) {
        Object result = jcrTemplate.execute(session -> {
            List<ResponseJcrFile> list = new ArrayList<>();
            try {
                Node destinationNode = session.getNodeByIdentifier(destination);
                for (String nodeId : nodeIds) {
                    Node node = session.getNodeByIdentifier(nodeId);
                    boolean isFileDefinition = node.hasProperty(FILE_NAME_PROPERTY_ATTR_NAME);
                    String nodeName = getNodeFileName(node);
                    String sourceNodeName = nodeName;
                    NodeIterator child = destinationNode.getNodes();
                    boolean foundNode = true;
                    int countCopy = 0;
                    while (foundNode) {
                        boolean foundLocal = false;
                        while (child.hasNext()) {
                            Node childNode = child.nextNode();
                            if (nodeName.equals(getNodeFileName(childNode))) {
                                foundLocal = true;
                                break;
                            }
                        }
                        foundNode = foundLocal;
                        if (foundNode) {
                            countCopy++;
                            nodeName = "Копия " + countCopy + " " + getNodeFileName(node);
                        }
                        if (countCopy > 200) {
                            break;
                        }
                    }

                    if (isFileDefinition) {
                        session.getWorkspace().copy(node.getPath(), destinationNode.getPath() + "/" + node.getName());
                        // Переименовываем на время название текущего файла
                        node.setProperty(FILE_NAME_PROPERTY_ATTR_NAME, "----------------------------------");
                    } else {
                        session.getWorkspace().copy(node.getPath(), destinationNode.getPath() + "/" + nodeName);
                    }

                    NodeIterator nodeIterator = destinationNode.getNodes();
                    Node newNode = null;
                    while(nodeIterator.hasNext()) {
                        Node childNode = nodeIterator.nextNode();
                        if (isFileDefinition) {
                            if (getNodeFileName(childNode).equals(sourceNodeName)) {
                                newNode = childNode;
                                break;
                            }
                        } else {
                            if (getNodeFileName(childNode).equals(nodeName)) {
                                newNode = childNode;
                                break;
                            }
                        }
                    }
                    if (isFileDefinition) {
                        // Переименовываем обратно исходный файл
                        node.setProperty(FILE_NAME_PROPERTY_ATTR_NAME, sourceNodeName);
                        newNode.setProperty(FILE_NAME_PROPERTY_ATTR_NAME, nodeName);
                    }

                    list.add(
                            new ResponseJcrFile(
                                    getNodeFileName(newNode),
                                    newNode.getIdentifier(),
                                    getModifiedNodeDate(newNode),
                                    getNodeMimeType(newNode),
                                    getNodeFileSize(newNode),
                                    newNode.getParent().getIdentifier(),
                                    isNodeFolder(newNode)
                            )
                    );
                }
                session.save();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return list;
        });
        return (List<ResponseJcrFile>)result;
    }

    public List<ResponseJcrFile> cutAndPast(List<String> nodeIds, String destination) {
        Object result = jcrTemplate.execute(session -> {
            List<ResponseJcrFile> list = new ArrayList<>();
            try {
                Node destinationNode = session.getNodeByIdentifier(destination);
                for (String nodeId : nodeIds) {
                    Node node = session.getNodeByIdentifier(nodeId);
                    session.move(node.getPath(), destinationNode.getPath() + "/" + node.getName());
                    list.add(
                            new ResponseJcrFile(
                                    getNodeFileName(node),
                                    node.getIdentifier(),
                                    getModifiedNodeDate(node),
                                    getNodeMimeType(node),
                                    getNodeFileSize(node),
                                    node.getParent().getIdentifier(),
                                    isNodeFolder(node)
                            )
                    );
                }
                session.save();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return list;
        });
        return (List<ResponseJcrFile>)result;
    }

    public List<ResponseJcrFile> remove(List<String> nodeIds) {
        Object result = jcrTemplate.execute(session -> {
            List<ResponseJcrFile> list = new ArrayList<>();
            try {
                for (String nodeId : nodeIds) {
                    Node node = session.getNodeByIdentifier(nodeId);
                    list.add(
                            new ResponseJcrFile(
                                    getNodeFileName(node),
                                    node.getIdentifier(),
                                    getModifiedNodeDate(node),
                                    getNodeMimeType(node),
                                    getNodeFileSize(node),
                                    node.getParent().getIdentifier(),
                                    isNodeFolder(node)
                            )
                    );
                    node.remove();
                }
                session.save();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return list;
        });
        return (List<ResponseJcrFile>)result;
    }

    public ResponseJcrFile changeFile(String fileDefinitionNodeId, InputStream fileInputStream, Long fileSize) {
        Object result = jcrTemplate.execute(session -> {
            ResponseJcrFile responseJcrFile = null;
            try {
                Node fileDefinitionNode = session.getNodeByIdentifier(fileDefinitionNodeId);
                fileDefinitionNode.setProperty(FILE_SIZE_PROPERTY_ATTR_NAME, fileSize);
                // Удаляем текущий файл
                Node fileNode = getFileNode(fileDefinitionNode);
                String fileNameNode = fileNode.getName();
                String mimeType = fileDefinitionNode.getProperty(FILE_MIME_TYPE_PROPERTY_ATTR_NAME).getString();
                fileNode.remove();

                // Файл кладём в ноду с инфой о файле
                JcrUtils.putFile(fileDefinitionNode, fileNameNode, mimeType, fileInputStream);

                responseJcrFile = new ResponseJcrFile(
                        getNodeFileName(fileDefinitionNode),
                        fileDefinitionNode.getIdentifier(),
                        getModifiedNodeDate(fileDefinitionNode),
                        getNodeMimeType(fileDefinitionNode),
                        getNodeFileSize(fileDefinitionNode),
                        fileDefinitionNode.getParent().getIdentifier(),
                        isNodeFolder(fileDefinitionNode)
                );

                session.save();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(fileInputStream);
            }
            return responseJcrFile;
        });
        return (ResponseJcrFile)result;
    }

}
