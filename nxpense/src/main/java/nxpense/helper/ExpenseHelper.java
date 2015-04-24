package nxpense.helper;


import nxpense.domain.Expense;

public class ExpenseHelper {
    private ExpenseHelper() {

    }

    /**
     * Overwrites the {@code destination} expense item's fields values with values coming from the {@code source} expense item
     * without regard to their respective source (see {@link nxpense.dto.ExpenseSource}) and {@link nxpense.domain.Expense#position}.
     * @param source Expense item whose value overwrites the destination item's values
     * @param destination Expense item whose values are to be overwritten
     */
    public static void overwriteFields(Expense source, Expense destination) {
        if(source != null && destination != null) {
            destination.setDate(source.getDate());
            destination.setAmount(source.getAmount());
            destination.setDescription(source.getDescription());
        }
    }
}
