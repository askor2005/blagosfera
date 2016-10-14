<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

<script id="requests-list-item-template" type="x-tmpl-mustache">
<div class="row request-item request-item-{{request.status}} " data-object-id="{{object.id}}" class="vertical-align: middle;">
    <div class="col-xs-1 registrator-item-link-text" style="line-height: 48px; text-align: right;">
        {{request.id}}
	</div>
    <div class="col-xs-2 my-request-item-date" style="text-align: right;">

            <div style="padding-top: 10px; line-height: 15px;">{{request.createdDate}}<br/> в {{request.createdTime}}</div>
	</div>
	<div class="col-xs-2">
		<div class="row">
			<div class="col-xs-12">
				<a class="object-item-avatar-link" href="{{object.link}}">
					<img src="{{resizedAvatar}}" class="img-thumbnail" style="height:60px;width:60px;{{#object.verified}}background-color : rgb(51, 142, 207);{{/object.verified}}" />
				</a>
			</div>
		</div>
	</div>
	<div class="col-xs-7">
		<div class="row">
		    <div class="col-xs-8" style="line-height: 48px;">
			    <a href="{{object.link}}" class="registrator-item-link-text">{{object.name}}</a>
			     {{#object.actualCountry}}
			    <div class="registrator-item-link-text">
			     {{object.actualCountry}}{{#object.actualCity}}, {{object.actualCity}}{{/object.actualCity}}
			    </div>
			    {{/object.actualCountry}}
			</div>
			<div class="col-xs-4" style="font-size: 18px; line-height: 48px;">
                <a href="#" class="fa fa-info-circle go-to-info" data-title="Посмотреть детали" style="text-decoration : none !important;"></a>
    	        <a href="javascript:void(0);" onclick="ChatView.showDialogWithSharer('{{requestOwnerId}}');" class="fa fa-comments-o go-to-chat" data-title="Написать сообщение" style="text-decoration : none !important;"></a>
    	        {{#isNew}}<a href="#" class="fa  fa-ban do-cancel-request" data-title="Отклонить заявку" style="text-decoration : none !important;"></a>{{/isNew}}
		        {{#isNew}}<a href="#" class="fa fa-user-plus do-process-request" data-title="Обработать заявку" style="text-decoration : none !important;"></a>{{/isNew}}
			</div>
		</div>
	</div>
</div>
</script>

<script type="text/javascript">
	
var $searchInput = null;
	
function initSearchInput($input, callback) {
    $searchInput = $input;
    $input.keyup(function () {

        var timeout = $input.data("timeout");
        if (timeout) {
            clearTimeout(timeout);
        }
        timeout = setTimeout(function () {
            var newValue = $input.val();
            var oldValue = $input.data("old-value");
            if (newValue != oldValue) {
                if ((newValue.length >= 4) || (newValue.length == 0)) {
                    $input.data("old-value", newValue);
                    callback(newValue);
                }
            }
        }, 300);
        $input.data("timeout", timeout);
    });
}

var registratorsListItemTemplate = $('#requests-list-item-template').html();
Mustache.parse(registratorsListItemTemplate);
	
var groups = null;


function getRequestMarkup(request) {
	
	var object = request.object;

	var model = {};

    if (request.objectType == "SHARER") {
        object.name = object.fullName;
    }

    model.requestOwnerId = request.requestOwnerId;
	model.object = object;
    model.request = request;
    model.isNew = (request.status == 'NEW');
	model.resizedAvatar = Images.getResizeUrl(object.avatar, "c60");
	
	var rendered = Mustache.render(registratorsListItemTemplate, model);
	
	var $row = $(rendered);

	$row.data("object", object);
	
	$row.find("span.request-distance").radomTooltip({
		placement : "top",
		container : "body"
	});
	
	$row.find("a.go-to-info, a.go-to-chat, a.do-cancel-request, a.do-process-request").radomTooltip({
		placement : "top",
		container : "body"
	});
	
    $row.find("a.do-process-request").click(function() {
        ProcessCertificationRequestDialog.show(request);
        return false;
    });

    $row.find("a.go-to-info").click(function() {
        ViewCertificationRequestDialog.show(request);
        return false;
    });

    $row.find("a.do-cancel-request").click(function() {
        CancelCertificationRequestDialog.show(request);
        return false;
    });

    return $row;
}

</script>