package util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class JPAUtil {
	private static EntityManagerFactory entityManagerFactory;
	static {
		entityManagerFactory = Persistence.createEntityManagerFactory("SKRestaurant_Server");
	}

	public static EntityManagerFactory sharedEntityManagerFactory() {
		return entityManagerFactory;
	}
}
