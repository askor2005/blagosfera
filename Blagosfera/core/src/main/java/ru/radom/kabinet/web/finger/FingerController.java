package ru.radom.kabinet.web.finger;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.domain.certification.UserCertificationSession;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.settings.SharerSettingDao;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.bio.FingerToken;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.bio.TokenProtected;
import ru.radom.kabinet.services.UserCertificationManager;
import ru.radom.kabinet.services.finger.FingerException;
import ru.radom.kabinet.services.finger.FingerService;
import ru.radom.kabinet.web.finger.dto.FingersDto;
import ru.radom.kabinet.web.finger.dto.InitByIkpRequestDto;
import ru.radom.kabinet.web.finger.dto.RequestDto;
import ru.radom.kabinet.web.finger.dto.ResponseDto;
import ru.radom.kabinet.web.ras.dto.ErrorDto;
import ru.radom.kabinet.web.ras.dto.MoreDto;
import ru.radom.kabinet.web.ras.dto.TokenDto;
import ru.radom.kabinet.web.utils.Breadcrumb;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Controller
public class FingerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FingerController.class);

    @Autowired
    private FingerService fingerService;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private SharerSettingDao sharerSettingDao;

    @Autowired
    private UserCertificationManager userCertificationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    public FingerController() {
    }

    @RequestMapping(value = "/finger/instruction", method = RequestMethod.GET)
    public String showInstructionPage(Model model) {
        model.addAttribute("breadcrumb", new Breadcrumb().add("Благосфера", "/").add("Инструкция по установке и запуску сервера авторизации", "/finger/instruction"));
        return "fingerInstruction";
    }

    @ResponseBody
    @RequestMapping(value = "/finger/inittoken.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestDto initToken(HttpServletRequest request, @RequestParam(name = "ikp", required = false) String ikp) throws Exception {
        FingerToken token = null;

        if (ikp != null) {
            UserEntity userEntity = sharerDao.getByIkp(ikp);
            if (userEntity == null) throw new Exception("Участник не найден");

            token = fingerService.initToken(userEntity.getId(), request.getRemoteAddr());
        } else {
            token = fingerService.initToken(SecurityUtils.getUser().getId(), request.getRemoteAddr());
        }

        RequestDto result = new RequestDto();
        result.requestId = token.getRequestId();
        result.finger = token.getFinger();
        result.ikp = ikp != null ? ikp : SecurityUtils.getUser().getIkp();
        result.secondsLeft = fingerService.getSecondsLeft(token);
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/finger/initTokenByEmail.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestDto initTokenByEmail(HttpServletRequest request, @RequestParam(name = "u") String username) {
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
        FingerToken fingerToken = fingerService.initToken(userDetails.getUser().getId(), request.getRemoteAddr());
        RequestDto requestDto = new RequestDto();
        requestDto.requestId = fingerToken.getRequestId();
        requestDto.finger = fingerToken.getFinger();
        requestDto.ikp = userDetails.getUser().getIkp();
        return requestDto;
    }

    @ResponseBody
    @RequestMapping(value = "/finger/initTokenByIkp.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public InitByIkpRequestDto initTokenByIkp(HttpServletRequest request,
                                              @RequestParam("ikp") String ikp) throws Exception {
        UserEntity userEntity = sharerDao.getByIkp(ikp);
        if (userEntity == null) throw new Exception("Участник не найден");

        FingerToken fingerToken = fingerService.initToken(userEntity.getId(), request.getRemoteAddr());

        InitByIkpRequestDto requestDto = new InitByIkpRequestDto();
        requestDto.requestId = fingerToken.getRequestId();
        requestDto.finger = fingerToken.getFinger();
        requestDto.ikp = userEntity.getIkp();
        return requestDto;
    }

    @ResponseBody
    @RequestMapping(value = "/finger/fingers.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public FingersDto getFingers(@RequestParam("ikp") String ikp) {
        FingersDto fingersDto = new FingersDto();
        fingersDto.fingers = sharerSettingDao.getIntegersList(sharerDao.getByIkp(ikp), "bio.finger.exists", Collections.EMPTY_LIST);
        return fingersDto;
    }

    @TokenProtected
    @ResponseBody
    @RequestMapping(value = "/finger/deletefinger.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto deleteFinger(@RequestParam("ikp") String ikp, @RequestParam("registrator_ikp") String registratorIkp, @RequestParam("finger") Integer finger) {
        fingerService.deleteFinger(ikp, finger, registratorIkp);
        return new ResponseDto("success");
    }

    @Deprecated
    @ResponseBody
    @RequestMapping(value = "/finger/get.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ru.radom.kabinet.web.ras.dto.ResponseDto getTokenMultipart(HttpServletRequest request) {
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory(1024 * 1024, null)).parseRequest(request);
            String ikp = null;
            String requestId = null;
            byte[] file = null;

            for (FileItem item : items) {
                if (item.isFormField()) {
                    switch (item.getFieldName()) {
                        case "ikp":
                            ikp = item.getString();
                            break;
                        case "request_id":
                            requestId = item.getString();
                            break;
                    }
                } else if ("file".equals(item.getFieldName())) file = item.get();
            }

            return new TokenDto(fingerService.getToken(ikp, requestId, file).getValue());
        } catch (FingerException e) {
            return new ErrorDto(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ErrorDto(null);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/finger/token.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ru.radom.kabinet.web.ras.dto.ResponseDto getToken(@RequestParam("ikp") String ikp, @RequestParam("request_id") String requestId,
                                                             @RequestBody byte[] file) {
        try {
            return new TokenDto(fingerService.getToken(ikp, requestId, file).getValue());
        } catch (FingerException e) {
            return new ErrorDto(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ErrorDto(null);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/finger/verifytoken.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ru.radom.kabinet.web.ras.dto.ResponseDto getSmsToken(@RequestParam("i") String ikp,
                                                                @RequestParam("r") String requestId,
                                                                @RequestParam("c") String code) {
        try {
            return new TokenDto(fingerService.getToken(ikp, requestId, code).getValue());
        } catch (FingerException e) {
            return new ErrorDto(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ErrorDto(null);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/finger/save.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ru.radom.kabinet.web.ras.dto.ResponseDto saveFingerMultipart(HttpServletRequest request) {
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory(1024 * 1024, null)).parseRequest(request);
            byte[] file = null;
            String ikp = null;
            String finger = null;
            String registratorIkp = null;
            String sessionId = null;

            for (FileItem item : items) {
                if (item.isFormField()) {
                    switch (item.getFieldName()) {
                        case "registrator_ikp":
                            registratorIkp = item.getString();
                            break;
                        case "ikp":
                            ikp = item.getString();
                            break;
                        case "finger":
                            finger = item.getString();
                            break;
                        case "session_id":
                            sessionId = item.getString();
                            break;
                    }
                } else if ("finger.bmp".equals(item.getName())) file = item.get();
            }

            if (sessionId != null) {
                UserCertificationSession userCertificationSession = userCertificationManager.getActiveCertificationSession(sessionId);

                if (userCertificationSession != null) {
                    return new MoreDto(fingerService.saveFinger(registratorIkp, ikp, finger, file));
                }
            }

            return new ErrorDto("Сессия идентификации пользователя не найдена или завершена.");
        } catch (FingerException e) {
            return new ErrorDto(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ErrorDto(null);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/finger/savefinger.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ru.radom.kabinet.web.ras.dto.ResponseDto saveFinger(@RequestParam("ikp") String ikp, @RequestParam("registrator_ikp") String registratorIkp,
                                                               @RequestParam("finger") String finger, @RequestParam("session_id") String sessionId,
                                                               @RequestBody byte[] file) {
        try {
            if (sessionId != null) {
                UserCertificationSession userCertificationSession = userCertificationManager.getActiveCertificationSession(sessionId);

                if (userCertificationSession != null) {
                    return new MoreDto(fingerService.saveFinger(registratorIkp, ikp, finger, file));
                }
            }

            return new ErrorDto("Сессия идентификации пользователя не найдена или завершена.");
        } catch (FingerException e) {
            return new ErrorDto(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ErrorDto(null);
        }
    }
}
