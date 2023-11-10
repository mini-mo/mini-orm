package io.github.minimo.orm.dsl.criteria;

public class GteCriteria extends BinaryCriteria {
    public final Object v;

    public GteCriteria(BinaryCriteria binaryCriteria, Object v) {
        super(binaryCriteria.name);
        this.v = v;
    }
}
