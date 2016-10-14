package ru.radom.kabinet.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.radom.kabinet.model.ProgressInfo;
import ru.radom.kabinet.services.AdminDataService;
import ru.radom.kabinet.utils.JsonUtils;

@Controller
public class AdminDataController {

    @Autowired
    private AdminDataService adminDataService;

    @RequestMapping(value = "/admin/data", method = RequestMethod.GET)
    public String showAdminDataPage(Model model) {
        return "adminDataPage";
    }

    // Фикс для координат

    @RequestMapping(value = "/admin/data/repairGeoPositions.json", method = RequestMethod.POST)
    @ResponseBody
    public String repairGeoPositions() {
        adminDataService.repairGeoPositions();
        return JsonUtils.getSuccessJson().toString();
    }

    @RequestMapping(value = "/admin/data/repairGeoPositionsStatus.json", method = RequestMethod.POST)
    @ResponseBody
    public ProgressInfo repairGeoPositionsStatus() {
        return adminDataService.getRepairGeoPositionsStatus();
    }

    // Фактические адреса

    @RequestMapping(value = "/admin/data/repairActualAddresses.json", method = RequestMethod.POST)
    @ResponseBody
    public String repairActualAddresses() {
        adminDataService.repairActualAddresses();
        return JsonUtils.getSuccessJson().toString();
    }

    @RequestMapping(value = "/admin/data/repairActualAddressesStatus.json", method = RequestMethod.POST)
    @ResponseBody
    public ProgressInfo repairActualAddressesStatus() {
        return adminDataService.getRepairActualAddressesStatus();
    }

    // Регистрационные адреса

    @RequestMapping(value = "/admin/data/repairRegistrationAddresses.json", method = RequestMethod.POST)
    @ResponseBody
    public String repairRegistrationAddresses() {
        adminDataService.repairRegistrationAddresses();
        return JsonUtils.getSuccessJson().toString();
    }

    @RequestMapping(value = "/admin/data/repairRegistrationAddressesStatus.json", method = RequestMethod.POST)
    @ResponseBody
    public ProgressInfo repairRegistrationAddressesStatus() {
        return adminDataService.getRepairRegistrationAddressesStatus();
    }

    // Адрес офиса регистратора

    @RequestMapping(value = "/admin/data/repairRegistratorAddresses.json", method = RequestMethod.POST)
    @ResponseBody
    public String repairRegistratorAddresses() {
        adminDataService.repairRegistratorAddresses();
        return JsonUtils.getSuccessJson().toString();
    }

    @RequestMapping(value = "/admin/data/repairRegistratorAddressesStatus.json", method = RequestMethod.POST)
    @ResponseBody
    public ProgressInfo repairRegistratorAddressesStatus() {
        return adminDataService.getRepairRegistratorAddressesStatus();
    }

    // Адрес объединения(фактический)

    @RequestMapping(value = "/admin/data/repairCommunityActualAddresses.json", method = RequestMethod.POST)
    @ResponseBody
    public String repairCommunityActualAddresses() {
        adminDataService.repairCommunityActualAddresses();
        return JsonUtils.getSuccessJson().toString();
    }

    @RequestMapping(value = "/admin/data/repairCommunityActualAddressesStatus.json", method = RequestMethod.POST)
    @ResponseBody
    public ProgressInfo repairCommunityActualAddressesStatus() {
        return adminDataService.getRepairCommunityActualAddressesStatus();
    }

    // Адрес объединения(регистрационный)

    @RequestMapping(value = "/admin/data/repairCommunityRegistrationAddresses.json", method = RequestMethod.POST)
    @ResponseBody
    public String repairCommunityRegistrationAddresses() {
        adminDataService.repairCommunityRegistrationAddresses();
        return JsonUtils.getSuccessJson().toString();
    }

    @RequestMapping(value = "/admin/data/repairCommunityRegistrationAddressesStatus.json", method = RequestMethod.POST)
    @ResponseBody
    public ProgressInfo repairCommunityRegistrationAddressesStatus() {
        return adminDataService.getRepairCommunityRegistrationAddressesStatus();
    }


}
