package ru.radom.kabinet.services.image;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.crypt.domain.HttpException;
import ru.askor.blagosfera.crypt.tls.HTTP;
import ru.askor.blagosfera.crypt.tls.HttpFile;
import ru.askor.blagosfera.crypt.tls.Response;
import ru.askor.blagosfera.domain.events.BlagosferaEventPublisher;
import ru.askor.blagosfera.domain.events.file.ImagesEvent;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dto.StringObjectHashMap;
import ru.radom.kabinet.json.SerializationManager;
import ru.radom.kabinet.model.ImageType;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.ImageException;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityException;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.utils.JsonUtils;
import ru.radom.kabinet.utils.WebUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service("imagesService")
public class ImagesService {

    private static final String IMG_PHOTO_MIN_WIDTH = "img.photo.min.width"; // img.photo.min.width - минимальная ширина фото
    private static final String IMG_PHOTO_MIN_HEIGHT = "img.photo.min.height"; //img.photo.min.height - минимальная высота фото
    private static final String IMG_PHOTO_MAX_WIDTH = "img.photo.max.width"; // img.photo.max.width - максимальная ширина фото
    private static final String IMG_PHOTO_MAX_HEIGHT = "img.photo.max.height"; //img.photo.max.height - максимальная высота фото
    private static final String IMG_PHOTO_MAX_UPLOADSIZE = "img.photo.max.uploadsize"; // img.photo.max.uploadsize - максимальный размер загружаемого фото (1 - 1 килобайт, 1m - 1 мегабайт, 1g - 1 гигабайт).
    private static final String IMG_PHOTO_TYPES_ALLOWED = "img.photo.types_allowed"; // img.photo.types_allowed - разрешенные типы файлов. Через запятую.

    private static final String IMG_ICON_MIN_WIDTH = "img.icon.min.width"; // минимальная ширина загружаемой иконки
    private static final String IMG_ICON_MIN_HEIGHT = "img.icon.min.height"; //минимальная высота загружаемой иконки
    private static final String IMG_ICON_MAX_WIDTH = "img.icon.max.width"; //  максимальная ширина загружаемой иконки
    private static final String IMG_ICON_MAX_HEIGHT = "img.icon.max.height"; // максимальная высота загружаемой иконки
    private static final String IMG_ICON_MAX_UPLOADSIZE = "img.icon.max.uploadsize"; //  максимальный размер загружаемой иконки (1 - 1 килобайт, 1m - 1 мегабайт, 1g - 1 гигабайт).
    private static final String IMG_ICON_TYPES_ALLOWED = "img.icon.types_allowed"; // разрешенные типы файлов. Через запятую.

    private static final String IMG_ICON_WIDTH = "img.icon.width"; // ширина иконки
    private static final String IMG_ICON_HEIGHT = "img.icon.height"; // высота иконки

    /**
     * Настройка - url хранилища картинок
     */
    private static final String IMG_REPOSITORY_SETTINGS = "img.repository";

    /**
     * Значение url хранилища картинок по умолчанию
     */
    private static final String IMG_REPOSITORY_DEFAULT = "https://images.blagosfera.su/";

    @Autowired
    private SettingsManager settingsManager;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private SerializationManager serializationManager;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private BlagosferaEventPublisher blagosferaEventPublisher;

    private final static Logger LOGGER = LoggerFactory.getLogger(ImagesService.class);
    public final static int SERVICE = 1100;

    /**
     * Получить объект со значениями системных переменных описывающих ограничения для заданного типа изображения
     *
     * @return строка с json объектом
     */
    public String getImageRestrictions(ImageType imageType) {
        String result = "";

        switch (imageType) {
            case PHOTO:
                result = getImagePhotoRestrictions();
                break;

            case ICON:
                result = getImageIconRestrictions();
                break;
        }

        return result;
    }

    /**
     * Получить объект со значениями системных переменных описывающих ограничения для загружаемых фотографий
     *
     * @return строка с json объектом
     */
    private String getImagePhotoRestrictions() {
        StringObjectHashMap payload = new StringObjectHashMap();
        payload.put("result", "success");

        String minWidth = settingsManager.getSystemSetting(IMG_PHOTO_MIN_WIDTH);
        String minHeight = settingsManager.getSystemSetting(IMG_PHOTO_MIN_HEIGHT);
        String maxWidth = settingsManager.getSystemSetting(IMG_PHOTO_MAX_WIDTH);
        String maxHeight = settingsManager.getSystemSetting(IMG_PHOTO_MAX_HEIGHT);
        String maxUploadsize = settingsManager.getSystemSetting(IMG_PHOTO_MAX_UPLOADSIZE);
        String typesAllowed = settingsManager.getSystemSetting(IMG_PHOTO_TYPES_ALLOWED);

        final String errMessage = "Отсутствует системная переменная ";
        if (minWidth == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_MIN_WIDTH);
        }
        if (minHeight == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_MIN_HEIGHT);
        }
        if (maxWidth == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_MAX_WIDTH);
        }
        if (maxHeight == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_MAX_HEIGHT);
        }
        if (maxUploadsize == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_MAX_UPLOADSIZE);
        }
        if (typesAllowed == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_TYPES_ALLOWED);
        }

        int size = parseMaxUploadSize(maxUploadsize);

        payload.put("minWidth", minWidth);
        payload.put("minHeight", minHeight);
        payload.put("maxWidth", maxWidth);
        payload.put("maxHeight", maxHeight);
        payload.put("maxUploadsize", size / 1024 / 1024 + " Мб");
        payload.put("typesAllowed", typesAllowed);

        return serializationManager.serialize(payload).toString();
    }

    /**
     * Получить объект со значениями системных переменных описывающих ограничения для загружаемых иконок
     *
     * @return строка с json объектом
     */
    private String getImageIconRestrictions() {
        StringObjectHashMap payload = new StringObjectHashMap();
        payload.put("result", "success");

        String minWidth = settingsManager.getSystemSetting(IMG_ICON_MIN_WIDTH);
        String minHeight = settingsManager.getSystemSetting(IMG_ICON_MIN_HEIGHT);
        String maxWidth = settingsManager.getSystemSetting(IMG_ICON_MAX_WIDTH);
        String maxHeight = settingsManager.getSystemSetting(IMG_ICON_MAX_HEIGHT);
        String maxUploadsize = settingsManager.getSystemSetting(IMG_ICON_MAX_UPLOADSIZE);
        String typesAllowed = settingsManager.getSystemSetting(IMG_ICON_TYPES_ALLOWED);

        final String errMessage = "Отсутствует системная переменная ";
        if (minWidth == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_MIN_WIDTH);
        }
        if (minHeight == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_MIN_HEIGHT);
        }
        if (maxWidth == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_MAX_WIDTH);
        }
        if (maxHeight == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_MAX_HEIGHT);
        }
        if (maxUploadsize == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_MAX_UPLOADSIZE);
        }
        if (typesAllowed == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_TYPES_ALLOWED);
        }

        int size = parseMaxUploadSize(maxUploadsize);

        payload.put("minWidth", minWidth);
        payload.put("minHeight", minHeight);
        payload.put("maxWidth", maxWidth);
        payload.put("maxHeight", maxHeight);
        payload.put("maxUploadsize", size / 1024 / 1024 + " Мб");
        payload.put("typesAllowed", typesAllowed);

        return serializationManager.serialize(payload).toString();
    }

    // Загружает картинку из формы на сервер картинок и возвращает на него ссылку
    public String uploadToImageServer(String fileName, final byte[] file) throws Exception {
        String imgRepository = settingsManager.getSystemSetting(IMG_REPOSITORY_SETTINGS, IMG_REPOSITORY_DEFAULT);
        ExceptionUtils.check(imgRepository == null, "Отсутствует системная переменная " + IMG_REPOSITORY_SETTINGS);

        try {
            List<HttpFile> httpFiles = new ArrayList<>();
            httpFiles.add(new HttpFile(fileName, fileName, file));

            Response httpResponse = new HTTP().doPostMultipart(imgRepository + "/gate/add?service_id=" + SERVICE + "&process=0", httpFiles, null, null);
            int status = httpResponse.getStatus();

            if (status == 200) {
                String response = httpResponse.getDataAsString();

                if (response != null && response.length() > 0) {
                    String[] files = response.split(":");
                    return files.length > 1 ? imgRepository + files[1] : null;
                } else throw new Exception("Image service empty url");
            } else throw new Exception("Image service returned not ok status " + status);
        } catch (HttpException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(e.getMessage(), e);
            }

            throw e;
        }
    }

    private BufferedImage getBufferedImageFromBytes(byte[] imageBuffer) throws IOException {
        InputStream stream = new ByteArrayInputStream(imageBuffer);
        BufferedImage image = ImageIO.read(stream);
        return image;
    }

    private byte[] getBytesFromBufferedImage(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        final byte[] bytes = baos.toByteArray();
        return bytes;
    }

    private AffineTransform getExifTransformation(int width, int height, int orientation) {

        AffineTransform t = new AffineTransform();

        switch (orientation) {
            case 1:
                break;
            case 2: // Flip X
                t.scale(-1.0, 1.0);
                t.translate(-width, 0);
                break;
            case 3: // PI rotation
                t.translate(width, height);
                t.rotate(Math.PI);
                break;
            case 4: // Flip Y
                t.scale(1.0, -1.0);
                t.translate(0, -height);
                break;
            case 5: // - PI/2 and Flip X
                t.rotate(-Math.PI / 2);
                t.scale(-1.0, 1.0);
                break;
            case 6: // -PI/2 and -width
                t.translate(height, 0);
                t.rotate(Math.PI / 2);
                break;
            case 7: // PI/2 and Flip
                t.scale(-1.0, 1.0);
                t.translate(-height, 0);
                t.translate(0, width);
                t.rotate(3 * Math.PI / 2);
                break;
            case 8: // PI / 2
                t.translate(0, width);
                t.rotate(3 * Math.PI / 2);
                break;
        }

        return t;
    }

    private BufferedImage transformImage(BufferedImage image, AffineTransform transform) {

        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);

        BufferedImage destinationImage = op.createCompatibleDestImage(image, (image.getType() == BufferedImage.TYPE_BYTE_GRAY) ? image.getColorModel() : null);
        Graphics2D g = destinationImage.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, destinationImage.getWidth(), destinationImage.getHeight());
        destinationImage = op.filter(image, destinationImage);

        return destinationImage;
    }

    private byte[] normalizeImageOrientation(byte[] bytes) throws IOException {
        byte[] resultBytes = bytes;

        try {
            InputStream stream = new ByteArrayInputStream(bytes);
            Metadata metadata = ImageMetadataReader.readMetadata(stream);

            JpegDirectory jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
            ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (jpegDirectory != null && exifIFD0Directory != null) {

                int width = jpegDirectory.getImageWidth();
                int height = jpegDirectory.getImageHeight();

                Integer orientation = exifIFD0Directory.getInteger(ExifIFD0Directory.TAG_ORIENTATION);
                if (orientation != null) {
                    AffineTransform transform = getExifTransformation(width, height, orientation);

                    BufferedImage srcImage = getBufferedImageFromBytes(bytes);
                    BufferedImage destinationImage = transformImage(srcImage, transform);
                    resultBytes = getBytesFromBufferedImage(destinationImage);
                }
            }
        } catch (Exception e) {
            // Ошибка при нормализации оринтации изображения.
            // В случае ошибки ничего не делаем и метод возвращает оригинальное изображение.
            System.out.println("Ошибка при попытке нормализовать ориентацию изображения.");
            e.printStackTrace();
        }

        return resultBytes;
    }

    private byte[] rotateImage(byte[] bytes, int angle) throws IOException {
        byte[] resultBytes = bytes;

        // Делаем угол положительным
        if (angle < 0) {
            angle += 360 + (-1) * (angle / 360) * 360;
        }

        // при angle % 360 == 0 - картинка не меняется
        if (angle % 360 != 0) {
            BufferedImage image = getBufferedImageFromBytes(bytes);

            int w = image.getWidth();
            int h = image.getHeight();

            int neww = image.getWidth();
            int newh = image.getHeight();
            if (angle % 180 == 90) {
                // Если осуществился повород под 90 градусов в любою сторону
                // то ширина и высота меняются местами
                newh = image.getWidth();
                neww = image.getHeight();
            }

            BufferedImage rotatedImage = new BufferedImage(neww, newh, image.getType());
            Graphics2D g2d = rotatedImage.createGraphics();
            g2d.translate((neww - w) / 2, (newh - h) / 2);
            g2d.rotate(Math.toRadians(angle), w / 2, h / 2);
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();

            resultBytes = getBytesFromBufferedImage(rotatedImage);
        }

        return resultBytes;
    }

    private void checkAngle(int angle) {
        if (angle % 90 != 0) {
            throw new ImageException("Угол вращения должен быть кратен 90 градусам");
        }
    }

    private void check(boolean condition, String message) {
        if (condition) {
            throw new CommunityException(message);
        }
    }

    // Загружает картинку из формы на сервер картинок и возвращает на него ссылку, публикует событие
    public String uploadFromClient(final HttpServletRequest request, String objectType, Long objectId, ImageType imageType) {
        String result = null;
        try {
            List<FileItem> items = WebUtils.parseMultipartRequest(request);
            FileItem item = items != null && items.size() > 0 ? items.get(0) : null;
            byte[] imageBuffer = null;
            if (item == null) {
                throw new ImageException("Необходимо выбрать файл");
            }
            imageBuffer = item.get();
            //String fileName = item.getName(); // убрал, т.к. из IE передаётся имя типа C:\...\..., с которым img.ra-dom почему-то работает некорректно
            String fileName = "upload_from_client";
            checkFile(imageBuffer, imageType, item.getName());

            BufferedImage image = getBufferedImageFromBytes(imageBuffer);
            checkImage(image, imageType);

            // TODO Магический не очень понятный метод для чего он нужен. Приводит к тому что картинка после него весит в разы больше изначальной
            //imageBuffer = normalizeImageOrientation(imageBuffer);
            String url = uploadToImageServer(fileName, imageBuffer);

            if (objectType != null && objectId != null) {
                ImagesEvent event = new ImagesEvent(this, objectType, objectId, url);
                blagosferaEventPublisher.publishEvent(event);
                // TODO избавиться от этого воркэраунда, сейчас не
                // получается сделать CommunitiesService implement
                // ApplicationListener, после этого он перестает
                // автовайриться
                if ("community".equals(objectType)) {
                    //communitiesService.onApplicationEvent(event);

                    // TODO костыль для установки аватарки для группы, т.к. на данный момент сломалось и communitiesService.onApplicationEvent(event); ведёт к исключению Illegal attempt to associate a collection with two open sessions
                    CommunityEntity community = communityDao.getById(event.getObjectId());
                    check(!community.getCreator().getId().equals(SecurityUtils.getUser().getId()), "У Вас нет прав на управление этим объединением");
                    community.setAvatarUrl(event.getUrl());
                    communityDao.update(community);
                }
            }
            result = url;
        } catch (Exception e) {
            ExceptionUtils.check(true, e.getMessage());
        }
        return result;
    }

    // Загружает картинку из формы на сервер картинок и возвращает на него ссылку.
    public String uploadFromClient(final HttpServletRequest request, ImageType imageType) {
        return uploadFromClient(request, null, null, imageType);
    }

    private void checkImage(BufferedImage image, ImageType imageType) throws RuntimeException, ImageException {
        switch (imageType) {
            case ICON:
                checkImageIcon(image);
                break;
            case PHOTO:
                checkImagePhoto(image);
                break;
        }
    }

    /**
     * Проверить изображение на соответствие размера допустимым значениям соответствующих системных переменных
     *
     * @param image изображение
     * @throws RuntimeException исключение если не заданы системные переменные либо они имеют неправильный формат
     * @throws ImageException   исключение если размер изображения не соответствует допустимым значениям системных переменных
     */
    private void checkImagePhoto(BufferedImage image) throws RuntimeException, ImageException {
        if (image == null) {
            throw new ImageException("Ошибка при загрузке изображения");
        }

        String minWidth = settingsManager.getSystemSetting(IMG_PHOTO_MIN_WIDTH);
        String minHeight = settingsManager.getSystemSetting(IMG_PHOTO_MIN_HEIGHT);
        String maxWidth = settingsManager.getSystemSetting(IMG_PHOTO_MAX_WIDTH);
        String maxHeight = settingsManager.getSystemSetting(IMG_PHOTO_MAX_HEIGHT);

        final String errMessage = "Отсутствует системная переменная ";
        if (minWidth == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_MIN_WIDTH);
        }
        if (minHeight == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_MIN_HEIGHT);
        }
        if (maxWidth == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_MAX_WIDTH);
        }
        if (maxHeight == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_MAX_HEIGHT);
        }

        int minW;
        int minH;
        int maxW;
        int maxH;
        try {
            minW = Integer.parseInt(minWidth);
            minH = Integer.parseInt(minHeight);
            maxW = Integer.parseInt(maxWidth);
            maxH = Integer.parseInt(maxHeight);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Одна или несколько системных переменных имеют неправильный формат: " + IMG_PHOTO_MIN_WIDTH + "," + IMG_PHOTO_MIN_HEIGHT + "," + IMG_PHOTO_MAX_WIDTH + "," + IMG_PHOTO_MAX_HEIGHT);
        }

        int w = image.getWidth();
        int h = image.getHeight();

        if (w < minW || w > maxW || h < minH || h > maxH) {
            throw new ImageException("Недопустимый размер изображения " + w + "x" + h + ". Допустимые параметры:" +
                    "\nМинимальная ширина: " + minW +
                    "\nМинимальная высота: " + minH +
                    "\nМаксимальная ширина: " + maxW +
                    "\nМаксимальная высота: " + maxH);
        }
    }

    /**
     * Получить значение максимального объёма файла в байтах
     *
     * @param strMaxUploadSize строка со значением систменой переменной IMG_PHOTO_MAX_UPLOADSIZE
     * @return значение максимального объёма файла в байтах
     * @throws RuntimeException исключение если системная переменная IMG_PHOTO_MAX_UPLOADSIZE имеет неправильный формат
     */
    private int parseMaxUploadSize(String strMaxUploadSize) throws RuntimeException {
        String number = "";
        String code = "";
        for (int i = 0; i < strMaxUploadSize.length(); i++) {
            char c = strMaxUploadSize.charAt(i);
            if (Character.isDigit(c)) {
                number += c;
            } else {
                code += c;
            }
        }

        int size;
        try {
            size = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Системная переменная " + IMG_PHOTO_MAX_UPLOADSIZE + " имеет неправильный формат");
        }
        switch (code) {
            case "":
                size *= 1024;
                break;
            case "m":
                size *= 1024 * 1024;
                break;
            case "g":
                size *= 1024 * 1024 * 1024;
                break;
            default:
                throw new RuntimeException("Системная переменная " + IMG_PHOTO_MAX_UPLOADSIZE + " имеет неправильный формат");
        }
        return size;
    }

    /**
     * Получить имя файла по юрл.
     *
     * @param url строка с юрл
     * @return имя файла или "default_name" в случае если определить имя не получилось
     */
    private String getFilenameFromUrl(String url) {
        String filename = "default_name";

        int slashIndex = url.lastIndexOf("/");
        int questionMarkIndex = url.indexOf("?");
        int length = url.length();

        int lastIndex = length;

        if (questionMarkIndex > 0) {
            lastIndex = questionMarkIndex;
        }

        if (slashIndex >= 0 && slashIndex < length) {
            String nameFromUrl = url.substring(url.lastIndexOf("/") + 1, lastIndex);
            if (!StringUtils.isBlank(nameFromUrl)) {
                filename = nameFromUrl;
            }
        }

        return filename;
    }

    /**
     * Получить имя расширения по mime типу файла
     *
     * @param mimeType mime тип файла
     * @return имя расширения
     * @throws ImageException в случае некорректного mime типа
     */
    private String getExtensionFromMimeType(String mimeType) throws ImageException {
        String extension = "";
        if (mimeType != null) {
            int slashIndex = mimeType.lastIndexOf("/");
            int length = mimeType.length();
            if (slashIndex >= 0 && slashIndex < length) {
                extension = mimeType.substring(slashIndex + 1, length);
                if (StringUtils.isBlank(extension)) {
                    throw new ImageException("Недопустимый формат файла");
                }
            } else {
                throw new ImageException("Недопустимый формат файла");
            }
        } else {
            throw new ImageException("Недопустимый формат файла");
        }
        return extension;
    }

    /**
     * Получить файл по url и возвратить массив байт
     *
     * @param url юрл файла
     * @return
     * @throws ImageException в случае ошибки загрузки файла
     */
    private byte[] getBytesFromUrl(URL url) throws ImageException {
        try {
            HTTP http = new HTTP();
            Response response = http.doGet(url.toString());
            return response.getData();
        } catch (HttpException e) {
            throw new ImageException("Ошибка загрузки изображения");
        }
    }
    /**
     * Получить файл по url и возвратить массив байт
     *
     * @param url юрл файла
     * @return
     * @throws ImageException в случае ошибки загрузки файла
     */
    public byte[] getBytesFromUrl(String url) throws ImageException {
        try {
            HTTP http = new HTTP();
            Response response = http.doGet(url);
            return response.getData();
        } catch (HttpException e) {
            throw new ImageException("Ошибка загрузки изображения");
        }
    }
    private void checkFile(final byte[] bytes, ImageType imageType, String fileUrl) throws RuntimeException, ImageException {
        switch (imageType) {
            case ICON:
                checkFileIcon(bytes);
                break;
            case PHOTO:
                checkFilePhoto(bytes, fileUrl);
                break;
        }
    }

    /**
     * Проверяет объём и формат файла по ограничениям, заданных системными переменными
     *
     * @param bytes файл
     * @throws RuntimeException исключение если не заданы системные переменные
     * @throws ImageException   исключение с сообщением для пользователя
     */
    private void checkFilePhoto(final byte[] bytes, String fileUrl) throws RuntimeException, ImageException {
        String maxUploadsize = settingsManager.getSystemSetting(IMG_PHOTO_MAX_UPLOADSIZE);
        String typesAllowed = settingsManager.getSystemSetting(IMG_PHOTO_TYPES_ALLOWED);

        final String errMessage = "Отсутствует системная переменная ";
        if (maxUploadsize == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_MAX_UPLOADSIZE);
        }
        if (typesAllowed == null) {
            throw new RuntimeException(errMessage + IMG_PHOTO_TYPES_ALLOWED);
        }

        int size = parseMaxUploadSize(maxUploadsize);
        if (bytes.length > size) {
            throw new ImageException("Загружаемый файл не должен превышать объём " + size / 1024 / 1024 + " мегабайт");
        }

        String extension;
        try {
            Magic parser = new Magic();
            MagicMatch match = parser.getMagicMatch(bytes);
            String type = match.getMimeType(); // mime type, например image/png
            extension = getExtensionFromMimeType(type);
        } catch (Exception e) {
            //throw new ImageException("Недопустимый формат файла");
            extension = getUrlExtension(fileUrl);
        }

        if (!typesAllowed.contains(extension)) {
            throw new ImageException("Недопустимый формат файла " + extension + ". Допустимые форматы файлов: " + typesAllowed);
        }
    }

    /**
     * Получить расширение файла из урла
     * @param url
     * @return
     */
    private static String getUrlExtension(String url) {
        String extension = null;
        if(url.contains(".")) {
            extension = url.substring(url.lastIndexOf(".") + 1).toLowerCase();
        }
        return extension;
    }

    /**
     * Проверяет корректность области миниатюры по отношению к изображению
     *
     * @param image изображение
     * @param x1    х-координата верхнего левого угла области
     * @param x2    х-координата нижнего правого угла области
     * @param y1    у-координата верхнего левого угла области
     * @param y2    у-координата нижнего правого угла области
     * @throws ImageException исключение если область не вписывается в изображение или имеет отрицательные ширину или высоту
     */
    public void checkCrop(BufferedImage image, int x1, int x2, int y1, int y2) throws ImageException {
        int w = image.getWidth();
        int h = image.getHeight();
        if (!(x2 - x1 > 0 && y2 - y1 > 0 && x1 >= 0 && x1 <= w && y1 >= 0 && y1 <= h)) {
            throw new ImageException("Некорректная область миниатюры");
        }
    }

    /**
     * Загружает обрезанную картинку на сервер картинок и возвращает на него ссылку.
     *
     * @param src             url картинки которую надо обрезать
     * @param x1f,x2f,y1f,y2f регион обрезки
     * @return url картинки на "img.repository"
     */
    public String cropImage(String src, Float x1f, Float x2f, Float y1f, Float y2f) {
        try {
            if (StringUtils.isBlank(src)) {
                throw new ImageException("Изображение не задано");
            }
            if (x1f == null || x2f == null || y1f == null || y2f == null) {
                throw new ImageException("Некорректная область миниатюры");
            }

            int x1 = x1f.intValue();
            int x2 = x2f.intValue();
            int y1 = y1f.intValue();
            int y2 = y2f.intValue();

            URL url = new URL(src);

            final byte[] fileBytes = getBytesFromUrl(url);
            checkFilePhoto(fileBytes, src);

            InputStream in = new ByteArrayInputStream(fileBytes);
            BufferedImage image = ImageIO.read(in);
            checkImagePhoto(image);

            checkCrop(image, x1, x2, y1, y2);
            BufferedImage cropImage = image.getSubimage(x1, y1, (x2 - x1), (y2 - y1));
            checkImagePhoto(cropImage);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(cropImage, "png", baos);
            final byte[] bytes = baos.toByteArray();
            String imageUrl = uploadToImageServer(getFilenameFromUrl(src), bytes);

            StringObjectHashMap payload = new StringObjectHashMap();
            payload.put("result", "success");
            payload.put("image", imageUrl);
            return serializationManager.serialize(payload).toString();
        } catch (MalformedURLException e) {
            return JsonUtils.getErrorJson("Некорректный URL").toString();
        } catch (ImageException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(e.getMessage(), e);
            }
            return JsonUtils.getErrorJson().toString();
        }
    }

    private void checkImageUrl(String src) throws ImageException {
        if (StringUtils.isBlank(src)) {
            throw new ImageException("Изображение не задано");
        }
    }

    /**
     * Редактирует изображение. Пока поддерживает только поворот изображения.
     *
     * @param src   - url изображения
     * @param angle - угол поворота в градусах, целое число
     * @return url картинки на "img.repository"
     */
    public String editImage(String src, int angle) {
        try {
            checkImageUrl(src);
            checkAngle(angle);

            URL url = new URL(src);

            final byte[] fileBytes = getBytesFromUrl(url);
            checkFilePhoto(fileBytes, src);

            InputStream in = new ByteArrayInputStream(fileBytes);
            BufferedImage image = ImageIO.read(in);
            checkImagePhoto(image);

            byte[] imageBuffer = rotateImage(fileBytes, angle);
            String imageUrl = uploadToImageServer(getFilenameFromUrl(src), imageBuffer);

            StringObjectHashMap payload = new StringObjectHashMap();
            payload.put("result", "success");
            payload.put("image", imageUrl);
            return serializationManager.serialize(payload).toString();
        } catch (MalformedURLException e) {
            return JsonUtils.getErrorJson("Некорректный URL").toString();
        } catch (ImageException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(e.getMessage(), e);
            }
            return JsonUtils.getErrorJson().toString();
        }
    }

    /**
     * Загружает картинку на сервер картинок и возвращает на него ссылку.
     *
     * @param src url картинки
     * @return url картинки на "img.repository"
     */
    public String uploadFromUrl(String src, ImageType imageType) {
        try {
            if (StringUtils.isBlank(src)) {
                throw new ImageException("Изображение не задано");
            }
            URL url = new URL(src);

            final byte[] imageBytes = getBytesFromUrl(url);
            checkFile(imageBytes, imageType, src);

            BufferedImage image = getBufferedImageFromBytes(imageBytes);
            checkImage(image, imageType);

            //String imageUrl = uploadToImageServer(getFilenameFromUrl(src), fileBytes);

            // TODO Магический не очень понятный метод для чего он нужен. Приводит к тому что картинка после него весит в разы больше изначальной
            //imageBytes = normalizeImageOrientation(imageBytes);
            String imageUrl = uploadToImageServer(getFilenameFromUrl(src), imageBytes);

            StringObjectHashMap payload = new StringObjectHashMap();
            payload.put("result", "success");
            payload.put("image", imageUrl);
            return serializationManager.serialize(payload).toString();
        } catch (MalformedURLException e) {
            return JsonUtils.getErrorJson("Некорректный URL").toString();
        } catch (ImageException e) {
            return JsonUtils.getErrorJson(e.getMessage()).toString();
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(e.getMessage(), e);
            }
            return JsonUtils.getErrorJson().toString();
        }
    }

    /**
     * Загружает картинку на сервер картинок и возвращает на него ссылку.
     *
     * @param data строка base64 и mime типом вначале, отделённого запятой
     * @return url картинки на "img.repository"
     */
    public String uploadFromBase64(String data, ImageType imageType) {
        String result = "";
        try {
            if (StringUtils.isBlank(data)) {
                throw new ImageException("Изображение не задано");
            }

            String[] splitData = data.split(",");
            if (splitData.length < 2) {
                throw new ImageException("Неверный формат изображения. Необходим base64 с mime типом в начале, отделённым запятой");
            }

            String byteStr = splitData[1]; //remove mimeType declaration in the data string
            final byte[] bytes = Base64.decodeBase64(byteStr); //get the byte array of the data
            checkFile(bytes, imageType, null);

            InputStream stream = new ByteArrayInputStream(bytes);
            BufferedImage image = ImageIO.read(stream);
            checkImage(image, imageType);

            result = uploadToImageServer("base64", bytes);
        } catch (Exception e) {
            ExceptionUtils.check(true, e.getMessage());
        }
        return result;
    }

    public String setAvatar(String url, String urlOriginal, String objectType, Long objectId) {
        try {
            if (objectType != null && objectId != null) {
                ImagesEvent event = new ImagesEvent(this, objectType, objectId, url, urlOriginal);
                blagosferaEventPublisher.publishEvent(event);
                // TODO избавиться от этого воркэраунда, сейчас не
                // получается сделать CommunitiesService implement
                // ApplicationListener, после этого он перестает
                // автовайриться
                if ("community".equals(objectType)) {
                    communitiesService.onApplicationEvent(event);
                }
            }
            return JsonUtils.getSuccessJson().toString();
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(e.getMessage(), e);
            }
            return JsonUtils.getErrorJson().toString();
        }

    }

    private void checkFileSign(final byte[] fileBytes, String fileUrl) throws RuntimeException, ImageException {
        // TODO
        checkFilePhoto(fileBytes, fileUrl);
    }

    private void checkImageSign(BufferedImage image) throws RuntimeException, ImageException {
        // TODO
        checkImagePhoto(image);
    }

    private BufferedImage prepareImageSing(BufferedImage image) {
        // Требуемые размеры подписи
        int scaledWidth = 100;
        int scaledHeight = 30;

        // Размеры подписи оригинального изображения
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        // Расчёт коэффициента масштабирования.
        // Выбирается исходя из того, чтобы подпись целиком помещалась в требуемые рамки
        float scaleWidth = ((float) originalWidth) / ((float) scaledWidth);
        float scaleHeight = ((float) originalHeight) / ((float) scaledHeight);
        float scale = Math.max(scaleWidth, scaleHeight);

        // Вычисляем новую ширину и высоту для оригинального изображения с подписью. (Пропорциональное масштабирование)
        int singWidth = Math.round(((float) originalWidth) / scale);
        int singHeight = Math.round(((float) originalHeight) / scale);

        // Создаём под подпись изображение требуемого размера
        BufferedImage imageSign = new BufferedImage(scaledWidth, scaledHeight, image.getType());

        // Создаём контекст рисования
        Graphics2D g2d = imageSign.createGraphics();

        // Очищаем его, заполним белым фоном
        g2d.setBackground(Color.WHITE);
        g2d.clearRect(0, 0, scaledWidth, scaledHeight);

        // Рисуем по центру продпись
        int verticalStripWidth = (scaledWidth - singWidth) / 2;
        int horizontalStripWidth = (scaledHeight - singHeight) / 2;
        int x = verticalStripWidth, y = horizontalStripWidth;
        g2d.drawImage(image, x, y, singWidth, singHeight, null);

        // Освобождаем ресурсы
        g2d.dispose();

        // profit!
        return imageSign;
    }

    public String setSign(String src, UserEntity userEntity) {
        try {
            if (userEntity != null) {
                if (StringUtils.isBlank(src)) {
                    throw new ImageException("Изображение с подписью не задано");
                }
                URL url = new URL(src);

                final byte[] fileBytes = getBytesFromUrl(url);
                checkFileSign(fileBytes, src);

                InputStream in = new ByteArrayInputStream(fileBytes);
                BufferedImage image = ImageIO.read(in);
                checkImageSign(image);

                BufferedImage imageSign = prepareImageSing(image);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(imageSign, "png", baos);
                final byte[] bytes = baos.toByteArray();

                String imageUrl = uploadToImageServer(getFilenameFromUrl(src), bytes);

                StringObjectHashMap payload = new StringObjectHashMap();
                payload.put("result", "success");
                payload.put("image", imageUrl);
                return serializationManager.serialize(payload).toString();
            }
            return JsonUtils.getSuccessJson().toString();
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(e.getMessage(), e);
            }
            return JsonUtils.getErrorJson().toString();
        }

    }

    private void checkFileIcon(final byte[] bytes) throws RuntimeException, ImageException {
        String maxUploadsize = settingsManager.getSystemSetting(IMG_ICON_MAX_UPLOADSIZE);
        String typesAllowed = settingsManager.getSystemSetting(IMG_ICON_TYPES_ALLOWED);

        final String errMessage = "Отсутствует системная переменная ";
        if (maxUploadsize == null) {
            throw new RuntimeException(errMessage + IMG_ICON_MAX_UPLOADSIZE);
        }
        if (typesAllowed == null) {
            throw new RuntimeException(errMessage + IMG_ICON_TYPES_ALLOWED);
        }

        int size = parseMaxUploadSize(maxUploadsize);
        if (bytes.length > size) {
            throw new ImageException("Загружаемый файл не должен превышать объём " + size / 1024 / 1024 + " мегабайт");
        }

        Magic parser = new Magic();
        MagicMatch match;
        try {
            match = parser.getMagicMatch(bytes);
        } catch (Exception e) {
            throw new ImageException("Недопустимый формат файла");
        }
        String type = match.getMimeType(); // mime type, например image/png
        String extension = getExtensionFromMimeType(type);

        if (!typesAllowed.contains(extension)) {
            throw new ImageException("Недопустимый формат файла " + extension + ". Допустимые форматы файлов: " + typesAllowed);
        }
    }

    private void checkImageIcon(BufferedImage image) throws RuntimeException, ImageException {
        if (image == null) {
            throw new ImageException("Ошибка при загрузке иконки");
        }

        String minIconWidth = settingsManager.getSystemSetting(IMG_ICON_MIN_WIDTH);
        String minIconHeight = settingsManager.getSystemSetting(IMG_ICON_MIN_HEIGHT);
        String maxIconWidth = settingsManager.getSystemSetting(IMG_ICON_MAX_WIDTH);
        String maxIconHeight = settingsManager.getSystemSetting(IMG_ICON_MAX_HEIGHT);

        final String errMessage = "Отсутствует системная переменная ";
        if (minIconWidth == null) {
            throw new RuntimeException(errMessage + IMG_ICON_MIN_WIDTH);
        }
        if (minIconHeight == null) {
            throw new RuntimeException(errMessage + IMG_ICON_MIN_HEIGHT);
        }
        if (maxIconWidth == null) {
            throw new RuntimeException(errMessage + IMG_ICON_MAX_WIDTH);
        }
        if (maxIconHeight == null) {
            throw new RuntimeException(errMessage + IMG_ICON_MAX_HEIGHT);
        }

        int minW;
        int minH;
        int maxW;
        int maxH;
        try {
            minW = Integer.parseInt(minIconWidth);
            minH = Integer.parseInt(minIconHeight);
            maxW = Integer.parseInt(maxIconWidth);
            maxH = Integer.parseInt(maxIconHeight);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Одна или несколько системных переменных имеют неправильный формат: " + IMG_ICON_MIN_WIDTH + "," + IMG_ICON_MIN_HEIGHT + "," + IMG_ICON_MAX_WIDTH + "," + IMG_ICON_MAX_HEIGHT);
        }

        int w = image.getWidth();
        int h = image.getHeight();

        if (w < minW || w > maxW || h < minH || h > maxH) {
            throw new ImageException("Недопустимый размер изображения " + w + "x" + h + ". Допустимые параметры:" +
                    "\nМинимальная ширина: " + minW +
                    "\nМинимальная высота: " + minH +
                    "\nМаксимальная ширина: " + maxW +
                    "\nМаксимальная высота: " + maxH);
        }
    }

    private BufferedImage prepareImageIcon(BufferedImage image) {
        String iconWidthSystemSetting = settingsManager.getSystemSetting(IMG_ICON_WIDTH);
        String iconHeightSystemSetting = settingsManager.getSystemSetting(IMG_ICON_HEIGHT);

        if (iconWidthSystemSetting == null) {
            throw new RuntimeException("Отсутствует системная переменная " + IMG_ICON_WIDTH);
        }
        if (iconHeightSystemSetting == null) {
            throw new RuntimeException("Отсутствует системная переменная " + IMG_ICON_HEIGHT);
        }

        int iconWidth;
        int iconHeight;
        try {
            iconWidth = Integer.parseInt(iconWidthSystemSetting);
            iconHeight = Integer.parseInt(iconWidthSystemSetting);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Одна или несколько системных переменных имеют неправильный формат: " + IMG_ICON_WIDTH + "," + IMG_ICON_HEIGHT);
        }

        // Создаём под подпись изображение требуемого размера
        BufferedImage imageIcon = new BufferedImage(iconWidth, iconHeight, image.getType());

        // Создаём контекст рисования
        Graphics2D g2d = imageIcon.createGraphics();
        g2d.drawImage(image, 0, 0, iconWidth, iconHeight, null);

        // Освобождаем ресурсы
        g2d.dispose();

        // profit!
        return imageIcon;
    }

    public String uploadSectionIcon(String src) {
        try {
            if (StringUtils.isBlank(src)) {
                throw new ImageException("Изображение с иконкой не задано");
            }
            URL url = new URL(src);

            final byte[] fileBytes = getBytesFromUrl(url);
            checkFileIcon(fileBytes);

            InputStream in = new ByteArrayInputStream(fileBytes);
            BufferedImage image = ImageIO.read(in);
            checkImageIcon(image);

            BufferedImage imageSign = prepareImageIcon(image);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imageSign, "png", baos);
            final byte[] bytes = baos.toByteArray();

            String imageUrl = uploadToImageServer(getFilenameFromUrl(src), bytes);

            StringObjectHashMap payload = new StringObjectHashMap();
            payload.put("result", "success");
            payload.put("image", imageUrl);
            return serializationManager.serialize(payload).toString();
        } catch (ImageException ie) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(ie.getMessage(), ie);
            }
            return JsonUtils.getErrorJson(ie.getMessage()).toString();
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(e.getMessage(), e);
            }
            return JsonUtils.getErrorJson().toString();
        }

    }
}
