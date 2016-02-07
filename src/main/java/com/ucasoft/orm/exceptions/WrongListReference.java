package com.ucasoft.orm.exceptions;

/**
 * Created by UCASoft.
 * User: Antonov Sergey
 * Date: 27.06.13
 * Time: 17:16
 */
public class WrongListReference extends Exception {
    public WrongListReference(Class listClass, Class entityClass) {
        super(String.format("Not found in class %s reference to %s!", listClass.getSimpleName(), entityClass.getSimpleName()));
    }
}
