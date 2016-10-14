package ru.radom.kabinet.model.skbcontur;

import java.util.Date;
import java.util.List;

/**
 * Created by vgusev on 24.06.2015.
 */
public class SkbConturUL {

    // Краткое наименование организации
    private String shortName;
    // Полное наименование организации
    private String longName;
    // ИНН организации
    private String inn;
    // КПП организации
    private String kpp;
    // ОГРН организации
    private String ogrn;
    // ОКПО организации
    private String okpo;
    // Дата регистрации
    private Date regDate;
    // Дата ликвидации
    private Date liquidationDate;

    // Статус ЮЛ в виде неформализованной строки
    private String statusText;
    // Статус ЮЛ - Действующее/Не действующее
    private boolean active;
    // Статус ЮЛ -ликвидировано\нет
    private boolean liquidated;
    // Статус ЮЛ - в процессе ликвидации\нет
    private boolean liquidating;
    // Статус ЮЛ - в процессе реорганизации\нет
    private boolean reorganizing;

    //-----------------------------------------------
    // Адрес
    //-----------------------------------------------
    // Индес
    private String zip;

    // Код региона
    private String regionCode;
    private String regionTypeNameAbr;
    private String regionTypeName;
    private String regionName;

    // Район
    private String districtTypeNameAbr;
    private String districtTypeName;
    private String districtName;

    // Город
    private String cityTypeNameAbr;
    private String cityTypeName;
    private String cityName;

    // Населенный пункт
    private String placeTypeNameAbr;
    private String placeTypeName;
    private String placeName;

    // Улица
    private String streetTypeNameAbr;
    private String streetTypeName;
    private String streetName;

    // Дом
    private String houseTypeNameAbr;
    private String houseTypeName;
    private String houseName;

    // Корпус
    private String bulkTypeNameAbr;
    private String bulkTypeName;
    private String bulkName;

    // Офис\квартира\комната
    private String flatTypeNameAbr;
    private String flatTypeName;
    private String flatName;

    private Date addressDate;

    // Количество компаний по тому же адресу с точностью до дома
    private int houseRegsCount;
    // Количество компаний по тому же адресу с точностью до офиса
    private int flatRegsCount;
    //-----------------------------------------------

    //-----------------------------------------------
    // Деятельность компании
    //-----------------------------------------------
    private SkbConturULActivity mainActivity;
    private List<SkbConturULActivity> activities;
    //-----------------------------------------------

    //-----------------------------------------------
    // Массив руководителей организации
    //-----------------------------------------------
    private List<SkbConturULHead> heads;
    //-----------------------------------------------

    //-----------------------------------------------
    // Уставной капитал организации
    //-----------------------------------------------

    // Сумма уставного капитала
    private double capitalSum;

    // Дата внесения данных в ЕГРЮЛ
    private Date capitalDate;
    //-----------------------------------------------

    //-----------------------------------------------
    // Учредители физ лица
    //-----------------------------------------------
    private List<SkbConturULFounderFL> foundersFL;
    //-----------------------------------------------

    //-----------------------------------------------
    // Учредители юр лица
    //-----------------------------------------------
    private List<SkbConturULFounderUL> foundersUL;
    //-----------------------------------------------

    // Список приемников.
    private List<SkbConturULSuccessor> successors;

    // Список предшественников ЮЛ
    private List<SkbConturULPredecessor> predecessors;

    // Регистрационныйномер ПФР
    private String pfrRegNumber;
    // Регистрационныйномер ФСС
    private String fssRegNumber;
    // Регистрационныйномер ФОМС
    private String fomsRegNumber;
    // Количество юрлиц, в уставном капитале которых есть доля текущего юрлица
    private int foundedULCount;

    //-----------------------------------------------
    // Статистика арбитражных дел
    //-----------------------------------------------

    // Истец:
    // Количество дел в качестве истца за последние 12 месяцев
    private int courtsCasesStatPlaintiffCount12month;
    // 	Общая сумма исковых требований в качестве истца за последние 12 месяцев
    private double courtsCasesStatPlaintiffTotalSum12month;
    // Количество дел в качестве истца
    private int courtsCasesStatPaintiffCount;
    // 	Общая сумма исковых требований в качестве истца
    private double courtsCasesStatPaintiffTotalSum;


    // В качестве ответчика:
    // Количество дел в качестве ответчика за последние 12 месяцев
    private int courtsCasesStatDefendantCount12month;
    // Общая сумма исковых требования в качестве ответчика за последние 12 месяцев
    private double courtsCasesStatDefendantTotalSum12month;
    // Количество дел в качестве ответчика
    private int courtsCasesStatDefendantCount;
    // Общая сумма исковых требования в качестве ответчика
    private double courtsCasesStatDefendantTotalSum;
    //-----------------------------------------------

    //-----------------------------------------------
    // Статистика заключенных государственных контрактов
    //-----------------------------------------------

    // Количество заключенных контрактов по 223, 94 и 44 ФЗ за последние 12 месяцев
    private int offeredContractsStatCount12month;

    // Общая сумма заключенных контрактов по 223, 94 и 44 ФЗ за последние 12 месяцев
    private double offeredContractsStatTotalSum12month;

    // Количество заключенных контрактов по 223, 94 и 44 ФЗ
    private int offeredContractsStatCount;

    // Общая сумма заключенных контрактов по 223, 94 и 44 ФЗ
    private double offeredContractsStatTotalSum;
    //-----------------------------------------------

    //-----------------------------------------------
    // Статистика размещённых государственных контрактов
    //-----------------------------------------------

    // Количество размещённых контрактов по 223, 94 и 44 ФЗ за последние 12 месяцев
    private int placedContractsStatCount12month;

    // Общая сумма размещённых контрактов по 223, 94 и 44 ФЗ за последние 12 месяцев
    private double placedContractsStatTotalSum12month;

    // Количество размещённых контрактов по 223, 94 и 44 ФЗ
    private int placedContractsStatCount;

    // Общая сумма размещённых контрактов по 223, 94 и 44 ФЗ
    private double placedContractsStatTotalSum;
    //-----------------------------------------------


    //-------------------------------------
    // Статистика упоминаний компании в интернете
    //-------------------------------------

    // Оценка количество сайтов и с упоминанием текущей компании
    private int internetMentionsStatCount;
    //-------------------------------------

    //-------------------------------------
    // Статистика по исполнительным производствам в отношении компании
    //-------------------------------------

    // Количество незавершенных исполнительных производств
    private int executoryProcessesStatCount;

    // Общая сумма незавершенных исполнительных производств
    private double executoryProcessesStatTotalSum;
    //-------------------------------------


    //-------------------------------------
    // Статистика по сообщениям о банкротстве
    //-------------------------------------

    // Количество найденных сообщений о банкротстве
    private int bankruptcyStatCount;

    // Дата последнего сообщения
    private Date bankruptcyStatLatestDate;
    //-------------------------------------

    //-------------------------------------
    // Статистика по связанным товарным знакам
    //-------------------------------------

    // Количество товарных знаков, действующих или недействующих, в которых упоминается текущая компания
    private int tradeMarksStatMentionsCount;
    //-------------------------------------

    //-------------------------------------
    // Статистика по наличию сведений о бухгалтерской отчетности в источниках Росстата
    //-------------------------------------

    // Последний отчетный год, за который найдена бухгалтерская отчетность (Пример: 2013)
    private int financialStatementsStatLatestYear;
    //-------------------------------------

    // Ссылка на карточку организации.
    private String hrefInSystem;


    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    public String getOkpo() {
        return okpo;
    }

    public void setOkpo(String okpo) {
        this.okpo = okpo;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getLiquidationDate() {
        return liquidationDate;
    }

    public void setLiquidationDate(Date liquidationDate) {
        this.liquidationDate = liquidationDate;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isLiquidated() {
        return liquidated;
    }

    public void setLiquidated(boolean liquidated) {
        this.liquidated = liquidated;
    }

    public boolean isLiquidating() {
        return liquidating;
    }

    public void setLiquidating(boolean liquidating) {
        this.liquidating = liquidating;
    }

    public boolean isReorganizing() {
        return reorganizing;
    }

    public void setReorganizing(boolean reorganizing) {
        this.reorganizing = reorganizing;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionTypeNameAbr() {
        return regionTypeNameAbr;
    }

    public void setRegionTypeNameAbr(String regionTypeNameAbr) {
        this.regionTypeNameAbr = regionTypeNameAbr;
    }

    public String getRegionTypeName() {
        return regionTypeName;
    }

    public void setRegionTypeName(String regionTypeName) {
        this.regionTypeName = regionTypeName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getDistrictTypeNameAbr() {
        return districtTypeNameAbr;
    }

    public void setDistrictTypeNameAbr(String districtTypeNameAbr) {
        this.districtTypeNameAbr = districtTypeNameAbr;
    }

    public String getDistrictTypeName() {
        return districtTypeName;
    }

    public void setDistrictTypeName(String districtTypeName) {
        this.districtTypeName = districtTypeName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getCityTypeNameAbr() {
        return cityTypeNameAbr;
    }

    public void setCityTypeNameAbr(String cityTypeNameAbr) {
        this.cityTypeNameAbr = cityTypeNameAbr;
    }

    public String getCityTypeName() {
        return cityTypeName;
    }

    public void setCityTypeName(String cityTypeName) {
        this.cityTypeName = cityTypeName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPlaceTypeNameAbr() {
        return placeTypeNameAbr;
    }

    public void setPlaceTypeNameAbr(String placeTypeNameAbr) {
        this.placeTypeNameAbr = placeTypeNameAbr;
    }

    public String getPlaceTypeName() {
        return placeTypeName;
    }

    public void setPlaceTypeName(String placeTypeName) {
        this.placeTypeName = placeTypeName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getStreetTypeNameAbr() {
        return streetTypeNameAbr;
    }

    public void setStreetTypeNameAbr(String streetTypeNameAbr) {
        this.streetTypeNameAbr = streetTypeNameAbr;
    }

    public String getStreetTypeName() {
        return streetTypeName;
    }

    public void setStreetTypeName(String streetTypeName) {
        this.streetTypeName = streetTypeName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getHouseTypeNameAbr() {
        return houseTypeNameAbr;
    }

    public void setHouseTypeNameAbr(String houseTypeNameAbr) {
        this.houseTypeNameAbr = houseTypeNameAbr;
    }

    public String getHouseTypeName() {
        return houseTypeName;
    }

    public void setHouseTypeName(String houseTypeName) {
        this.houseTypeName = houseTypeName;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getBulkTypeNameAbr() {
        return bulkTypeNameAbr;
    }

    public void setBulkTypeNameAbr(String bulkTypeNameAbr) {
        this.bulkTypeNameAbr = bulkTypeNameAbr;
    }

    public String getBulkTypeName() {
        return bulkTypeName;
    }

    public void setBulkTypeName(String bulkTypeName) {
        this.bulkTypeName = bulkTypeName;
    }

    public String getBulkName() {
        return bulkName;
    }

    public void setBulkName(String bulkName) {
        this.bulkName = bulkName;
    }

    public String getFlatTypeNameAbr() {
        return flatTypeNameAbr;
    }

    public void setFlatTypeNameAbr(String flatTypeNameAbr) {
        this.flatTypeNameAbr = flatTypeNameAbr;
    }

    public String getFlatTypeName() {
        return flatTypeName;
    }

    public void setFlatTypeName(String flatTypeName) {
        this.flatTypeName = flatTypeName;
    }

    public String getFlatName() {
        return flatName;
    }

    public void setFlatName(String flatName) {
        this.flatName = flatName;
    }

    public Date getAddressDate() {
        return addressDate;
    }

    public void setAddressDate(Date addressDate) {
        this.addressDate = addressDate;
    }

    public int getHouseRegsCount() {
        return houseRegsCount;
    }

    public void setHouseRegsCount(int houseRegsCount) {
        this.houseRegsCount = houseRegsCount;
    }

    public int getFlatRegsCount() {
        return flatRegsCount;
    }

    public void setFlatRegsCount(int flatRegsCount) {
        this.flatRegsCount = flatRegsCount;
    }

    public SkbConturULActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(SkbConturULActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public List<SkbConturULActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<SkbConturULActivity> activities) {
        this.activities = activities;
    }

    public List<SkbConturULHead> getHeads() {
        return heads;
    }

    public void setHeads(List<SkbConturULHead> heads) {
        this.heads = heads;
    }

    public double getCapitalSum() {
        return capitalSum;
    }

    public void setCapitalSum(double capitalSum) {
        this.capitalSum = capitalSum;
    }

    public Date getCapitalDate() {
        return capitalDate;
    }

    public void setCapitalDate(Date capitalDate) {
        this.capitalDate = capitalDate;
    }

    public List<SkbConturULFounderFL> getFoundersFL() {
        return foundersFL;
    }

    public void setFoundersFL(List<SkbConturULFounderFL> foundersFL) {
        this.foundersFL = foundersFL;
    }

    public List<SkbConturULFounderUL> getFoundersUL() {
        return foundersUL;
    }

    public void setFoundersUL(List<SkbConturULFounderUL> foundersUL) {
        this.foundersUL = foundersUL;
    }

    public List<SkbConturULSuccessor> getSuccessors() {
        return successors;
    }

    public void setSuccessors(List<SkbConturULSuccessor> successors) {
        this.successors = successors;
    }

    public List<SkbConturULPredecessor> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<SkbConturULPredecessor> predecessors) {
        this.predecessors = predecessors;
    }

    public String getPfrRegNumber() {
        return pfrRegNumber;
    }

    public void setPfrRegNumber(String pfrRegNumber) {
        this.pfrRegNumber = pfrRegNumber;
    }

    public String getFssRegNumber() {
        return fssRegNumber;
    }

    public void setFssRegNumber(String fssRegNumber) {
        this.fssRegNumber = fssRegNumber;
    }

    public String getFomsRegNumber() {
        return fomsRegNumber;
    }

    public void setFomsRegNumber(String fomsRegNumber) {
        this.fomsRegNumber = fomsRegNumber;
    }

    public int getFoundedULCount() {
        return foundedULCount;
    }

    public void setFoundedULCount(int foundedULCount) {
        this.foundedULCount = foundedULCount;
    }

    public int getCourtsCasesStatPlaintiffCount12month() {
        return courtsCasesStatPlaintiffCount12month;
    }

    public void setCourtsCasesStatPlaintiffCount12month(int courtsCasesStatPlaintiffCount12month) {
        this.courtsCasesStatPlaintiffCount12month = courtsCasesStatPlaintiffCount12month;
    }

    public double getCourtsCasesStatPlaintiffTotalSum12month() {
        return courtsCasesStatPlaintiffTotalSum12month;
    }

    public void setCourtsCasesStatPlaintiffTotalSum12month(double courtsCasesStatPlaintiffTotalSum12month) {
        this.courtsCasesStatPlaintiffTotalSum12month = courtsCasesStatPlaintiffTotalSum12month;
    }

    public int getCourtsCasesStatPaintiffCount() {
        return courtsCasesStatPaintiffCount;
    }

    public void setCourtsCasesStatPaintiffCount(int courtsCasesStatPaintiffCount) {
        this.courtsCasesStatPaintiffCount = courtsCasesStatPaintiffCount;
    }

    public double getCourtsCasesStatPaintiffTotalSum() {
        return courtsCasesStatPaintiffTotalSum;
    }

    public void setCourtsCasesStatPaintiffTotalSum(double courtsCasesStatPaintiffTotalSum) {
        this.courtsCasesStatPaintiffTotalSum = courtsCasesStatPaintiffTotalSum;
    }

    public int getCourtsCasesStatDefendantCount12month() {
        return courtsCasesStatDefendantCount12month;
    }

    public void setCourtsCasesStatDefendantCount12month(int courtsCasesStatDefendantCount12month) {
        this.courtsCasesStatDefendantCount12month = courtsCasesStatDefendantCount12month;
    }

    public double getCourtsCasesStatDefendantTotalSum12month() {
        return courtsCasesStatDefendantTotalSum12month;
    }

    public void setCourtsCasesStatDefendantTotalSum12month(double courtsCasesStatDefendantTotalSum12month) {
        this.courtsCasesStatDefendantTotalSum12month = courtsCasesStatDefendantTotalSum12month;
    }

    public int getCourtsCasesStatDefendantCount() {
        return courtsCasesStatDefendantCount;
    }

    public void setCourtsCasesStatDefendantCount(int courtsCasesStatDefendantCount) {
        this.courtsCasesStatDefendantCount = courtsCasesStatDefendantCount;
    }

    public double getCourtsCasesStatDefendantTotalSum() {
        return courtsCasesStatDefendantTotalSum;
    }

    public void setCourtsCasesStatDefendantTotalSum(double courtsCasesStatDefendantTotalSum) {
        this.courtsCasesStatDefendantTotalSum = courtsCasesStatDefendantTotalSum;
    }

    public int getOfferedContractsStatCount12month() {
        return offeredContractsStatCount12month;
    }

    public void setOfferedContractsStatCount12month(int offeredContractsStatCount12month) {
        this.offeredContractsStatCount12month = offeredContractsStatCount12month;
    }

    public double getOfferedContractsStatTotalSum12month() {
        return offeredContractsStatTotalSum12month;
    }

    public void setOfferedContractsStatTotalSum12month(double offeredContractsStatTotalSum12month) {
        this.offeredContractsStatTotalSum12month = offeredContractsStatTotalSum12month;
    }

    public int getOfferedContractsStatCount() {
        return offeredContractsStatCount;
    }

    public void setOfferedContractsStatCount(int offeredContractsStatCount) {
        this.offeredContractsStatCount = offeredContractsStatCount;
    }

    public double getOfferedContractsStatTotalSum() {
        return offeredContractsStatTotalSum;
    }

    public void setOfferedContractsStatTotalSum(double offeredContractsStatTotalSum) {
        this.offeredContractsStatTotalSum = offeredContractsStatTotalSum;
    }

    public int getPlacedContractsStatCount12month() {
        return placedContractsStatCount12month;
    }

    public void setPlacedContractsStatCount12month(int placedContractsStatCount12month) {
        this.placedContractsStatCount12month = placedContractsStatCount12month;
    }

    public double getPlacedContractsStatTotalSum12month() {
        return placedContractsStatTotalSum12month;
    }

    public void setPlacedContractsStatTotalSum12month(double placedContractsStatTotalSum12month) {
        this.placedContractsStatTotalSum12month = placedContractsStatTotalSum12month;
    }

    public int getPlacedContractsStatCount() {
        return placedContractsStatCount;
    }

    public void setPlacedContractsStatCount(int placedContractsStatCount) {
        this.placedContractsStatCount = placedContractsStatCount;
    }

    public double getPlacedContractsStatTotalSum() {
        return placedContractsStatTotalSum;
    }

    public void setPlacedContractsStatTotalSum(double placedContractsStatTotalSum) {
        this.placedContractsStatTotalSum = placedContractsStatTotalSum;
    }

    public int getInternetMentionsStatCount() {
        return internetMentionsStatCount;
    }

    public void setInternetMentionsStatCount(int internetMentionsStatCount) {
        this.internetMentionsStatCount = internetMentionsStatCount;
    }

    public int getExecutoryProcessesStatCount() {
        return executoryProcessesStatCount;
    }

    public void setExecutoryProcessesStatCount(int executoryProcessesStatCount) {
        this.executoryProcessesStatCount = executoryProcessesStatCount;
    }

    public double getExecutoryProcessesStatTotalSum() {
        return executoryProcessesStatTotalSum;
    }

    public void setExecutoryProcessesStatTotalSum(double executoryProcessesStatTotalSum) {
        this.executoryProcessesStatTotalSum = executoryProcessesStatTotalSum;
    }

    public int getBankruptcyStatCount() {
        return bankruptcyStatCount;
    }

    public void setBankruptcyStatCount(int bankruptcyStatCount) {
        this.bankruptcyStatCount = bankruptcyStatCount;
    }

    public Date getBankruptcyStatLatestDate() {
        return bankruptcyStatLatestDate;
    }

    public void setBankruptcyStatLatestDate(Date bankruptcyStatLatestDate) {
        this.bankruptcyStatLatestDate = bankruptcyStatLatestDate;
    }

    public int getTradeMarksStatMentionsCount() {
        return tradeMarksStatMentionsCount;
    }

    public void setTradeMarksStatMentionsCount(int tradeMarksStatMentionsCount) {
        this.tradeMarksStatMentionsCount = tradeMarksStatMentionsCount;
    }

    public int getFinancialStatementsStatLatestYear() {
        return financialStatementsStatLatestYear;
    }

    public void setFinancialStatementsStatLatestYear(int financialStatementsStatLatestYear) {
        this.financialStatementsStatLatestYear = financialStatementsStatLatestYear;
    }

    public String getHrefInSystem() {
        return hrefInSystem;
    }

    public void setHrefInSystem(String hrefInSystem) {
        this.hrefInSystem = hrefInSystem;
    }
}
