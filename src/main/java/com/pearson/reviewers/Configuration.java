package com.pearson.reviewers;

import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;

public class Configuration {
    private static final String FILE_NAME = "reviewers";
    private static final String SECTION_NAME = "whitelist";
    private static final String PROJECTS_WHITELIST_NAME = "projects";
    private static final String PROJECTS_WHITELIST_ITEM = "project";
    private static final String GROUPS_WHITELIST_NAME = "groups";
    private static final String GROUPS_WHITELIST_ITEM = "group";

    private PluginConfigFactory pluginConfigFactory;

    @Inject
    public Configuration(PluginConfigFactory pluginConfigFactory) {
        this.pluginConfigFactory = pluginConfigFactory;
    }

    public String[] getProjectsWhitelist() {
        return pluginConfigFactory
            .getGlobalPluginConfig(FILE_NAME)
            .getStringList(
                SECTION_NAME,
                PROJECTS_WHITELIST_NAME,
                PROJECTS_WHITELIST_ITEM
            );
    }

    public String[] getGroupsWhitelist() {
        return pluginConfigFactory
            .getGlobalPluginConfig(FILE_NAME)
            .getStringList(
                SECTION_NAME,
                GROUPS_WHITELIST_NAME,
                GROUPS_WHITELIST_ITEM
            );
    }
}
