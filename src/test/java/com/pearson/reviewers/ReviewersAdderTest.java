package com.pearson.reviewers;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.changes.AddReviewerInput;
import com.google.gerrit.extensions.api.changes.ChangeApi;
import com.google.gerrit.extensions.api.changes.Changes;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gerrit.server.data.ChangeAttribute;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class ReviewersAdderTest {
    private ChangeApi changeApiMock;
    private ChangeAttribute changeAttributeMock;

    ReviewersAdder sut;

    @Before
    public void setUp()  throws RestApiException {
        GerritApi gerritApiMock = Mockito.mock(GerritApi.class);
        Changes changesMock = Mockito.mock(Changes.class);
        changeApiMock = Mockito.mock(ChangeApi.class);
        changeAttributeMock = Mockito.mock(ChangeAttribute.class);

        when(changesMock.id(anyString(), anyString(), anyString())).thenReturn(changeApiMock);
        when(gerritApiMock.changes()).thenReturn(changesMock);

        sut = new ReviewersAdder(gerritApiMock);
    }

    @Test
    public void shouldNotAddReviewersIfReviewersAreEmpty() throws RestApiException {
        // given
        List<String> reviewers = new ArrayList<>();

        // when
        sut.addReviewers(changeAttributeMock, reviewers);

        // then
        verify(changeApiMock, never()).addReviewer(anyString());
    }

    @Test
    public void shouldAddReviewersIfReviewersAreDefined() throws RestApiException {
        // given
        List<String> reviewers = new ArrayList<>();
        reviewers.add("developer1");
        reviewers.add("developer2");

        // when
        sut.addReviewers(changeAttributeMock, reviewers);

        // then
        verify(changeApiMock, times(2)).addReviewer(any(AddReviewerInput.class));
    }
}
