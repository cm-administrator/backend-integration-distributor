package com.br.distributors.specification;

import java.time.Instant;

import org.springframework.data.jpa.domain.Specification;

import com.br.distributors.models.RecentlyReadFiles;

import jakarta.persistence.criteria.Expression;

public final class RecentlyReadFilesSpecification {

	public static Specification<RecentlyReadFiles> distributorIdentifierEquals(String distributorIdentifier) {
		return (root, query, cb) -> {
			query.orderBy(cb.desc(root.get("createdDate")));
			return cb.equal(root.get("distributor").get("identifier"), distributorIdentifier);
		};
	}

	public static Specification<RecentlyReadFiles> customersInstantEquals(Instant v) {
		return instantEquals("customersInstant", v);
	}

	public static Specification<RecentlyReadFiles> salesInstantEquals(Instant v) {
		return instantEquals("salesInstant", v);
	}

	public static Specification<RecentlyReadFiles> stockInstantEquals(Instant v) {
		return instantEquals("stockInstant", v);
	}

	public static Specification<RecentlyReadFiles> productInstantEquals(Instant v) {
		return instantEquals("productInstant", v);
	}

	public static Specification<RecentlyReadFiles> salesPersonInstantEquals(Instant v) {
		return instantEquals("salesPersonInstant", v);
	}

	private static Specification<RecentlyReadFiles> instantEquals(String field, Instant v) {
		return (root, query, cb) -> {
			Expression<Instant> path = root.get(field);
			return (v == null) ? cb.isNull(path) : cb.equal(path, v);
		};
	}

	public static Specification<RecentlyReadFiles> allInstantsEqual(String distributorIdentifier, Instant customers,
			Instant sales, Instant stock, Instant product, Instant salesPerson) {
		return Specification.where(distributorIdentifierEquals(distributorIdentifier))
				.and(customersInstantEquals(customers)).and(salesInstantEquals(sales)).and(stockInstantEquals(stock))
				.and(productInstantEquals(product)).and(salesPersonInstantEquals(salesPerson));
	}
}
