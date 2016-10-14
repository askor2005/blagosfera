package ru.radom.kabinet.services.bpmhandlers.document;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.document.dao.FlowOfDocumentDao;
import ru.radom.kabinet.document.model.DocumentEntity;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.document.dto.BPMDocumentsZipDto;
import ru.radom.kabinet.services.pdf.PdfService;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * Created by vgusev on 12.08.2016.
 */
@Service("createDocumentPdfZipHandler")
@Transactional
public class BPMCreateDocumentPdfZipHandler implements BPMHandler {

    private static final Logger logger = LoggerFactory.getLogger(BPMCreateDocumentPdfZipHandler.class);

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private FlowOfDocumentDao documentDao;

    @Autowired
    @Qualifier("calibreHtmlToPdfServiceImpl")
    private PdfService pdfService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        logger.info("Запуск создания zip архива pdf документов.");
        FileOutputStream fileOutputStream = null;
        String filePath = null;

        try {
            BPMDocumentsZipDto bpmDocumentsZipDto = serializeService.toObject(parameters, BPMDocumentsZipDto.class);
            List<Long> docIds = bpmDocumentsZipDto.getDocumentMap() == null ?
                    Collections.<Long>emptyList() : new ArrayList<>(bpmDocumentsZipDto.getDocumentMap().values());
            List<DocumentEntity> documents = documentDao.getByIds(docIds);
            File tempFile = File.createTempFile("documents", ".zip");
            fileOutputStream = new FileOutputStream(tempFile);
            writeZipToStream(documents, fileOutputStream);
            tempFile.deleteOnExit();
            filePath = tempFile.getAbsolutePath();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ExceptionUtils.check(true, "При создании zip архива pdf документов произошла ошибка.");
        } finally {
            IOUtils.closeQuietly(fileOutputStream); // NPE safe
        }
        return filePath;
    }

    /**
     * Записать документы в виде pdf в zip архив
     * @param documents список документов
     * @param outputStream стрим zip архива
     * @throws IOException
     */
    private void writeZipToStream(List<DocumentEntity> documents, OutputStream outputStream) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(outputStream, Charset.forName("CP866"));
        for(DocumentEntity document : documents) {
            String fileName = document.getName() + ".pdf";
            ZipEntry ze= new ZipEntry(fileName);
            zos.putNextEntry(ze);
            pdfService.writeStreamPdfByHtml(document.getContent(), zos, document.getPdfExportArguments());
        }
        zos.closeEntry();
        zos.close();
    }
}