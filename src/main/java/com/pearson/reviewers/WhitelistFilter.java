package com.pearson.reviewers;

import com.google.inject.Inject;

public class WhitelistFilter {
    private Configuration configuration;

    @Inject
    public WhitelistFilter(Configuration configuration) {
        this.configuration = configuration;
    }

    public boolean isProjectNameOnTheWhitelist(String projectName) {
        for(String projectNamePattern : configuration.getProjectsWhitelist()) {
            if (projectName.matches(projectNamePattern)) {
                return true;
            }
        }
        return false;
    }

    public boolean isGroupNameOnTheWhitelist(String groupName) {
        for(String groupNamePattern : configuration.getGroupsWhitelist()) {
            if (groupName.matches(groupNamePattern)) {
                return true;
            }
        }
        return false;
    }
}
