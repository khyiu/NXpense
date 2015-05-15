package nxpense.dto;


import java.io.Serializable;
import java.math.BigDecimal;

public class BalanceInfoDTO implements Serializable {

    private static final long serialVersionUID = 5854550725690276307L;

    private BigDecimal verified;
    private BigDecimal nonVerified;
    private BigDecimal global;

    public BalanceInfoDTO() {

    }

    public BalanceInfoDTO(BigDecimal verified, BigDecimal nonVerified, BigDecimal global) {
        this.verified = verified;
        this.nonVerified = nonVerified;
        this.global = global;
    }

    public BigDecimal getVerified() {
        return this.verified;
    }

    public BigDecimal getNonVerified() {
        return this.nonVerified;
    }

    public BigDecimal getGlobal() {
        return this.global;
    }
}
