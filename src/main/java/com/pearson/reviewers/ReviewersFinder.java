package com.pearson.reviewers;

import com.google.gerrit.server.events.PatchSetCreatedEvent;
import com.google.gwtorm.server.OrmException;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

public class ReviewersFinder {
    private WhitelistFilter filter;
    private GroupsProvider groupsProvider;

    @Inject
    public ReviewersFinder(WhitelistFilter filter, GroupsProvider groupsProvider) {
        this.filter = filter;
        this.groupsProvider = groupsProvider;
    }

    public List<String> find (PatchSetCreatedEvent event)
        throws IllegalArgumentException, OrmException {

        String projectName = event.change.project;
        String uploaderEmail = event.uploader.email;

        List<String> reviewers = new ArrayList<>();

        if (!filter.isProjectNameOnTheWhitelist(projectName)) {
            return reviewers;
        }

        for (String groupName : groupsProvider.getGroupNamesByEmail(uploaderEmail)) {
            if (filter.isGroupNameOnTheWhitelist(groupName)) {
                reviewers.add(groupName);
            }
        }

        return reviewers;
    }
}
