package com.mywork.zookeepr.service.discovery;

import java.io.IOException;
import java.util.Properties;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Component;

@Component
public class ZooKeeperServiceRegistry implements ServiceRegistry {

    private final CuratorFramework curatorFramework;
    
    private final String connectionString = "ip-172-31-2-110.us-east-2.compute.internal:2181,ip-172-31-6-199.us-east-2.compute.internal:2181,ip-172-31-10-239.us-east-2.compute.internal:2181";
        
    public ZooKeeperServiceRegistry() {
        try {
            Properties props = new Properties();
            props.load(this.getClass().getResourceAsStream("/zookeeper.properties"));
            
            curatorFramework = CuratorFrameworkFactory
                    .newClient(connectionString, new RetryNTimes(5, 1000));
            curatorFramework.start();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getLocalizedMessage());
        }
    }

    @Override
    public void registerService(String name, String uri) {
        try {
            String znode = name;

            if (curatorFramework.checkExists().forPath(znode) == null) {
                curatorFramework.create().
                creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(znode,uri.getBytes());
            }else {
            	curatorFramework.setData()
            	.forPath(znode,uri.getBytes());
            }
            
        } catch (Exception ex) {
            throw new RuntimeException("Could not register service \"" 
                    + name 
                    + "\", with URI \"" + uri + "\": " + ex.getLocalizedMessage());
        }
    }
    
    @Override
    public void unregisterService(String name) {
        try {
        	if (curatorFramework.checkExists().forPath(name) != null) {
                curatorFramework.delete().forPath(name);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not unregister service \"" 
                    + name 
                    +  "\": " + ex.getLocalizedMessage());
        }
    }

    @Override
    public String discoverServiceURI(String name) {
        try {
            String znode = name;
            return new String(curatorFramework.getData().forPath(znode));
        } catch (Exception ex) {
            throw new RuntimeException("Service \"" + name + "\" not found: " + ex.getLocalizedMessage());
        }
    }
}
