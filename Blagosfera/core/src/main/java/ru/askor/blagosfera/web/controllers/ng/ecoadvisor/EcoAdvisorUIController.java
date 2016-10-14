package ru.askor.blagosfera.web.controllers.ng.ecoadvisor;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.domain.cashbox.CashboxException;
import ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto.*;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.CashboxService;
import ru.radom.kabinet.services.EcoAdvisorService;
import ru.radom.kabinet.services.communities.CommunitiesService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/ecoadvisor")
public class EcoAdvisorUIController {

    @Autowired
    private CashboxService cashboxService;

    @Autowired
    private EcoAdvisorService ecoAdvisorService;

    @Autowired
    private CommunitiesService communitiesService;

    public EcoAdvisorUIController() {
    }

    @RequestMapping(value = "communities.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CommunityDto> communities() {
        return ecoAdvisorService.getCommunities(SecurityUtils.getUser());
    }

    @RequestMapping(value = "subgroups.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CommunityDto> subgroups(@RequestParam(name = "communityId", required = true) Long communityId) {
        return ecoAdvisorService.getSubgroups(SecurityUtils.getUser(), communityId);
    }

    @RequestMapping(value = "community.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CommunityDto community(@RequestParam(name = "communityId", required = true) Long communityId) {
        return ecoAdvisorService.getCommunity(communityId);
    }

    @RequestMapping(value = "workplaces.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CashboxWorkplaceDto> workplaces(@RequestParam(name = "communityId", required = true) Long communityId) {
        if (!communitiesService.hasPermission(communityId, SecurityUtils.getUser().getId(), EcoAdvisorService.ECO_ADVISOR_USER_PERMISSION))
            throw new AccessDeniedException("Доступ запрещен");
        return cashboxService.getWorkplaces(communityId);
    }

    @RequestMapping(value = "sessions.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CashboxOperatorSessionsResponseDto sessions(@RequestParam(name = "communityId", required = true) Long communityId,
                                                       @RequestParam(name = "workplaceIds", required = false) List<Long> workplaceIds,
                                                       @RequestParam(name = "page", required = true) Integer page,
                                                       @RequestParam(name = "size", required = true) Integer size,
                                                       @RequestParam(name = "sortDirection", required = false) String sortDirection,
                                                       @RequestParam(name = "sortColumn", required = false) String sortColumn,
                                                       @RequestParam(name = "operator", required = false) String operator,
                                                       @RequestParam(name = "createdDateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date createdDateFrom,
                                                       @RequestParam(name = "createdDateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date createdDateTo,
                                                       @RequestParam(name = "active", required = false) String active) {
        if (!communitiesService.hasPermission(communityId, SecurityUtils.getUser().getId(), EcoAdvisorService.ECO_ADVISOR_USER_PERMISSION))
            throw new AccessDeniedException("Доступ запрещен");
        if (workplaceIds == null) workplaceIds = new ArrayList<>();
        return cashboxService.getSessions(communityId, workplaceIds, page, size, sortDirection, sortColumn, operator, createdDateFrom, createdDateTo, active);
    }

    @RequestMapping(value = "operations.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CashboxExchangeOperationsResponseDto operations(@RequestParam(name = "communityId", required = true) Long communityId,
                                                           @RequestParam(name = "sessionId", required = true) Long sessionId,
                                                           @RequestParam(name = "page", required = true) Integer page,
                                                           @RequestParam(name = "size", required = true) Integer size,
                                                           @RequestParam(name = "sortDirection", required = false) String sortDirection,
                                                           @RequestParam(name = "sortColumn", required = false) String sortColumn) {
        if (!communitiesService.hasPermission(communityId, SecurityUtils.getUser().getId(), EcoAdvisorService.ECO_ADVISOR_USER_PERMISSION))
            throw new AccessDeniedException("Доступ запрещен");
        return cashboxService.getOperations(communityId, sessionId, page, size, sortDirection, sortColumn);
    }

    @RequestMapping(value = "products.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CashboxExchangeProductsResponseDto products(@RequestParam(name = "communityId", required = true) Long communityId,
                                                       @RequestParam(name = "exchangeIds", required = false) List<Long> exchangeIds,
                                                       @RequestParam(name = "page", required = true) Integer page,
                                                       @RequestParam(name = "size", required = true) Integer size,
                                                       @RequestParam(name = "sortDirection", required = false) String sortDirection,
                                                       @RequestParam(name = "sortColumn", required = false) String sortColumn) {
        if (!communitiesService.hasPermission(communityId, SecurityUtils.getUser().getId(), EcoAdvisorService.ECO_ADVISOR_USER_PERMISSION))
            throw new AccessDeniedException("Доступ запрещен");
        if (exchangeIds == null) exchangeIds = new ArrayList<>();
        return cashboxService.getProducts(communityId, exchangeIds, page, size, sortDirection, sortColumn);
    }

    @RequestMapping(value = "closeSession.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CashboxOperatorSessionDto closeSession(@RequestParam(name = "communityId", required = true) Long communityId,
                                                  @RequestParam(name = "sessionId", required = true) Long sessionId) {
        if (!communitiesService.hasPermission(communityId, SecurityUtils.getUser().getId(), EcoAdvisorService.ECO_ADVISOR_USER_PERMISSION))
            throw new AccessDeniedException("Доступ запрещен");
        return cashboxService.closeOperatorSession(communityId, sessionId);
    }

    @RequestMapping(value = "store.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public StoreResponseDto store(@RequestParam(name = "communityId", required = true) Long communityId,
                                  @RequestParam(name = "page", required = true) Integer page,
                                  @RequestParam(name = "size", required = true) Integer size,
                                  @RequestParam(name = "sortDirection", required = false) String sortDirection,
                                  @RequestParam(name = "sortColumn", required = false) String sortColumn,
                                  @RequestParam(name = "productGroupId", required = false) Long productGroupId) {
        if (!communitiesService.hasPermission(communityId, SecurityUtils.getUser().getId(), EcoAdvisorService.ECO_ADVISOR_USER_PERMISSION))
            throw new AccessDeniedException("Доступ запрещен");
        return cashboxService.getProductsFromStore(communityId, page, size, sortDirection, sortColumn, productGroupId);
    }

    @RequestMapping(value = "bonusAllocations.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BonusAllocationDto> bonusAllocations(@RequestParam(name = "communityId", required = true) Long communityId) throws CashboxException {
        if (!communitiesService.hasPermission(communityId, SecurityUtils.getUser().getId(), EcoAdvisorService.ECO_ADVISOR_USER_PERMISSION))
            throw new AccessDeniedException("Доступ запрещен");
        return ecoAdvisorService.getBonusAllocationDtos(communityId).stream().map(BonusAllocationDto::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "bonusAllocations.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveBonusAllocations(@RequestParam(name = "communityId", required = true) Long communityId,
                                     @RequestBody List<BonusAllocationDto> bonusAllocations) throws CashboxException {
        if (!communitiesService.hasPermission(communityId, SecurityUtils.getUser().getId(), EcoAdvisorService.ECO_ADVISOR_USER_PERMISSION))
            throw new AccessDeniedException("Доступ запрещен");
        ecoAdvisorService.saveBonusAllocations(communityId, bonusAllocations);
    }

    @RequestMapping(value = "advisorSettings.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public AdvisorSettingsDto advisorSettings(@RequestParam(name = "communityId", required = true) Long communityId,
                                              @RequestParam(name = "productGroupId", required = false) Long productGroupId) throws CashboxException {
        if (!communitiesService.hasPermission(communityId, SecurityUtils.getUser().getId(), EcoAdvisorService.ECO_ADVISOR_USER_PERMISSION))
            throw new AccessDeniedException("Доступ запрещен");
        return ecoAdvisorService.getAdvisorSettings(communityId, productGroupId);
    }

    @RequestMapping(value = "advisorSettings.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveAdvisorSettings(@RequestParam(name = "communityId", required = true) Long communityId,
                                    @RequestParam(name = "productGroupId", required = false) Long productGroupId,
                                    @RequestBody AdvisorSettingsDto advisorSettings) throws CashboxException {
        if (!communitiesService.hasPermission(communityId, SecurityUtils.getUser().getId(), EcoAdvisorService.ECO_ADVISOR_USER_PERMISSION))
            throw new AccessDeniedException("Доступ запрещен");
        ecoAdvisorService.saveAdvisorSettings(communityId, advisorSettings, productGroupId);
    }

    @RequestMapping(value = "export2excel.xlsx", method = RequestMethod.POST, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void export2excel(HttpServletResponse response, @RequestBody Map<String, Object> advisorData) throws Exception {
        //new FileInputStream("C:/projects/ecoadvisor_template.xlsx")
        try (InputStream source = getClass().getClassLoader().getResourceAsStream("xlsx/ecoadvisor_template.xlsx");
             CountingOutputStream destination = new CountingOutputStream(response.getOutputStream())) {
            Workbook workbook = new XSSFWorkbook(OPCPackage.open(source));
            Sheet sheet = workbook.getSheetAt(0);

            Map<String, Number> settings = (Map<String, Number>) advisorData.get("settings");
            //Map<String, Number> data = (Map<String, Number>) advisorData.get("data");
            Map<String, Number> report1data = ((Map<String, Map>) advisorData.get("reports")).get("report1");
            Map<String, Number> report2data = ((Map<String, Map>) advisorData.get("reports")).get("report2");
            List<Number> chart1data = ((List<List>) ((Map<String, Object>) ((Map<String, Object>) advisorData.get("charts")).get("chart1")).get("data")).get(0);
            List<Number> chart2data = ((List<List>) ((Map<String, Object>) ((Map<String, Object>) advisorData.get("charts")).get("chart2")).get("data")).get(0);

            int value_cell_index = 4;

            // SETTINGS

            sheet.getRow(1).getCell(value_cell_index).setCellValue(settings.get("generalRunningCosts").doubleValue() / 100);
            sheet.getRow(2).getCell(value_cell_index).setCellValue(settings.get("wage").doubleValue() / 100);
            sheet.getRow(3).getCell(value_cell_index).setCellValue(settings.get("vat").doubleValue() / 100);
            sheet.getRow(4).getCell(value_cell_index).setCellValue(settings.get("taxOnProfits").doubleValue() / 100);
            sheet.getRow(5).getCell(value_cell_index).setCellValue(settings.get("incomeTax").doubleValue() / 100);
            sheet.getRow(6).getCell(value_cell_index).setCellValue(settings.get("proprietorshipInterest").doubleValue() / 100);
            sheet.getRow(7).getCell(value_cell_index).setCellValue(settings.get("taxOnDividends").doubleValue() / 100);

            sheet.getRow(13).getCell(value_cell_index).setCellValue(settings.get("companyProfit").doubleValue() / 100);

            sheet.getRow(16).getCell(value_cell_index).setCellValue(settings.get("margin").doubleValue() / 100);

            //"shareValue" -> "0"
            //"departmentPart" -> "100"

            // DATA

            // REPORT 1

            sheet.getRow(19).getCell(value_cell_index).setCellValue(report1data.get("directCosts").doubleValue());
            sheet.getRow(20).getCell(value_cell_index).setCellValue(report1data.get("finalCosts").doubleValue());

            sheet.getRow(25).getCell(value_cell_index).setCellValue(report1data.get("generalRunningCosts").doubleValue());
            sheet.getRow(26).getCell(value_cell_index).setCellValue(report1data.get("vatBase").doubleValue());
            sheet.getRow(27).getCell(value_cell_index).setCellValue(report1data.get("vat").doubleValue());

            sheet.getRow(30).getCell(value_cell_index).setCellValue(report1data.get("wage").doubleValue());
            sheet.getRow(31).getCell(value_cell_index).setCellValue(report1data.get("totalCosts").doubleValue());
            sheet.getRow(32).getCell(value_cell_index).setCellValue(report1data.get("taxOnProfitsBase").doubleValue());
            sheet.getRow(33).getCell(value_cell_index).setCellValue(report1data.get("taxOnProfits").doubleValue());
            sheet.getRow(34).getCell(value_cell_index).setCellValue(report1data.get("taxes").doubleValue());
            sheet.getRow(35).getCell(value_cell_index).setCellValue(report1data.get("netProfit").doubleValue());

            sheet.getRow(38).getCell(value_cell_index).setCellValue(report1data.get("companyProfit").doubleValue());
            sheet.getRow(39).getCell(value_cell_index).setCellValue(report1data.get("dividends").doubleValue());

            sheet.getRow(42).getCell(value_cell_index).setCellValue(report1data.get("taxOnDividends").doubleValue());
            sheet.getRow(43).getCell(value_cell_index).setCellValue(report1data.get("proprietorProfit").doubleValue());

            sheet.getRow(45).getCell(value_cell_index).setCellValue(report1data.get("total").doubleValue());

            // REPORT 2

            sheet.getRow(51).getCell(value_cell_index).setCellValue(report2data.get("minShareValue").doubleValue());
            sheet.getRow(52).getCell(value_cell_index).setCellValue(report2data.get("maxShareValue").doubleValue());
            sheet.getRow(53).getCell(value_cell_index).setCellValue(report2data.get("shareValue").doubleValue());
            sheet.getRow(54).getCell(value_cell_index).setCellValue(report2data.get("minShareValue").doubleValue());
            sheet.getRow(55).getCell(value_cell_index).setCellValue(report2data.get("maxShareValue").doubleValue());
            sheet.getRow(56).getCell(value_cell_index).setCellValue(report2data.get("shareValue").doubleValue());
            sheet.getRow(57).getCell(value_cell_index).setCellValue(report2data.get("companyProfit").doubleValue());

            sheet.getRow(60).getCell(value_cell_index).setCellValue(report2data.get("vat").doubleValue());

            sheet.getRow(62).getCell(value_cell_index).setCellValue(report2data.get("taxOnProfitsBase").doubleValue());
            sheet.getRow(63).getCell(value_cell_index).setCellValue(report2data.get("taxOnProfits").doubleValue());

            sheet.getRow(65).getCell(value_cell_index).setCellValue(report2data.get("proprietorPartInCooperative").doubleValue());
            sheet.getRow(66).getCell(value_cell_index).setCellValue(report2data.get("taxOnCompanyProfits").doubleValue());

            sheet.getRow(69).getCell(value_cell_index).setCellValue(report2data.get("cooperativeAmount").doubleValue());
            sheet.getRow(70).getCell(value_cell_index).setCellValue(report2data.get("taxOnProprietorIncome").doubleValue());
            sheet.getRow(71).getCell(value_cell_index).setCellValue(report2data.get("proprietorAndCompanyBonus").doubleValue());
            sheet.getRow(72).getCell(value_cell_index).setCellValue(report2data.get("effect").doubleValue());

            sheet.getRow(75).getCell(value_cell_index).setCellValue(report2data.get("effect").doubleValue());

            sheet.getRow(77).getCell(value_cell_index).setCellValue(report2data.get("consumerBonus").doubleValue());
            sheet.getRow(78).getCell(value_cell_index).setCellValue(report2data.get("cooperativeBonus").doubleValue());
            sheet.getRow(79).getCell(value_cell_index).setCellValue(report2data.get("proprietorAndCompanyBonus").doubleValue());

            sheet.getRow(82).getCell(value_cell_index).setCellValue(report2data.get("finalPriceForConsumer").doubleValue());
            sheet.getRow(82).getCell(value_cell_index + 2).setCellValue(report2data.get("consumerBonus").doubleValue());
            sheet.getRow(82).getCell(value_cell_index + 6).setCellValue(report2data.get("differenceInPercentsForConsumer").doubleValue() / 100);
            sheet.getRow(83).getCell(value_cell_index).setCellValue(report2data.get("cooperativeBonus").doubleValue());
            sheet.getRow(84).getCell(value_cell_index).setCellValue(report2data.get("totalProprietorAndCompanyBonus").doubleValue());
            sheet.getRow(84).getCell(value_cell_index + 2).setCellValue(report1data.get("total").doubleValue());
            sheet.getRow(84).getCell(value_cell_index + 6).setCellValue(report2data.get("differenceInPercents").doubleValue() / 100);

            // CHART 1

            sheet.getRow(102).getCell(0).setCellValue(chart1data.get(0).doubleValue() / 100);
            sheet.getRow(102).getCell(1).setCellValue(chart1data.get(1).doubleValue() / 100);

            // CHART 2

            sheet.getRow(88).getCell(1).setCellValue(chart2data.get(0).doubleValue() / 100);
            sheet.getRow(89).getCell(1).setCellValue(chart2data.get(1).doubleValue() / 100);
            sheet.getRow(90).getCell(1).setCellValue(chart2data.get(2).doubleValue() / 100);
            sheet.getRow(91).getCell(1).setCellValue(chart2data.get(3).doubleValue() / 100);
            sheet.getRow(92).getCell(1).setCellValue(chart2data.get(4).doubleValue() / 100);
            sheet.getRow(93).getCell(1).setCellValue(chart2data.get(5).doubleValue() / 100);
            sheet.getRow(94).getCell(1).setCellValue(chart2data.get(6).doubleValue() / 100);
            sheet.getRow(95).getCell(1).setCellValue(chart2data.get(7).doubleValue() / 100);
            sheet.getRow(96).getCell(1).setCellValue(chart2data.get(8).doubleValue() / 100);
            sheet.getRow(97).getCell(1).setCellValue(chart2data.get(9).doubleValue() / 100);
            sheet.getRow(98).getCell(1).setCellValue(chart2data.get(10).doubleValue() / 100);

            // save sheet

            workbook.write(destination);

            response.setHeader("Content-Disposition", "attachment; filename=report.xlsx");
            response.setHeader("Content-Length", String.valueOf(destination.getByteCount()));
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Pragma", "public");
        } catch (InvalidFormatException | IOException e) {
            throw new Exception("Невозможно сгенерировать отчет. Обратитесь к администратору.");
        }
    }

    @RequestMapping(value = "productGroups.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProductGroupDto> productGroups(@RequestParam(name = "communityId", required = true) Long communityId) throws CashboxException {
        if (!communitiesService.hasPermission(communityId, SecurityUtils.getUser().getId(), EcoAdvisorService.ECO_ADVISOR_USER_PERMISSION))
            throw new AccessDeniedException("Доступ запрещен");
        return ecoAdvisorService.getProductGroups(communityId);
    }

    @RequestMapping(value = "deleteProductGroup.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteProductGroup(@RequestParam(name = "communityId", required = true) Long communityId,
                                     @RequestParam(name = "groupId", required = true) Long groupId) throws CashboxException {
        if (!communitiesService.hasPermission(communityId, SecurityUtils.getUser().getId(), EcoAdvisorService.ECO_ADVISOR_USER_PERMISSION))
            throw new AccessDeniedException("Доступ запрещен");

        try {
            ecoAdvisorService.deleteProductGroup(communityId, groupId);
        } catch (Throwable e) {
            throw new CashboxException("Невозможно удалить группу. Убедитесь что в ней нет продуктов.");
        }
    }

    @RequestMapping(value = "saveProductGroup.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductGroupDto saveProductGroup(@RequestParam(name = "communityId", required = true) Long communityId,
                                            @RequestBody ProductGroupDto group) throws CashboxException {
        if (!communitiesService.hasPermission(communityId, SecurityUtils.getUser().getId(), EcoAdvisorService.ECO_ADVISOR_USER_PERMISSION))
            throw new AccessDeniedException("Доступ запрещен");
        return ecoAdvisorService.saveProductGroup(communityId, group);
    }

    @RequestMapping(value = "setProductsGroup.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void setProductsGroup(@RequestParam(name = "communityId", required = true) Long communityId,
                                   @RequestParam(name = "groupId", required = true) Long groupId,
                                   @RequestBody List<Long> productIds) {
        if (!communitiesService.hasPermission(communityId, SecurityUtils.getUser().getId(), EcoAdvisorService.ECO_ADVISOR_USER_PERMISSION))
            throw new AccessDeniedException("Доступ запрещен");
        ecoAdvisorService.setProductGroup(communityId, groupId, productIds);
    }

    @RequestMapping(value = "resetProductsGroup.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void resetProductsGroup(@RequestParam(name = "communityId", required = true) Long communityId,
                                     @RequestBody List<Long> productIds) {
        if (!communitiesService.hasPermission(communityId, SecurityUtils.getUser().getId(), EcoAdvisorService.ECO_ADVISOR_USER_PERMISSION))
            throw new AccessDeniedException("Доступ запрещен");
        ecoAdvisorService.resetProductGroup(communityId, productIds);
    }
}
