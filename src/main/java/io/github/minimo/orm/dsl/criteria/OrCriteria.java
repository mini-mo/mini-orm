package io.github.minimo.orm.dsl.criteria;

public class OrCriteria implements Criteria {
    public final Criteria[] criteria;

    public OrCriteria(Criteria[] criteria) {
        this.criteria = criteria;
    }
}
