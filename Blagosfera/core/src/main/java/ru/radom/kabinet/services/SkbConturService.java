package ru.radom.kabinet.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.radom.kabinet.hibernate.HibernateProxyTypeAdapter;
import ru.radom.kabinet.model.skbcontur.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс для работы с системой СКБ Контур для получения данных по юр лицам
 * Created by vgusev on 23.06.2015.
 */
@Service
public class SkbConturService {

    /**
     * Кеш данных запроса.
     */
    private static Map<String, SkbConturUL> dataCache = new ConcurrentHashMap<>();

    /**
     * Время в которое были закешированы данные по ИНН.
     */
    private static Map<String, Date> timeCreateCache = new ConcurrentHashMap<>();

    /**
     * Период в мс на который кешируются данные.
     */
    private static final long CACHE_TIME = 24 * 60 * 60 * 1000;

    /**
     * Урл веб сервиса по умолчанию.
     */
    private static final String DEFAULT_BASE_WEB_SERVICE_URL = "https://focus-api.kontur.ru/api2";

    /**
     * Ключ настройки УРЛ веб сервиса.
     */
    private static final String BASE_WEB_SERVICE_URL_PARAM_NAME = "skb.contur.service.url";

    /**
     * Путь урла для запросов данных ИП
     */
    private static final String GET_ORGANISATION_IP_BASE_DATA_BY_INN_OGRN_PATH = "/ip";

    /**
     * Путь урал для запросов данныз Юр лиц.
     */
    private static final String GET_ORGANISATION_UL_BASE_DATA_BY_INN_OGRN_PATH = "/ul";

    private static final String WEB_SERVICE_KEY_PARAM_NAME = "skb.contur.service.key";

    /**
     * Ключ по умолчанию
     */
    private static final String DEFAULT_WEB_SERVICE_KEY = "f295f6b44e5ef003681893acc3b3228fea4025ed";

    private static Gson gson = null;

    static {
        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        gson = b.create();
    }

    @Autowired
    private SettingsManager settingsManager;

    private String getBaseUrl(String urlPath) {
        String key = settingsManager.getSystemSetting(WEB_SERVICE_KEY_PARAM_NAME, DEFAULT_WEB_SERVICE_KEY);
        if (key == null) {
            throw new RuntimeException("Не установлен ключ доступа к сервису. Наименование параметра: " + WEB_SERVICE_KEY_PARAM_NAME);
        }
        String url = settingsManager.getSystemSetting(BASE_WEB_SERVICE_URL_PARAM_NAME, DEFAULT_BASE_WEB_SERVICE_URL);
        url = url + urlPath + "?key=" + key;
        return url;
    }

    public SkbConturUL getULByINN(String ulInn) {
        Map<String, Object> jsonMap = null;
        SkbConturUL result = null;
        if (ulInn.length() != 10) {
            throw new RuntimeException("ИНН юридического лица должен быть из 10 цифр!");
        }
        Date cacheDate = timeCreateCache.get(ulInn);
        Date now = new Date();
        // Если время на которое кешируются данные ещё не истекло
        if (cacheDate != null && (now.getTime() - cacheDate.getTime()) < CACHE_TIME) {
            result = dataCache.get(ulInn);
        }
        if (result == null) {
            String url = getBaseUrl(GET_ORGANISATION_UL_BASE_DATA_BY_INN_OGRN_PATH);
            url += "&inn=" + ulInn;
            String jsonData = doGet(url);
            if (jsonData != null) {
                try {
                    result = buildSkbConturULFromJson(jsonData);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("При обработке данных с сервиса СКБ контур возникла ошибка. Тип ошибки: " + e.getClass().getName() + ". Текст ошибки: " + e.getMessage());
                }
                // Время создания кеша
                timeCreateCache.put(ulInn, new Date());
                // Кеш
                dataCache.put(ulInn, result);
            }
        }
        return result;
    }

    private static String getStringFromMap(Map<String, Object> itemMap, String key) {
        String result = null;
        if (itemMap.get(key) != null) {
            result = itemMap.get(key).toString();
        }
        return result;
    }

    private static Map<String, Object> getMapFromMap(Map<String, Object> itemMap, String key) {
        Map<String, Object> result = null;
        if (itemMap.get(key) != null) {
            result = (Map<String, Object>)itemMap.get(key);
        } else {
            result = new HashMap<>();
        }
        return result;
    }

    private static boolean getBoolFromMap(Map<String, Object> itemMap, String key) {
        boolean result = false;
        if (itemMap.get(key) != null) {
            result = ((Boolean)itemMap.get(key)).booleanValue();
        }
        return result;
    }

    private static int getIntFromMap(Map<String, Object> itemMap, String key) {
        int result = -1;
        if (itemMap.get(key) != null) {
            result = ((Double)itemMap.get(key)).intValue();
        }
        return result;
    }

    private static double getDoubleFromMap(Map<String, Object> itemMap, String key) {
        double result = -1d;
        if (itemMap.get(key) != null) {
            result = ((Double)itemMap.get(key)).doubleValue();
        }
        return result;
    }

    private static float getFloatFromMap(Map<String, Object> itemMap, String key) {
        float result = -1f;
        if (itemMap.get(key) != null) {
            result = ((Double)itemMap.get(key)).floatValue();
        }
        return result;
    }

    private static SkbConturUL buildSkbConturULFromJson(String json) throws Exception {
        SkbConturUL result = null;
        Map<String,Object> jsonMap = gson.fromJson(json, Map.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<Object> items = (List<Object>)jsonMap.get("items");
        if (items != null && items.size() > 0) {
            result = new SkbConturUL();
            Map<String, Object> itemMap = (Map<String, Object>)items.get(0);
            result.setShortName(getStringFromMap(itemMap, "shortName"));
            result.setLongName(getStringFromMap(itemMap, "longName"));
            result.setInn(getStringFromMap(itemMap, "inn"));
            result.setKpp(getStringFromMap(itemMap, "kpp"));
            result.setOgrn(getStringFromMap(itemMap, "ogrn"));

            // Дата регистрации
            String regDateStr = getStringFromMap(itemMap, "regDate");
            if (regDateStr != null) {
                result.setRegDate(dateFormat.parse(regDateStr));
            }

            // Дата ликвидации
            String liquidationDateStr = getStringFromMap(itemMap, "liquidationDate");
            if (liquidationDateStr != null) {
                result.setLiquidationDate(dateFormat.parse(liquidationDateStr));
            }

            // Статус ЮЛ в виде неформализованной строки
            result.setStatusText(getStringFromMap(itemMap, "statusText"));

            // Карта со статусами организации
            Map<String, Object> statusExMap = getMapFromMap(itemMap, "statusEx");

            // Статус ЮЛ - Действующее/Не действующее
            result.setActive(getBoolFromMap(statusExMap, "active"));
            // Статус ЮЛ -ликвидировано\нет
            result.setLiquidated(getBoolFromMap(statusExMap, "liquidated"));
            // Статус ЮЛ - в процессе ликвидации\нет
            result.setLiquidating(getBoolFromMap(statusExMap, "liquidating"));
            // Статус ЮЛ - в процессе реорганизации\нет
            result.setReorganizing(getBoolFromMap(statusExMap, "reorganizing"));

            // Карта с Юридическим адресом
            Map<String, Object> addressMap = getMapFromMap(itemMap, "address");
            // Индекс
            result.setZip(getStringFromMap(addressMap, "zip"));
            // Код региона
            result.setRegionCode(getStringFromMap(addressMap, "regionCode"));

            // Карта с регионом
            Map<String, Object> regionMap = getMapFromMap(addressMap, "regionName");
            // Тип региона аббревиатура
            result.setRegionTypeNameAbr(getStringFromMap(regionMap, "abbr"));
            // Тип региона
            result.setRegionTypeName(getStringFromMap(regionMap, "abbrEx"));
            // Тип региона
            result.setRegionName(getStringFromMap(regionMap, "value"));

            // TODO Сдесь должно быть описание района!

            // Карта с городом
            Map<String, Object> cityMap = getMapFromMap(addressMap, "city");
            // Тип города аббревиатура
            result.setCityTypeNameAbr(getStringFromMap(cityMap, "abbr"));
            // Тип города
            result.setCityTypeName(getStringFromMap(cityMap, "abbrEx"));
            // Наименование города
            result.setCityName(getStringFromMap(cityMap, "value"));

            // TODO Сдесь должно быть описание населенный пункта!

            // Карта с улицей
            Map<String, Object> streetMap = getMapFromMap(addressMap, "street");
            // Тип города аббревиатура
            result.setStreetTypeNameAbr(getStringFromMap(streetMap, "abbr"));
            // Тип города
            result.setStreetTypeName(getStringFromMap(streetMap, "abbrEx"));
            // Наименование города
            result.setStreetName(getStringFromMap(streetMap, "value"));

            // Карта с домом
            Map<String, Object> houseMap = getMapFromMap(addressMap, "house");
            // Тип города аббревиатура
            result.setHouseTypeNameAbr(getStringFromMap(houseMap, "abbr"));
            // Тип города
            result.setHouseTypeName(getStringFromMap(houseMap, "abbrEx"));
            // Наименование города
            result.setHouseName(getStringFromMap(houseMap, "value"));

            // TODO Сдесь должно быть описание корпуса

            // TODO Сдесь должно быть описание офиса

            // Дата у адреса???
            String addressDateStr = getStringFromMap(addressMap, "date");
            if (addressDateStr != null) {
                result.setAddressDate(dateFormat.parse(addressDateStr));
            }

            // Количество компаний по тому же адресу с точностью до дома
            result.setHouseRegsCount(getIntFromMap(addressMap, "houseRegsCount_ESTIMATE"));
            // Количество компаний по тому же адресу с точностью до офиса
            result.setFlatRegsCount(getIntFromMap(addressMap, "flatRegsCount_ESTIMATE"));

            // Сферы деятельности:
            Map<String, Object> mainActivityMap = getMapFromMap(itemMap, "mainActivity");
            String mainActivityCode = getStringFromMap(mainActivityMap, "code");
            SkbConturULActivity mainActivity = null;

            List<Object> activities = (List<Object>)itemMap.get("activities");
            List<SkbConturULActivity> activityList = new ArrayList<>();
            if (activities != null) {
                for (Object activity : activities) {
                    Map<String, Object> activityMap = (Map<String, Object>) activity;
                    SkbConturULActivity skbConturULActivity = new SkbConturULActivity();
                    skbConturULActivity.setCode(getStringFromMap(activityMap, "code"));
                    skbConturULActivity.setText(getStringFromMap(activityMap, "text"));
                    if (mainActivityCode.equals(skbConturULActivity.getCode())) {
                        mainActivity = skbConturULActivity;
                    }
                    activityList.add(skbConturULActivity);
                }
            }
            result.setMainActivity(mainActivity);
            result.setActivities(activityList);

            // Руководство организации
            List<Object> heads = (List<Object>)itemMap.get("heads");
            List<SkbConturULHead> headsList = new ArrayList<>();
            if (heads != null) {
                for (Object head : heads) {
                    Map<String, Object> headMap = (Map<String, Object>) head;

                    SkbConturULHead skbConturULHead = new SkbConturULHead();
                    // ФИО
                    skbConturULHead.setFio(getStringFromMap(headMap, "fio"));
                    // ИНН физ лица
                    skbConturULHead.setInn(getStringFromMap(headMap, "inn"));
                    // Наименование должности главы
                    skbConturULHead.setPost(getStringFromMap(headMap, "post"));

                    // Дата внесения данных в ЕГРЮЛ
                    String headDateStr = getStringFromMap(headMap, "date");
                    if (headDateStr != null) {
                        skbConturULHead.setDate(dateFormat.parse(headDateStr));
                    }

                    // Примерное количество организаций, в которых данное физ лицо находится в руководстве
                    skbConturULHead.setFioMentionsCountEstimate(getIntFromMap(headMap, "fioMentionsCount_ESTIMATE"));
                    headsList.add(skbConturULHead);
                }
            }
            result.setHeads(headsList);

            // Уставной капитал
            Map<String, Object> capitalMap = getMapFromMap(itemMap, "capital");
            result.setCapitalSum(getDoubleFromMap(capitalMap, "sum"));
            // Дата внесения данных в ЕГРЮЛ
            String capitalDateStr = getStringFromMap(capitalMap, "date");
            if (capitalDateStr != null) {
                result.setCapitalDate(dateFormat.parse(capitalDateStr));
            }

            // Учредители физ лица
            List<Object> foundersFL = (List<Object>)itemMap.get("foundersFL");
            List<SkbConturULFounderFL> foundersFLList = new ArrayList<>();
            if (foundersFL != null) {
                for (Object founder : foundersFL) {
                    Map<String, Object> founderFLMap = (Map<String, Object>) founder;
                    SkbConturULFounderFL skbConturULFounderFL = new SkbConturULFounderFL();
                    //ФИО
                    skbConturULFounderFL.setFio(getStringFromMap(founderFLMap, "fio"));
                    // ИННфиз.лица
                    skbConturULFounderFL.setInn(getStringFromMap(founderFLMap, "inn"));
                    // Сумма в уставномкапитале
                    skbConturULFounderFL.setShare(getDoubleFromMap(founderFLMap, "share"));
                    // Доля в процентах
                    skbConturULFounderFL.setSharePercent(getFloatFromMap(founderFLMap, "sharePercent_ESTIMATE"));
                    // Примерное количество компаний, где данное ФИО упоминается в качестве руководителя или учредителя
                    skbConturULFounderFL.setFioMentionsCountEstimate(getIntFromMap(founderFLMap, "fioMentionsCount_ESTIMATE"));
                    // Дата внесения записи в ЕГРЮЛ
                    String founderDateStr = getStringFromMap(founderFLMap, "date");
                    if (founderDateStr != null) {
                        skbConturULFounderFL.setDate(dateFormat.parse(founderDateStr));
                    }
                    foundersFLList.add(skbConturULFounderFL);
                }
            }
            result.setFoundersFL(foundersFLList);

            // Учредители юр лица
            List<Object> foundersUL = (List<Object>)itemMap.get("foundersUL");
            List<SkbConturULFounderUL> foundersULList = new ArrayList<>();
            if (foundersUL != null) {
                for (Object founder : foundersUL) {
                    Map<String, Object> founderULMap = (Map<String, Object>) founder;
                    SkbConturULFounderUL skbConturULFounderUL = new SkbConturULFounderUL();

                    // Наименование организации
                    skbConturULFounderUL.setName(getStringFromMap(founderULMap, "name"));
                    // ИНН
                    skbConturULFounderUL.setInn(getStringFromMap(founderULMap, "inn"));
                    // ОГРН
                    skbConturULFounderUL.setOgrn(getStringFromMap(founderULMap, "ogrn"));
                    // Сумма в уставномкапитале
                    skbConturULFounderUL.setShare(getDoubleFromMap(founderULMap, "share"));
                    // Доля в процентах
                    skbConturULFounderUL.setSharePercent(getFloatFromMap(founderULMap, "sharePercent_ESTIMATE"));
                    // Дата внесения записи в ЕГРЮЛ
                    String founderDateStr = getStringFromMap(founderULMap, "date");
                    if (founderDateStr != null) {
                        skbConturULFounderUL.setDate(dateFormat.parse(founderDateStr));
                    }
                    foundersULList.add(skbConturULFounderUL);
                }
            }
            result.setFoundersUL(foundersULList);

            // Список преемников
            List<Object> successors = (List<Object>)itemMap.get("successors");
            List<SkbConturULSuccessor> successorList = new ArrayList<>();
            if (successors != null) {
                for (Object successor : successors) {
                    Map<String, Object> successorMap = (Map<String, Object>) successor;
                    SkbConturULSuccessor skbConturULSuccessor = new SkbConturULSuccessor();

                    // Наименование организации
                    skbConturULSuccessor.setName(getStringFromMap(successorMap, "name"));
                    // ИНН
                    skbConturULSuccessor.setInn(getStringFromMap(successorMap, "inn"));
                    // ОГРН
                    skbConturULSuccessor.setOgrn(getStringFromMap(successorMap, "ogrn"));
                    // Дата внесения записи в ЕГРЮЛ
                    String successorDateStr = getStringFromMap(successorMap, "date");
                    if (successorDateStr != null) {
                        skbConturULSuccessor.setDate(dateFormat.parse(successorDateStr));
                    }
                    successorList.add(skbConturULSuccessor);
                }
            }
            result.setSuccessors(successorList);

            // Список предшественников
            List<Object> predecessors = (List<Object>)itemMap.get("predecessors");
            List<SkbConturULPredecessor> predecessorList = new ArrayList<>();
            if (predecessors != null) {
                for (Object predecessor : predecessors) {
                    Map<String, Object> predecessorMap = (Map<String, Object>) predecessor;
                    SkbConturULPredecessor skbConturULPredecessor = new SkbConturULPredecessor();

                    // Наименование организации
                    skbConturULPredecessor.setName(getStringFromMap(predecessorMap, "name"));
                    // ИНН
                    skbConturULPredecessor.setInn(getStringFromMap(predecessorMap, "inn"));
                    // ОГРН
                    skbConturULPredecessor.setOgrn(getStringFromMap(predecessorMap, "ogrn"));
                    // Дата внесения записи в ЕГРЮЛ
                    String predecessorDateStr = getStringFromMap(predecessorMap, "date");
                    if (predecessorDateStr != null) {
                        skbConturULPredecessor.setDate(dateFormat.parse(predecessorDateStr));
                    }
                    predecessorList.add(skbConturULPredecessor);
                }
            }
            result.setPredecessors(predecessorList);

            // Регистрационныйномер ПФР
            result.setPfrRegNumber(getStringFromMap(itemMap, "pfrRegNumber"));
            // Регистрационныйномер ФСС
            result.setFssRegNumber(getStringFromMap(itemMap, "pfrRegNumber"));
            // Регистрационныйномер ФОМС
            result.setFomsRegNumber(getStringFromMap(itemMap, "pfrRegNumber"));
            // Количество юрлиц, в уставном капитале которых есть доля текущего юрлица
            result.setFoundedULCount(getIntFromMap(itemMap, "foundedULCount_ESTIMATE"));

            // Статистика арбитражных дел
            Map<String, Object> courtsCasesStatMap = getMapFromMap(itemMap, "courtsCasesStat");
            // В качестве истца
            Map<String, Object> plaintiffMap = getMapFromMap(courtsCasesStatMap, "plaintiff");

            // Количество дел в качестве истца за последние 12 месяцев
            result.setCourtsCasesStatPlaintiffCount12month(getIntFromMap(plaintiffMap, "count_12month_ESTIMATE"));
            // Общая сумма исковых требований в качестве истца за последние 12 месяцев
            result.setCourtsCasesStatPlaintiffTotalSum12month(getDoubleFromMap(plaintiffMap, "totalSum_12month_ESTIMATE"));
            // Количество дел в качестве истца
            result.setCourtsCasesStatPaintiffCount(getIntFromMap(plaintiffMap, "count_ESTIMATE"));
            // Общая сумма исковых требований в качестве истца
            result.setCourtsCasesStatPaintiffTotalSum(getDoubleFromMap(plaintiffMap, "totalSum_ESTIMATE"));

            // В качестве ответчика
            Map<String, Object> defendantMap = getMapFromMap(courtsCasesStatMap, "defendant");

            // Количество дел в качестве ответчика за последние 12 месяцев
            result.setCourtsCasesStatDefendantCount12month(getIntFromMap(defendantMap, "count_12month_ESTIMATE"));
            // Общая сумма исковых требования в качестве ответчика за последние 12 месяцев
            result.setCourtsCasesStatDefendantTotalSum12month(getDoubleFromMap(defendantMap, "totalSum_12month_ESTIMATE"));
            // Количество дел в качестве ответчика
            result.setCourtsCasesStatDefendantCount(getIntFromMap(defendantMap, "count_ESTIMATE"));
            // Общая сумма исковых требования в качестве ответчика
            result.setCourtsCasesStatDefendantTotalSum(getDoubleFromMap(defendantMap, "totalSum_ESTIMATE"));


            // Статистика заключенных государственных контрактов
            Map<String, Object> govContractsStatMap = getMapFromMap(itemMap, "govContractsStat");

            // Выигранные гос.контракты
            Map<String, Object> offeredMap = getMapFromMap(govContractsStatMap, "offered");

            // Количество заключенных контрактов по 223, 94 и 44 ФЗ за последние 12 месяцев
            result.setOfferedContractsStatCount12month(getIntFromMap(offeredMap, "count_12month_ESTIMATE"));
            // Общая сумма заключенных контрактов по 223, 94 и 44 ФЗ за последние 12 месяцев
            result.setOfferedContractsStatTotalSum12month(getDoubleFromMap(offeredMap, "totalSum_12month_ESTIMATE"));
            // Количество заключенных контрактов по 223, 94 и 44 ФЗ
            result.setOfferedContractsStatCount(getIntFromMap(offeredMap, "count_ESTIMATE"));
            // Общая сумма заключенных контрактов по 223, 94 и 44 ФЗ
            result.setOfferedContractsStatTotalSum(getDoubleFromMap(offeredMap, "totalSum_ESTIMATE"));

            // Размещенные гос.контракты
            Map<String, Object> placedMap = getMapFromMap(govContractsStatMap, "placed");

            // Количество размещенных контрактов по 223, 94 и 44 ФЗ за последние 12 месяцев
            result.setPlacedContractsStatCount12month(getIntFromMap(placedMap, "count_12month_ESTIMATE"));
            // Общая сумма размещенных контрактов по 223, 94 и 44 ФЗ за последние 12 месяцев
            result.setPlacedContractsStatTotalSum12month(getDoubleFromMap(placedMap, "totalSum_12month_ESTIMATE"));
            // Количество размещенных контрактов по 223, 94 и 44 ФЗ
            result.setPlacedContractsStatCount(getIntFromMap(placedMap, "count_ESTIMATE"));
            // Общая сумма размещенных контрактов по 223, 94 и 44 ФЗ
            result.setPlacedContractsStatTotalSum(getDoubleFromMap(placedMap, "totalSum_ESTIMATE"));

            // Статистика упоминаний компании в интернете
            Map<String, Object> internetMentionsStatMap = getMapFromMap(itemMap, "internetMentionsStat");
            // Оценка количество сайтов и с упоминанием текущей компании
            result.setInternetMentionsStatCount(getIntFromMap(internetMentionsStatMap, "mentionsCount_ESTIMATE"));

            // Статистика по исполнительным производствам в отношении компании
            Map<String, Object> executoryProcessesStatMap = getMapFromMap(itemMap, "executoryProcessesStat");
            // Количество незавершенных исполнительных производств
            result.setExecutoryProcessesStatCount(getIntFromMap(executoryProcessesStatMap, "count_ESTIMATE"));
            // Общая сумма незавершенных исполнительных производств
            result.setExecutoryProcessesStatTotalSum(getDoubleFromMap(executoryProcessesStatMap, "totalSum_ESTIMATE"));

            // Статистика по сообщениям о банкротстве
            Map<String, Object> bankruptcyStatMap = getMapFromMap(itemMap, "bankruptcyStat");
            // Количество найденных сообщений о банкротстве
            result.setBankruptcyStatCount(getIntFromMap(bankruptcyStatMap, "count_ESTIMATE"));
            // Дата последнего сообщения
            String bankruptcyStatDateStr = getStringFromMap(bankruptcyStatMap, "latestDate_ESTIMATE");
            if (bankruptcyStatDateStr != null) {
                result.setBankruptcyStatLatestDate(dateFormat.parse(bankruptcyStatDateStr));
            }

            // Статистика по связанным товарным знакам
            Map<String, Object> tradeMarksStatMap = getMapFromMap(itemMap, "tradeMarksStat");
            // Количество товарных знаков, действующих или недействующих, в которых упоминается текущая компания
            result.setTradeMarksStatMentionsCount(getIntFromMap(bankruptcyStatMap, "mentionsCount_ESTIMATE"));


            // Статистика по наличию сведений о бухгалтерской отчетности в источниках Росстата
            Map<String, Object> financialStatementsStatMap = getMapFromMap(itemMap, "financialStatementsStat");
            // Последний отчетный год, за который найдена бухгалтерская отчетность
            result.setFinancialStatementsStatLatestYear(getIntFromMap(financialStatementsStatMap, "latestYear_ESTIMATE"));

            // Ссылка на карточку организации в Фокусе
            result.setHrefInSystem(getStringFromMap(itemMap, "href"));
        }
        return result;
    }

    private static final int TIMEOUT = 10000;

    private static String doGet(String url) {
        HttpURLConnection c = null;
        String result = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(TIMEOUT);
            c.setReadTimeout(TIMEOUT);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    result = IOUtils.toString(c.getInputStream(), "UTF-8");
                    break;
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    // do nothing
                }
            }
        }
        return result;
    }
}
