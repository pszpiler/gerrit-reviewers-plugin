package com.pearson.reviewers;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.changes.AddReviewerInput;
import com.google.gerrit.extensions.api.changes.ChangeApi;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gerrit.server.data.ChangeAttribute;
import com.google.inject.Inject;

import java.util.List;

public class ReviewersAdder {

    private final GerritApi gerritApi;

    @Inject
    public ReviewersAdder(GerritApi gerritApi) {
        this.gerritApi = gerritApi;
    }

    public void addReviewers(ChangeAttribute changeAttribute, List<String> reviewers)
        throws RestApiException {
        ChangeApi changeApi = createChangeApi(changeAttribute);
        AddReviewerInput addReviewerInput = createAddReviewerInput();
        for (String reviewer : reviewers) {
            addReviewerInput.reviewer = reviewer;
            changeApi.addReviewer(addReviewerInput);
        }
    }

    private ChangeApi createChangeApi(ChangeAttribute changeAttribute)
        throws RestApiException {
        return gerritApi.changes().id(
            changeAttribute.project,
            changeAttribute.branch,
            changeAttribute.id
        );
    }

    private AddReviewerInput createAddReviewerInput() {
        AddReviewerInput addReviewerInput = new AddReviewerInput();
        addReviewerInput.confirmed = true;
        return addReviewerInput;
    }
}
