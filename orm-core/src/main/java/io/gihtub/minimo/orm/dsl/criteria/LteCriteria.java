package io.gihtub.minimo.orm.dsl.criteria;

public class LteCriteria extends BinaryCriteria {
    public final Object v;

    public LteCriteria(BinaryCriteria binaryCriteria, Object v) {
        super(binaryCriteria.name);
        this.v = v;
    }
}
