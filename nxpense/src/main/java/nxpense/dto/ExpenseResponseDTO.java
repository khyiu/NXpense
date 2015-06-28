package nxpense.dto;

/**
 *
 */
public class ExpenseResponseDTO extends ExpenseDTO {

    private Integer id;
    private Integer nbAttachment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNbAttachment() {
        return nbAttachment;
    }

    public void setNbAttachment(Integer nbAttachment) {
        this.nbAttachment = nbAttachment;
    }
}
