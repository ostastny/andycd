package cz.ostastny.andycd;

import javax.inject.Singleton;

import org.glassfish.hk2.api.Factory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Provides shared instance of Hibernate SessionFactory
 *
 */
public class HbSessionFactoryFactory implements Factory<SessionFactory> {
	@SuppressWarnings("deprecation")
	@Override
	@Singleton
	public SessionFactory provide() {
		try {
	        System.out.println("Building SessionFactory");
	        
			// Create the SessionFactory from hibernate.cfg.xml
			return new Configuration().configure().buildSessionFactory();
		} catch (Exception ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	@Override
	public void dispose(SessionFactory t) {
		// Close caches and connection pools
		if(t != null)
			t.close();
	}
}