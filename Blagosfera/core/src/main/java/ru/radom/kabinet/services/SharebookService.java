package ru.radom.kabinet.services;

import java.math.BigDecimal;

public interface SharebookService {

    BigDecimal getCommunitySharebooksTotalBalance(Long communityId);
}
