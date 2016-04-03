package com.pearson.reviewers;

import com.google.gerrit.server.data.AccountAttribute;
import com.google.gerrit.server.data.ChangeAttribute;
import com.google.gerrit.server.events.PatchSetCreatedEvent;
import com.google.gwtorm.server.OrmException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

public class ReviewersFinderTest {
    private PatchSetCreatedEvent eventMock;
    private ChangeAttribute changeAttributeMock;
    private AccountAttribute accountAttributeMock;
    private WhitelistFilter whitelistFilterMock;
    private GroupsProvider groupsProviderMock;
    private ReviewersFinder sut;

    @Before
    public void setUp() {
        eventMock = Mockito.mock(PatchSetCreatedEvent.class);
        changeAttributeMock = Mockito.mock(ChangeAttribute.class);
        accountAttributeMock = Mockito.mock(AccountAttribute.class);
        whitelistFilterMock = Mockito.mock(WhitelistFilter.class);
        groupsProviderMock = Mockito.mock(GroupsProvider.class);

        eventMock.change = changeAttributeMock;
        eventMock.uploader = accountAttributeMock;

        sut = new ReviewersFinder(whitelistFilterMock, groupsProviderMock);
    }

    @Test
    public void shouldReturnEmptyReviewersListIfProjectNotOnWhitelist()
        throws OrmException {
        // given
        String projectName = "some-project";

        changeAttributeMock.project = projectName;

        when(whitelistFilterMock.isProjectNameOnTheWhitelist(projectName))
            .thenReturn(false);

        // when
        List<String> result = sut.find(eventMock);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnEmptyReviewersListIfUploaderGroupsNotOnWhitelist()
        throws OrmException {
        // given
        String projectName = "some-project";
        String uploaderEmail = "test@test.com";

        String group01 = "some-group-01";
        String group02 = "some-group-02";

        List<String> groupNames = new ArrayList<>();

        groupNames.add(group01);
        groupNames.add(group02);

        changeAttributeMock.project = projectName;
        accountAttributeMock.email = uploaderEmail;

        when(whitelistFilterMock.isProjectNameOnTheWhitelist(projectName))
            .thenReturn(true);

        when(groupsProviderMock.getGroupNamesByEmail(uploaderEmail))
            .thenReturn(groupNames);

        when(whitelistFilterMock.isGroupNameOnTheWhitelist(group01))
            .thenReturn(false);

        when(whitelistFilterMock.isGroupNameOnTheWhitelist(group02))
            .thenReturn(false);

        // when
        List<String> result = sut.find(eventMock);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnOneReviewerOnListIfOneUploaderGroupsOnWhitelist()
        throws OrmException {
        // given
        String projectName = "some-project";
        String uploaderEmail = "test@test.com";

        String group01 = "some-group-01";
        String group02 = "some-group-02";

        List<String> groupNames = new ArrayList<>();

        groupNames.add(group01);
        groupNames.add(group02);

        changeAttributeMock.project = projectName;
        accountAttributeMock.email = uploaderEmail;

        when(whitelistFilterMock.isProjectNameOnTheWhitelist(projectName))
            .thenReturn(true);

        when(groupsProviderMock.getGroupNamesByEmail(uploaderEmail))
            .thenReturn(groupNames);

        when(whitelistFilterMock.isGroupNameOnTheWhitelist(group01))
            .thenReturn(false);

        when(whitelistFilterMock.isGroupNameOnTheWhitelist(group02))
            .thenReturn(true);

        // when
        List<String> result = sut.find(eventMock);

        // then
        assertEquals(1, result.size());
        assertTrue(result.contains(group02));
    }
}
