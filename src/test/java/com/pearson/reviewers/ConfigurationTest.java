package com.pearson.reviewers;

import com.google.gerrit.server.config.PluginConfigFactory;
import org.eclipse.jgit.lib.Config;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

public class ConfigurationTest {
    private Config configMock;
    private PluginConfigFactory pluginConfigFactoryMock;
    private Configuration sut;

    @Before
    public void setUp() {
        configMock = Mockito.mock(Config.class);
        pluginConfigFactoryMock = Mockito.mock(PluginConfigFactory.class);

        sut = new Configuration(pluginConfigFactoryMock);
    }

    @Test
    public void shouldReturnProjectsWhitelist() {
        // given
        String [] expectedProjects = { "^project01$", "^project02$" };

        when(configMock.getStringList("whitelist", "projects", "project")).thenReturn(expectedProjects);
        when(pluginConfigFactoryMock.getGlobalPluginConfig("reviewers")).thenReturn(configMock);

        // when
        String [] result = sut.getProjectsWhitelist();

        // then
        assertEquals(expectedProjects, result);
    }

    @Test
    public void shouldReturnGroupsWhitelist() {
        // given
        String [] expectedGroups = { "^group01$", "^group02$" };

        when(configMock.getStringList("whitelist", "groups", "group")).thenReturn(expectedGroups);
        when(pluginConfigFactoryMock.getGlobalPluginConfig("reviewers")).thenReturn(configMock);

        // when
        String [] result = sut.getGroupsWhitelist();

        // then
        assertEquals(expectedGroups, result);
    }
}
