function initCertificationPage(params) {
	
	function getPersonalDataBlock() {
		return $("div#personal-data-block");
	}
	
	function initPersonalDataStage() {
		
		$.each(getPersonalDataBlock().find("input[data-field-type=DATE]"), function(index, input){
			$(input).radomDateInput({
				startView : 2
			});
		});
		
		getPersonalDataBlock().find("[data-field-name=LASTNAME]").capitalizeInput();
		getPersonalDataBlock().find("[data-field-name=FIRSTNAME]").capitalizeInput();
		getPersonalDataBlock().find("[data-field-name=SECONDNAME]").capitalizeInput();
		
		getPersonalDataBlock().find("[data-field-name=PASSPORT_SERIAL]").mask("99 99", {placeholder:"_"} ).attr("placeholder", "__ __");
		getPersonalDataBlock().find("[data-field-name=PASSPORT_NUMBER]").mask("999999", {placeholder:"_"} ).attr("placeholder", "______");
		getPersonalDataBlock().find("[data-field-name=PASSPORT_DIVISION]").mask("999-999", {placeholder:"_"} ).attr("placeholder", "___-___");
		getPersonalDataBlock().find("[data-field-name=PERSON_INN]").mask("999999999999", {placeholder:"_"} ).attr("placeholder", "____________");

		getPersonalDataBlock().find('input[data-field-type="REGION"]').attr("data-kladr-object-type", "region");
		getPersonalDataBlock().find('input[data-field-type="DISTRICT"]').attr("data-kladr-object-type", "district");
		getPersonalDataBlock().find('input[data-field-type="CITY"]').attr("data-kladr-object-type", "city");
		getPersonalDataBlock().find('input[data-field-type="STREET"]').attr("data-kladr-object-type", "street");
		getPersonalDataBlock().find('input[data-field-type="BUILDING"]').attr("data-kladr-object-type", "building");
		
        var $addressBlock = getPersonalDataBlock().find("[data-group-name=PERSON_REGISTRATION_ADDRESS]");
        var groupName = $addressBlock.attr("data-group-name");
        $addressBlock.find(".panel-body").append("<div class='row'><div class='col-xs-12' id='" + groupName + "_MAP' style='height : 300px;'></div></div>")
        $.radomKladr($addressBlock, $addressBlock.find("input[data-field-type=GEO_LOCATION]"), $addressBlock.find("input[data-field-type=GEO_POSITION]"), $addressBlock.find("div#" + groupName + "_MAP"));
        		
	}
	
	initPersonalDataStage();
	
}

$(document).ready(function() {
	initCertificationPage();
});