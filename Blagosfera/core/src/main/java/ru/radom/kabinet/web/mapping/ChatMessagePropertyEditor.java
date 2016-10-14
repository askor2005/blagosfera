package ru.radom.kabinet.web.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.data.jpa.repositories.ChatMessageRepository;

import java.beans.PropertyEditorSupport;

@Component("chatMessagePropertyEditor")
public class ChatMessagePropertyEditor extends PropertyEditorSupport {

	@Autowired
    private ChatMessageRepository chatMessageRepository;

	@Override
	public void setAsText(String text) {
		setValue(chatMessageRepository.findOne(Long.parseLong(text)));
	}
}
