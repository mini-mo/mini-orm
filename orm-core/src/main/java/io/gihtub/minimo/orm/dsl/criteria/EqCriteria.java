package io.gihtub.minimo.orm.dsl.criteria;

public class EqCriteria extends BinaryCriteria {
    public final Object v;

    public EqCriteria(BinaryCriteria binaryCriteria, Object v) {
        super(binaryCriteria.name);
        this.v = v;
    }
}
