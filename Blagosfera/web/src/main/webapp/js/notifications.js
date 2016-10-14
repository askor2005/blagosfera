Notifications = {
	
	getPriorityName : function(notification) {
		switch (notification.priority) {
		case "LOW":
			return "Низкий";
			break;
		case "NORMAL":
			return "Обычный";
			break;
		case "HIGH":
			return "Высокий";
			break;
		case "CRITICAL":
			return "Критический";
			break;
		case "BLOCKING":
			return "Блокирующий";
			break;
		default:
			return "";
			break;
		}
	}
	
}