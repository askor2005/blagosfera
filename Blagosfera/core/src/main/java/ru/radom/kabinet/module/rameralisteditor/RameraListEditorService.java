package ru.radom.kabinet.module.rameralisteditor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorDAO;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.hibernate.HibernateProxyTypeAdapter;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditor;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.askor.blagosfera.domain.listEditor.RameraListEditorType;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vgusev on 02.06.2015.
 * Класс обработчик действий над виджетами
 */
@Service
@Transactional
public class RameraListEditorService {

    public static final String JSON_PARAMETER_NAME = "rameraListEditorData";

    @Autowired
    private RameraListEditorDAO rameraListEditorDAO;

    @Autowired
    private RameraListEditorItemDAO rameraListEditorItemDAO;

    private static Gson gson = null;

    static {
        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        gson = b.create();
    }

    /**
     * Загрузить элементы списка из CSV файла
     * @param listId
     * @param file
     * @return
     */
    /*public String jsonSaveItemsFromCSV(Long listId, MultipartFile file) {
        return gson.toJson(saveItemsFromCSV(listId, file));
    }*/

    /**
     * Рекурсивное добавление вложенных элементов.
     * @param items
     * @param parentItem
     */
    private void recursiveAddListEditorItems(List<RameraListEditorItem> items, RameraListEditorItem parentItem) {
        for (RameraListEditorItem item : items) {
            if (parentItem != null) {
                item.setParent(parentItem);
            }
            rameraListEditorItemDAO.saveOrUpdate(item);
            if (item.getChildren() != null && item.getChildren().size() > 0) {
                recursiveAddListEditorItems(item.getChildren(), item);
            }
        }
    }

    /**
     * Загрузить элементы списка из CSV файла
     * @param listId
     * @param fileInputStream
     */
    public void saveItemsFromCSV(Long listId, InputStream fileInputStream) throws Exception {
        //String csvFileContent = IOUtils.toString(file.getInputStream());
        //String[] lines = csvFileContent.split("\r");
        List<String> lines = IOUtils.readLines(fileInputStream);
        int prevLineLevel = 0;
        //List<RameraListEditorItem> rootItems = new ArrayList<>();
        //List<RameraListEditorItem> items = rootItems;

        RameraListEditorItem rootItem = new RameraListEditorItem();
        rootItem.setChildren(new ArrayList<RameraListEditorItem>());

        // Последний родитель уровня
        Map<Integer, RameraListEditorItem> lastLevelParent = new HashMap<>();
        lastLevelParent.put(0, rootItem);

        RameraListEditorItem prevLineItem = null;
        for (String line : lines) {
            //text;mnemoCode;isActive;type;isSelected;order
            String[] parameters = line.split(";");
            boolean isFindItemParam = false;
            int level = 0;
            int columnIndex = 0;

            RameraListEditorItem item = new RameraListEditorItem();

            for (String parameter : parameters) {
                System.err.println(parameter);
                if (parameter.equals("") && !isFindItemParam) {
                    level++;
                } else {
                    isFindItemParam = true;
                    switch (columnIndex) {
                        case 0: // text
                            item.setText(parameter);
                            break;
                        case 1: // mnemoCode
                            item.setMnemoCode(parameter);
                            break;
                        case 2: //isActive
                            boolean isActive = false;
                            try {
                                isActive = Boolean.valueOf(parameter).booleanValue();
                            } catch (Exception e) {
                                throw new RuntimeException("Ошибка при парсинге строки : " + line + ". Не правильный параметр isActive со значением " + parameter);
                            }
                            item.setIsActive(isActive);
                            break;
                        case 3: // type
                            for (RameraListEditorType listEditorItemType : RameraListEditorType.values()) {
                                if (listEditorItemType.toString().equals(parameter)) {
                                    item.setListEditorItemType(listEditorItemType);
                                }
                            }
                            if (item.getListEditorItemType() == null) {
                                throw new RuntimeException("Ошибка при парсинге строки : " + line + ". Не правильный параметр type со значением " + parameter);
                            }
                            break;
                        case 4: // isSelected
                            boolean isSelected = false;
                            try {
                                isSelected = Boolean.valueOf(parameter).booleanValue();
                            } catch (Exception e) {
                                throw new RuntimeException("Ошибка при парсинге строки : " + line + ". Не правильный параметр isSelected со значением " + parameter);
                            }
                            item.setIsSelectedItem(isSelected);
                            break;
                        case 5: // order
                            long order = 0;
                            try {
                                order = Long.valueOf(parameter).longValue();
                            } catch (Exception e) {
                                throw new RuntimeException("Ошибка при парсинге строки : " + line + ". Не правильный параметр order со значением " + parameter);
                            }
                            item.setOrder(order);
                            break;
                    }
                    columnIndex++;
                }
            }

            if (level > prevLineLevel) { // Увеличился уровень
                lastLevelParent.put(level, prevLineItem);
            }/* else if (prevLineLevel > level) {

            }*/

            RameraListEditorItem itemParent = lastLevelParent.get(level);
            if (itemParent.getChildren() == null) {
                itemParent.setChildren(new ArrayList<RameraListEditorItem>());
            }

            itemParent.getChildren().add(item);
            item.setParent(itemParent);
            prevLineItem = item;
            prevLineLevel = level;


            /*
            if (level > prevLineLevel) { // Дочерний элемент по отношению к предыдущему
                itemParent = prevLineItem;
                if (prevLineItem.getChildren() == null) {
                    prevLineItem.setChildren(new ArrayList<RameraListEditorItem>());
                }
                items = prevLineItem.getChildren();
            } else if (prevLineLevel > level) { // Дочерние элементы кончились
                itemParent = prevLineItem.getParent().getParent();
                if (itemParent == null) {
                    items = rootItems;
                } else {
                    if (itemParent.getChildren() == null) {
                        itemParent.setChildren(new ArrayList<RameraListEditorItem>());
                    }
                    items = itemParent.getChildren();
                }
            }*/

            //item.setParent(itemParent);
            //items.add(item);
            //prevLineLevel = level;
            //prevLineItem = item;
        }

        RameraListEditor rameraListEditor = rameraListEditorDAO.getById(listId);
        List<RameraListEditorItem> rootItems = rootItem.getChildren();
        rameraListEditor.getItems().addAll(rootItems);
        for (RameraListEditorItem item : rootItems) {
            item.setParent(null);
            item.setListEditor(rameraListEditor);
        }
        //
        rameraListEditorDAO.saveOrUpdate(rameraListEditor);
        // Сохраняем элементы
        recursiveAddListEditorItems(rootItems, null);
    }
}
