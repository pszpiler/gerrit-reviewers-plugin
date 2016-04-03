package com.pearson.reviewers;

import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gerrit.server.data.ChangeAttribute;
import com.google.gerrit.server.events.Event;
import com.google.gerrit.server.events.PatchSetCreatedEvent;
import com.google.gwtorm.server.OrmException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class PatchSetListenerTest {

    private ReviewersFinder reviewersFinderMock;
    private ReviewersAdder reviewersAdderMock;
    private PluginLogger loggerMock;
    private ChangeAttribute changeAttributeMock;
    private PatchSetListener sut;

    @Before
    public void setUp() {
        reviewersFinderMock = Mockito.mock(ReviewersFinder.class);
        reviewersAdderMock = Mockito.mock(ReviewersAdder.class);
        loggerMock = Mockito.mock(PluginLogger.class);
        changeAttributeMock = Mockito.mock(ChangeAttribute.class);

        sut = new PatchSetListener(reviewersFinderMock, reviewersAdderMock, loggerMock);
    }

    @Test
    public void shouldNotDoAnythingIfWrongEventInstance() throws OrmException, RestApiException {
        // given
        Event eventMock = Mockito.mock(Event.class);

        // when
        sut.onEvent(eventMock);

        // then
        verify(reviewersFinderMock, never())
            .find(any(PatchSetCreatedEvent.class));

        verify(reviewersAdderMock, never())
            .addReviewers(any(ChangeAttribute.class), anyListOf(String.class));

        verify(loggerMock, never())
            .logSuccess(any(PatchSetCreatedEvent.class), anyListOf(String.class));

        verify(loggerMock, never())
            .logError(any(PatchSetCreatedEvent.class), any(Exception.class));
    }

    @Test
    public void shouldNotAddReviewersAndLogErrorIfFindReviewerFails() throws OrmException, RestApiException {
        // given
        PatchSetCreatedEvent eventMock = Mockito.mock(PatchSetCreatedEvent.class);
        OrmException exception = new OrmException("some-error");

        doThrow(exception).when(reviewersFinderMock)
            .find(eventMock);

        // when
        sut.onEvent(eventMock);

        // then
        verify(reviewersAdderMock, never())
            .addReviewers(any(ChangeAttribute.class), anyListOf(String.class));

        verify(loggerMock, never())
            .logSuccess(any(PatchSetCreatedEvent.class), anyListOf(String.class));

        verify(loggerMock, times(1))
            .logError(eventMock, exception);
    }

    @Test
    public void shouldNotAddReviewersAndLogErrorIfAddReviewerFails() throws OrmException, RestApiException {
        // given
        PatchSetCreatedEvent eventMock = Mockito.mock(PatchSetCreatedEvent.class);
        RestApiException exception = new RestApiException();

        List<String> reviewers = new ArrayList<>();
        reviewers.add("developer1");

        eventMock.change = changeAttributeMock;

        when(reviewersFinderMock.find(eventMock))
            .thenReturn(reviewers);

        doThrow(exception).when(reviewersAdderMock)
            .addReviewers(changeAttributeMock, reviewers);

        // when
        sut.onEvent(eventMock);

        // then
        verify(reviewersFinderMock, times(1))
            .find(eventMock);

        verify(loggerMock, never())
            .logSuccess(any(PatchSetCreatedEvent.class), anyListOf(String.class));

        verify(loggerMock, times(1))
            .logError(eventMock, exception);
    }

    @Test
    public void shouldAddReviewersAndLogSuccess() throws OrmException, RestApiException {
        // given
        PatchSetCreatedEvent eventMock = Mockito.mock(PatchSetCreatedEvent.class);

        eventMock.change = changeAttributeMock;

        List<String> reviewers = new ArrayList<>();
        reviewers.add("developer1");

        when(reviewersFinderMock.find(eventMock))
            .thenReturn(reviewers);

        // when
        sut.onEvent(eventMock);

        // then
        verify(reviewersFinderMock, times(1))
            .find(eventMock);

        verify(reviewersAdderMock, times(1))
            .addReviewers(changeAttributeMock, reviewers);

        verify(loggerMock, times(1))
            .logSuccess(eventMock, reviewers);

        verify(loggerMock, never())
            .logError(eq(eventMock), any(Exception.class));
    }
}