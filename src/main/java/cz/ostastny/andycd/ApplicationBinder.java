package cz.ostastny.andycd;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import cz.ostastny.andycd.services.GitHubService;

public class ApplicationBinder extends AbstractBinder {
    
	@Override
    protected void configure() {    	
        bindFactory(HbSessionFactoryFactory.class).to(SessionFactory.class).in(Singleton.class);
        bindFactory(HbSessionFactory.class).to(Session.class).in(RequestScoped.class);
        
        bind(GitHubService.class).to(GitHubService.class).in(Singleton.class);
    }
}
