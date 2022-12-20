package io.gihtub.minimo.orm.dsl.criteria;

public class NotEqCriteria extends BinaryCriteria {
    public final Object v;
    public NotEqCriteria(BinaryCriteria binaryCriteria, Object v) {
        super(binaryCriteria.name);
        this.v = v;
    }
}
