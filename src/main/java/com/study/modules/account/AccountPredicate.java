package com.study.modules.account;

import java.util.Set;

import javax.persistence.criteria.From;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import com.study.modules.tag.Tag;
import com.study.modules.zone.Zone;

public class AccountPredicate {
	
	public static Predicate findByZonesOrTags(Set<Zone> zones, Set<Tag> tags) {
		return QAccount.account.zones.any().in(zones).and(QAccount.account.tags.any().in(tags));
	}

}
