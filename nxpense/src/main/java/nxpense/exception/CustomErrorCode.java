package nxpense.exception;

public enum CustomErrorCode {

    ENTITY_OUT_OF_SYNC(499);

    private int httpStatus;

    CustomErrorCode(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return this.httpStatus;
    }
}
