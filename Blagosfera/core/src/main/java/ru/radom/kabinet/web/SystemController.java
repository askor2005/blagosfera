package ru.radom.kabinet.web;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;
import java.util.Date;

@Controller
@RequestMapping("/system")
public class SystemController {

	@RequestMapping("/sessions")
	public String showSessionsPage(Model model, @RequestParam(value = "from_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate, @RequestParam(value = "to_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate) {
		if (toDate == null) {
			toDate = new Date();
		}
		if (fromDate == null) {
			Calendar startDateCalendar = Calendar.getInstance();
			startDateCalendar.setTime(toDate);
			startDateCalendar.add(Calendar.MONTH, -1);
			fromDate = startDateCalendar.getTime();
		}
		model.addAttribute("fromDate", fromDate);
		model.addAttribute("toDate", toDate);
		return "systemSessionsList";
	}

	@RequestMapping(value = "/sessions.json", method = RequestMethod.GET)
	public @ResponseBody String getSessionsList(@RequestParam(value = "last_loaded_id", defaultValue = "-1") Long LoginLogEntryLastLoadedId, @RequestParam(value = "success", required = false) Boolean success, @RequestParam(value = "idle", required = false) Boolean idle, @RequestParam(value = "closed", required = false) Boolean closed, @RequestParam(value = "query", required = false) String query, @RequestParam(value = "per_page", defaultValue = "20") int perPage, @RequestParam(value = "from_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate, @RequestParam(value = "to_date", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate) {
		return null;
	}
}
