package ewing.dandelion;

/**
 * 数据访问异常类。
 *
 * @author Ewing
 */
public class DaoException extends RuntimeException {

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

}
