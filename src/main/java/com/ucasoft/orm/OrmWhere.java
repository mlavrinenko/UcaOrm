package com.ucasoft.orm;

import com.ucasoft.orm.exceptions.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by UCASoft.
 * User: Antonov Sergey
 * Date: 11.07.13
 * Time: 16:19
 */
public class OrmWhere {

    private final Class<? extends OrmEntity> entityClass;
    private String where;
    private String order = null;
    private List<String> params;

    public OrmWhere(Class<? extends OrmEntity> entityClass) {
        this.entityClass = entityClass;
        where = "";
        params = new ArrayList<String>();
    }

    public OrmWhere Equals(String column, Object value){
        where += String.format("%s = ?", column);
        if (String.class.isAssignableFrom(value.getClass()))
            params.add(String.format("'%s'", value));
        else
        params.add(value.toString());
        return this;
    }

    private <T extends OrmEntity> List<T> Select(Class<T> entityClass) throws NotFindTableAnnotation, InstantiationException, InvocationTargetException, NoSuchMethodException, WrongRightJoinReference, IllegalAccessException, NotFindPrimaryKeyField, DiscrepancyMappingColumns, WrongJoinLeftReference, WrongListReference {
        return OrmUtils.getEntitiesWhere(entityClass, where, params.toArray(new String[params.size()]), order);
    }

    public <T extends OrmEntity> List<T> Select() throws NotFindTableAnnotation, InstantiationException, InvocationTargetException, NoSuchMethodException, WrongRightJoinReference, IllegalAccessException, WrongListReference, NotFindPrimaryKeyField, DiscrepancyMappingColumns, WrongJoinLeftReference {
        return (List<T>) Select(entityClass);
    }

    private <T extends OrmEntity> T SelectFirst(Class<T> entityClass) throws IllegalAccessException, WrongJoinLeftReference, InstantiationException, InvocationTargetException, NoSuchMethodException, WrongRightJoinReference, NotFindTableAnnotation, DiscrepancyMappingColumns, NotFindPrimaryKeyField, WrongListReference {
        List<T> result = Select(entityClass);
        if (result.size() > 0)
            return result.get(0);
        return null;
    }

    public <T extends OrmEntity> T SelectFirst() throws NotFindTableAnnotation, InstantiationException, InvocationTargetException, NoSuchMethodException, WrongRightJoinReference, IllegalAccessException, NotFindPrimaryKeyField, DiscrepancyMappingColumns, WrongJoinLeftReference, WrongListReference {
        return (T) SelectFirst(entityClass);
    }

    public OrmWhere And() {
        where += " and ";
        return this;
    }

    public OrmWhere Or() {
        where += " or ";
        return this;
    }

    public OrmWhere OrderBy(OrmOrder direction, String... columns) throws TooManyOrdersInOrmWhere {
        if (order == null){
            order = String.format("%s %s", Arrays.deepToString(columns).replace("[", "").replace("]", ""), direction);
            return this;
        }
        throw new TooManyOrdersInOrmWhere();
    }

    public OrmWhere FindChild(Class<? extends OrmEntity> entityClass) {
        return FindChild(entityClass, null);
    }

    public OrmWhere FindChild(Class<? extends OrmEntity> entityClass, OrmWhere where) {
        try {
            this.where += String.format("exists(select 1 from %s c where c.%s = %s", OrmTableWorker.getTableName(entityClass), OrmFieldWorker.getReferenceField(entityClass, this.entityClass).getName(), OrmFieldWorker.getPrimaryKeyField(this.entityClass).getName());
            if (where != null) {
                this.where += String.format(" and c.%s", String.format(where.where.replace("?", "%s"), where.params).replace("[", "").replace("]", ""));
            }
            this.where += ")";
            return this;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
