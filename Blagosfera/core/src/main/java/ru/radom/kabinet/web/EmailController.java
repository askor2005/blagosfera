package ru.radom.kabinet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import ru.radom.kabinet.model.EmailTemplate;
import ru.radom.kabinet.services.EmailService;

/**
 *
 * @author dfilinberg
 */
@Controller
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @RequestMapping
    public String view(Model model) {
        model.addAttribute("templates", emailService.findTemplates());
        return "emailView";
    }

    @RequestMapping(value = "/edit")
    public ModelAndView edit(@RequestParam(value = "id", required = false) Long id) {
        EmailTemplate template = emailService.getTemplateById(id);
        if (template == null) {
            template = new EmailTemplate();
        }
        return new ModelAndView("emailEdit", "command", template);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public RedirectView save(EmailTemplate template) {
        emailService.editTemplate(template);
        return new RedirectView("/email/edit?id=" + template.getId());
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) {
        emailService.deleteTemplate(id);
    }
    
}
