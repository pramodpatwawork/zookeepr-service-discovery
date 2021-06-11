package com.mywork.zookeepr.service.discovery;

public interface ServiceRegistry {

	public void registerService(String name, String uri);

	public void unregisterService(String name);

	public String discoverServiceURI(String name);
}
