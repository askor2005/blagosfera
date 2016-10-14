<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<style>
  #egrulData .row {margin-left: 0px; margin-right: 0px;}
</style>
<div id="egrulData">
  <h1>Информация о юридическом лице</h1>
  <c:choose>
    <c:when test="${ulInfo != null}">
      <c:if test="${ulInfo.shortName != null && ulInfo.shortName != ''}">
        <div class="row">
          <label>Краткое наименование организации</label>
          <div>${ulInfo.shortName}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.longName != null && ulInfo.longName != ''}">
        <div class="row">
          <label>Полное наименование организации</label>
          <div>${ulInfo.longName}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.inn != null && ulInfo.inn != ''}">
        <div class="row">
          <label>ИНН</label>
          <div>${ulInfo.inn}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.kpp != null && ulInfo.kpp != ''}">
        <div class="row">
          <label>КПП</label>
          <div>${ulInfo.kpp}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.ogrn != null && ulInfo.ogrn != ''}">
        <div class="row">
          <label>ОГРН</label>
          <div>${ulInfo.ogrn}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.okpo != null && ulInfo.okpo != ''}">
        <div class="row">
          <label>ОКПО</label>
          <div>${ulInfo.okpo}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.regDate != null}">
        <div class="row">
          <label>Дата регистрации</label>
          <div><fmt:formatDate pattern="dd.MM.yyyy" value="${ulInfo.regDate}" /></div>
        </div>
      </c:if>
      <c:choose>
        <c:when test="${ulInfo.statusText != null && ulInfo.statusText != ''}">
          <div class="row">
            <label>Статус организации</label>
            <div>${ulInfo.statusText}</div>
          </div>
        </c:when>
        <c:otherwise>
          <div class="row">
            <label>Статус организации</label>
            <c:choose>
              <c:when test="${ulInfo.active}">
                Действующая.
              </c:when>
              <c:otherwise>
                Не действующая.
              </c:otherwise>
            </c:choose>
            <c:if test="${ulInfo.liquidated}">
              Ликвидирована.
            </c:if>
            <c:if test="${ulInfo.liquidating}">
              Находится в стадии ликвидации.
            </c:if>
            <c:if test="${ulInfo.reorganizing}">
              Находится в стадии реорганизации.
            </c:if>
          </div>
        </c:otherwise>
      </c:choose>
      <c:if test="${ulInfo.liquidationDate != null}">
        <div class="row">
          <label>Дата ликвидации</label>
          <div><fmt:formatDate pattern="dd.MM.yyyy" value="${ulInfo.liquidationDate}" /></div>
        </div>
      </c:if>

      <div class="row">
        <label>Адрес регистрации</label>
        <div>
          ${ulInfo.regionName} ${ulInfo.regionTypeName},
          <c:if test="${ulInfo.districtName != null && ulInfo.districtName != ''}">
            ${ulInfo.districtName} ${ulInfo.districtTypeName},
          </c:if>
          <c:if test="${ulInfo.cityName != null && ulInfo.cityName != ''}">
            ${ulInfo.cityTypeName} ${ulInfo.cityName},
          </c:if>
          <c:if test="${ulInfo.placeName != null && ulInfo.placeName != ''}">
            ${ulInfo.placeTypeName} ${ulInfo.placeName},
          </c:if>
          <c:if test="${ulInfo.streetName != null && ulInfo.streetName != ''}">
            ${ulInfo.streetTypeName} ${ulInfo.streetName},
          </c:if>
          <c:if test="${ulInfo.houseName != null && ulInfo.houseName != ''}">
            ${ulInfo.houseTypeName} ${ulInfo.houseName},
          </c:if>
          <c:if test="${ulInfo.bulkName != null && ulInfo.bulkName != ''}">
            ${ulInfo.bulkTypeName} ${ulInfo.bulkName},
          </c:if>
          <c:if test="${ulInfo.flatName != null && ulInfo.flatName != ''}">
            ${ulInfo.flatTypeName} ${ulInfo.flatName},
          </c:if>
          <c:if test="${ulInfo.zip != null && ulInfo.zip != ''}">
            индекс ${ulInfo.zip}
          </c:if>
        </div>
      </div>
      <c:if test="${ulInfo.addressDate != null}">
        <div class="row">
          <label>Дата регистрации по адресу</label>
          <div><fmt:formatDate pattern="dd.MM.yyyy" value="${ulInfo.addressDate}" /></div>
        </div>
      </c:if>

      <c:if test="${ulInfo.houseRegsCount > 0}">
        <div class="row">
          <label>Количество компаний в этом же здании</label>
          <div>${ulInfo.houseRegsCount}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.flatRegsCount > 0}">
        <div class="row">
          <label>Количество компаний в этом же офисе</label>
          <div>${ulInfo.flatRegsCount}</div>
        </div>
      </c:if>
      <hr/>

      <c:if test="${ulInfo.activities != null && fn:length(ulInfo.activities) > 0}">
        <div class="row">
          <label>Сферы деятельности</label>
          <div>
            <c:forEach var="activity" items="${ulInfo.activities}" varStatus="counter">
              <c:if test="${activity.code != null && activity.code != ''}">
                Код ${activity.code},
              </c:if>
              <c:if test="${activity.text != null && activity.text != ''}">
                ${activity.text};
              </c:if>
              <c:if test="${counter.count != (fn:length(ulInfo.activities) - 1)}">
                <br/>
              </c:if>
            </c:forEach>
          </div>
        </div>
      </c:if>

      <c:if test="${ulInfo.mainActivity != null}">
        <div class="row">
          <label>Основная сфера деятельности</label>
          <div>
            <c:if test="${ulInfo.mainActivity.code != null && ulInfo.mainActivity.code != ''}">
              Код ${ulInfo.mainActivity.code},
            </c:if>
            <c:if test="${ulInfo.mainActivity.text != null && ulInfo.mainActivity.text != ''}">
              ${ulInfo.mainActivity.text}.
            </c:if>
          </div>
        </div>
        <hr/>
      </c:if>


      <c:if test="${ulInfo.heads != null && fn:length(ulInfo.heads) > 0}">
        <div class="row">
          <label>Руководители</label>
          <div>
            <c:forEach var="head" items="${ulInfo.heads}" varStatus="counter">
              <c:if test="${head.post != null && head.post != ''}">
                ${head.post}
              </c:if>
              <c:if test="${head.fio != null && head.fio != ''}">
                ${head.fio}
              </c:if>
              <c:if test="${head.inn != null && head.inn != ''}">
                <br/>ИНН физ лица: ${head.inn}
              </c:if>
              <c:if test="${head.date != null}">
                <br/>Дата внесения данных в ЕГРЮЛ: <fmt:formatDate pattern="dd.MM.yyyy" value="${head.date}" />
              </c:if>
              <c:if test="${head.fioMentionsCountEstimate > 0}">
                <br/>Количество организаций, где данное физ лицо находится в руководстве: ${head.fioMentionsCountEstimate}
              </c:if>
              <c:if test="${counter.count != (fn:length(ulInfo.heads) - 1)}">
                <br/>
              </c:if>
            </c:forEach>
          </div>
        </div>
        <hr/>
      </c:if>

      <c:if test="${ulInfo.capitalSum != null && ulInfo.capitalSum > 0}">
        <div class="row">
          <label>Сумма уставного капитала</label>
          <div><fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${ulInfo.capitalSum}" /></div>
        </div>
      </c:if>
      <c:if test="${ulInfo.capitalDate != null}">
        <div class="row">
          <label>Дата внесения данных в ЕГРЮЛ о сумме уставного капитала</label>
          <div><fmt:formatDate pattern="dd.MM.yyyy" value="${ulInfo.capitalDate}" /></div>
        </div>
        <hr/>
      </c:if>

      <c:if test="${ulInfo.foundersFL != null && fn:length(ulInfo.foundersFL) > 0}">
        <div class="row">
          <label>Учредители физ лица</label>
          <div>
            <c:forEach var="founderFL" items="${ulInfo.foundersFL}" varStatus="counter">
              <c:if test="${founderFL.fio != null && founderFL.fio != ''}">
                ${founderFL.fio}
              </c:if>
              <c:if test="${founderFL.inn != null && founderFL.inn != ''}">
                <br/>ИНН физ лица: ${founderFL.inn}
              </c:if>
              <c:if test="${founderFL.share != null && founderFL.share > 0}">
                <br/>Сумма в уставном капитале: <fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${founderFL.share}" />
              </c:if>
              <c:if test="${founderFL.sharePercent != null && founderFL.sharePercent > 0}">
                <br/>Доля в процентах: <fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${founderFL.sharePercent}" />
              </c:if>
              <c:if test="${founderFL.fioMentionsCountEstimate != null && founderFL.fioMentionsCountEstimate > 0}">
                <br/>Примерное количество компаний, где данное ФИО упоминается в качестве руководителя или учредителя: ${founderFL.fioMentionsCountEstimate}
              </c:if>
              <c:if test="${founderFL.date != null}">
                <br/>Дата внесения записи в ЕГРЮЛ: <fmt:formatDate pattern="dd.MM.yyyy" value="${founderFL.date}" />
              </c:if>

              <c:if test="${counter.count != (fn:length(ulInfo.foundersFL) - 1)}">
                <br/>
              </c:if>
            </c:forEach>
          </div>
        </div>
        <hr/>
      </c:if>

      <c:if test="${ulInfo.foundersUL != null && fn:length(ulInfo.foundersUL) > 0}">
        <div class="row">
          <label>Учредители юр лица</label>
          <div>
            <c:forEach var="founderUL" items="${ulInfo.foundersUL}" varStatus="counter">
              <c:if test="${founderUL.name != null && founderUL.name != ''}">
                Наименование организации: ${founderUL.name}
              </c:if>
              <c:if test="${founderUL.inn != null && founderUL.inn != ''}">
                <br/>ИНН юр лица: ${founderUL.inn}
              </c:if>
              <c:if test="${founderUL.ogrn != null && founderUL.ogrn != ''}">
                <br/>ОГРН юр лица: ${founderUL.ogrn}
              </c:if>

              <c:if test="${founderUL.share != null && founderUL.share > 0}">
                <br/>Сумма в уставном капитале: <fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${founderUL.share}" />
              </c:if>
              <c:if test="${founderUL.sharePercent != null && founderUL.sharePercent > 0}">
                <br/>Доля в процентах: <fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${founderUL.sharePercent}" />
              </c:if>
              <c:if test="${founderFL.fioMentionsCountEstimate != null && founderFL.fioMentionsCountEstimate > 0}">
                <br/>Примерное количество компаний, где данное ФИО упоминается в качестве руководителя или учредителя: ${founderFL.fioMentionsCountEstimate}
              </c:if>
              <c:if test="${founderUL.date != null}">
                <br/>Дата внесения записи в ЕГРЮЛ: <fmt:formatDate pattern="dd.MM.yyyy" value="${founderUL.date}" />
              </c:if>

              <c:if test="${counter.count != (fn:length(ulInfo.foundersUL) - 1)}">
                <br/>
              </c:if>
            </c:forEach>
          </div>
        </div>
        <hr/>
      </c:if>

      <c:if test="${ulInfo.successors != null && fn:length(ulInfo.successors) > 0}">
        <div class="row">
          <label>Преемники</label>
          <div>
            <c:forEach var="successor" items="${ulInfo.successors}" varStatus="counter">
              <c:if test="${successor.name != null && successor.name != ''}">
                Наименование организации: ${successor.name}
              </c:if>
              <c:if test="${successor.inn != null && successor.inn != ''}">
                <br/>ИНН юр лица: ${successor.inn}
              </c:if>
              <c:if test="${successor.ogrn != null && successor.ogrn != ''}">
                <br/>ОГРН юр лица: ${successor.ogrn}
              </c:if>
              <c:if test="${successor.date != null}">
                <br/>Дата внесения записи в ЕГРЮЛ: <fmt:formatDate pattern="dd.MM.yyyy" value="${successor.date}" />
              </c:if>

              <c:if test="${counter.count != (fn:length(ulInfo.foundersUL) - 1)}">
                <br/>
              </c:if>
            </c:forEach>
          </div>
        </div>
      </c:if>

      <c:if test="${ulInfo.predecessors != null && fn:length(ulInfo.predecessors) > 0}">
        <div class="row">
          <label>Предшественники</label>
          <div>
            <c:forEach var="predecessor" items="${ulInfo.predecessors}" varStatus="counter">
              <c:if test="${predecessor.name != null && predecessor.name != ''}">
                Наименование организации: ${predecessor.name}
              </c:if>
              <c:if test="${predecessor.inn != null && predecessor.inn != ''}">
                <br/>ИНН юр лица: ${predecessor.inn}
              </c:if>
              <c:if test="${predecessor.ogrn != null && predecessor.ogrn != ''}">
                <br/>ОГРН юр лица: ${predecessor.ogrn}
              </c:if>
              <c:if test="${predecessor.date != null}">
                <br/>Дата внесения записи в ЕГРЮЛ: <fmt:formatDate pattern="dd.MM.yyyy" value="${predecessor.date}" />
              </c:if>

              <c:if test="${counter.count != (fn:length(ulInfo.foundersUL) - 1)}">
                <br/>
              </c:if>
            </c:forEach>
          </div>
        </div>
        <hr/>
      </c:if>

      <c:if test="${ulInfo.pfrRegNumber != null && ulInfo.pfrRegNumber != ''}">
        <div class="row">
          <label>Регистрационный номер ПФР</label>
          <div>${ulInfo.pfrRegNumber}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.fssRegNumber != null && ulInfo.fssRegNumber != ''}">
        <div class="row">
          <label>Регистрационный номер ФСС</label>
          <div>${ulInfo.fssRegNumber}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.fomsRegNumber != null && ulInfo.fomsRegNumber != ''}">
        <div class="row">
          <label>Регистрационный номер ФОМС</label>
          <div>${ulInfo.fomsRegNumber}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.foundedULCount != null && ulInfo.foundedULCount > 0}">
        <div class="row">
          <label>Количество юрлиц, в уставном капитале которых есть доля текущего юрлица</label>
          <div>${ulInfo.foundedULCount}</div>
        </div>
      </c:if>
      <hr/>

      <c:if test="${(ulInfo.courtsCasesStatPaintiffCount != null && ulInfo.courtsCasesStatPaintiffCount > 0) or (ulInfo.courtsCasesStatDefendantCount != null && ulInfo.courtsCasesStatDefendantCount > 0)} ">
        <h4>Статистика арбитражных дел</h4>
      </c:if>
      <c:if test="${ulInfo.courtsCasesStatPlaintiffCount12month != null && ulInfo.courtsCasesStatPlaintiffCount12month > 0}">
        <div class="row">
          <label>Количество дел в качестве истца за последние 12 месяцев</label>
          <div>${ulInfo.courtsCasesStatPlaintiffCount12month}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.courtsCasesStatPlaintiffTotalSum12month != null && ulInfo.courtsCasesStatPlaintiffTotalSum12month > 0}">
        <div class="row">
          <label>Общая сумма исковых требований в качестве истца за последние 12 месяцев</label>
          <div><fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${ulInfo.courtsCasesStatPlaintiffTotalSum12month}" /></div>
        </div>
      </c:if>
      <c:if test="${ulInfo.courtsCasesStatPaintiffCount != null && ulInfo.courtsCasesStatPaintiffCount > 0}">
        <div class="row">
          <label>Количество дел в качестве истца</label>
          <div>${ulInfo.courtsCasesStatPaintiffCount}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.courtsCasesStatPaintiffTotalSum != null && ulInfo.courtsCasesStatPaintiffTotalSum > 0}">
        <div class="row">
          <label>Общая сумма исковых требований в качестве истца</label>
          <div><fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${ulInfo.courtsCasesStatPaintiffTotalSum}" /></div>
        </div>
      </c:if>

      <c:if test="${ulInfo.courtsCasesStatDefendantCount12month != null && ulInfo.courtsCasesStatDefendantCount12month > 0}">
        <div class="row">
          <label>Количество дел в качестве ответчика за последние 12 месяцев</label>
          <div>${ulInfo.courtsCasesStatDefendantCount12month}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.courtsCasesStatDefendantTotalSum12month != null && ulInfo.courtsCasesStatDefendantTotalSum12month > 0}">
        <div class="row">
          <label>Общая сумма исковых требований в качестве ответчика за последние 12 месяцев</label>
          <div><fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${ulInfo.courtsCasesStatDefendantTotalSum12month}" /></div>
        </div>
      </c:if>
      <c:if test="${ulInfo.courtsCasesStatDefendantCount != null && ulInfo.courtsCasesStatDefendantCount > 0}">
        <div class="row">
          <label>Количество дел в качестве ответчика</label>
          <div>${ulInfo.courtsCasesStatDefendantCount}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.courtsCasesStatDefendantTotalSum != null && ulInfo.courtsCasesStatDefendantTotalSum > 0}">
        <div class="row">
          <label>Общая сумма исковых требований в качестве ответчика</label>
          <div><fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${ulInfo.courtsCasesStatDefendantTotalSum}" /></div>
        </div>
      </c:if>
      <c:if test="${(ulInfo.courtsCasesStatPaintiffCount != null && ulInfo.courtsCasesStatPaintiffCount > 0) or (ulInfo.courtsCasesStatDefendantCount != null && ulInfo.courtsCasesStatDefendantCount > 0)} ">
        <hr/>
      </c:if>


      <c:if test="${(ulInfo.offeredContractsStatCount != null && ulInfo.offeredContractsStatCount > 0) or (ulInfo.placedContractsStatCount12month != null && ulInfo.placedContractsStatCount12month > 0)}">
        <h4>Статистика государственных контрактов</h4>
      </c:if>
      <c:if test="${ulInfo.offeredContractsStatCount12month != null && ulInfo.offeredContractsStatCount12month > 0}">
        <div class="row">
          <label>Количество заключенных контрактов по 223, 94 и 44 ФЗ за последние 12 месяцев</label>
          <div>${ulInfo.offeredContractsStatCount12month}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.offeredContractsStatTotalSum12month != null && ulInfo.offeredContractsStatTotalSum12month > 0}">
        <div class="row">
          <label>Общая сумма заключенных контрактов по 223, 94 и 44 ФЗ за последние 12 месяцев</label>
          <div><fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${ulInfo.offeredContractsStatTotalSum12month}" /></div>
        </div>
      </c:if>
      <c:if test="${ulInfo.offeredContractsStatCount != null && ulInfo.offeredContractsStatCount > 0}">
        <div class="row">
          <label>Количество заключенных контрактов по 223, 94 и 44 ФЗ</label>
          <div>${ulInfo.offeredContractsStatCount}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.offeredContractsStatTotalSum != null && ulInfo.offeredContractsStatTotalSum > 0}">
        <div class="row">
          <label>Общая сумма заключенных контрактов по 223, 94 и 44 ФЗ</label>
          <div><fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${ulInfo.offeredContractsStatTotalSum}" /></div>
        </div>
      </c:if>

      <c:if test="${ulInfo.placedContractsStatCount12month != null && ulInfo.placedContractsStatCount12month > 0}">
        <div class="row">
          <label>Количество размещённых контрактов по 223, 94 и 44 ФЗ за последние 12 месяцев</label>
          <div>${ulInfo.placedContractsStatCount12month}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.placedContractsStatTotalSum12month != null && ulInfo.placedContractsStatTotalSum12month > 0}">
        <div class="row">
          <label>Общая сумма размещённых контрактов по 223, 94 и 44 ФЗ за последние 12 месяцев</label>
          <div><fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${ulInfo.placedContractsStatTotalSum12month}" /></div>
        </div>
      </c:if>
      <c:if test="${ulInfo.placedContractsStatCount != null && ulInfo.placedContractsStatCount > 0}">
        <div class="row">
          <label>Количество размещённых контрактов по 223, 94 и 44 ФЗ</label>
          <div>${ulInfo.placedContractsStatCount}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.placedContractsStatTotalSum != null && ulInfo.placedContractsStatTotalSum > 0}">
        <div class="row">
          <label>Общая сумма размещённых контрактов по 223, 94 и 44 ФЗ</label>
          <div><fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${ulInfo.placedContractsStatTotalSum}" /></div>
        </div>
      </c:if>
      <c:if test="${(ulInfo.offeredContractsStatCount != null && ulInfo.offeredContractsStatCount > 0) or (ulInfo.placedContractsStatCount12month != null && ulInfo.placedContractsStatCount12month > 0)}">
        <hr/>
      </c:if>


      <c:if test="${ulInfo.internetMentionsStatCount != null && ulInfo.internetMentionsStatCount > 0}">
        <div class="row">
          <label>Оценка количества сайтов с упоминанием текущей компании</label>
          <div>${ulInfo.internetMentionsStatCount}</div>
        </div>
        <hr/>
      </c:if>

      <c:if test="${ulInfo.executoryProcessesStatCount != null && ulInfo.executoryProcessesStatCount > 0}">
        <h4>Статистика по исполнительным производствам в отношении компании</h4>
        <div class="row">
          <label>Количество незавершенных исполнительных производств</label>
          <div>${ulInfo.executoryProcessesStatCount}</div>
        </div>
      </c:if>
      <c:if test="${ulInfo.executoryProcessesStatTotalSum != null && ulInfo.executoryProcessesStatTotalSum > 0}">
        <div class="row">
          <label>Общая сумма незавершенных исполнительных производств</label>
          <div><fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${ulInfo.executoryProcessesStatTotalSum}" /></div>
        </div>
        <hr/>
      </c:if>


      <c:if test="${ulInfo.bankruptcyStatCount != null && ulInfo.bankruptcyStatCount > 0}">
        <h4>Статистика по сообщениям о банкротстве</h4>
        <div class="row">
          <label>Количество найденных сообщений о банкротстве</label>
          <div>${ulInfo.bankruptcyStatCount}</div>
        </div>
        <c:if test="${ulInfo.bankruptcyStatLatestDate == null}" >
          <hr/>
        </c:if>
      </c:if>
      <c:if test="${ulInfo.bankruptcyStatLatestDate != null}" >
        <div class="row">
          <label>Дата последнего сообщения о банкротстве</label>
          <div><fmt:formatDate pattern="dd.MM.yyyy" value="${ulInfo.bankruptcyStatLatestDate}" /></div>
        </div>
        <hr/>
      </c:if>

      <c:if test="${ulInfo.tradeMarksStatMentionsCount != null && ulInfo.tradeMarksStatMentionsCount > 0}">
        <div class="row">
          <label>Количество товарных знаков, действующих или недействующих, в которых упоминается текущая компания</label>
          <div>${ulInfo.tradeMarksStatMentionsCount}</div>
        </div>
        <hr/>
      </c:if>

      <c:if test="${ulInfo.financialStatementsStatLatestYear != null && ulInfo.financialStatementsStatLatestYear > 0}">
        <div class="row">
          <label>Последний отчетный год, за который найдена бухгалтерская отчетность</label>
          <div>${ulInfo.financialStatementsStatLatestYear}</div>
        </div>
        <hr/>
      </c:if>

      <c:if test="${ulInfo.hrefInSystem != null && ulInfo.hrefInSystem != ''}">
        <div class="row">
          <label>Ссылка на карточку организации в СКБ Контур Фокус</label>
          <div><a href="${ulInfo.hrefInSystem}" target="_blank">${ulInfo.hrefInSystem}</a></div>
        </div>
      </c:if>
    </c:when>
    <c:otherwise>
      Юр лицо с данным ИНН не найдено.
    </c:otherwise>
  </c:choose>
  <br/>
</div>