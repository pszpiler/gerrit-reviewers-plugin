package com.pearson.reviewers;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class WhitelistFilterTest {

    private Configuration configurationMock;
    private WhitelistFilter sut;

    @Before
    public void setUp() {
        configurationMock = Mockito.mock(Configuration.class);
        sut = new WhitelistFilter(configurationMock);
    }

    @Test
    @UseDataProvider("dpShouldReturnTrueIfProjectNameIsOnWhitelist")
    public void shouldReturnTrueIfProjectNameIsOnWhitelist(String projectName, String [] projectsWhitelist) {
        // given
        when(configurationMock.getProjectsWhitelist()).thenReturn(projectsWhitelist);

        // when
        boolean result = sut.isProjectNameOnTheWhitelist(projectName);

        // then
        assertTrue(result);
    }

    @DataProvider
    public static Object[][] dpShouldReturnTrueIfProjectNameIsOnWhitelist() {
        return new Object[][] {
            { "nmel-project", new String[] { "^monitoring-aggregator$", "^nmel-project$" } },
            { "nmel-project", new String[] { "^monitoring-aggregator$", "^nmel.*" } },
            { "nmel-project", new String[] { "^monitoring-aggregator$", ".*project$" } },
            { "nmel-project", new String[] { "^nmel-project$" } },
        };
    }

    @Test
    @UseDataProvider("dpShouldReturnFalseIfProjectNameIsNotOnWhitelist")
    public void shouldReturnFalseIfProjectNameIsNotOnWhitelist(String projectName, String [] projectsWhitelist) {
        // given
        when(configurationMock.getProjectsWhitelist()).thenReturn(projectsWhitelist);

        // when
        boolean result = sut.isProjectNameOnTheWhitelist(projectName);

        // then
        assertFalse(result);
    }

    @DataProvider
    public static Object[][] dpShouldReturnFalseIfProjectNameIsNotOnWhitelist() {
        return new Object[][] {
            { "nmel-project", new String[] { } },
            { "nmel-project", new String[] { "xyz" } },
            { "nmel-project", new String[] { "^monitoring-aggregator$" } },
            { "nmel-project", new String[] { "^monitoring-aggregator$", "newngmel" } },
            { "nmel-project", new String[] { "^monitoring-aggregator$", ".*[0-9].*" } },
        };
    }

    @Test
    @UseDataProvider("dpShouldReturnTrueIfGroupNameIsOnWhitelist")
    public void shouldReturnTrueIfGroupNameIsOnWhitelist(String groupName, String [] groupsWhitelist) {
        // given
        when(configurationMock.getGroupsWhitelist()).thenReturn(groupsWhitelist);

        // when
        boolean result = sut.isGroupNameOnTheWhitelist(groupName);

        // then
        assertTrue(result);
    }

    @DataProvider
    public static Object[][] dpShouldReturnTrueIfGroupNameIsOnWhitelist() {
        return new Object[][] {
            { "memory-leak", new String[] { "^green-bandits$", "^memory-leak$" } },
            { "memory-leak", new String[] { "^green-bandits$", "^memory.*" } },
            { "memory-leak", new String[] { "^green-bandits$", ".*leak$" } },
            { "memory-leak", new String[] { "^memory-leak$" } },
        };
    }

    @Test
    @UseDataProvider("dpShouldReturnFalseIfGroupNameIsNotOnWhitelist")
    public void shouldReturnFalseIfGroupNameIsNotOnWhitelist(String groupName, String [] groupsWhitelist) {
        // given
        when(configurationMock.getGroupsWhitelist()).thenReturn(groupsWhitelist);

        // when
        boolean result = sut.isGroupNameOnTheWhitelist(groupName);

        // then
        assertFalse(result);
    }

    @DataProvider
    public static Object[][] dpShouldReturnFalseIfGroupNameIsNotOnWhitelist() {
        return new Object[][] {
            { "memory-leak", new String[] { } },
            { "memory-leak", new String[] { "xyz" } },
            { "memory-leak", new String[] { "^green-bandits$", ".*leeak$" } },
            { "memory-leak", new String[] { "^emory-leak$" } },
        };
    }
}
