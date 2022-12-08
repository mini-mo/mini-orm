package io.gihtub.minimo.orm.dsl.criteria;

public class LikeCriteria extends BinaryCriteria {
    public final String v;

    public LikeCriteria(BinaryCriteria binaryCriteria, String v) {
        super(binaryCriteria.name);
        this.v = v;
    }
}
