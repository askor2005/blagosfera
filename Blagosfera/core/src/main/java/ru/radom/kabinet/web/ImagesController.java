package ru.radom.kabinet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.askor.blagosfera.domain.user.UserDetailsImpl;
import ru.radom.kabinet.model.ImageType;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.image.ImagesService;
import ru.radom.kabinet.services.image.dto.UploadResultDto;
import ru.radom.kabinet.utils.JsonUtils;

import javax.servlet.http.HttpServletRequest;

@Controller("imagesController")
public class ImagesController {

    @Autowired
    private ImagesService imagesService;

    @RequestMapping(value = "/images/{type}/restrictions.json", method = RequestMethod.GET)
    @ResponseBody
    public String getImageRestrictions(@PathVariable("type") ImageType imageType) {
        return imagesService.getImageRestrictions(imageType);
    }

    @RequestMapping(value = "/images/{type}/upload.json", method = RequestMethod.POST)
    @ResponseBody
    public UploadResultDto uploadImage(HttpServletRequest request, @PathVariable("type") ImageType imageType) {
        return new UploadResultDto(imagesService.uploadFromClient(request, imageType));
    }

    @RequestMapping(value = "/images/{type}/upload_url.json", method = RequestMethod.POST)
    @ResponseBody
    public String uploadImageUrl(@RequestParam(value = "url", required = true) String url, @PathVariable("type") ImageType imageType) {
        return imagesService.uploadFromUrl(url, imageType);
    }

    @RequestMapping(value = "/images/{type}/upload_base64.json", method = RequestMethod.POST)
    @ResponseBody
    public String uploadPhotoBase64(@RequestBody String data, @PathVariable("type") ImageType imageType) {
        return imagesService.uploadFromBase64(data, imageType);
    }

    // TODO может получитсья очень нехорошая штука, если составить этот запрос в ручную, то аватарка и фото оригинала могут отличаться
    @RequestMapping(value = "/images/avatar/{object_type}/{object_id}.json", method = RequestMethod.POST)
    @ResponseBody
    public String setAvatar(HttpServletRequest request, @RequestParam(value = "url", required = true) String url, @RequestParam(value = "urlOriginal", required = true) String urlOriginal, @PathVariable(value = "object_type") String objectType, @PathVariable(value = "object_id") Long objectId) {
        return imagesService.setAvatar(url, urlOriginal, objectType, objectId);
    }

    @RequestMapping(value = "/images/crop.json", method = RequestMethod.POST)
    @ResponseBody
    public String crop(@RequestParam("src") String src, @RequestParam("x1") Float x1, @RequestParam("x2") Float x2, @RequestParam("y1") Float y1, @RequestParam("y2") Float y2) {
        return imagesService.cropImage(src, x1, x2, y1, y2);
    }

    @RequestMapping(value = "/images/edit.json", method = RequestMethod.POST)
    @ResponseBody
    public String edit(@RequestParam("src") String src, @RequestParam(value = "angle", required = false, defaultValue = "0") String angle) {
        try {
            int intAngle = Integer.parseInt(angle);
            return imagesService.editImage(src, intAngle);
        } catch (NumberFormatException e) {
            return JsonUtils.getErrorJson("Некорректный формат угла поворота").toString();
        }
    }

    @RequestMapping(value = "/images/sign.json", method = RequestMethod.POST)
    @ResponseBody
    public String setSing(@RequestParam(value = "url", required = true) String url, @RequestParam(value = "sharer_id", required = true) UserEntity userEntity) {
        return imagesService.setSign(url, userEntity);
    }

    @RequestMapping(value = "/images/upload_section_icon.json", method = RequestMethod.POST)
    @ResponseBody
    public String uploadSectionIcon(@RequestParam(value = "url", required = true) String url) {
        return imagesService.uploadSectionIcon(url);
    }

    // --- legacy

    @RequestMapping(value = "/images/upload/{object_type}/{object_id}.json", method = RequestMethod.POST)
    @ResponseBody
    public UploadResultDto uploadPhoto(HttpServletRequest request, @PathVariable("object_type") String objectType, @PathVariable("object_id") Long objectId) {
        return new UploadResultDto(
                imagesService.uploadFromClient(request, objectType, objectId, ImageType.PHOTO)
        );
    }

    @RequestMapping(value = "/images/upload.json", method = RequestMethod.POST)
    @ResponseBody
    public UploadResultDto uploadPhoto(HttpServletRequest request) {
        return new UploadResultDto(imagesService.uploadFromClient(request, ImageType.PHOTO));
    }
}
