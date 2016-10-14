package ru.radom.kabinet.services.files;

import org.springframework.beans.factory.annotation.Autowired;
import ru.radom.kabinet.dao.files.FileDao;
import ru.radom.kabinet.dao.files.FileDownloadDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.files.File;
import ru.radom.kabinet.model.files.FileDownload;

import java.util.Date;

abstract public class FileService {

	@Autowired
	protected FileDao fileDao;

	@Autowired
	protected FileDownloadDao fileDownloadDao;

	/**
	 * 
	 * Конкретная реализация данного метода должна сохранить файл в хранилице и
	 * вернуть прямую ссылку для загрузки, например
	 * http://files.ra-dom.ru/protected/IENDOSLBFU.pdf На этом методе также
	 * лежит ответственность за разрешение коллизий имен: все имена файлов
	 * должны буть уникальными, файлы в хранилище не должны перезатираться
	 * 
	 * @param filename
	 * @param buffer
	 * @return
	 */

	abstract protected String saveToStorage(String filename, byte[] buffer);

	/**
	 * 
	 * Метод должен вернуть контролируемую ссылку на файл, которая будет
	 * использоваться для отображения в интерфейсе пользователя. Например
	 * /files/{id файла}/{имя файла}. Ссылка должна быть относительной,
	 * контроллер, обрабатывающий обращения по таким ссылкам, должен вызывать
	 * метод processDownload
	 * 
	 * @param file
	 * @return
	 */

	abstract protected String getControlledLink(File file);

	/**
	 * 
	 * Метод должен осуществить проверку прав на скачивание конкрентого файла
	 * конкретным участником, вернуть true если скачивание разрешено и false в
	 * противном случае
	 * 
	 * @param file
	 * @param downloader
	 * @return
	 */

	abstract protected boolean checkPermission(File file, UserEntity downloader);

	/**
	 * 
	 * Метод должен сохранить в БД информацию о скачивании файла
	 * 
	 * @param file
	 * @param downloader
	 * @return
	 */

	private FileDownload saveDownload(File file, UserEntity downloader) {
		FileDownload download = new FileDownload(file, downloader, new Date());
		fileDownloadDao.save(download);
		return download;
	}

	/**
	 * 
	 * Метод должен быть вызван контроллером при попытке скачивания файла по
	 * контролируемой ссылке. В случае успешного проходжения проверок он
	 * сохранит информацию о скачивании и вернет ссылку для скачивания
	 * 
	 * @param file
	 * @param downloader
	 * @return
	 */

	public String processDownload(File file, UserEntity downloader) {
		if (downloader == null) {
			throw new NullPointerException("Скачивающий участник не существует");
		}
		if (file == null) {
			throw new NotFoundFileExeption("Файл не существует");
		}
		if (!checkPermission(file, downloader)) {
			throw new AccessDeniedFileExeption("Доступ запрещен");
		}
		saveDownload(file, downloader);
		return file.getProtectedUrl();
	}

	/**
	 * 
	 * Метод должен быть вызван контроллером при загрузке файла от пользователя
	 * 
	 * @param name
	 *            - исходное имся файла
	 * @param owner
	 * @param buffer
	 * @return
	 */

	public File processUpload(String name, UserEntity owner, byte[] buffer) {
		String protectedUrl = saveToStorage(name, buffer);
		File file = new File(name, protectedUrl, owner);
		fileDao.save(file);
		return file;
	}

	/**
	 * 
	 * Метод редактирования пользовательской информации о файле
	 * 
	 * @param file
	 *            - файл для редактирования
	 * @param title
	 *            - название данное пользователем - не обязательное
	 * @param description
	 *            - описание данное пользователем - не обязательное
	 * @return
	 */

	public File editFile(File file, String title, String description) {
		if (file == null) {
			throw new NotFoundFileExeption("Файл не существует");
		}
		file.setTitle(title);
		file.setDescription(description);
		fileDao.update(file);
		return file;
	}

}
