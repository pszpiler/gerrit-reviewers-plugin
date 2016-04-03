package com.pearson.reviewers;

import com.google.gerrit.server.events.PatchSetCreatedEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PluginLogger {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(PatchSetListener.class);

    void logSuccess(PatchSetCreatedEvent event, List<String> reviewers) {
        logger.info(
            String.format(
                "ChangeId: %s, Added reviewers: %s",
                event.change.id,
                StringUtils.join(reviewers, ',')
            )
        );
    }

    void logError(PatchSetCreatedEvent event, Exception exception) {
        logger.error(
            String.format(
                "ChangeId: %s, Cannot add reviewers. Details: %s",
                event.change.id,
                exception.getMessage()
            )
        );
    }
}
