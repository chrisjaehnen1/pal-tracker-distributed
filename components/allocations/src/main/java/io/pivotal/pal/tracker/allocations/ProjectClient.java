package io.pivotal.pal.tracker.allocations;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProjectClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RestOperations restOperations;
    private final String endpoint;
    private final ConcurrentMap<Long, ProjectInfo> projectCache;

    public ProjectClient(RestOperations restOperations, String endpoint) {
        this.restOperations= restOperations;
        this.endpoint = endpoint;
        this.projectCache = new ConcurrentHashMap<>();
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo project = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        projectCache.put(projectId, project);
        return project;
    }

    private ProjectInfo getProjectFromCache(long projectId) {
        logger.info("Getting project with id {} from cache", projectId);
        return projectCache.get(projectId);
    }
}
