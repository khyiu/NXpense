package nxpense.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "USR")
@SequenceGenerator(name = "USER_SEQ", sequenceName = "user_seq")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="USER_SEQ")
    private Integer id;

    private String email;

    private char[] password;

    @Column(name = "REMAINING_LOGON_ATTEMPT")
    private Integer remainingLogonAttempt;

    private char[] temporaryPassword;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ID")
    private UserAccount userAccount;

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public char[] getPassword() {
	return password;
    }

    public void setPassword(char[] password) {
	this.password = password;
    }

    public Integer getRemainingLogonAttempt() {
	return remainingLogonAttempt;
    }

    public void setRemainingLogonAttempt(Integer remainingLogonAttempt) {
	this.remainingLogonAttempt = remainingLogonAttempt;
    }

    public char[] getTemporaryPassword() {
	return temporaryPassword;
    }

    public void setTemporaryPassword(char[] temporaryPassword) {
	this.temporaryPassword = temporaryPassword;
    }

    public UserAccount getUserAccount() {
	return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
	this.userAccount = userAccount;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((email == null) ? 0 : email.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}

	if (obj == null) {
	    return false;
	}

	if (!(obj instanceof User)) {
	    return false;
	}

	User other = (User) obj;

	if (email == null) {
	    if (other.email != null) {
	    	return false;
	    }
	} else if (!email.equals(other.email)) {
	    return false;
	}

	return true;
    }

}
