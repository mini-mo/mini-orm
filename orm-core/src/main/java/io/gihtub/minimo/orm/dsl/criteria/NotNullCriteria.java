package io.gihtub.minimo.orm.dsl.criteria;

public class NotNullCriteria extends BinaryCriteria {
    public NotNullCriteria(BinaryCriteria binaryCriteria) {
        super(binaryCriteria.name);
    }
}
