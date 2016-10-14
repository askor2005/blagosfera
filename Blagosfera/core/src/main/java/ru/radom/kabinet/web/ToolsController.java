package ru.radom.kabinet.web;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.radom.kabinet.tools.EksProcessor;
import ru.radom.kabinet.web.utils.Breadcrumb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/tools")
public class ToolsController {

	@Autowired
	private EksProcessor eksProcessor;

	@RequestMapping(value = "/eks", method = RequestMethod.GET)
	public String showEksPage(Model model) {
		model.addAttribute("breadcrumb", new Breadcrumb().add("РаМЕРА", "/").add("/tools", "#").add("ЭКС", "/tools/eks"));
		return "toolsEks";
	}

	@RequestMapping(value = "/eks", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public @ResponseBody String uploadEksFile(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (!ServletFileUpload.isMultipartContent(request)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(1024 * 1024);
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<FileItem> items = upload.parseRequest(request);
			String data = null;
			for (FileItem item : items) {
				if (!item.isFormField()) {
					data = item.getString("UTF-8");
					break;
				}
			}
			return eksProcessor.process(data);
		} catch (Exception e) {
			return null;
		}
	}
}
