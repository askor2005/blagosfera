var Accounts = {
		
		refresh : function() {
			$.radomJsonGet("/account/list.json", {}, function(response) {
				$(radomEventsManager).trigger("accounts.refresh", response);
			});
		}
		
};