package nxpense.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USR")
@SequenceGenerator(name = "USER_SEQ", sequenceName = "user_seq")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ")
    private Integer id;

    private String email;

    private char[] password;

    @Column(name = "REMAINING_LOGON_ATTEMPT", insertable = false, updatable = false)
    private Integer remainingLogonAttempt;

    private char[] temporaryPassword;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "USER_ID", nullable = false, updatable = false)
    @OrderColumn(nullable = false, name = "POSITION")
    private List<Expense> expenses = new ArrayList<Expense>();

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

    public void addExpense(Expense expense) {
        this.expenses.add(expense);
    }

    public void removeExpense(Expense expense) {
        this.expenses.remove(expense);
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
