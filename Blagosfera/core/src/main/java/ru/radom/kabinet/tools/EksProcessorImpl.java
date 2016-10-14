package ru.radom.kabinet.tools;

import org.springframework.stereotype.Component;

@Component
public class EksProcessorImpl implements EksProcessor {

	@Override
	public String process(String data) {
		return data;
	}

}
