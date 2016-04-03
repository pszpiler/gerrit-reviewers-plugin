package com.pearson.reviewers;

import com.google.gerrit.common.EventListener;
import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.server.events.Event;
import com.google.gerrit.server.events.PatchSetCreatedEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

@Listen
@Singleton
public class PatchSetListener implements EventListener {
    private ReviewersFinder reviewersFinder;
    private ReviewersAdder reviewersAdder;
    private PluginLogger logger;

    @Inject
    public PatchSetListener(
        ReviewersFinder reviewersFinder,
        ReviewersAdder reviewersAdder,
        PluginLogger logger
    ) {
        this.reviewersFinder = reviewersFinder;
        this.reviewersAdder = reviewersAdder;
        this.logger = logger;
    }

    @Override
    public void onEvent(Event changeEvent) {
        if (!(changeEvent instanceof PatchSetCreatedEvent)) {
            return;
        }
        PatchSetCreatedEvent event = (PatchSetCreatedEvent) changeEvent;
        try {
            List<String> reviewers = reviewersFinder.find(event);
            reviewersAdder.addReviewers(event.change, reviewers);
            logger.logSuccess(event, reviewers);
        } catch (Exception exception) {
            logger.logError(event, exception);
        }
    }
}
