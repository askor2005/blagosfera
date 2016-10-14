package ru.askor.blagosfera.web.controllers.ng.ecoadvisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.askor.blagosfera.core.services.account.AccountService;
import ru.askor.blagosfera.domain.cashbox.CashboxException;
import ru.radom.kabinet.services.EcoAdvisorService;
import ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto.AdvisorSettingsDto;

@PreAuthorize("hasRole('ECO_ADVISOR_ADMIN')")
@RestController
@RequestMapping("/api/ecoadvisor/admin")
public class EcoAdvisorAdminUIController {

    @Autowired
    private EcoAdvisorService ecoAdvisorService;

    @Autowired
    private AccountService accountService;

    public EcoAdvisorAdminUIController() {
    }

    @RequestMapping(value = "advisorSettings.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public AdvisorSettingsDto advisorSettings() throws CashboxException {
        return ecoAdvisorService.getAdvisorSettings(0L, null);
    }

    @RequestMapping(value = "advisorSettings.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveAdvisorSettings(@RequestBody AdvisorSettingsDto advisorSettings) throws CashboxException {
        ecoAdvisorService.saveAdvisorSettings(0L, advisorSettings, null);
    }
}
