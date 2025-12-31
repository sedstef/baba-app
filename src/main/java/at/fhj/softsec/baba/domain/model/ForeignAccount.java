package at.fhj.softsec.baba.domain.model;

import java.math.BigDecimal;

public interface ForeignAccount extends AccountView {

    void transferIn(AccountView source, BigDecimal amount);
}
